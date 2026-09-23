package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.CollectionCommand
import com.raquo.laminar.inserters.CollectionCommand.{Append, Remove, RemoveAll, Replace}
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.utils.UnitSpec

/** Once another inserter takes a node out of a `children.command <--` span (last write wins),
  * the command's later `Remove` / `Replace` of that node must leave the thief undisturbed,
  * no matter whether the thief lives under the same parent element or a different one.
  */
class ChildrenCommandStolenNodeSpec extends UnitSpec {

  it("REFERENCE: Remove / Replace of a node stolen by a list under ANOTHER parent leave the thief undisturbed") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("a")
    val b = tracker.createSpan("b")
    tracker.clear()
    val commandBus = new EventBus[CollectionCommand[HtmlElement]]
    val thiefItems = Var[List[HtmlElement]](Nil)
    val root = div(
      div("CMD", children.command <-- commandBus.events),
      div("THIEF", children <-- thiefItems.signal)
    )

    mount(root)

    withClue("the command appends `a`, then the thief list takes it:") {
      commandBus.emit(Append(a))
      tracker.assertEvents(_.mounted("a")).clear()
      thiefItems.set(List(a))
      tracker.assertNoEvents.clear() // moved between two mounted parents
      expectNode(
        div.of(
          div.of("CMD", sentinel, sentinel),
          div.of("THIEF", sentinel, span of "a", sentinel)
        )
      )
    }

    withClue("Remove(a) is a no-op:") {
      commandBus.emit(Remove(a))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("CMD", sentinel, sentinel),
          div.of("THIEF", sentinel, span of "a", sentinel)
        )
      )
    }

    withClue("Replace(a, b) is a no-op, `b` is never inserted:") {
      commandBus.emit(Replace(a, b))
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("CMD", sentinel, sentinel),
          div.of("THIEF", sentinel, span of "a", sentinel)
        )
      )
    }
  }

  it("Remove of a node stolen by the command's OWN enclosing list leaves it in that list") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("a")
    tracker.clear()
    val commandBus = new EventBus[CollectionCommand[HtmlElement]]
    val command: Inserter = children.command <-- commandBus.events
    val items = Var[List[Inserter]](List(command))
    val root = div(children <-- items.signal)

    mount(root)

    withClue("the command appends `a`, then its own list takes it:") {
      commandBus.emit(Append(a))
      tracker.assertEvents(_.mounted("a")).clear()
      items.set(List(command, a))
      tracker.assertNoEvents.clear() // moved within one mounted parent
      expectNode(div.of(sentinel, sentinel, sentinel, span of "a", sentinel))
    }

    withClue("Remove(a) is a no-op, `a` stays in the list:") {
      commandBus.emit(Remove(a))
      tracker.assertNoEvents.clear()
      expectNode(div.of(sentinel, sentinel, sentinel, span of "a", sentinel))
    }
  }

  it("Replace of a node stolen by the command's OWN enclosing list neither disturbs it nor orphans the new node") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("a")
    val b = tracker.createSpan("b")
    tracker.clear()
    val commandBus = new EventBus[CollectionCommand[HtmlElement]]
    val command: Inserter = children.command <-- commandBus.events
    val items = Var[List[Inserter]](List(command))
    val root = div(children <-- items.signal)

    mount(root)

    withClue("the command appends `a`, then its own list takes it:") {
      commandBus.emit(Append(a))
      tracker.assertEvents(_.mounted("a")).clear()
      items.set(List(command, a))
      tracker.assertNoEvents.clear()
      expectNode(div.of(sentinel, sentinel, sentinel, span of "a", sentinel))
    }

    withClue("Replace(a, b) is a no-op, `b` is never inserted:") {
      commandBus.emit(Replace(a, b))
      tracker.assertNoEvents.clear()
      expectNode(div.of(sentinel, sentinel, sentinel, span of "a", sentinel))
    }

    withClue("the list drops `a`; nothing is left behind that no inserter manages:") {
      commandBus.emit(RemoveAll)
      items.set(List(command))
      tracker.assertEvents(_.unmounted("a")).clear()
      expectNode(div.of(sentinel, sentinel, sentinel, sentinel))
    }
  }

  it("Remove of a node stolen by a sibling `child <--` item in the same list leaves it with that item") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("a")
    tracker.clear()
    val commandBus = new EventBus[CollectionCommand[HtmlElement]]
    val thiefBus = new EventBus[HtmlElement]
    val root = div(
      children <-- Val(List[Inserter](
        children.command <-- commandBus.events,
        child <-- thiefBus.events
      ))
    )

    mount(root)

    withClue("the command appends `a`, then the sibling `child <--` takes it:") {
      commandBus.emit(Append(a))
      tracker.assertEvents(_.mounted("a")).clear()
      thiefBus.emit(a)
      tracker.assertNoEvents.clear() // moved within one mounted parent
      expectNode(div.of(sentinel, sentinel, sentinel, sentinel, span of "a", sentinel, sentinel))
    }

    withClue("Remove(a) is a no-op, `a` stays with the sibling:") {
      commandBus.emit(Remove(a))
      tracker.assertNoEvents.clear()
      expectNode(div.of(sentinel, sentinel, sentinel, sentinel, span of "a", sentinel, sentinel))
    }
  }

  it("Replace of a node stolen by a sibling `child <--` item in the same list neither disturbs it nor orphans the new node") {
    val tracker = createEventTracker()
    val a = tracker.createSpan("a")
    val b = tracker.createSpan("b")
    val c = tracker.createSpan("c")
    tracker.clear()
    val commandBus = new EventBus[CollectionCommand[HtmlElement]]
    val thiefBus = new EventBus[HtmlElement]
    val root = div(
      children <-- Val(List[Inserter](
        children.command <-- commandBus.events,
        child <-- thiefBus.events
      ))
    )

    mount(root)

    withClue("the command appends `a`, then the sibling `child <--` takes it:") {
      commandBus.emit(Append(a))
      tracker.assertEvents(_.mounted("a")).clear()
      thiefBus.emit(a)
      tracker.assertNoEvents.clear()
      expectNode(div.of(sentinel, sentinel, sentinel, sentinel, span of "a", sentinel, sentinel))
    }

    withClue("Replace(a, b) is a no-op, `b` is never inserted:") {
      commandBus.emit(Replace(a, b))
      tracker.assertNoEvents.clear()
      expectNode(div.of(sentinel, sentinel, sentinel, sentinel, span of "a", sentinel, sentinel))
    }

    withClue("both inserters move on; nothing is left behind that no inserter manages:") {
      commandBus.emit(RemoveAll)
      thiefBus.emit(c)
      tracker.assertEvents(
        _.unmounted("a"),
        _.mounted("c")
      ).clear()
      expectNode(div.of(sentinel, sentinel, sentinel, sentinel, span of "c", sentinel, sentinel))
    }
  }
}
