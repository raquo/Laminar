package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement}
import com.raquo.laminar.utils.UnitSpec

import scala.collection.mutable

class SignalChangesPullSpec extends UnitSpec {

  // When signal.changes restarts, it pulls the current value from signal,
  // and emits it in a new transaction. However, we have a special
  // Transaction.onStart mechanism to make sure that all newly added observers
  // of signal.changes have a chance to receive this event, and avoid glitches.
  // This test suite verifies that signal.changes restarts without a glitch.

  def _changes(v: Var[Int]): EventStream[Int] = {
    v
      .signal.setDisplayName("VarSignal")
      .changes.setDisplayName("VarSignal.changes")
  }

  def _isPositive(changes: EventStream[Int], log: mutable.Buffer[String]): EventStream[Boolean] = {
    changes.map { num =>
      val isPositive = num > 0
      log += s"$num isPositive = $isPositive"
      isPositive
    }.setDisplayName("IsPositive")
  }

  def _isEven(changes: EventStream[Int], log: mutable.Buffer[String]): EventStream[Boolean] = {
    changes.map { num =>
      val isEven = num % 2 == 0
      log += s"$num isEven = $isEven"
      isEven
    }.setDisplayName("IsEven")
  }

  def _combined(changes: EventStream[Int], isPositive: EventStream[Boolean], isEven: EventStream[Boolean]): EventStream[String] = {
    changes.combineWithFn(isPositive, isEven) { (num, isPositive, isEven) =>
      s"$num isPositive = $isPositive, isEven = $isEven"
    }.setDisplayName("Combined")
  }

  def _logCombined(log: mutable.Buffer[String], prefix: String): Observer[String] = Observer { v =>
    log += prefix + ": " + v
  }

  it("signal.changes with multiple observers in tag.apply") {

    val log = mutable.Buffer[String]()

    val v = Var(0)

    val changes = _changes(v)
    val isPositive = _isPositive(changes, log)
    val isEven = _isEven(changes, log)
    val combined = _combined(changes, isPositive, isEven)

    val el = div(
      "hello",
      combined --> _logCombined(log, "combined-init-1"),
      combined --> _logCombined(log, "combined-init-2"),
      EventStream.fromValue("x") --> _logCombined(log, "xxx")
    )

    // --

    mount(el)

    log.toList shouldBe List(
      "xxx: x"
    )
    log.clear()

    // --

    v.set(1)

    log.toList shouldBe List(
      "1 isPositive = true",
      "1 isEven = false",
      "combined-init-1: 1 isPositive = true, isEven = false",
      "combined-init-2: 1 isPositive = true, isEven = false",
    )
    log.clear()

    // --

    unmount()

    // -- update var while unmounted, then re-mount

    v.set(-2)

    mount(el)

    log.toList shouldBe List(
      "xxx: x",
      "-2 isPositive = false",
      "-2 isEven = true",
      "combined-init-1: -2 isPositive = false, isEven = true",
      "combined-init-2: -2 isPositive = false, isEven = true"
    )
    log.clear()

    // -- add another listener while mounted

    el.amend(combined --> _logCombined(log, "combined-init-3"))

    log.toList shouldBe Nil // we haven't emitted yet

    // -- emit again

    v.set(3)

    log.toList shouldBe List(
      "3 isPositive = true",
      "3 isEven = false",
      "combined-init-1: 3 isPositive = true, isEven = false",
      "combined-init-2: 3 isPositive = true, isEven = false",
      "combined-init-3: 3 isPositive = true, isEven = false"
    )
    log.clear()

  }

  it("signal.changes adding child with multiple observers") {

    // an element exists, and

    val log = mutable.Buffer[String]()

    val v = Var(0)

    val changes = _changes(v)
    val isPositive = _isPositive(changes, log)
    val isEven = _isEven(changes, log)
    val combined = _combined(changes, isPositive, isEven)

    def makeChild(ix: Int) = span(
      combined --> _logCombined(log, s"combined-ch$ix-1"),
      combined --> _logCombined(log, s"combined-ch$ix-2")
    )

    val maybeChildVar = Var[Option[ChildNode.Base]](initial = Some(makeChild(ix = 1)))

    val el = div(
      "hello",
      child.maybe <-- maybeChildVar
    )

    // --

    mount(el)

    log.toList shouldBe Nil

    // --

    v.set(1)

    log.toList shouldBe List(
      "1 isPositive = true",
      "1 isEven = false",
      "combined-ch1-1: 1 isPositive = true, isEven = false",
      "combined-ch1-2: 1 isPositive = true, isEven = false"
    )
    log.clear()

    // -- update var ch1 is mounted

    v.set(-2)

    log.toList shouldBe List(
      "-2 isPositive = false",
      "-2 isEven = true",
      "combined-ch1-1: -2 isPositive = false, isEven = true",
      "combined-ch1-2: -2 isPositive = false, isEven = true"
    )
    log.clear()

    // -- unmount ch1 and update var while ch1 is unmounted

    maybeChildVar.set(None)

    v.set(3)

    log.toList shouldBe Nil

    // -- mount ch2 and expect it to pull the new value

    maybeChildVar.set(Some(makeChild(ix = 2)))

    log.toList shouldBe List(
      "3 isPositive = true",
      "3 isEven = false",
      "combined-ch2-1: 3 isPositive = true, isEven = false",
      "combined-ch2-2: 3 isPositive = true, isEven = false"
    )
    log.clear()

    // -- switch to another child live

    maybeChildVar.set(Some(makeChild(ix = 3)))

    log.toList shouldBe Nil

    // -- and emit an update

    v.set(-4)

    log.toList shouldBe List(
      "-4 isPositive = false",
      "-4 isEven = true",
      "combined-ch3-1: -4 isPositive = false, isEven = true",
      "combined-ch3-2: -4 isPositive = false, isEven = true"
    )
    log.clear()

  }

  it("signal.changes adding children with multiple observers") {

    // an element exists, and

    val log = mutable.Buffer[String]()

    val v = Var(0)

    val changes = _changes(v)
    val isPositive = _isPositive(changes, log)
    val isEven = _isEven(changes, log)
    val combined = _combined(changes, isPositive, isEven)

    def makeChild(ix: Int) = span(
      combined --> _logCombined(log, s"combined-ch$ix-1"),
      combined --> _logCombined(log, s"combined-ch$ix-2")
    )

    val childrenVar = Var[List[ChildNode.Base]](initial = List(makeChild(ix = 1), makeChild(ix = 2)))

    val el = div(
      "hello",
      children <-- childrenVar
    )

    // --

    mount(el)

    log.toList shouldBe Nil

    // --

    v.set(1)

    log.toList shouldBe List(
      "1 isPositive = true",
      "1 isEven = false",
      "combined-ch1-1: 1 isPositive = true, isEven = false",
      "combined-ch1-2: 1 isPositive = true, isEven = false",
      "combined-ch2-1: 1 isPositive = true, isEven = false",
      "combined-ch2-2: 1 isPositive = true, isEven = false"
    )
    log.clear()

    // -- update var ch1 is mounted

    v.set(-2)

    log.toList shouldBe List(
      "-2 isPositive = false",
      "-2 isEven = true",
      "combined-ch1-1: -2 isPositive = false, isEven = true",
      "combined-ch1-2: -2 isPositive = false, isEven = true",
      "combined-ch2-1: -2 isPositive = false, isEven = true",
      "combined-ch2-2: -2 isPositive = false, isEven = true"
    )
    log.clear()

    // -- unmount all children, and update var while they are unmounted

    childrenVar.set(Nil)

    v.set(3)

    log.toList shouldBe Nil

    // -- mount new children, and expect all of them to pull the new value

    childrenVar.set(List(makeChild(ix = 3), makeChild(ix = 4)))

    log.toList shouldBe List(
      "3 isPositive = true",
      "3 isEven = false",
      "combined-ch3-1: 3 isPositive = true, isEven = false",
      "combined-ch3-2: 3 isPositive = true, isEven = false",
      "combined-ch4-1: 3 isPositive = true, isEven = false",
      "combined-ch4-2: 3 isPositive = true, isEven = false"
    )
    log.clear()

  }

  it("signal.changes adding multiple observers with .amend") {

    // Precondition: we need the changes stream to have isFirstPull==false, yet have no observers,
    // so we add a child with an observer first, and then remove it.

    val log = mutable.Buffer[String]()

    val v = Var(0)

    val changes = _changes(v)
    val isPositive = _isPositive(changes, log)
    val isEven = _isEven(changes, log)
    val combined = _combined(changes, isPositive, isEven)

    def makeChild(ix: Int) = span(
      combined --> _logCombined(log, s"combined-ch$ix-1"),
      combined --> _logCombined(log, s"combined-ch$ix-2")
    )

    val maybeChildVar = Var[Option[ChildNode.Base]](initial = None)

    val el = div(
      "hello",
      child.maybe <-- maybeChildVar
    )

    // --

    mount(el)

    v.set(-2)

    maybeChildVar.set(Some(makeChild(ix = 1)))

    // -- btw, testing different order of multi-observer child insertion
    //    compared to the dedicated child test above: here we first
    //    mount the component and emit a value, and only then insert
    //    the child. We get no events because isFirstPull==true here.

    log.toList shouldBe Nil

    // --

    maybeChildVar.set(None)

    // -- update signal while .changes is stopped, and then add multiple observers using .amend

    v.set(3)

    el.amend(
      combined --> _logCombined(log, "combined-amend-1"),
      combined --> _logCombined(log, "combined-amend-2"),
    )

    log.toList shouldBe List(
      "3 isPositive = true",
      "3 isEven = false",
      "combined-amend-1: 3 isPositive = true, isEven = false",
      "combined-amend-2: 3 isPositive = true, isEven = false"
    )
    log.clear()

  }

  it("signal.changes adding multiple observers with a single seqToModifier") {

    // This is like the .amend test above, but we do the multi-amend using the
    // implicit seq-to-modifier conversion, it also needs Transaction.onStart.shared

    val log = mutable.Buffer[String]()

    val v = Var(0)

    val changes = _changes(v)
    val isPositive = _isPositive(changes, log)
    val isEven = _isEven(changes, log)
    val combined = _combined(changes, isPositive, isEven)

    def makeChild(ix: Int) = span(
      combined --> _logCombined(log, s"combined-ch$ix-1"),
      combined --> _logCombined(log, s"combined-ch$ix-2")
    )

    val maybeChildVar = Var[Option[ChildNode.Base]](initial = None)

    val el = div(
      "hello",
      child.maybe <-- maybeChildVar
    )

    // --

    mount(el)

    v.set(-2)

    maybeChildVar.set(Some(makeChild(ix = 1)))

    log.toList shouldBe Nil

    // --

    maybeChildVar.set(None)

    // -- update signal while .changes is stopped, and then add multiple observers using .amend

    v.set(3)

    // Using the seqToModifier implicit conversion
    val combinedMod: Modifier[ReactiveElement.Base] = List(
      combined --> _logCombined(log, "combined-amend-1"),
      combined --> _logCombined(log, "combined-amend-2"),
    )

    combinedMod(el) // Not using the .amend method

    log.toList shouldBe List(
      "3 isPositive = true",
      "3 isEven = false",
      "combined-amend-1: 3 isPositive = true, isEven = false",
      "combined-amend-2: 3 isPositive = true, isEven = false"
    )
    log.clear()

  }

}
