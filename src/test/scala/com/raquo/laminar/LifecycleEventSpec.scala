package com.raquo.laminar

import com.raquo.laminar.bundle._
import com.raquo.laminar.lifecycle.{MountEvent, NodeDidMount, NodeWillBeDiscarded, NodeWillUnmount, ParentChangeEvent}
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec
import com.raquo.xstream.Listener
import org.scalajs.dom

class LifecycleEventSpec extends UnitSpec {

  case class TestCase(
    action: ReactiveElement[dom.html.Element] => Unit,
    expectedChangeEvents: Seq[ParentChangeEvent],
    expectedAncestorMountEvents: Seq[MountEvent] = Seq(),
    expectedMountEvents: Seq[MountEvent]
  )

  it("Changing parent on a node fires appropriate events") {

    val grandChild = b("grandChild ")
    val child = span("child ", grandChild)
    val otherChild = i("i ")

    val parent1 = div("parent1") // @TODO remove this child
    val parent2 = p("parent2 ", "(two) ")

    var parentEvents = Seq[ParentChangeEvent]()
    var mountEvents = Seq[MountEvent]()
    var ancestorMountEvents = Seq[MountEvent]()
    var grandChildMountEvents = Seq[MountEvent]()
    var grandChildAncestorMountEvents = Seq[MountEvent]()

    mount(div("root ", parent1, parent2))

    val testCases = Seq(
      TestCase(
        parent1.appendChild,
        expectedChangeEvents = Seq(
          ParentChangeEvent(
            alreadyChanged = false,
            maybePrevParent = None,
            maybeNextParent = Some(parent1)
          ),
          ParentChangeEvent(
            alreadyChanged = true,
            maybePrevParent = None,
            maybeNextParent = Some(parent1)
          )
        ),
        expectedMountEvents = Seq(NodeDidMount)
      ),
      TestCase(
        parent1.appendChild,
        expectedChangeEvents = Seq(),
        expectedMountEvents = Seq()
      ),
      TestCase(
        child => parent2.insertChild(child, atIndex = 1),
        expectedChangeEvents = Seq(
          ParentChangeEvent(
            alreadyChanged = false,
            maybePrevParent = Some(parent1),
            maybeNextParent = Some(parent2)
          ),
          ParentChangeEvent(
            alreadyChanged = true,
            maybePrevParent = Some(parent1),
            maybeNextParent = Some(parent2)
          )
        ),
        expectedMountEvents = Seq()
      ),
      TestCase(
        parent2.removeChild,
        expectedChangeEvents = Seq(
          ParentChangeEvent(
            alreadyChanged = false,
            maybePrevParent = Some(parent2),
            maybeNextParent = None
          ),
          ParentChangeEvent(
            alreadyChanged = true,
            maybePrevParent = Some(parent2),
            maybeNextParent = None
          )
        ),
        expectedMountEvents = Seq(
          NodeWillUnmount,
          NodeWillBeDiscarded
        )
      ),
      TestCase(
        parent2.appendChild,
        expectedChangeEvents = Seq(
          ParentChangeEvent(
            alreadyChanged = false,
            maybePrevParent = None,
            maybeNextParent = Some(parent2)
          ),
          ParentChangeEvent(
            alreadyChanged = true,
            maybePrevParent = None,
            maybeNextParent = Some(parent2)
          )
        ),
        expectedMountEvents = Seq(NodeDidMount)
      ),
      TestCase(
        parent2.replaceChild(_, otherChild),
        expectedChangeEvents = Seq(
          ParentChangeEvent(
            alreadyChanged = false,
            maybePrevParent = Some(parent2),
            maybeNextParent = None
          ),
          ParentChangeEvent(
            alreadyChanged = true,
            maybePrevParent = Some(parent2),
            maybeNextParent = None
          )
        ),
        expectedMountEvents = Seq(
          NodeWillUnmount,
          NodeWillBeDiscarded
        )
      )
    )

    // @TODO[Test] do we care about garbage-collecting these subscriptions?

    child.$parentChange.addListener(Listener(onNext = ev => {
      parentEvents = parentEvents :+ ev
    }))

    child.$mountEvent.addListener(Listener(onNext = ev => {
      mountEvents = mountEvents :+ ev
    }))

    child.$ancestorMountEvent.addListener(Listener(onNext = ev => {
      ancestorMountEvents = ancestorMountEvents :+ ev
    }))

    grandChild.$parentChange.addListener(Listener(onNext = _ => {
      fail("grandChild received a ParentChangeEvent. This is not supposed to happen, as such events should not be inherited by child nodes.")
    }))

    grandChild.$mountEvent.addListener(Listener(onNext = ev => {
      grandChildMountEvents = grandChildMountEvents :+ ev
    }))

    grandChild.$ancestorMountEvent.addListener(Listener(onNext = ev => {
      grandChildAncestorMountEvents = grandChildAncestorMountEvents :+ ev
    }))

    testCases.zipWithIndex.foreach { case (testCase, index) =>
      withClue(s"Case index=$index:") {
        testCase.action(child)
        parentEvents shouldEqual testCase.expectedChangeEvents
        mountEvents shouldEqual testCase.expectedMountEvents
        ancestorMountEvents shouldEqual testCase.expectedAncestorMountEvents
        grandChildMountEvents shouldEqual testCase.expectedMountEvents // inheritance. assumes grandchild has no other parents and no mount events of its own
        grandChildAncestorMountEvents shouldEqual testCase.expectedMountEvents // inheritance. assumes grandchild has no other parents
        parentEvents = Seq()
        mountEvents = Seq()
        grandChildMountEvents = Seq()
        grandChildAncestorMountEvents = Seq()
      }
    }
  }

  it("$parentChange lazy val is not initialized if nothing asks for it even if the node has children") {

    val parent1 = div()
    val parent2 = p("Yolo")

    val child = span(b("orly"), "yarly")

    child.maybeParentChangeBus.isDefined shouldBe false
    child.maybeThisNodeMountEventBus.isDefined shouldBe false

    parent1(child)

    child.maybeParentChangeBus.isDefined shouldBe false
    child.maybeThisNodeMountEventBus.isDefined shouldBe false

    parent2(child)

    child.maybeParentChangeBus.isDefined shouldBe false
    child.maybeThisNodeMountEventBus.isDefined shouldBe false

    child.$parentChange // Touching this should be enough to initialize `maybeParentChangeBus`

    child.maybeParentChangeBus.isDefined shouldBe true
    child.maybeThisNodeMountEventBus.isDefined shouldBe false

    child.$thisNodeMountEvent // Touching this should be enough to initialize `maybeThisNodeMountEventBus`

    child.maybeParentChangeBus.isDefined shouldBe true
    child.maybeThisNodeMountEventBus.isDefined shouldBe true
  }

  def makeMountEventTest(
    testAncestorMountEvents: Boolean,
    testThisNodeMountEvents: Boolean,
    testMountEvents: Boolean
  ) {
    if (!testAncestorMountEvents && !testThisNodeMountEvents && testAncestorMountEvents) {
      fail("EventTest: there is nothing to test! Specify some options")
    }

    val parent1 = div("parent1 ")
    val parent2 = p("parent2 ")
    val parent3 = article("parent3 ")
    val parent4 = article("parent4 ")
    val parent5 = article("parent5 ")

    val grandChild3 = b("orly3")

    val child1 = span("child1 ", b("orly1"))
    val child2 = span("child2 ", b("orly2"))
    val child3 = span("child3 ", grandChild3)
    val child4 = span("child4 ", b("orly4"))

    type TargetNodeWithMountEvent = (ReactiveElement[dom.html.Element], MountEvent)

    var ancestorMountEvents: Seq[TargetNodeWithMountEvent] = Nil
    var thisNodeMountEvents: Seq[TargetNodeWithMountEvent] = Nil
    var mountEvents: Seq[TargetNodeWithMountEvent] = Nil

    def subscribeToEvents(node: ReactiveElement[dom.html.Element]): Unit = {
      if (testAncestorMountEvents) {
        node.$ancestorMountEvent.addListener(Listener(onNext = ev => {
          ancestorMountEvents = ancestorMountEvents :+ (node, ev)
        }))
      }
      if (testThisNodeMountEvents) {
        node.$thisNodeMountEvent.addListener(Listener(onNext = ev => {
          thisNodeMountEvents = thisNodeMountEvents :+ (node, ev)
        }))
      }
      if (testMountEvents) {
        node.$mountEvent.addListener(Listener(onNext = ev => {
          mountEvents = mountEvents :+ (node, ev)
        }))
      }
    }

    def expectNewEvents(
      clue: String,
      expectedAncestorMountEvents: Seq[(ReactiveElement[dom.html.Element], MountEvent)],
      expectedThisNodeMountEvents: Seq[(ReactiveElement[dom.html.Element], MountEvent)],
      expectedMountEvents: Seq[(ReactiveElement[dom.html.Element], MountEvent)]
    ): Unit = {
      withClue(clue + ": ") {
        if (testAncestorMountEvents) {
          ancestorMountEvents shouldEqual expectedAncestorMountEvents
        }
        if (testThisNodeMountEvents) {
          thisNodeMountEvents shouldEqual expectedThisNodeMountEvents
        }
        if (testMountEvents) {
          mountEvents shouldEqual expectedMountEvents
        }
        resetEvents()
      }
    }

    def resetEvents(): Unit = {
      ancestorMountEvents = Nil
      thisNodeMountEvents = Nil
      mountEvents = Nil
    }

    parent1.appendChild(child1)
    parent2.appendChild(child2)

    subscribeToEvents(child1)

    val grandParent = div("root ", parent1, parent2, parent3)

    subscribeToEvents(grandParent)

    expectNewEvents(
      "grandparent was initialized",
      expectedAncestorMountEvents = Nil,
      expectedThisNodeMountEvents = Nil,
      expectedMountEvents = Nil
    )

    mount(grandParent)

    expectNewEvents(
      "grandparent was mounted",
      expectedAncestorMountEvents = Seq((child1, NodeDidMount)),
      expectedThisNodeMountEvents = Seq((grandParent, NodeDidMount)),
      expectedMountEvents = Seq((child1, NodeDidMount), (grandParent, NodeDidMount))
    )

    subscribeToEvents(child2)

    subscribeToEvents(child3)
    subscribeToEvents(grandChild3)
    expectNewEvents(
      "child3 and grandChild3 streams were initialized and subscribed to",
      expectedAncestorMountEvents = Nil, // MemoryStream inside `$ancestorMountEvent` should not trigger any events here
      expectedThisNodeMountEvents = Nil,
      expectedMountEvents = Nil
    )

    parent3.appendChild(child3)

    expectNewEvents(
      "child3 was added to a mounted parent3",
      expectedAncestorMountEvents = Seq((grandChild3, NodeDidMount)),
      expectedThisNodeMountEvents = Seq((child3, NodeDidMount)),
      expectedMountEvents = Seq((child3, NodeDidMount), (grandChild3, NodeDidMount))
    )

    subscribeToEvents(parent4)
    subscribeToEvents(child4)
    parent4.appendChild(child4)

    expectNewEvents(
      "child4 was added to unmounted parent4",
      expectedAncestorMountEvents = Nil,
      expectedThisNodeMountEvents = Nil,
      expectedMountEvents = Nil
    )

    grandParent.appendChild(parent4)

    expectNewEvents(
      "parent4 was mounted",
      expectedAncestorMountEvents = Seq((child4, NodeDidMount)), // not parent4 because that is not an ancestor event for parent4
      expectedThisNodeMountEvents = Seq((parent4, NodeDidMount)),
      expectedMountEvents = Seq((parent4, NodeDidMount), (child4, NodeDidMount)) // @TODO[Docss] Document: the same mount event is propagated to listeners in the order in which the listeners subscribed
    )

    parent3.appendChild(child4)

    expectNewEvents(
      "child4 was moved to parent3 which is also mounted",
      expectedAncestorMountEvents = Nil,
      expectedThisNodeMountEvents = Nil,
      expectedMountEvents = Nil
    )

    parent5.appendChild(child4)

    expectNewEvents(
      "child4 was moved to parent5 which is unmounted",
      expectedAncestorMountEvents = Nil,
      expectedThisNodeMountEvents = Seq((child4, NodeWillUnmount), (child4, NodeWillBeDiscarded)),
      expectedMountEvents = Seq((child4, NodeWillUnmount), (child4, NodeWillBeDiscarded))
    )

    parent5.appendChild(parent2)

    expectNewEvents(
      "parent2 was moved into parent5 which is unmounted",
      expectedAncestorMountEvents = Seq((child2, NodeWillUnmount), (child2, NodeWillBeDiscarded)),
      expectedThisNodeMountEvents = Nil, // because we're not listening for parent2 events
      expectedMountEvents = Seq((child2, NodeWillUnmount), (child2, NodeWillBeDiscarded))
    )

    subscribeToEvents(parent3)

    expectNewEvents(
      "parent3 streams were subscribed to",
      expectedAncestorMountEvents = Nil,
      expectedThisNodeMountEvents = Nil,
      expectedMountEvents = Nil
    )

    parent5.appendChild(parent3)

    expectNewEvents(
      "parent3 was moved into parent5 which is unmounted",
      expectedAncestorMountEvents = Seq(
        (child3, NodeWillUnmount),
        (grandChild3, NodeWillUnmount),
        (child3, NodeWillBeDiscarded),
        (grandChild3, NodeWillBeDiscarded)
      ),
      expectedThisNodeMountEvents = Seq(
        (parent3, NodeWillUnmount),
        (parent3, NodeWillBeDiscarded)
      ),
      expectedMountEvents = Seq(
        (child3, NodeWillUnmount),
        (grandChild3, NodeWillUnmount),
        (parent3, NodeWillUnmount),
        (child3, NodeWillBeDiscarded),
        (grandChild3, NodeWillBeDiscarded),
        (parent3, NodeWillBeDiscarded)
      )
    )

  }

  // We test various combinations to ensure that the resulkts are the same whether oir not subscribe to streams that we test

  it("MountEventTest (ancestor, thisNode, combined) (false, false, true)")(makeMountEventTest(testAncestorMountEvents = false, testThisNodeMountEvents = false, testMountEvents = true))

  it("MountEventTest (ancestor, thisNode, combined) (false, true, false)")(makeMountEventTest(testAncestorMountEvents = false, testThisNodeMountEvents = true, testMountEvents = false))

  it("MountEventTest (ancestor, thisNode, combined) (false, true, true)")(makeMountEventTest(testAncestorMountEvents = false, testThisNodeMountEvents = true, testMountEvents = true))

  it("MountEventTest (ancestor, thisNode, combined) (true, false, false)")(makeMountEventTest(testAncestorMountEvents = true, testThisNodeMountEvents = false, testMountEvents = false))

  it("MountEventTest (ancestor, thisNode, combined) (true, false, true)")(makeMountEventTest(testAncestorMountEvents = true, testThisNodeMountEvents = false, testMountEvents = true))

  it("MountEventTest (ancestor, thisNode, combined) (true, true, false)")(makeMountEventTest(testAncestorMountEvents = true, testThisNodeMountEvents = true, testMountEvents = false))

  it("MountEventTest (ancestor, thisNode, combined) (true, true, true)")(makeMountEventTest(testAncestorMountEvents = true, testThisNodeMountEvents = true, testMountEvents = true))
}
