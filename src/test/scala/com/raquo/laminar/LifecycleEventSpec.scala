package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.InserterHooks
import com.raquo.laminar.nodes.{ParentNode, ReactiveElement}
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.collection.mutable
import scala.scalajs.js

class LifecycleEventSpec extends UnitSpec {

  val noHooks: js.UndefOr[InserterHooks] = js.undefined

  sealed trait LifecycleEvent
  object NodeDidMount extends LifecycleEvent
  object NodeWillUnmount extends LifecycleEvent

  case class TestCase(
    action: ReactiveElement[dom.html.Element] => Unit,
    expectedLifecycleEvents: Seq[LifecycleEvent]
  )

  it("event timing - level 1") {

    val textBus = new EventBus[String]

    var events = mutable.Buffer[LifecycleEvent]();

    val readLifecycleEvents: Mod[HtmlElement] = new Mod[HtmlElement] {
      override def apply(element: HtmlElement): Unit = {
        element.amend(
          onMountCallback(_ => events.append(NodeDidMount)),
          onUnmountCallback(_ => events.append(NodeWillUnmount))
        )
      }
    }

    def makeChild(text: String): Div = div(readLifecycleEvents, text)

    val childStream = textBus.events.map(makeChild)

    // --

    mount(sectionTag("Hello, ", child <-- childStream))

    events shouldBe mutable.Buffer()
    expectNode(sectionTag.of("Hello, ", sentinel))

    // --

    textBus.writer.onNext("blah")

    events shouldBe mutable.Buffer(NodeDidMount)
    expectNode(sectionTag.of("Hello, ", sentinel, div.of("blah")))

    // --

    textBus.writer.onNext("world")

    events shouldBe mutable.Buffer(NodeDidMount, NodeWillUnmount, NodeDidMount)
    expectNode(sectionTag.of("Hello, ", sentinel, div.of("world")))

  }

  it("event timing - level 2 (nesting)") {

    val textBus = new EventBus[String]

    var events = mutable.Buffer[LifecycleEvent]();

    val readLifecycleEvents: Mod[HtmlElement] = new Mod[HtmlElement] {
      override def apply(element: HtmlElement): Unit = {
        element.amend(
          onMountCallback(_ => events.append(NodeDidMount)),
          onUnmountCallback(_ => events.append(NodeWillUnmount))
        )
      }
    }

    def makeChild(text: String): Div = div(span(readLifecycleEvents, text))

    val childStream = textBus.events.map(makeChild)

    // --

    mount(sectionTag("Hello, ", child <-- childStream))

    events shouldBe mutable.Buffer()
    expectNode(sectionTag.of("Hello, ", sentinel))

    // --

    textBus.writer.onNext("blah")

    events shouldBe mutable.Buffer(NodeDidMount)
    expectNode(sectionTag.of("Hello, ", sentinel, div.of(span.of("blah"))))

    // --

    textBus.writer.onNext("world")

    events shouldBe mutable.Buffer(NodeDidMount, NodeWillUnmount, NodeDidMount)
    expectNode(sectionTag.of("Hello, ", sentinel, div.of( span.of("world"))))
  }

  it("Changing parent on a node fires appropriate events") {

    val grandChild = b("grandChild ")
    val child = span("the child ", grandChild)
    val otherChild = i("i ")

    val parent1 = div("parent1") // @TODO remove this child
    val parent2 = p("parent2 ", "(two) ")

    var lifecycleEvents = Seq[LifecycleEvent]()
    var grandChildLifecycleEvents = Seq[LifecycleEvent]()

    mount(div("root ", parent1, parent2))

    val testCases = Seq(
      TestCase(
        child => ParentNode.appendChild(parent = parent1, child, noHooks),
        expectedLifecycleEvents = Seq(NodeDidMount)
      ),
      TestCase(
        child => ParentNode.appendChild(parent = parent1, child, noHooks),
        expectedLifecycleEvents = Seq()
      ),
      TestCase(
        child => ParentNode.insertChildAtIndex(parent = parent2, child, index = 1, noHooks),
        expectedLifecycleEvents = Seq()
      ),
      TestCase(
        child => ParentNode.removeChild(parent = parent2, child),
        expectedLifecycleEvents = Seq(
          NodeWillUnmount
        )
      ),
      TestCase(
        child => ParentNode.appendChild(parent = parent2, child, noHooks),
        expectedLifecycleEvents = Seq(NodeDidMount)
      ),
      TestCase(
        child => ParentNode.replaceChild(parent = parent2, oldChild = child, newChild = otherChild, noHooks),
        expectedLifecycleEvents = Seq(
          NodeWillUnmount
        )
      )
    )

    child.amend(
      onMountCallback(_ => lifecycleEvents = lifecycleEvents :+ NodeDidMount),
      onUnmountCallback(_ => lifecycleEvents = lifecycleEvents :+ NodeWillUnmount)
    )

    grandChild.amend(
      onMountCallback(_ => grandChildLifecycleEvents = grandChildLifecycleEvents :+ NodeDidMount),
      onUnmountCallback(_ => grandChildLifecycleEvents = grandChildLifecycleEvents :+ NodeWillUnmount)
    )

    testCases.zipWithIndex.foreach { case (testCase, index) =>
      withClue(s"Case index=$index:") {
        testCase.action(child)
        lifecycleEvents shouldBe testCase.expectedLifecycleEvents
        grandChildLifecycleEvents shouldBe testCase.expectedLifecycleEvents // inheritance. assumes grandchild has no other parents and no mount events of its own
        lifecycleEvents = Seq()
        grandChildLifecycleEvents = Seq()
      }
    }
  }

  it("Nested lifecycle events") {
    val parent1 = div("parent1 ")
    val parent2 = p("parent2 ")
    val parent3 = articleTag("parent3 ")
    val parent4 = articleTag("parent4 ")
    val parent5 = articleTag("parent5 ")

    val grandChild3 = b("orly3")

    val child1 = span("child1 ", b("orly1"))
    val child2 = span("child2 ", b("orly2"))
    val child3 = span("child3 ", grandChild3)
    val child4 = span("child4 ", b("orly4"))

    type TargetNodeWithLifecycleEvent = (ReactiveElement[dom.html.Element], LifecycleEvent)

    var lifecycleEvents: Seq[TargetNodeWithLifecycleEvent] = Nil

    def subscribeToEvents(node: ReactiveElement[dom.html.Element]): Unit = {
      node.amend(
        onMountCallback(_ => lifecycleEvents = lifecycleEvents :+ ((node, NodeDidMount))),
        onUnmountCallback(_ => lifecycleEvents = lifecycleEvents :+ ((node, NodeWillUnmount)))
      )
    }

    def expectNewEvents(
      clue: String,
      expectedEvents: Seq[(ReactiveElement[dom.html.Element], LifecycleEvent)]
    ): Unit = {
      withClue(clue + ": ") {
        lifecycleEvents shouldBe expectedEvents
        lifecycleEvents = Nil
      }
    }

    ParentNode.appendChild(parent = parent1, child = child1, noHooks)
    ParentNode.appendChild(parent = parent2, child = child2, noHooks)

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
      Seq((child1, NodeDidMount), (grandParent, NodeDidMount))
    )

    subscribeToEvents(child2)

    subscribeToEvents(child3)
    subscribeToEvents(grandChild3)
    expectNewEvents(
      "child3 and grandChild3 streams were initialized and subscribed to",
      Nil
    )

    ParentNode.appendChild(parent = parent3, child = child3, noHooks)

    expectNewEvents(
      "child3 was added to a mounted parent3",
      Seq((grandChild3, NodeDidMount), (child3, NodeDidMount))
    )

    subscribeToEvents(parent4)
    subscribeToEvents(child4)
    ParentNode.appendChild(parent = parent4, child = child4, noHooks)

    expectNewEvents(
      "child4 was added to unmounted parent4",
      Nil
    )

    ParentNode.appendChild(parent = grandParent, child = parent4, noHooks)

    expectNewEvents(
      "parent4 was mounted",
      Seq((parent4, NodeDidMount), (child4, NodeDidMount)) // @TODO[Docs] Document: the same mount event is propagated to listeners in the order in which the listeners subscribed
    )

    ParentNode.appendChild(parent = parent3, child = child4, noHooks)

    expectNewEvents(
      "child4 was moved to parent3 which is also mounted",
      Nil
    )

    ParentNode.appendChild(parent = parent5, child = child4, noHooks)

    expectNewEvents(
      "child4 was moved to parent5 which is unmounted",
      Seq((child4, NodeWillUnmount))
    )

    ParentNode.appendChild(parent = parent5, child = parent2, noHooks)

    expectNewEvents(
      "parent2 was moved into parent5 which is unmounted",
      Seq((child2, NodeWillUnmount))
    )

    subscribeToEvents(parent3)

    expectNewEvents(
      "parent3 streams were subscribed to",
      Nil
    )

    ParentNode.appendChild(parent = parent5, child = parent3, noHooks)

    expectNewEvents(
      "parent3 was moved into parent5 which is unmounted",
      Seq(
        (grandChild3, NodeWillUnmount),
        (child3, NodeWillUnmount),
        (parent3, NodeWillUnmount)
      )
    )
  }
}
