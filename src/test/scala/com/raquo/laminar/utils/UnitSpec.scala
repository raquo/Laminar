package com.raquo.laminar.utils

import com.raquo.airstream.core.AirstreamError
import com.raquo.domtestutils.Utils
import com.raquo.domtestutils.scalatest.{Matchers, MountSpec}
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement}
import org.scalajs.dom
import org.scalatest.{BeforeAndAfterAll, Failed, Outcome}
import org.scalatest.funspec.AnyFunSpec

import scala.collection.mutable

class UnitSpec
extends AnyFunSpec
with LaminarSpec
with MountSpec
with Matchers
with Utils
with BeforeAndAfterAll {

  // We install a *collecting* unhandled-error callback for the whole suite, not a rethrowing
  // one. `AirstreamError.unsafeRethrowErrorCallback` throws synchronously from inside
  // `sendUnhandledError`, i.e. mid-transaction-propagation, which aborts Airstream's transaction
  // machinery before it can clean up. That corrupts global transaction state and cascades
  // failures into unrelated later tests. Collecting the error instead lets propagation finish
  // cleanly; `withFixture` then fails the specific test that leaked it. Error-path tests use
  // `withCollectedAirstreamErrors` to intercept expected errors before they reach this buffer.
  private val leakedErrors = mutable.Buffer[Throwable]()

  private val leakCollectorCallback: Throwable => Unit = leakedErrors += _

  override protected def beforeAll(): Unit = {
    AirstreamError.unregisterUnhandledErrorCallback(AirstreamError.consoleErrorCallback)
    AirstreamError.registerUnhandledErrorCallback(leakCollectorCallback)
  }

  override protected def afterAll(): Unit = {
    AirstreamError.registerUnhandledErrorCallback(AirstreamError.consoleErrorCallback)
    AirstreamError.unregisterUnhandledErrorCallback(leakCollectorCallback)
  }

  /** Fail the test that leaked unhandled Airstream errors, rather than letting them corrupt
    * shared state and surface as failures in unrelated later tests.
    */
  override def withFixture(test: NoArgTest): Outcome = {
    leakedErrors.clear()
    val outcome = super.withFixture(test)
    val leaked = leakedErrors.toList
    leakedErrors.clear()
    if (outcome.isSucceeded && leaked.nonEmpty) {
      Failed(new Exception(
        s"Test leaked ${leaked.size} unhandled Airstream error(s) not intercepted by " +
          s"`withCollectedAirstreamErrors`: ${leaked.map(AirstreamError.getFullMessage).mkString("; ")}"
      ))
    } else {
      outcome
    }
  }

  /** A fresh [[EventTracker]] for asserting on the ordered sequence of element / lifecycle events. */
  def createEventTracker(): EventTracker = new EventTracker()

  /** Run `body` with unhandled Airstream errors collected into the provided buffer instead of
    * counting as leaks, restoring the suite's default leak collector afterwards even if `body`
    * throws.
    *
    * Useful for asserting that Laminar reports (rather than throws on) a recoverable condition,
    * e.g. a DOM exception, or an externally-inserted node found inside a tracked span. Replaces
    * the register / try / finally / unregister boilerplate previously inlined in several specs.
    *
    * {{{
    *   withCollectedAirstreamErrors { errors =>
    *     bus.emit(badValue)
    *     assert(errors.size == 1)
    *   }
    * }}}
    */
  def withCollectedAirstreamErrors(body: mutable.Buffer[Throwable] => Unit): Unit = {
    val errors = mutable.Buffer[Throwable]()
    val collectingCallback: Throwable => Unit = errors += _
    try {
      AirstreamError.unregisterUnhandledErrorCallback(leakCollectorCallback)
      AirstreamError.registerUnhandledErrorCallback(collectingCallback)
      body(errors)
    } finally {
      AirstreamError.unregisterUnhandledErrorCallback(collectingCallback)
      AirstreamError.registerUnhandledErrorCallback(leakCollectorCallback)
    }
  }

  /** Mutate the DOM behind Laminar's back, as a third-party script or browser extension might.
    *
    * These go straight to the raw browser API and deliberately do NOT route through Laminar's
    * `DomApi`, so Laminar is not notified and its `InsertContext` bookkeeping goes stale — which
    * is exactly the condition these helpers exist to set up. Use them to test that Laminar stays
    * correct (recovers, or reports) when the DOM changes underneath it.
    */
  object external {

    /** Detach `child` from its current parent without telling Laminar. */
    def removeChild(child: ChildNode.Base): Unit = {
      val ref = child.ref
      val parent = ref.parentNode
      if (parent != null) {
        parent.removeChild(ref)
      }
    }

    /** Insert a raw `newChild` node right before `referenceChild`, without telling Laminar. */
    def insertBefore(newChild: dom.Node, referenceChild: ChildNode.Base): Unit = {
      referenceChild.ref.parentNode.insertBefore(newChild, referenceChild.ref)
    }

    /** Append a raw `newChild` node to `parent`, without telling Laminar. */
    def appendChild(parent: ReactiveElement.Base, newChild: dom.Node): Unit = {
      parent.ref.appendChild(newChild)
    }
  }

}
