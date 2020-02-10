package com.raquo.laminar

import com.raquo.airstream.core.{Observable, Observer}
import com.raquo.airstream.eventbus.WriteBus
import com.raquo.airstream.eventstream.EventStream
import com.raquo.airstream.signal.{Signal, Val}
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Style
import com.raquo.laminar.Implicits.{RichEventStream, RichObservable, RichSignal}
import com.raquo.laminar.emitter.EventPropTransformation
import com.raquo.laminar.keys.CompositeAttr.CompositeValueMappers
import com.raquo.laminar.keys.{ReactiveEventProp, ReactiveStyle}
import com.raquo.laminar.modifiers.{ChildInserter, Inserter, Setter}
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement, TextNode}
import org.scalajs.dom

import scala.scalajs.js.|

trait Implicits extends CompositeValueMappers {

  implicit def eventPropToEventPropTransformation[Ev <: dom.Event, El <: ReactiveElement.Base](
    eventProp: ReactiveEventProp[Ev]
  ): EventPropTransformation[Ev, Ev] = {
    new EventPropTransformation(eventProp, useCapture = false, processor = Some(_))
  }

  @inline implicit def styleToReactiveStyle[V](style: Style[V]): ReactiveStyle[V] = {
    new ReactiveStyle[V](style)
  }

  @inline implicit def textToNode(text: String): TextNode = {
    new TextNode(text)
  }

  /** Create a modifier that applies each of the modifiers in a seq */
  implicit def nodeToInserter(node: ChildNode.Base): Inserter[ReactiveElement.Base] = {
    ChildInserter[ReactiveElement.Base](_ => Val(node), initialInsertContext = None)
  }

  /** Create a modifier that applies each of the modifiers in a seq */
  implicit def seqToModifier[A, El <: ReactiveElement.Base](seq: scala.collection.Seq[A])(implicit evidence: A => Modifier[El]): Modifier[El] = {
    // @TODO[Performance] See if we might want a separate implicit conversion for cases when we don't need `evidence`
    new Modifier[El] {
      override def apply(element: El): Unit = {
        seq.foreach(evidence(_).apply(element))
      }
    }
  }

  /** Create a setter that applies each of the setters in a seq */
  implicit def seqToSetter[El <: ReactiveElement.Base](seq: scala.collection.Seq[Setter[El]]): Setter[El] = {
    Setter(element => seq.foreach(_.apply(element)))
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

  @inline implicit def enrichObservable[A](observable: Observable[A]): RichObservable[A] = {
    new RichObservable(observable)
  }

  @inline implicit def enrichEventStream[A](eventStream: EventStream[A]): RichEventStream[A] = {
    new RichEventStream(eventStream)
  }

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

  /** Some of these methods are redundant, but we need them for type inference to work. */

  class RichObservable[A](val observable: Observable[A]) extends AnyVal {

    def -->(observer: Observer[A]): Setter[ReactiveElement.Base] = {
      Setter(ReactiveElement.bindObserver(_, observable)(observer))
    }

    def -->(onNext: A => Unit): Setter[ReactiveElement.Base] = {
      Setter(ReactiveElement.bindFn(_, observable)(onNext))
    }
  }

  class RichSignal[A](val signal: Signal[A]) extends AnyVal {

    def -->(observer: Observer[A]): Setter[ReactiveElement.Base] = {
      Setter(ReactiveElement.bindObserver(_, signal)(observer))
    }

    def -->(onNext: A => Unit): Setter[ReactiveElement.Base] = {
      Setter(ReactiveElement.bindFn(_, signal)(onNext))
    }
  }

  class RichEventStream[A](val eventStream: EventStream[A]) extends AnyVal {

    def -->(observer: Observer[A]): Setter[ReactiveElement.Base] = {
      Setter(ReactiveElement.bindObserver(_, eventStream)(observer))
    }

    def -->(onNext: A => Unit): Setter[ReactiveElement.Base] = {
      Setter(ReactiveElement.bindFn(_, eventStream)(onNext))
    }

    def -->(writeBus: WriteBus[A]): Setter[ReactiveElement.Base] = {
      Setter(ReactiveElement.bindBus(_, eventStream)(writeBus))
    }
  }
}
