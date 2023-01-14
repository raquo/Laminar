package com.raquo.laminar

import com.raquo.airstream.core.{Sink, Source, Transaction}
import com.raquo.airstream.state.Val
import com.raquo.ew.{JsArray, ewArray}
import com.raquo.laminar.Implicits.RichSource
import com.raquo.laminar.api.Laminar.StyleEncoder
import com.raquo.laminar.api.UnitArrowsFeature
import com.raquo.laminar.keys.CompositeKey.CompositeValueMappers
import com.raquo.laminar.keys.{DerivedStyleProp, EventProcessor, EventProp}
import com.raquo.laminar.modifiers.{Binder, ChildInserter, ChildTextInserter, ChildrenInserter, Inserter, Modifier, Renderable, Setter}
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement, TextNode}
import org.scalajs.dom

import scala.scalajs.js

trait Implicits extends Implicits.LowPriorityImplicits with CompositeValueMappers {

  implicit def derivedStyleIntToDouble[V](style: DerivedStyleProp[Int]): DerivedStyleProp[Double] = {
    style.asInstanceOf[DerivedStyleProp[Double]] // Safe because Int-s and Double-s have identical runtime repr in SJS
  }

  implicit def styleEncoderIntToDouble[V](encoder: StyleEncoder[Int]): StyleEncoder[Double] = {
    encoder.asInstanceOf[StyleEncoder[Double]] // Safe because Int-s and Double-s have identical runtime repr in SJS
  }

  @inline implicit def eventPropToProcessor[Ev <: dom.Event](eventProp: EventProp[Ev]): EventProcessor[Ev, Ev] = {
    EventProcessor.empty(eventProp)
  }

  /** Implicit [[Renderable]] instances are available for primitive types (defined in modifiers/Renderable.scala) */
  implicit def renderableToTextNode[A](value: A)(implicit renderable: Renderable[A]): TextNode = {
    renderable.asTextNode(value)
  }

  /** Create a setter that applies each of the setters in a seq */
  implicit def seqToSetter[El <: ReactiveElement.Base](setters: scala.collection.Seq[Setter[El]]): Setter[El] = {
    Setter(element => setters.foreach(_.apply(element)))
  }

  /** Create a setter that applies each of the setters in an array */
  implicit def arrayToSetter[El <: ReactiveElement.Base](setters: Array[Setter[El]]): Setter[El] = {
    Setter(element => setters.foreach(_.apply(element)))
  }

  /** Create a binder that combines several binders */
  // This can only be implemented with significant caveats, I think. See https://gitter.im/Laminar_/Lobby?at=631a58b4cf6cfd27af7c96b4
  //implicit def seqToBinder[El <: ReactiveElement.Base](binders: scala.collection.Seq[Binder[El]]): Binder[El] = {
  //  Binder[El] { ??? }
  //}

  /** Create a modifier that applies each of the modifiers in a seq */
  implicit def seqToModifier[A, El <: ReactiveElement.Base](
    modifiers: scala.collection.Seq[A]
  )(
    implicit asModifier: A => Modifier[El]
  ): Modifier[El] = {
    // @TODO[Performance] See if we might want a separate implicit conversion for cases when we don't need `evidence`
    Modifier[El] { element =>
      Transaction.onStart.shared {
        modifiers.foreach(asModifier(_).apply(element))
      }
    }
  }

  /** Create a modifier that applies each of the modifiers in an array */
  implicit def arrayToModifier[A, El <: ReactiveElement.Base](
    modifiers: Array[A]
  )(
    implicit asModifier: A => Modifier[El]
  ): Modifier[El] = {
    Modifier[El] { element =>
      Transaction.onStart.shared {
        modifiers.foreach(asModifier(_).apply(element))
      }
    }
  }

  /** Create a modifier that applies each of the modifiers in an array */
  implicit def jsArrayToModifier[A, El <: ReactiveElement.Base](
    modifiers: JsArray[A]
  )(
    implicit asModifier: A => Modifier[El]
  ): Modifier[El] = {
    Modifier[El] { element =>
      Transaction.onStart.shared {
        modifiers.forEach(asModifier(_).apply(element))
      }
    }
  }

  /** Create a modifier that applies each of the modifiers in an array */
  implicit def sjsArrayToModifier[A, El <: ReactiveElement.Base](
    modifiers: js.Array[A]
  )(
    implicit asModifier: A => Modifier[El]
  ): Modifier[El] = {
    jsArrayToModifier(modifiers.ew)
  }

  /** Create a modifier that applies the modifier in an option, if it's defined, or does nothing otherwise */
  implicit def optionToModifier[A, El <: ReactiveElement.Base](
    maybeModifier: Option[A]
  )(
    implicit asModifier: A => Modifier[El]
  ): Modifier[El] = {
    Modifier[El](element => maybeModifier.foreach(asModifier(_).apply(element)))
  }

  implicit def optionToSetter[El <: ReactiveElement.Base](maybeSetter: Option[Setter[El]]): Setter[El] = {
    Setter(element => maybeSetter.foreach(_.apply(element)))
  }

  /** This modifier exists to prevent collections of nodes from being converted using [[nodesSeqToInserter]],
    * which is more expensive. We have a test that will fail should the order of implicits be wrong. */
  implicit def nodesSeqToModifier(nodes: scala.collection.Seq[ChildNode.Base]): Modifier[ReactiveElement.Base] = {
    Modifier[ReactiveElement.Base](element => nodes.foreach(_.apply(element)))
  }

  /** This modifier exists to prevent collections of nodes from being converted using [[nodesArrayToInserter]],
    * which is more expensive. We have a test that will fail should the order of implicits be wrong. */
  @inline implicit def nodesArrayToModifier[N <: ChildNode.Base](nodes: Array[N]): Modifier[ReactiveElement.Base] = {
    Modifier[ReactiveElement.Base](element => nodes.foreach(_.apply(element)))
  }

  /** This modifier exists to prevent collections of nodes from being converted using [[nodesJsArrayToInserter]],
    * which is more expensive. We have a test that will fail should the order of implicits be wrong. */
  @inline implicit def nodesJsArrayToModifier[N <: ChildNode.Base](nodes: js.Array[N]): Modifier[ReactiveElement.Base] = {
    Modifier[ReactiveElement.Base](element => nodes.ew.forEach(_.apply(element)))
  }

  /** Add --> methods on Observables */
  @inline implicit def enrichSource[A](source: Source[A]): RichSource[A] = {
    new RichSource(source)
  }
}

object Implicits {

  trait LowPriorityImplicits {

    // Inserter implicits are needlessly expensive if we just need a Modifier, so we de-prioritize them

    implicit def nodeToInserter(node: ChildNode.Base): Inserter[ReactiveElement.Base] = {
      ChildInserter[ReactiveElement.Base](Val(node))
    }

    implicit def nodesSeqToInserter(nodes: scala.collection.Seq[ChildNode.Base]): Inserter[ReactiveElement.Base] = {
      ChildrenInserter[ReactiveElement.Base](Val(nodes.toList))
    }

    implicit def nodesArrayToInserter(nodes: Array[ChildNode.Base]): Inserter[ReactiveElement.Base] = {
      nodesSeqToInserter(nodes)
    }

    @inline implicit def nodesJsArrayToInserter[N <: ChildNode.Base](nodes: js.Array[N]): Inserter[ReactiveElement.Base] = {
      nodesSeqToInserter(nodes)
    }

    implicit def renderableToInserter[A](value: A)(implicit renderable: Renderable[A]): Inserter[ReactiveElement.Base] = {
      ChildTextInserter[A, ReactiveElement.Base](Val(value), renderable)
    }
  }

  /** Some of these methods are redundant, but we need them for type inference to work. */

  class RichSource[A](val source: Source[A]) extends AnyVal {

    def -->(sink: Sink[A]): Binder.Base = {
      Binder(ReactiveElement.bindSink(_, source.toObservable)(sink))
    }

    def -->(onNext: A => Unit): Binder.Base = {
      Binder(ReactiveElement.bindFn(_, source.toObservable)(onNext))
    }

    def -->(onNext: => Unit)(implicit evidence: UnitArrowsFeature): Binder.Base = {
      Binder(ReactiveElement.bindFn(_, source.toObservable)(_ => onNext))
    }

  }

}
