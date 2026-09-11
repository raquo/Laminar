package com.raquo.laminar.utils

import com.raquo.airstream.core.AirstreamError
import com.raquo.domtestutils.Utils
import com.raquo.domtestutils.scalatest.{Matchers, MountSpec}
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement}
import org.scalajs.dom
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec

import scala.collection.mutable

class UnitSpec
extends AnyFunSpec
with LaminarSpec
with MountSpec
with Matchers
with Utils
with BeforeAndAfterAll {

  // These help detect and track unexpected unhandled errors.
  // Tests that want to

  override protected def beforeAll(): Unit = {
    AirstreamError.unregisterUnhandledErrorCallback(AirstreamError.consoleErrorCallback)
    AirstreamError.registerUnhandledErrorCallback(AirstreamError.unsafeRethrowErrorCallback)
  }

  override protected def afterAll(): Unit = {
    AirstreamError.registerUnhandledErrorCallback(AirstreamError.consoleErrorCallback)
    AirstreamError.unregisterUnhandledErrorCallback(AirstreamError.unsafeRethrowErrorCallback)
  }

  /** A fresh [[EventTracker]] for asserting on the ordered sequence of element / lifecycle events. */
  def createEventTracker(): EventTracker = new EventTracker()

  /** Run `body` with unhandled Airstream errors collected into the provided buffer instead of
    * being rethrown, restoring the default (rethrow) callback afterwards even if `body` throws.
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
      AirstreamError.unregisterUnhandledErrorCallback(AirstreamError.unsafeRethrowErrorCallback)
      AirstreamError.registerUnhandledErrorCallback(collectingCallback)
      body(errors)
    } finally {
      AirstreamError.unregisterUnhandledErrorCallback(collectingCallback)
      AirstreamError.registerUnhandledErrorCallback(AirstreamError.unsafeRethrowErrorCallback)
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
