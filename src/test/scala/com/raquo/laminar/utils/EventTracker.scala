package com.raquo.laminar.utils

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.Inserter

import scala.collection.mutable

/** Test fixture for tracking an ordered sequence of DOM / lifecycle events.
  *
  * Build tracked elements with [[createDiv]] / [[createSpan]] (each tagged with a String `id`, also set as the
  * element's text so it shows up in `expectNode`), or a tracked `text <--` source with [[text]].
  * Every interesting thing that happens is appended, in order, to [[log]] as a compact string:
  *
  *  - `element-create:<id>` — a tracked element was BUILT (at `tracker.createDiv(id)` / `createSpan(id))
  *  - `mount:<id>` / `unmount:<id>` — that element mounted / unmounted
  *  - `text-update:<id>:<value>` — a tracked `text <--` (see [[text]]) rendered `<value>`
  *  - any string pushed via [[logRaw]] — an arbitrary caller-defined event
  *
  * The primary way to assert is [[assertEvents]], which pins the EXACT ordered sequence of events
  * since the last [[clear]] using a small matcher DSL:
  *
  * {{{
  *   tracker.assertEvents(
  *     _.elementCreated("b"),
  *     _.mounted("b"),
  *     _.textUpdated("b", "hello"),
  *     _.unmounted("a")
  *   )
  *   tracker.clear()
  * }}}
  *
  * Asserting the whole sequence (rather than per-element counts) means an unexpected extra event,
  * a missing one, or a wrong ORDER all fail — and asserting the rendered VALUE (not just that
  * something rendered) catches a stale re-emit. The usual pattern is to `clear()` at the end of
  * each test section so the next section asserts only its own events; an empty `assertEvents()`
  * then precisely pins "this operation produced no events at all" (e.g. a seamless move).
  */
class EventTracker {

  /** Ordered log of every event since construction / last [[clear]]. See the class doc for formats. */
  val log: mutable.Buffer[String] = mutable.Buffer[String]()

  /** Forget all recorded events. Call at the end of a section so the next section asserts only its
    * own events. Returns `this` for chaining, e.g. `tracker.assertEvents(...).clear()`. */
  def clear(): this.type = {
    log.clear()
    this
  }

  /** A `<div>` tagged with `id` that records its creation and lifecycle into this tracker. */
  def createDiv(id: String, mods: Modifier[Div]*): Div =
    track(L.div(id), id).amend(mods: _*)

  /** A `<span>` tagged with `id` that records its creation and lifecycle into this tracker. */
  def createSpan(id: String, mods: Modifier[Span]*): Span =
    track(L.span(id), id).amend(mods: _*)

  /** A `text <--` inserter whose every rendered value is logged as `text-update:<id>:<value>`.
    *
    * Use this to observe what a text source actually renders (and in what order) as part of the
    * event sequence, instead of maintaining a side buffer. */
  def text(id: String, source: Source[String]): Inserter =
    L.text <-- source.toObservable.map { value =>
      log += s"text-update:$id:$value"
      value
    }

  /** Push an arbitrary raw event string into the log (matched by `_.rawEvent(...)`). */
  def logRaw(event: String): this.type = {
    log += event
    this
  }

  // -- Sequence assertion (exact, ordered) --

  /** Assert the EXACT ordered sequence of events recorded since the last [[clear]].
    *
    * Each argument is a matcher applied to [[EventTracker.Match]], e.g. `_.mounted("a")`, and
    * yields the single event string it must equal. The actual log must equal the expected list
    * element-for-element; any extra, missing, or reordered event fails. Pass no arguments to
    * assert that no events occurred. */
  def assertEvents(expected: (EventTracker.Match.type => String)*): this.type = {
    val expectedEvents = expected.iterator.map(_(EventTracker.Match)).toList
    val actualEvents = log.toList
    if (actualEvents != expectedEvents) {
      throw new AssertionError(
        "Event sequence mismatch.\n" +
          s"  expected (${expectedEvents.size}): [${expectedEvents.mkString(", ")}]\n" +
          s"  actual   (${actualEvents.size}): [${actualEvents.mkString(", ")}]"
      )
    }
    this
  }

  private def track[El <: HtmlElement](el: El, id: String): El = {
    log += s"element-create:$id"
    el.amend(
      L.onMountCallback[El] { _ =>
        log += s"mount:$id"
      },
      L.onUnmountCallback[El] { _ =>
        log += s"unmount:$id"
      }
    )
  }
}

object EventTracker {

  /** Receiver for the `_.mounted("a")` matcher DSL used by [[EventTracker.assertEvents]]. Each
    * method returns the exact event string it should match against [[EventTracker.log]]. */
  object Match {

    /** Matches a raw event pushed via `logRaw` (or any literal event string). */
    def rawEvent(event: String): String = event

    /** Matches `element-create:<id>` — a tracked element was built. */
    def elementCreated(id: String): String = s"element-create:$id"

    /** Matches `mount:<id>`. */
    def mounted(id: String): String = s"mount:$id"

    /** Matches `unmount:<id>`. */
    def unmounted(id: String): String = s"unmount:$id"

    /** Matches `text-update:<id>:<text>` — a tracked `text <--` rendered `text`. */
    def textUpdated(id: String, text: String): String = s"text-update:$id:$text"
  }
}
