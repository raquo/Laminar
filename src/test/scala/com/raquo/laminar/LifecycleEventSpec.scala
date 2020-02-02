package com.raquo.laminar

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.api.L._
import com.raquo.laminar.lifecycle.{LifecycleEvent, NodeDidMount, NodeWillUnmount, ParentChangeEvent}
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.collection.mutable

class LifecycleEventSpec extends UnitSpec {

  case class TestCase(
    action: ReactiveElement[dom.html.Element] => Unit,
//    expectedChangeEvents: Seq[ParentChangeEvent],
    expectedLifecycleEvents: Seq[LifecycleEvent]
  )

  it("mount event transaction timing - level 1") {

    val textBus = new EventBus[String]

    var events = mutable.Buffer[LifecycleEvent]();

    val readMountEvents: Mod[HtmlElement] = new Mod[HtmlElement] {
      override def apply(element: HtmlElement): Unit = {
        element.subscribe(_.lifecycleEvents){ ev => events.append(ev) }
      }
    }

    def makeChild(text: String): Div = div(readMountEvents, text)

    val $child = textBus.events.map(makeChild)

    // --

    mount(section("Hello, ", child <-- $child))

    events shouldEqual mutable.Buffer()
    expectNode(section like ("Hello, ", ExpectedNode.comment()))

    // --

    textBus.writer.onNext("blah")

    events shouldEqual mutable.Buffer(NodeDidMount)
    expectNode(section like ("Hello, ", div like ("blah")))

    // --

    textBus.writer.onNext("world")

    events shouldEqual mutable.Buffer(NodeDidMount, NodeWillUnmount, NodeDidMount)
    expectNode(section like ("Hello, ", div like ("world")))

  }

  it("mount event transaction timing - level 2 (nesting)") {

    val textBus = new EventBus[String]

    var events = mutable.Buffer[LifecycleEvent]();

    val readMountEvents: Mod[HtmlElement] = new Mod[HtmlElement] {
      override def apply(element: HtmlElement): Unit = {
        element.subscribe(_.lifecycleEvents){ ev => events.append(ev) }
      }
    }

    def makeChild(text: String): Div = div(span(readMountEvents, text))

    val $child = textBus.events.map(makeChild)

    // --

    mount(section("Hello, ", child <-- $child))

    events shouldEqual mutable.Buffer()
    expectNode(section like ("Hello, ", ExpectedNode.comment()))

    // --

    textBus.writer.onNext("blah")

    events shouldEqual mutable.Buffer(NodeDidMount)
    expectNode(section like ("Hello, ", div like ( span like ("blah"))))

    // --

    textBus.writer.onNext("world")

    events shouldEqual mutable.Buffer(NodeDidMount, NodeWillUnmount, NodeDidMount)
    expectNode(section like ("Hello, ", div like ( span like ("world"))))
  }

  it("Changing parent on a node fires appropriate events") {

    val grandChild = b("grandChild ")
    val child = span("the child ", grandChild)
    val otherChild = i("i ")

    val parent1 = div("parent1") // @TODO remove this child
    val parent2 = p("parent2 ", "(two) ")

//    var parentEvents = Seq[ParentChangeEvent]()
    var mountEvents = Seq[LifecycleEvent]()
    var grandChildMountEvents = Seq[LifecycleEvent]()

    mount(div("root ", parent1, parent2))

    // If we wanted to test ParentChangeEvent-s, we would need to:
    // - relax val visibility to allow that, and
    // - use a different owner to subscribe to those events, because subscriptions on the element itself
    //   will not see those events when they coincide with mounting / unmounting because of transaction delay.
    //   We could potentially special-case these similar to pilotSubscription, but I don't know id that's wise.

    val testCases = Seq(
      TestCase(
        parent1.appendChild,
//        expectedChangeEvents = Seq(
//          ParentChangeEvent(
//            alreadyChanged = false,
//            maybePrevParent = None,
//            maybeNextParent = Some(parent1)
//          ),
//          ParentChangeEvent(
//            alreadyChanged = true,
//            maybePrevParent = None,
//            maybeNextParent = Some(parent1)
//          )
//        ),
        expectedLifecycleEvents = Seq(NodeDidMount)
      ),
      TestCase(
        parent1.appendChild,
//        expectedChangeEvents = Seq(),
        expectedLifecycleEvents = Seq()
      ),
      TestCase(
        child => parent2.insertChild(child, atIndex = 1),
//        expectedChangeEvents = Seq(
//          ParentChangeEvent(
//            alreadyChanged = false,
//            maybePrevParent = Some(parent1),
//            maybeNextParent = Some(parent2)
//          ),
//          ParentChangeEvent(
//            alreadyChanged = true,
//            maybePrevParent = Some(parent1),
//            maybeNextParent = Some(parent2)
//          )
//        ),
        expectedLifecycleEvents = Seq()
      ),
      TestCase(
        parent2.removeChild,
//        expectedChangeEvents = Seq(
//          ParentChangeEvent(
//            alreadyChanged = false,
//            maybePrevParent = Some(parent2),
//            maybeNextParent = None
//          ),
//          ParentChangeEvent(
//            alreadyChanged = true,
//            maybePrevParent = Some(parent2),
//            maybeNextParent = None
//          )
//        ),
        expectedLifecycleEvents = Seq(
          NodeWillUnmount
        )
      ),
      TestCase(
        parent2.appendChild,
//        expectedChangeEvents = Seq(
//          ParentChangeEvent(
//            alreadyChanged = false,
//            maybePrevParent = None,
//            maybeNextParent = Some(parent2)
//          ),
//          ParentChangeEvent(
//            alreadyChanged = true,
//            maybePrevParent = None,
//            maybeNextParent = Some(parent2)
//          )
//        ),
        expectedLifecycleEvents = Seq(NodeDidMount)
      ),
      TestCase(
        parent2.replaceChild(_, otherChild),
//        expectedChangeEvents = Seq(
//          ParentChangeEvent(
//            alreadyChanged = false,
//            maybePrevParent = Some(parent2),
//            maybeNextParent = None
//          ),
//          ParentChangeEvent(
//            alreadyChanged = true,
//            maybePrevParent = Some(parent2),
//            maybeNextParent = None
//          )
//        ),
        expectedLifecycleEvents = Seq(
          NodeWillUnmount
        )
      )
    )

//    child.subscribe(_.parentChangeEvents) { ev =>
//      parentEvents = parentEvents :+ ev
//    }

    child.subscribe(_.lifecycleEvents) { ev =>
      mountEvents = mountEvents :+ ev
    }

//    grandChild.subscribe(_.parentChangeEvents) { _ =>
//      fail("grandChild received a ParentChangeEvent. This is not supposed to happen, as such events should not be inherited by child nodes.")
//    }

    grandChild.subscribe(_.lifecycleEvents) { ev =>
      grandChildMountEvents = grandChildMountEvents :+ ev
    }

    testCases.zipWithIndex.foreach { case (testCase, index) =>
      withClue(s"Case index=$index:") {
        testCase.action(child)
        // parentEvents shouldEqual testCase.expectedChangeEvents
        mountEvents shouldEqual testCase.expectedLifecycleEvents
        grandChildMountEvents shouldEqual testCase.expectedLifecycleEvents // inheritance. assumes grandchild has no other parents and no mount events of its own
//        parentEvents = Seq()
        mountEvents = Seq()
        grandChildMountEvents = Seq()
      }
    }
  }

  // This test will not pass because
  // - these fields are now hidden
  // - more importantly, we now initialize mountEvents from the start. Maybe we'll get around that eventually
//  it("parentChangeEvents lazy val is not initialized if nothing asks for it even if the node has children") {
//
//    val parent1 = div()
//    val parent2 = p("Yolo")
//
//    val child = span(b("orly"), "yarly")
//
//    child.maybeParentChangeBus.isDefined shouldBe false
//    child.maybeThisLifecycleMountEventBus.isDefined shouldBe false
//
//    parent1(child)
//
//    child.maybeParentChangeBus.isDefined shouldBe false
//    child.maybeThisLifecycleMountEventBus.isDefined shouldBe false
//
//    parent2(child)
//
//    child.maybeParentChangeBus.isDefined shouldBe false
//    child.maybeThisLifecycleMountEventBus.isDefined shouldBe false
//
//    child.parentChangeEvents // Touching this should be enough to initialize `maybeParentChangeBus`
//
//    child.maybeParentChangeBus.isDefined shouldBe true
//    child.maybeThisLifecycleMountEventBus.isDefined shouldBe false
//
//    child.mountEvents // Touching this should be enough to initialize `maybeThisLifecycleMountEventBus`
//
//    child.maybeParentChangeBus.isDefined shouldBe true
//    child.maybeThisLifecycleMountEventBus.isDefined shouldBe true
//  }

  it("Nested mount events") {
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

    type TargetNodeWithLifecycleEvent = (ReactiveElement[dom.html.Element], LifecycleEvent)

    var lifecycleEvents: Seq[TargetNodeWithLifecycleEvent] = Nil

    def subscribeToEvents(node: ReactiveElement[dom.html.Element]): Unit = {
      node.subscribe(_.lifecycleEvents) { ev =>
        lifecycleEvents = lifecycleEvents :+ (node, ev)
      }
    }

    def expectNewEvents(
      clue: String,
      expectedMountEvents: Seq[(ReactiveElement[dom.html.Element], LifecycleEvent)]
    ): Unit = {
      withClue(clue + ": ") {
        lifecycleEvents shouldEqual expectedMountEvents
        lifecycleEvents = Nil
      }
    }

    parent1.appendChild(child1)
    parent2.appendChild(child2)

    subscribeToEvents(child1)

    val grandParent = div("root ", parent1, parent2, parent3)

    subscribeToEvents(grandParent)

    expectNewEvents(
      "grandparent was initialized",
      Nil
    )

    mount(grandParent)

    expectNewEvents(
      "grandparent was mounted",
      Seq((grandParent, NodeDidMount), (child1, NodeDidMount))
    )

    subscribeToEvents(child2)

    subscribeToEvents(child3)
    subscribeToEvents(grandChild3)
    expectNewEvents(
      "child3 and grandChild3 streams were initialized and subscribed to",
      Nil
    )

    parent3.appendChild(child3)

    expectNewEvents(
      "child3 was added to a mounted parent3",
      Seq((child3, NodeDidMount), (grandChild3, NodeDidMount))
    )

    subscribeToEvents(parent4)
    subscribeToEvents(child4)
    parent4.appendChild(child4)

    expectNewEvents(
      "child4 was added to unmounted parent4",
      Nil
    )

    grandParent.appendChild(parent4)

    expectNewEvents(
      "parent4 was mounted",
      Seq((parent4, NodeDidMount), (child4, NodeDidMount)) // @TODO[Docs] Document: the same mount event is propagated to listeners in the order in which the listeners subscribed
    )

    parent3.appendChild(child4)

    expectNewEvents(
      "child4 was moved to parent3 which is also mounted",
      Nil
    )

    parent5.appendChild(child4)

    expectNewEvents(
      "child4 was moved to parent5 which is unmounted",
      Seq((child4, NodeWillUnmount))
    )

    parent5.appendChild(parent2)

    expectNewEvents(
      "parent2 was moved into parent5 which is unmounted",
      Seq((child2, NodeWillUnmount))
    )

    subscribeToEvents(parent3)

    expectNewEvents(
      "parent3 streams were subscribed to",
      Nil
    )

    parent5.appendChild(parent3)

    expectNewEvents(
      "parent3 was moved into parent5 which is unmounted",
      Seq(
        (parent3, NodeWillUnmount),
        (child3, NodeWillUnmount),
        (grandChild3, NodeWillUnmount)
      )
    )
  }
}
