package com.raquo.laminar

import com.raquo.airstream.core.{Observable, Sink, Source}
import com.raquo.airstream.state.Val
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Style
import com.raquo.laminar.Implicits.RichSource
import com.raquo.laminar.keys.CompositeKey.CompositeValueMappers
import com.raquo.laminar.keys.{EventProcessor, ReactiveEventProp, ReactiveStyle}
import com.raquo.laminar.modifiers.{Binder, ChildInserter, ChildrenInserter, Inserter, Setter}
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement, TextNode}
import org.scalajs.dom

import scala.scalajs.js

trait Implicits extends Implicits.LowPriorityImplicits with CompositeValueMappers {

  @inline implicit def eventPropToProcessor[Ev <: dom.Event](eventProp: ReactiveEventProp[Ev]): EventProcessor[Ev, Ev] = {
    EventProcessor.empty(eventProp)
  }

  @inline implicit def styleToReactiveStyle[V](style: Style[V]): ReactiveStyle[V] = new ReactiveStyle[V](style)

  @inline implicit def textToNode(text: String): TextNode = new TextNode(text)

  @inline implicit def boolToNode(bool: Boolean): TextNode = new TextNode(bool.toString)

  @inline implicit def intToNode(int: Int): TextNode = new TextNode(int.toString)

  @inline implicit def doubleToNode(double: Double): TextNode = new TextNode(double.toString)

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
  @inline implicit def enrichSource[A](source: Source[A]): RichSource[A] = {
    new RichSource(source)
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

  class RichSource[A](val source: Source[A]) extends AnyVal {

    def -->(sink: Sink[A]): Binder[ReactiveElement.Base] = {
      Binder(ReactiveElement.bindSink(_, source.toObservable)(sink))
    }

    def -->(onNext: A => Unit): Binder[ReactiveElement.Base] = {
      Binder(ReactiveElement.bindFn(_, source.toObservable)(onNext))
    }

  }

}
