package com.raquo.laminar.utils

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.{Div, HtmlElement, Modifier, Span, textToTextNode}

import scala.collection.mutable

/** Test fixture for tracking element mount / unmount lifecycle.
  *
  * Build tracked elements with [[div]] / [[span]], each tagged with a String `id` (also set as
  * the element's text, so it shows up in `expectNode`). Every mount and unmount of a tracked
  * element is recorded both as per-id counts ([[mounts]] / [[unmounts]]) and in an ordered
  * [[log]] (e.g. `"mount:a"`, `"unmount:a"`).
  *
  * Prefer the `assert*` helpers over reading [[mounts]] / [[unmounts]] directly: they pin the
  * EXACT event counts (so an unexpected extra mount/unmount fails the test), and on failure they
  * report the actual counts and the full ordered event log, which is far easier to debug than a
  * bare boolean. Reach for [[mounts]] / [[unmounts]] / [[log]] only for assertions the helpers
  * don't cover (e.g. exact cross-element ordering via `log`).
  *
  * Extra modifiers can be passed after the id, e.g. `tracker.div("a", cls := "x")`.
  *
  * This consolidates the ad-hoc `trackedDiv` / lifecycle-`Buffer` fixtures that were previously
  * redeclared in each spec (NestedInsertersSpec, ChildrenTakeoverSpec, ChildrenCommandTakeoverSpec).
  */
class LifecycleTracker {

  /** How many times each id has been mounted so far. */
  val mounts: mutable.Map[String, Int] = mutable.Map[String, Int]().withDefaultValue(0)

  /** How many times each id has been unmounted so far. */
  val unmounts: mutable.Map[String, Int] = mutable.Map[String, Int]().withDefaultValue(0)

  /** Ordered lifecycle events across all tracked elements, e.g. `"mount:a"`, `"unmount:a"`. */
  val log: mutable.Buffer[String] = mutable.Buffer[String]()

  /** Forget all recorded events (counts and log). Handy between phases of a test. */
  def clear(): Unit = {
    mounts.clear()
    unmounts.clear()
    log.clear()
  }

  /** A `<div>` tagged with `id` that records its lifecycle into this tracker. */
  def div(id: String, mods: Modifier[Div]*): Div =
    track(L.div(id), id).amend(mods: _*)

  /** A `<span>` tagged with `id` that records its lifecycle into this tracker. */
  def span(id: String, mods: Modifier[Span]*): Span =
    track(L.span(id), id).amend(mods: _*)

  // -- Assertions (fail with actual counts + full event log) --

  /** Assert the EXACT number of mount and unmount events recorded for `id`. */
  def assertCounts(id: String, mounts: Int, unmounts: Int): Unit = {
    val actualMounts = this.mounts(id)
    val actualUnmounts = this.unmounts(id)
    if (actualMounts != mounts || actualUnmounts != unmounts) {
      throw new AssertionError(
        s"Lifecycle mismatch for '$id': expected (mounts=$mounts, unmounts=$unmounts) " +
          s"but got (mounts=$actualMounts, unmounts=$actualUnmounts). " +
          s"Full event log: [${log.mkString(", ")}]"
      )
    }
  }

  /** Assert `id` was mounted exactly once and never unmounted (freshly mounted, still in place,
    * never re-run). */
  def assertMountedOnce(id: String): Unit = assertCounts(id, mounts = 1, unmounts = 0)

  /** Assert `id` was mounted exactly once and then unmounted exactly once (cleanly torn down,
    * with no redundant re-mount in between). */
  def assertMountedThenUnmounted(id: String): Unit = assertCounts(id, mounts = 1, unmounts = 1)

  /** Assert `id` never had any lifecycle event (built but never mounted). */
  def assertNeverMounted(id: String): Unit = assertCounts(id, mounts = 0, unmounts = 0)

  private def track[El <: HtmlElement](el: El, id: String): El = {
    el.amend(
      L.onMountCallback[El] { _ =>
        mounts(id) += 1
        log += s"mount:$id"
      },
      L.onUnmountCallback[El] { _ =>
        unmounts(id) += 1
        log += s"unmount:$id"
      }
    )
  }
}
