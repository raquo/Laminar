package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.modifiers.RenderableInserter
import com.raquo.laminar.tests.ChildrenInserterConversionSpec.{Item, ItemRenderable}
import com.raquo.laminar.utils.{EventTracker, UnitSpec}

class ChildrenInserterConversionSpec extends UnitSpec {

  it("converts each item once per update while preserving order and nested lifecycle") {
    val tracker = createEventTracker()
    implicit val renderable: RenderableInserter[Item] = new ItemRenderable(tracker)
    val items = EventBus[List[Item]]()
    val inner = EventBus[HtmlElement]()
    val a = tracker.createSpan("A")
    val b = tracker.createSpan("B")
    val c = tracker.createSpan("C")
    val staticA = new Item("A", a)
    val dynamicB = new Item("B", child <-- inner.events)
    val staticC = new Item("C", c)
    tracker.clear()

    mount(div(children <-- items.events))

    withClue("insertion converts items in source order, including an empty nested item:") {
      items.emit(List(staticA, dynamicB, staticC))
      tracker.assertEvents(
        _.rawEvent("convert:A"), _.rawEvent("convert:B"), _.rawEvent("convert:C"),
        _.mounted("A"), _.mounted("C")
      ).clear()
      expectNode(div.of(sentinel, span of "A", sentinel, sentinel, span of "C", sentinel))
    }

    withClue("an inner update does not reconvert the outer items:") {
      inner.emit(b)
      tracker.assertEvents(_.mounted("B")).clear()
      expectNode(div.of(sentinel, span of "A", sentinel, span of "B", sentinel, span of "C", sentinel))
    }

    withClue("reordering converts once per item and preserves mounted content:") {
      items.emit(List(staticC, dynamicB, staticA))
      tracker.assertEvents(
        _.rawEvent("convert:C"), _.rawEvent("convert:B"), _.rawEvent("convert:A")
      ).clear()
      expectNode(div.of(sentinel, span of "C", sentinel, span of "B", sentinel, span of "A", sentinel))
    }

    withClue("removal converts only retained items and unmounts removed content:") {
      items.emit(List(dynamicB))
      tracker.assertEvents(
        _.rawEvent("convert:B"), _.unmounted("C"), _.unmounted("A")
      ).clear()
      expectNode(div.of(sentinel, sentinel, span of "B", sentinel, sentinel))
    }

    withClue("emptying the list removes the nested content without conversions:") {
      items.emit(Nil)
      tracker.assertEvents(_.unmounted("B")).clear()
      expectNode(div.of(sentinel, sentinel))
    }
  }
}

object ChildrenInserterConversionSpec {

  final class Item(val id: String, val inserter: Inserter)

  final class ItemRenderable(tracker: EventTracker) extends RenderableInserter[Item] {

    override def asInserter(item: Item): Inserter = {
      tracker.logRaw(s"convert:${item.id}")
      item.inserter
    }
  }
}
