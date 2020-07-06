package com.raquo.laminar

import com.raquo.airstream.core.{Observable, Observer}
import com.raquo.airstream.eventbus.{EventBus, WriteBus}
import com.raquo.airstream.eventstream.EventStream
import com.raquo.airstream.signal.{Signal, Val, Var}
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Style
import com.raquo.laminar.Implicits.{RichEventStream, RichObservable, RichSignal}
import com.raquo.laminar.emitter.EventPropTransformation
import com.raquo.laminar.keys.CompositeAttr.CompositeValueMappers
import com.raquo.laminar.keys.{ReactiveEventProp, ReactiveStyle}
import com.raquo.laminar.modifiers.{Binder, ChildInserter, ChildrenInserter, Inserter, Setter}
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement, TextNode}
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.|

trait Implicits extends Implicits.LowPriorityImplicits with CompositeValueMappers {

  implicit def eventPropToEventPropTransformation[Ev <: dom.Event, El <: ReactiveElement.Base](
    eventProp: ReactiveEventProp[Ev]
  ): EventPropTransformation[Ev, Ev] = {
    new EventPropTransformation(eventProp, shouldUseCapture = false, processor = Some(_))
  }

  @inline implicit def styleToReactiveStyle[V](style: Style[V]): ReactiveStyle[V] = {
    new ReactiveStyle[V](style)
  }

  @inline implicit def textToNode(text: String): TextNode = {
    new TextNode(text)
  }

  /** Create a setter that applies each of the setters in a seq */
  implicit def seqToSetter[El <: ReactiveElement.Base](setters: scala.collection.Seq[Setter[El]]): Setter[El] = {
    Setter(element => setters.foreach(_.apply(element)))
  }

  /** Create a binder that combines several binders */
  //implicit def seqToBinder[El <: ReactiveElement.Base](binders: scala.collection.Seq[Binder[El]]): Binder[El] = {
  //  Binder[El] { ??? }
  //}

  /** Create a modifier that applies each of the modifiers in a seq */
  implicit def seqToModifier[A, El <: ReactiveElement.Base](
    modifiers: scala.collection.Seq[A]
  )(implicit
    evidence: A => Modifier[El]
  ): Modifier[El] = {
    // @TODO[Performance] See if we might want a separate implicit conversion for cases when we don't need `evidence`
    new Modifier[El] {
      override def apply(element: El): Unit = {
        modifiers.foreach(evidence(_).apply(element))
      }
    }
  }

  /** Create a modifier that applies the modifier in an option, if it's defined, or does nothing otherwise */
  implicit def optionToModifier[A, El <: ReactiveElement.Base](
    maybeModifier: Option[A]
  )(
    implicit evidence: A => Modifier[El]
  ): Modifier[El] = {
    // @TODO[Performance] See if we might want a separate implicit conversion for cases when we don't need `evidence`
    new Modifier[El] {
      override def apply(element: El): Unit = {
        maybeModifier.foreach(evidence(_).apply(element))
      }
    }
  }

  implicit def optionToSetter[El <: ReactiveElement.Base](maybeSetter: Option[Setter[El]]): Setter[El] = {
    Setter(element => maybeSetter.foreach(_.apply(element)))
  }

  /** This modifier exists to prevent collections of nodes from being converted using [[nodesSeqToInserter]],
    * which is more expensive. We have a test that will fail should the order of implicits be wrong. */
  implicit def nodesSeqToModifier(nodes: scala.collection.Seq[ChildNode.Base]): Modifier[ReactiveElement.Base] = {
    new Modifier[ReactiveElement.Base] {
      override def apply(element: ReactiveElement.Base): Unit = {
        nodes.foreach(_.apply(element))
      }
    }
  }

  /** This modifier exists to prevent collections of nodes from being converted using [[nodesArrayToInserter]],
    * which is more expensive. We have a test that will fail should the order of implicits be wrong. */
  @inline implicit def nodesArrayToModifier[N <: ChildNode.Base](nodes: js.Array[N]): Modifier[ReactiveElement.Base] = {
    nodesSeqToModifier(nodes)
  }

  /** Add --> methods on Observables */
  @inline implicit def enrichObservable[A](observable: Observable[A]): RichObservable[A] = {
    new RichObservable(observable)
  }

  /** Add --> methods on EventStreams */
  @inline implicit def enrichEventStream[A](eventStream: EventStream[A]): RichEventStream[A] = {
    new RichEventStream(eventStream)
  }

  /** Add --> methods on Signals */
  @inline implicit def enrichSignal[A](observable: Signal[A]): RichSignal[A] = {
    new RichSignal(observable)
  }

  // @TODO[IDE] This implicit conversion is only needed to make the Scala plugin for IntelliJ 2019.3 happy.
  //  - this kind of type signature is relevant to Style props which often expect Observable[Int | String] or something like that.
  //  - note that even though this conversion is not required for your code to work, it might get called when it's available,
  //    but it's an @inline noop, so it should have no effect on runtime.
  @inline implicit def intellijObservableOfOrConversion[A](stringStream: Observable[A]): Observable[A | String] = {
    stringStream.asInstanceOf[Observable[A | String]]
  }
}

object Implicits {

  trait LowPriorityImplicits {

    // Inserter implicits are needlessly expensive if we just need a Modifier, so we de-prioritize them

    implicit def nodeToInserter(node: ChildNode.Base): Inserter[ReactiveElement.Base] = {
      ChildInserter[ReactiveElement.Base](_ => Val(node), initialInsertContext = None)
    }

    implicit def nodesSeqToInserter(nodes: scala.collection.Seq[ChildNode.Base]): Inserter[ReactiveElement.Base] = {
      ChildrenInserter[ReactiveElement.Base](_ => Val(nodes.toList), initialInsertContext = None)
    }

    @inline implicit def nodesArrayToInserter[N <: ChildNode.Base](nodes: js.Array[N]): Inserter[ReactiveElement.Base] = {
      nodesSeqToInserter(nodes)
    }
  }

  /** Some of these methods are redundant, but we need them for type inference to work. */

  class RichObservable[A](val observable: Observable[A]) extends AnyVal {

    def -->(observer: Observer[A]): Binder[ReactiveElement.Base] = {
      Binder(ReactiveElement.bindObserver(_, observable)(observer))
    }

    def -->(onNext: A => Unit): Binder[ReactiveElement.Base] = {
      Binder(ReactiveElement.bindFn(_, observable)(onNext))
    }

    def -->(targetVar: Var[A]): Binder[ReactiveElement.Base] = {
      Binder(ReactiveElement.bindObserver(_, observable)(targetVar.writer))
    }
  }

  class RichSignal[A](val signal: Signal[A]) extends AnyVal {

    def -->(observer: Observer[A]): Binder[ReactiveElement.Base] = {
      Binder(ReactiveElement.bindObserver(_, signal)(observer))
    }

    def -->(onNext: A => Unit): Binder[ReactiveElement.Base] = {
      Binder(ReactiveElement.bindFn(_, signal)(onNext))
    }
  }

  class RichEventStream[A](val eventStream: EventStream[A]) extends AnyVal {

    def -->(observer: Observer[A]): Binder[ReactiveElement.Base] = {
      Binder(ReactiveElement.bindObserver(_, eventStream)(observer))
    }

    def -->(onNext: A => Unit): Binder[ReactiveElement.Base] = {
      Binder(ReactiveElement.bindFn(_, eventStream)(onNext))
    }

    def -->(writeBus: WriteBus[A]): Binder[ReactiveElement.Base] = {
      Binder(ReactiveElement.bindBus(_, eventStream)(writeBus))
    }

    def -->(eventBus: EventBus[A]): Binder[ReactiveElement.Base] = {
      Binder(ReactiveElement.bindBus(_, eventStream)(eventBus.writer))
    }
  }
}
