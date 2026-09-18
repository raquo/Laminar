package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.{CollectionCommand, Inserter}
import com.raquo.laminar.utils.UnitSpec

/** Containment and cleanup of out-of-range command insertions.
  * Clamping out-of-range indices is a proposed policy, not an established API guarantee.
  */
class ChildrenCommandBoundsRegressionSpec extends UnitSpec {

  it("a `children.command <--` Insert past the end of its span lands at the end of the span") {
    // An insertion beyond the command's content must remain inside its span so RemoveAll
    // can remove it without affecting the enclosing list's other items.
    // This test proposes clamping; rejecting invalid indices would need different assertions.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val bus = EventBus[CollectionCommand[Node]]()
    val cmd: Inserter = children.command <-- bus.events
    val items = Var[List[Inserter]](List(cmd, e))
    val host = div(children <-- items.signal)

    withClue("command span holds A, followed by E in the enclosing list:") {
      mount(host)
      bus.emit(CollectionCommand.Append(tracker.createDiv("a")))
      tracker.assertEvents(_.mounted("E"), _.elementCreated("a"), _.mounted("a")).clear()
      expectNode(div.of(sentinel, sentinel, div of "a", sentinel, span of "E", sentinel))
    }

    withClue("Insert at index 3 into a 1-node span appends inside the span, not after E:") {
      bus.emit(CollectionCommand.Insert(tracker.createDiv("n"), atIndex = 3))
      tracker.assertEvents(_.elementCreated("n"), _.mounted("n")).clear()
      expectNode(div.of(sentinel, sentinel, div of "a", div of "n", sentinel, span of "E", sentinel))
    }

    withClue("RemoveAll reaches every node the command inserted:") {
      bus.emit(CollectionCommand.RemoveAll)
      tracker.assertEvents(_.unmounted("a"), _.unmounted("n")).clear()
      expectNode(div.of(sentinel, sentinel, sentinel, span of "E", sentinel))
    }

    withClue("the enclosing list keeps reconciling normally afterwards:") {
      withCollectedAirstreamErrors { errors =>
        items.set(List(cmd))
        items.set(Nil)
        assert(errors.isEmpty, s"enclosing list reported: ${errors.mkString("; ")}")
      }
      tracker.assertEvents(_.unmounted("E")).clear()
      expectNode(div.of(sentinel, sentinel))
    }
  }

  it("an out-of-range command Insert before a retained sibling does not block outer reconciliation") {
    // Clearing the command span must leave the outer list able to remove E normally.
    // Check cleanup and reconciliation together so the test covers the full lifecycle,
    // including removal of every inserted node.
    val tracker = createEventTracker()
    val e = tracker.createSpan("E")
    tracker.clear()
    val bus = EventBus[CollectionCommand[Node]]()
    val cmd: Inserter = children.command <-- bus.events
    val items = Var[List[Inserter]](List(cmd, e))

    withClue("command span holds a, followed by E in the enclosing list:") {
      mount(div(children <-- items.signal))
      bus.emit(CollectionCommand.Append(tracker.createDiv("a")))
      tracker.assertEvents(
        _.mounted("E"),
        _.elementCreated("a"),
        _.mounted("a")
      ).clear()
      expectNode(div.of(sentinel, sentinel, div of "a", sentinel, span of "E", sentinel))
    }

    withClue("Insert at index 2, clear the command span, then drop E:") {
      bus.emit(CollectionCommand.Insert(tracker.createDiv("n"), atIndex = 2))
      tracker.assertEvents(
        _.elementCreated("n"),
        _.mounted("n")
      ).clear()
      bus.emit(CollectionCommand.RemoveAll)
      withCollectedAirstreamErrors { errors =>
        items.set(List(cmd))
        assert(errors.isEmpty, s"enclosing list reported: ${errors.mkString("; ")}")
      }
      tracker.assertEvents(
        _.unmounted("a"),
        _.unmounted("n"),
        _.unmounted("E")
      ).clear()
      expectNode(div.of(sentinel, sentinel, sentinel, sentinel))
    }

    withClue("removing the empty command group leaves no orphaned nodes:") {
      items.set(Nil)
      tracker.assertNoEvents.clear()
      expectNode(div.of(sentinel, sentinel))
    }
  }
}
