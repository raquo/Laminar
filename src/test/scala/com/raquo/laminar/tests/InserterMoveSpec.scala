package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.{CollectionCommand, Inserter}
import com.raquo.laminar.utils.UnitSpec

/** Extends the #157 move / transfer matrix across inserter TYPES that `NestedInsertersSpec` leaves
  * out — `children.command <--` and `text <--` as moved items, list → plain-element "demotion",
  * and the degenerate same-transaction double-add.
  *
  * Scope: the #157 guarantee that a move relocates an item's DOM span and transfers its
  * subscriptions without re-mounting its content. See notes/Testing.md for assertion conventions.
  */
class InserterMoveSpec extends UnitSpec {

  // A `children.command <--` item is a multi-node group (needsTrailingSentinel), so as a list
  // item it looks just like a nested `children <--`: [outer-leading, group-leading, ...content...,
  // group-trailing, outer-trailing]. Moving it must carry the whole span (both its own sentinels
  // and its content) to the new host, and subsequent commands must anchor on the MOVED trailing
  // sentinel — Append lands just before it, Prepend just after the group's leading sentinel.

  it("`children.command <--` stolen between two lists: later commands land correctly, no re-mount") {
    val tracker = createEventTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val cmd: Inserter = children.command <-- cmdBus.events

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    items1.set(List(cmd))
    cmdBus.emit(CollectionCommand.Append(tracker.createDiv("a")))
    cmdBus.emit(CollectionCommand.Append(tracker.createDiv("b")))
    withClue("command group holds a, b in L1:") {
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, div of "a", div of "b", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("a"),
          _.mounted("a"),
          _.elementCreated("b"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("steal into L2 before removing from L1 (whole command span moves, no re-mount):") {
      items2.set(List(cmd)) // add to L2 while still in L1 -> transfer
      items1.set(Nil) // removal from L1 is a no-op: already stolen
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "a", div of "b", sentinel, sentinel)
        )
      )
      tracker
        .assertNoEvents // the crux: transferred, not rebuilt — no events at all
        .clear()
    }

    withClue("commands after the steal anchor on the MOVED span (Append before trailing, Prepend after leading):") {
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("c"))) // after b, before the (moved) trailing sentinel
      cmdBus.emit(CollectionCommand.Prepend(tracker.createDiv("d"))) // right after the (moved) group-leading sentinel
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "d", div of "a", div of "b", div of "c", sentinel, sentinel)
        )
      )
      // Only the two new nodes appear; the pre-existing content is not touched.
      tracker
        .assertEvents(
          _.elementCreated("c"),
          _.mounted("c"),
          _.elementCreated("d"),
          _.mounted("d")
        )
        .clear()
    }

    withClue("Insert(atIndex) after the steal counts from the moved span's start (index 2 = between a and b):") {
      cmdBus.emit(CollectionCommand.Insert(tracker.createDiv("e"), atIndex = 2)) // d,a,b,c -> d,a,e,b,c
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "d", div of "a", div of "e", div of "b", div of "c", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("e"),
          _.mounted("e")
        )
        .clear()
    }

    withClue("normal removal from the new host L2 tears the whole group down exactly once:") {
      items2.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.unmounted("d"),
        _.unmounted("a"),
        _.unmounted("e"),
        _.unmounted("b"),
        _.unmounted("c")
      )
    }
  }

  it("`children.command <--` emptied (RemoveAll) then refilled AFTER a steal: empty span stays anchored at the moved location") {
    // A moved command span emptied to ZERO must keep its own leading + trailing
    // sentinels at the NEW host, so a following Append lands back inside the moved span (not in the
    // old list, and not detached). Exercises RemoveAll + Append on a command group after `moveToParent`.
    val tracker = createEventTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val cmd: Inserter = children.command <-- cmdBus.events

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("command group holds a, b in L1:") {
      items1.set(List(cmd))
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("a")))
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("b")))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, div of "a", div of "b", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("a"),
          _.mounted("a"),
          _.elementCreated("b"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("steal into L2 (whole span moves, no re-mount):") {
      items2.set(List(cmd))
      items1.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "a", div of "b", sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear()
    }

    withClue("RemoveAll at the moved location: content unmounts, the group's two sentinels remain as an empty span:") {
      cmdBus.emit(CollectionCommand.RemoveAll)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, sentinel, sentinel)
        )
      )
      // #Note: command RemoveAll tears down in current order (a, b).
      tracker
        .assertEvents(
          _.unmounted("a"),
          _.unmounted("b")
        )
        .clear()
    }

    withClue("Append after emptying still lands inside the moved (now-empty) span, between its sentinels:") {
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("c")))
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, div of "c", sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.elementCreated("c"),
        _.mounted("c")
      )
    }
  }

  it("`children.command <--` moved within one list: span moves as a unit, no re-mount") {
    val tracker = createEventTracker()
    val cmdBus = new EventBus[CollectionCommand[Node]]
    val itemsVar = Var[List[Inserter]](Nil)

    val staticX: Inserter = tracker.createSpan("X")
    val cmd: Inserter = children.command <-- cmdBus.events

    mount(div("H", children <-- itemsVar.signal))

    itemsVar.set(List(staticX, cmd))
    cmdBus.emit(CollectionCommand.Append(tracker.createDiv("a")))
    cmdBus.emit(CollectionCommand.Append(tracker.createDiv("b")))
    withClue("X then command group (a, b):") {
      expectNode(div.of("H", sentinel, span of "X", sentinel, div of "a", div of "b", sentinel, sentinel))
      // X was built up-front (element-create) then mounted; a and b built and appended in turn.
      tracker
        .assertEvents(
          _.elementCreated("X"),
          _.mounted("X"),
          _.elementCreated("a"),
          _.mounted("a"),
          _.elementCreated("b"),
          _.mounted("b")
        )
        .clear()
    }

    withClue("reorder: the whole command span moves ahead of X, as a unit, without re-mounting:") {
      itemsVar.set(List(cmd, staticX))
      expectNode(div.of("H", sentinel, sentinel, div of "a", div of "b", sentinel, span of "X", sentinel))
      tracker
        .assertNoEvents // a reorder is a move: no events for the moved span OR the passed-over X
        .clear()
    }

    withClue("commands after the move still land within the moved span (Append before trailing sentinel):") {
      cmdBus.emit(CollectionCommand.Append(tracker.createDiv("c")))
      expectNode(div.of("H", sentinel, sentinel, div of "a", div of "b", div of "c", sentinel, span of "X", sentinel))
      tracker.assertEvents(
        _.elementCreated("c"),
        _.mounted("c")
      )
    }
  }

  // `text <--` is single-node, but as a list item it is forced to keep a trailing sentinel, so its
  // span is [group-leading, text, group-trailing]. Stealing it between lists must transfer the live
  // subscription (owner moved via TransferableSubscription), NOT tear it down and rebuild it — which
  // we prove via the `text-update` events: it must NOT re-render on the steal (owner transferred),
  // then render exactly the NEW value on a genuine later update. Mirrors the `child <--` steal test.

  it("`text <--` stolen between two lists: subscription transferred (not re-run), stays live") {
    val tracker = createEventTracker()
    val textVar = Var("hi")
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dynText: Inserter = tracker.text("t", textVar.signal)

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    items1.set(List(dynText))
    withClue("text item in L1 renders the current value once:") {
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, "hi", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(_.textUpdated("t", "hi"))
        .clear()
    }

    withClue("steal into L2 before removing from L1 — owner transferred, observer does NOT re-run:") {
      items2.set(List(dynText))
      items1.set(Nil)
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, "hi", sentinel, sentinel)
        )
      )
      tracker
        .assertNoEvents // no re-render during the transfer (a rebuild would re-emit "hi")
        .clear()
    }

    withClue("reacts in L2 to a later update — the same live subscription moved with it:") {
      textVar.set("bye")
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, "bye", sentinel, sentinel)
        )
      )
      tracker.assertEvents(_.textUpdated("t", "bye")) // exactly the new value, not a stale re-emit
    }
  }

  // list -> plain element ("demote"): an inserter that currently lives as a `children <--` list
  // item is applied directly to a plain element via `el.amend(inserter)`. This drives
  // DynamicInserter.apply's move branch out of the list-item state (the group already has a trailing
  // sentinel). The span must relocate to the plain element and stay live, with no re-mount; the old
  // list is left clean.

  it("demote: a `children <--` item applied onto a plain element relocates seamlessly, no re-mount") {
    val tracker = createEventTracker()
    val innerVar = Var[List[String]](List("n1", "n2"))
    val items = Var[List[Inserter]](Nil)
    val nested: Inserter = children <-- innerVar.signal.map(_.map(tracker.createSpan(_)))

    val host = div("HOST")
    val listEl = div("LIST", children <-- items.signal)

    mount(div(host, listEl))

    items.set(List(nested))
    withClue("first placement: the item lives in LIST as a bracketed group:") {
      expectNode(
        div.of(
          div.of("HOST"),
          div.of("LIST", sentinel, sentinel, span of "n1", span of "n2", sentinel, sentinel)
        )
      )
      // The source renders [n1, n2]: both spans are built, then both mount.
      tracker
        .assertEvents(
          _.elementCreated("n1"),
          _.elementCreated("n2"),
          _.mounted("n1"),
          _.mounted("n2")
        )
        .clear()
    }

    withClue("demote onto the plain HOST element via amend: span relocates, LIST emptied, no re-mount:") {
      host.amend(nested)
      items.set(Nil) // removal from LIST is a no-op: the span was already moved to HOST
      expectNode(
        div.of(
          div.of("HOST", sentinel, span of "n1", span of "n2", sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      tracker
        .assertNoEvents // seamless: no re-render, no re-mount
        .clear()
    }

    withClue("still live on HOST after the demote — updates flow to the new location:") {
      innerVar.set(List("n3"))
      expectNode(
        div.of(
          div.of("HOST", sentinel, span of "n3", sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      // The (live) subscription reacts: builds n3, mounts it, and tears down the old content.
      tracker.assertEvents(
        _.elementCreated("n3"),
        _.mounted("n3"),
        _.unmounted("n1"),
        _.unmounted("n2")
      )
    }
  }

  // Single-node demote: a `child <--` (which normally needs NO trailing sentinel) keeps one once
  // it has been a `children <--` list item, because `_forceTrailingSentinel` is sticky (set when it
  // joined the list, never cleared). So demoting it onto a plain element yields a "heavyweight"
  // group [leading, node, trailing], NOT the lightweight [leading, node] shape a never-listed
  // `child <--` has. This pins that deliberate behaviour (the trailing sentinel is retained, not
  // dropped), and that the demote is still seamless (no re-mount).

  it("demote: a single-node `child <--` item retains its (sticky) trailing sentinel on a plain element, no re-mount") {
    val tracker = createEventTracker()
    val valueVar = Var("x")
    val items = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- valueVar.signal.map(tracker.createSpan(_))

    val host = div("HOST")
    val listEl = div("LIST", children <-- items.signal)

    mount(div(host, listEl))

    items.set(List(dyn))
    withClue("as a list item, a `child <--` is forced to keep a trailing sentinel: [leading, node, trailing]:") {
      expectNode(
        div.of(
          div.of("HOST"),
          div.of("LIST", sentinel, sentinel, span of "x", sentinel, sentinel)
        )
      )
      tracker
        .assertEvents(
          _.elementCreated("x"),
          _.mounted("x")
        )
        .clear()
    }

    withClue("demote onto HOST: span relocates, no re-mount, and the trailing sentinel is RETAINED (sticky):") {
      host.amend(dyn)
      items.set(Nil)
      // Contrast the never-listed static-to-static case, which is [leading, node] with NO trailing
      // sentinel: here the trailing sentinel survives the demotion because the flag never clears.
      expectNode(
        div.of(
          div.of("HOST", sentinel, span of "x", sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      tracker
        .assertNoEvents // seamless: no re-render, no re-mount
        .clear()
    }

    withClue("still live on HOST after the demote:") {
      valueVar.set("y")
      expectNode(
        div.of(
          div.of("HOST", sentinel, span of "y", sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.elementCreated("y"),
        _.unmounted("x"),
        _.mounted("y")
      )
    }
  }

  // Demote ordering counterpart: a `children <--` list item is stolen out onto a plain element
  // (`moveToParent`) WHILE EMPTY (bare [leading, trailing], zero content). The empty span must
  // relocate to the element, the list must be left clean, and the first population after the move
  // must land on the element between the moved sentinels — not back in the list.

  it("demote: an EMPTY `children <--` list item applied onto a plain element relocates, then populates on the element") {
    val tracker = createEventTracker()
    val innerVar = Var[List[Node]](Nil)
    val items = Var[List[Inserter]](Nil)

    val n1 = tracker.createSpan("n1")
    val n2 = tracker.createSpan("n2")
    tracker.clear()

    val nested: Inserter = children <-- innerVar.signal

    val host = div("HOST")
    val listEl = div("LIST", children <-- items.signal)

    mount(div(host, listEl))

    withClue("place the empty item in LIST: bare leading + trailing sentinels, no content:") {
      items.set(List(nested))
      expectNode(
        div.of(
          div.of("HOST"),
          div.of("LIST", sentinel, sentinel, sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear()
    }

    withClue("demote the EMPTY span onto HOST via amend: the bare pair relocates, LIST emptied, no events:") {
      host.amend(nested)
      items.set(Nil) // removal from LIST is a no-op: already moved
      // The sticky trailing sentinel travels too, so HOST holds a [leading, trailing] pair.
      expectNode(
        div.of(
          div.of("HOST", sentinel, sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      tracker.assertNoEvents.clear()
    }

    withClue("populate on HOST after the demote: content lands on HOST between the moved sentinels:") {
      innerVar.set(List(n1, n2))
      expectNode(
        div.of(
          div.of("HOST", sentinel, span of "n1", span of "n2", sentinel),
          div.of("LIST", sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.mounted("n1"),
        _.mounted("n2")
      )
    }
  }

  // Degenerate case: the SAME inserter added to two lists in ONE transaction. An inserter can only
  // render in one place, so this cannot duplicate it. We pin that it converges gracefully — the span
  // ends up in exactly one list, stays live, and nothing throws — rather than corrupting the DOM.

  it("same-transaction double-add of one inserter converges to a single live location") {
    val tracker = createEventTracker()
    val valueVar = Var("A")
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dyn: Inserter = child <-- valueVar.signal.map(tracker.createSpan(_))

    mount(
      div(
        div("L1", children <-- items1.signal),
        div("L2", children <-- items2.signal)
      )
    )

    withClue("add the same inserter to both lists atomically (no error, lands in exactly one list):") {
      withCollectedAirstreamErrors { errors =>
        Var.set(items1 -> List(dyn), items2 -> List(dyn))
        assert(errors.isEmpty)
      }
      // It converges into L2 (the later-processed list steals it); L1 is left clean.
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "A", sentinel, sentinel)
        )
      )
      // Rendered exactly once (value "A") — not duplicated across the two lists.
      tracker
        .assertEvents(
          _.elementCreated("A"),
          _.mounted("A")
        )
        .clear()
    }

    withClue("the single surviving placement is fully live afterwards:") {
      valueVar.set("B")
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, span of "B", sentinel, sentinel)
        )
      )
      tracker.assertEvents(
        _.elementCreated("B"),
        _.unmounted("A"),
        _.mounted("B")
      )
    }
  }
}
