package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter
import com.raquo.laminar.nodes.Slot
import com.raquo.laminar.utils.UnitSpec

/** `text <--` never slots its text node: named slots only accept elements, so a `text <--` item
  * inside a slotted `children <--` list renders quietly into the default slot. Relocating that item
  * must be just as quiet – a move is not allowed to report an error that the original placement
  * did not.
  */
class TextInserterSlotMoveSpec extends UnitSpec {

  it("a `text <--` item stolen between two identically-slotted lists reports no error and stays live") {
    val tracker = createEventTracker()
    val textVar = Var("hi")
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dynText: Inserter = tracker.text("t", textVar.signal)

    mount(
      div(
        div("L1", new Slot("prefix")(children <-- items1.signal)),
        div("L2", new Slot("prefix")(children <-- items2.signal))
      )
    )

    withClue("first placement in the slotted L1 renders quietly (text is never slotted):") {
      withCollectedAirstreamErrors { errors =>
        items1.set(List(dynText))
        assert(errors.isEmpty, s"placing the text item reported: ${errors.mkString("; ")}")
      }
      tracker.assertEvents(_.textUpdated("t", "hi")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel, "hi", sentinel, sentinel),
          div.of("L2", sentinel, sentinel)
        )
      )
    }

    withClue("add-first steal into the slotted L2 must be just as quiet as the placement:") {
      withCollectedAirstreamErrors { errors =>
        items2.set(List(dynText))
        items1.set(Nil)
        assert(errors.isEmpty, s"moving the text item reported: ${errors.mkString("; ")}")
      }
      tracker.assertNoEvents.clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, "hi", sentinel, sentinel)
        )
      )
    }

    withClue("still live in L2:") {
      textVar.set("bye")
      tracker.assertEvents(_.textUpdated("t", "bye")).clear()
      expectNode(
        div.of(
          div.of("L1", sentinel, sentinel),
          div.of("L2", sentinel, sentinel, "bye", sentinel, sentinel)
        )
      )
    }
  }

  it("a `text <--` item re-stolen between two differently-slotted sibling lists under one parent reports no error") {
    // Same-parent layout: the steal-back is a raw same-parent reposition followed by a slot
    // re-affirm, a different path than the cross-parent transfer above. Both must stay quiet.
    val tracker = createEventTracker()
    val textVar = Var("hi")
    val items1 = Var[List[Inserter]](Nil)
    val items2 = Var[List[Inserter]](Nil)
    val dynText: Inserter = tracker.text("t", textVar.signal)

    mount(
      div(
        new Slot("a")(children <-- items1.signal),
        new Slot("b")(children <-- items2.signal)
      )
    )

    withClue("first placement in the `a`-slotted list renders quietly:") {
      withCollectedAirstreamErrors { errors =>
        items1.set(List(dynText))
        assert(errors.isEmpty, s"placing the text item reported: ${errors.mkString("; ")}")
      }
      tracker.assertEvents(_.textUpdated("t", "hi")).clear()
      expectNode(div.of(sentinel, sentinel, "hi", sentinel, sentinel, sentinel, sentinel))
    }

    withClue("steal into the `b`-slotted sibling list: quiet:") {
      withCollectedAirstreamErrors { errors =>
        items2.set(List(dynText))
        assert(errors.isEmpty, s"stealing the text item reported: ${errors.mkString("; ")}")
      }
      tracker.assertNoEvents.clear()
      expectNode(div.of(sentinel, sentinel, sentinel, sentinel, "hi", sentinel, sentinel))
    }

    withClue("steal back into the `a`-slotted list (stale re-emit): quiet:") {
      withCollectedAirstreamErrors { errors =>
        items1.set(List(dynText))
        assert(errors.isEmpty, s"stealing the text item back reported: ${errors.mkString("; ")}")
      }
      tracker.assertNoEvents.clear()
      expectNode(div.of(sentinel, sentinel, "hi", sentinel, sentinel, sentinel, sentinel))
    }

    withClue("still live after the round trip:") {
      textVar.set("bye")
      tracker.assertEvents(_.textUpdated("t", "bye")).clear()
      expectNode(div.of(sentinel, sentinel, "bye", sentinel, sentinel, sentinel, sentinel))
    }
  }
}
