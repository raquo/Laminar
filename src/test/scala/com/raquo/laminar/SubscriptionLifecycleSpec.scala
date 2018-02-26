package com.raquo.laminar

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.bundle._
import com.raquo.laminar.experimental.airstream.eventbus.EventBus
import com.raquo.laminar.experimental.airstream.eventstream.EventStream
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
      val $value = bus.events.map(value => {
        counter += 1
        value
      })

      val element = makeElement($value)
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
      mount(div(rel := "unmounted"))
      expectNode(div like (rel is "unmounted"))

      bus.writer.onNext(values(3))
      expectNode(div like (rel is "unmounted"))
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
      val $valueA = busA.events.map(value => {
        counterA += 1
        value
      })
      val $valueB = busB.events.map(value => {
        counterB += 1
        value
      })
      val $child = childBus.events.map(child => {
        childCounter += 1
        child
      })

      val childA = makeChildA($valueA)
      val childB = makeChildB($valueB)

      val element = makeElement($child)
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
      counterB shouldBe 1 // even if not mounted, the subscriptions are active already
      childCounter shouldBe 1

      val value1 = values(1)
      childBus.writer.onNext(childB)

      busA.writer.onNext(value1)

      expectNode(makeExpectedNodeB(value0)) // This used to expect an empty node B, but now with Airstream the noode starts listening when it's initialized, not when it's mounted
      counterA shouldBe 1
      counterB shouldBe 1
      childCounter shouldBe 2

      busB.writer.onNext(value1)
      expectNode(makeExpectedNodeB(value1))
      counterA shouldBe 1
      counterB shouldBe 2
      childCounter shouldBe 2

      val value2 = values(2)
      busB.writer.onNext(value2)
      expectNode(makeExpectedNodeB(value2))
      counterA shouldBe 1
      counterB shouldBe 3
      childCounter shouldBe 2

      unmount()

      mount(div(rel := "unmounted"))

      expectNode(div like (rel is "unmounted"))

      busA.writer.onNext(values(3))
      busB.writer.onNext(values(3))
      childBus.writer.onNext(childA)
      expectNode(div like (rel is "unmounted"))
      counterA shouldBe 1
      counterB shouldBe 3
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
    makeElement = $title => span(title <-- $title, "Hello"),
    emptyExpectedNode = span like(title isEmpty, "Hello"),
    makeExpectedNode = expectedTitle => span like(title is expectedTitle, "Hello"),
    values = Seq("Title 1", "Title 2", "Title 3", "Title 4").map(randomString(_))
  ).run())

  it("PARENT UNMOUNT: heightAttr integer attribute")(SimpleTest[Int](
    makeElement = $height => span(heightAttr <-- $height, "Hello"),
    emptyExpectedNode = span like(heightAttr isEmpty, "Hello"),
    makeExpectedNode = expectedHeight => span like(heightAttr is expectedHeight, "Hello"),
    values = Seq(10, 20, 30, 40)
  ).run())

  it("PARENT UNMOUNT: checked boolean property")(SimpleTest[Boolean](
    makeElement = $checked => input(checked <-- $checked, "Hello"),
    emptyExpectedNode = input like(checked is false, "Hello"),
    makeExpectedNode = expectedChecked => input like(checked is expectedChecked, "Hello"),
    values = Seq(false, true, false, true)
  ).run())

  it("PARENT UNMOUNT: color CSS rule")(SimpleTest[String](
    makeElement = $color => div(color <-- $color, "Hello"),
    emptyExpectedNode = div like(color is "", "Hello"),
    makeExpectedNode = expectedColor => div like(color is expectedColor, "Hello"),
    values = Seq("red", "orange", "blue", "cyan")
  ).run())

  it("GRANDPARENT UNMOUNT: href reflectedAttr")(SimpleTest[String](
    makeElement = $href => span(bundle.a(href <-- $href, "Hello")),
    emptyExpectedNode = span like (bundle.a like(href isEmpty, "Hello")),
    makeExpectedNode = expectedHref => span like (bundle.a like(href is expectedHref, "Hello")),
    values = Seq("href 1", "href 2", "href 3", "href 4").map(randomString(_))
  ).run())

  // @TODO[Test] Also test that removing only one subscription does not unsubscribe the other one
  it("GRANDPARENT UNMOUNT: title reflectedAttr (two subscriptions for the same stream)")(SimpleTest[String](
    makeElement = $str => div(span(title <-- $str, href <-- $str, "Hello")),
    emptyExpectedNode = div like (span like(title isEmpty, href isEmpty, "Hello")),
    makeExpectedNode = expectedTitle => div like (span like(title is expectedTitle, href is expectedTitle, "Hello")),
    values = Seq("Str 1", "Str 2", "Str 3", "Str 4").map(randomString(_))
  ).run())

  it("GRANDPARENT UNMOUNT: heightAttr integer attribute")(SimpleTest[Int](
    makeElement = $height => div(span(heightAttr <-- $height, "Hello")),
    emptyExpectedNode = div like (span like(heightAttr isEmpty, "Hello")),
    makeExpectedNode = expectedHeight => div like (span like(heightAttr is expectedHeight, "Hello")),
    values = Seq(10, 20, 30, 40)
  ).run())

  it("GRANDPARENT UNMOUNT: checked boolean property")(SimpleTest[Boolean](
    makeElement = $checked => form(input(checked <-- $checked, "Hello")),
    emptyExpectedNode = form like (input like(checked is false, "Hello")),
    makeExpectedNode = expectedChecked => form like (input like(checked is expectedChecked, "Hello")),
    values = Seq(false, true, false, true)
  ).run())

  it("GRANDPARENT UNMOUNT: color CSS rule")(SimpleTest[String](
    makeElement = $color => span(div(color <-- $color, "Hello")),
    emptyExpectedNode = span like (div like(color is "", "Hello")),
    makeExpectedNode = expectedColor => span like (div like(color is expectedColor, "Hello")),
    values = Seq("red", "orange", "blue", "cyan")
  ).run())

  it("PARENT UNMOUNT [$CHILD]: title reflectedAttr")(NestedSubscriptionChildTest[String](
    makeElement = $child => span(child <-- $child, "Hello"),
    makeChildA = $testTitle => bundle.a(title <-- $testTitle),
    makeChildB = $testTitle => b(title <-- $testTitle),
    emptyExpectedNode = span like(ExpectedNode.comment(), "Hello"), // @TODO[API] We should not need to reference EN.comment here like this (it's a sentinel node, and we should use implicit conversion)
    emptyExpectedNodeA = span like(bundle.a like (title isEmpty), "Hello"),
    emptyExpectedNodeB = span like(b like (title isEmpty), "Hello"),
    makeExpectedNodeA = expectedTitle => span like(bundle.a like (title is expectedTitle), "Hello"),
    makeExpectedNodeB = expectedTitle => span like(b like (title is expectedTitle), "Hello"),
    values = Seq("Title 1", "Title 2", "Title 3", "Title 4").map(randomString(_))
  ).run())
}
