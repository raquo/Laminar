package com.raquo.laminar

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom
import org.scalatest.Assertion

class SubscriptionLifecycleSpec extends UnitSpec {

  case class SimpleTest[V](
    makeElement: EventStream[V] => ReactiveElement[dom.html.Element],
    emptyExpectedNode: ExpectedNode,
    makeExpectedNode: V => ExpectedNode,
    values: Seq[V]
  ) {

    def run(): Assertion = {
      val bus = new EventBus[V]
      var counter = 0
      val valueStream = bus.events.map(value => {
        counter += 1
        value
      })

      val element = makeElement(valueStream)
      mount(element)
      expectNode(emptyExpectedNode)
      counter shouldBe 0

      val value0 = values(0)
      bus.writer.onNext(value0)
      expectNode(makeExpectedNode(value0))
      counter shouldBe 1

      val value1 = values(1)
      bus.writer.onNext(value1)
      expectNode(makeExpectedNode(value1))
      counter shouldBe 2

      val value2 = values(2)
      bus.writer.onNext(value2)
      expectNode(makeExpectedNode(value2))
      counter shouldBe 3

      unmount()
      mount(div(alt := "unmounted"))
      expectNode(div.of(alt is "unmounted"))

      bus.writer.onNext(values(3))
      expectNode(div.of(alt is "unmounted"))
      counter shouldBe 3
    }
  }

  case class NestedSubscriptionChildTest[V](
    makeElement: EventStream[ReactiveElement[dom.html.Element]] => ReactiveElement[dom.html.Element],
    makeChildA: EventStream[V] => ReactiveElement[dom.html.Element],
    makeChildB: EventStream[V] => ReactiveElement[dom.html.Element],
    emptyExpectedNode: ExpectedNode,
    emptyExpectedNodeA: ExpectedNode,
    emptyExpectedNodeB: ExpectedNode,
    makeExpectedNodeA: V => ExpectedNode,
    makeExpectedNodeB: V => ExpectedNode,
    values: Seq[V]
  ) {

    def run(): Assertion = {
      val busA = new EventBus[V]
      val busB = new EventBus[V]
      val childBus = new EventBus[ReactiveElement[dom.html.Element]]
      var counterA = 0
      var counterB = 0
      var childCounter = 0
      val valueAStream = busA.events.map(value => {
        counterA += 1
        value
      })
      val valueBStream = busB.events.map(value => {
        counterB += 1
        value
      })
      val childStream = childBus.events.map(child => {
        childCounter += 1
        child
      })

      val childA = makeChildA(valueAStream)
      val childB = makeChildB(valueBStream)

      val element = makeElement(childStream)
      mount(element)
      expectNode(emptyExpectedNode)
      counterA shouldBe 0
      counterB shouldBe 0
      childCounter shouldBe 0

      childBus.writer.onNext(childA)
      expectNode(emptyExpectedNodeA)
      counterA shouldBe 0
      counterB shouldBe 0
      childCounter shouldBe 1

      val value0 = values(0)
      busA.writer.onNext(value0)
      expectNode(makeExpectedNodeA(value0))
      counterA shouldBe 1
      counterB shouldBe 0
      childCounter shouldBe 1

      busB.writer.onNext(value0)
      expectNode(makeExpectedNodeA(value0))
      counterA shouldBe 1
      counterB shouldBe 0 // new in Laminar v0.8: childB subscriptions are not active yet because it is not mounted yet
      childCounter shouldBe 1

      val value1 = values(1)
      childBus.writer.onNext(childB)

      busA.writer.onNext(value1)

      expectNode(emptyExpectedNodeB) // node B is mounted and activated, but it missed the value0 event in BusB
      counterA shouldBe 1
      counterB shouldBe 0
      childCounter shouldBe 2

      busB.writer.onNext(value1)
      expectNode(makeExpectedNodeB(value1))
      counterA shouldBe 1
      counterB shouldBe 1
      childCounter shouldBe 2

      val value2 = values(2)
      busB.writer.onNext(value2)
      expectNode(makeExpectedNodeB(value2))
      counterA shouldBe 1
      counterB shouldBe 2
      childCounter shouldBe 2

      unmount()

      mount(div(alt := "unmounted"))

      expectNode(div.of(alt is "unmounted"))

      busA.writer.onNext(values(3))
      busB.writer.onNext(values(3))
      childBus.writer.onNext(childA)
      expectNode(div.of(alt is "unmounted"))
      counterA shouldBe 1
      counterB shouldBe 2
      childCounter shouldBe 2
    }
  }

  // @TODO[Test] Ideally we should use describe-s, but the messed up order of these async tests makes the test report unreadable.
  // describe("Children's subscriptions are active until *parent* nosw is unmounted") { ... }
  //
  // Legend:
  // PARENT UNMOUNT = Children's subscriptions are active until *parent* nosw is unmounted
  // GRANDPARENT UNMOUNT = Children's subscriptions are active until *grandparent* node is unmounted
  // PARENT UNMOUNT [$CHILD] = Subscriptions inside $child are active until said child is replaced with the next child

  it("PARENT UNMOUNT: title reflectedAttr")(SimpleTest[String](
    makeElement = titleStream => span(title <-- titleStream, "Hello"),
    emptyExpectedNode = span.of(title.isEmpty, "Hello"),
    makeExpectedNode = expectedTitle => span.of(title is expectedTitle, "Hello"),
    values = Seq("Title 1", "Title 2", "Title 3", "Title 4").map(randomString(_))
  ).run())

  it("PARENT UNMOUNT: heightAttr integer attribute")(SimpleTest[Int](
    makeElement = heightStream => span(heightAttr <-- heightStream, "Hello"),
    emptyExpectedNode = span.of(heightAttr.isEmpty, "Hello"),
    makeExpectedNode = expectedHeight => span.of(heightAttr is expectedHeight, "Hello"),
    values = Seq(10, 20, 30, 40)
  ).run())

  it("PARENT UNMOUNT: checked boolean property")(SimpleTest[Boolean](
    makeElement = checkedStream => input(checked <-- checkedStream, "Hello"),
    emptyExpectedNode = input.of(checked is false, "Hello"),
    makeExpectedNode = expectedChecked => input.of(checked is expectedChecked, "Hello"),
    values = Seq(false, true, false, true)
  ).run())

  it("PARENT UNMOUNT: color CSS rule")(SimpleTest[String](
    makeElement = colorStream => div(color <-- colorStream, "Hello"),
    emptyExpectedNode = div.of(color is "", "Hello"),
    makeExpectedNode = expectedColor => div.of(color is expectedColor, "Hello"),
    values = Seq("red", "orange", "blue", "cyan")
  ).run())

  // @Note href property reflection is apparently slightly broken in jsdom environment so I changed this test to use title
  it("GRANDPARENT UNMOUNT: title reflectedAttr")(SimpleTest[String](
    makeElement = titleStream => span(L.a(title <-- titleStream, "Hello")),
    emptyExpectedNode = span.of(L.a.of(title.isEmpty, "Hello")),
    makeExpectedNode = expectedTitle => span.of(L.a.of(title is expectedTitle, "Hello")),
    values = Seq("title 1", "title 2", "title 3", "title 4").map(randomString(_))
  ).run())

  // @TODO[Test] Also test that removing only one subscription does not unsubscribe the other one
  it("GRANDPARENT UNMOUNT: title reflectedAttr (two subscriptions for the same stream)")(SimpleTest[String](
    makeElement = strStream => div(span(title <-- strStream, href <-- strStream, "Hello")),
    emptyExpectedNode = div.of(span.of(title.isEmpty, href.isEmpty, "Hello")),
    makeExpectedNode = expectedTitle => div.of(span.of(title is expectedTitle, href is expectedTitle, "Hello")),
    values = Seq("Str 1", "Str 2", "Str 3", "Str 4").map(randomString(_))
  ).run())

  it("GRANDPARENT UNMOUNT: heightAttr integer attribute")(SimpleTest[Int](
    makeElement = heightStream => div(span(heightAttr <-- heightStream, "Hello")),
    emptyExpectedNode = div.of(span.of(heightAttr.isEmpty, "Hello")),
    makeExpectedNode = expectedHeight => div.of(span.of(heightAttr is expectedHeight, "Hello")),
    values = Seq(10, 20, 30, 40)
  ).run())

  it("GRANDPARENT UNMOUNT: checked boolean property")(SimpleTest[Boolean](
    makeElement = checkedStream => form(input(checked <-- checkedStream, "Hello")),
    emptyExpectedNode = form.of(input.of(checked is false, "Hello")),
    makeExpectedNode = expectedChecked => form.of(input.of(checked is expectedChecked, "Hello")),
    values = Seq(false, true, false, true)
  ).run())

  it("GRANDPARENT UNMOUNT: color CSS rule")(SimpleTest[String](
    makeElement = colorStream => span(div(color <-- colorStream, "Hello")),
    emptyExpectedNode = span.of(div.of(color is "", "Hello")),
    makeExpectedNode = expectedColor => span.of(div.of(color is expectedColor, "Hello")),
    values = Seq("red", "orange", "blue", "cyan")
  ).run())

  it("PARENT UNMOUNT [$CHILD]: title reflectedAttr")(NestedSubscriptionChildTest[String](
    makeElement = childStream => span(child <-- childStream, "Hello"),
    makeChildA = testTitleStream => L.a(title <-- testTitleStream),
    makeChildB = testTitleStream => b(title <-- testTitleStream),
    emptyExpectedNode = span.of(sentinel, "Hello"),
    emptyExpectedNodeA = span.of(sentinel, L.a.of(title.isEmpty), "Hello"),
    emptyExpectedNodeB = span.of(sentinel, b.of(title.isEmpty), "Hello"),
    makeExpectedNodeA = expectedTitle => span.of(sentinel, L.a.of(title is expectedTitle), "Hello"),
    makeExpectedNodeB = expectedTitle => span.of(sentinel, b.of(title is expectedTitle), "Hello"),
    values = Seq("Title 1", "Title 2", "Title 3", "Title 4").map(randomString(_))
  ).run())
}
