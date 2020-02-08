package com.raquo.laminar

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.collection.mutable

class LifecycleEventSpec extends UnitSpec {

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
        element.onLifecycle(
          _ => events.append(NodeDidMount),
          _ => events.append(NodeWillUnmount)
        )
      }
    }

    def makeChild(text: String): Div = div(readLifecycleEvents, text)

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

  it("event timing - level 2 (nesting)") {

    val textBus = new EventBus[String]

    var events = mutable.Buffer[LifecycleEvent]();

    val readLifecycleEvents: Mod[HtmlElement] = new Mod[HtmlElement] {
      override def apply(element: HtmlElement): Unit = {
        element.onLifecycle(
          _ => events.append(NodeDidMount),
          _ => events.append(NodeWillUnmount)
        )
      }
    }

    def makeChild(text: String): Div = div(span(readLifecycleEvents, text))

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

    var lifecycleEvents = Seq[LifecycleEvent]()
    var grandChildLifecycleEvents = Seq[LifecycleEvent]()

    mount(div("root ", parent1, parent2))

    val testCases = Seq(
      TestCase(
        parent1.appendChild,
        expectedLifecycleEvents = Seq(NodeDidMount)
      ),
      TestCase(
        parent1.appendChild,
        expectedLifecycleEvents = Seq()
      ),
      TestCase(
        child => parent2.insertChild(child, atIndex = 1),
        expectedLifecycleEvents = Seq()
      ),
      TestCase(
        parent2.removeChild,
        expectedLifecycleEvents = Seq(
          NodeWillUnmount
        )
      ),
      TestCase(
        parent2.appendChild,
        expectedLifecycleEvents = Seq(NodeDidMount)
      ),
      TestCase(
        parent2.replaceChild(_, otherChild),
        expectedLifecycleEvents = Seq(
          NodeWillUnmount
        )
      )
    )

    child.onLifecycle(
      _ => lifecycleEvents = lifecycleEvents :+ NodeDidMount,
      _ => lifecycleEvents = lifecycleEvents :+ NodeWillUnmount
    )

    grandChild.onLifecycle(
      _ => grandChildLifecycleEvents = grandChildLifecycleEvents :+ NodeDidMount,
      _ => grandChildLifecycleEvents = grandChildLifecycleEvents :+ NodeWillUnmount
    )

    testCases.zipWithIndex.foreach { case (testCase, index) =>
      withClue(s"Case index=$index:") {
        testCase.action(child)
        lifecycleEvents shouldEqual testCase.expectedLifecycleEvents
        grandChildLifecycleEvents shouldEqual testCase.expectedLifecycleEvents // inheritance. assumes grandchild has no other parents and no mount events of its own
        lifecycleEvents = Seq()
        grandChildLifecycleEvents = Seq()
      }
    }
  }

  it("Nested lifecycle events") {
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
      node.onLifecycle(
        _ => lifecycleEvents = lifecycleEvents :+ (node, NodeDidMount),
        _ => lifecycleEvents = lifecycleEvents :+ (node, NodeWillUnmount)
      )
    }

    def expectNewEvents(
      clue: String,
      expectedEvents: Seq[(ReactiveElement[dom.html.Element], LifecycleEvent)]
    ): Unit = {
      withClue(clue + ": ") {
        lifecycleEvents shouldEqual expectedEvents
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
      Seq((child1, NodeDidMount), (grandParent, NodeDidMount))
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
      Seq((grandChild3, NodeDidMount), (child3, NodeDidMount))
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
        (grandChild3, NodeWillUnmount),
        (child3, NodeWillUnmount),
        (parent3, NodeWillUnmount)
      )
    )
  }
}
