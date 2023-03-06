package com.raquo.laminar

import com.raquo.airstream.core.{Sink, Source}
import com.raquo.airstream.state.Val
import com.raquo.ew
import com.raquo.ew.ewArray
import com.raquo.laminar.Implicits.RichSource
import com.raquo.laminar.api.L.{StyleEncoder, child, children}
import com.raquo.laminar.api.UnitArrowsFeature
import com.raquo.laminar.keys.CompositeKey.CompositeValueMappers
import com.raquo.laminar.keys.{DerivedStyleProp, EventProcessor, EventProp}
import com.raquo.laminar.modifiers.{Binder, Inserter, Modifier, RenderableNode, RenderableText, Setter}
import com.raquo.laminar.nodes.{ChildNode, ReactiveElement, TextNode}
import com.raquo.laminar.receivers.ChildOptionReceiver.RichChildOptionReceiver
import com.raquo.laminar.receivers.ChildReceiver.RichChildReceiver
import org.scalajs.dom

import scala.scalajs.js

trait Implicits extends Implicits.LowPriorityImplicits with CompositeValueMappers {

  /** Add --> methods to Observables */
  @inline implicit def enrichSource[A](source: Source[A]): RichSource[A] = {
    new RichSource(source)
  }

  /** Allow both Int-s and Double-s in numeric style props */
  @inline implicit def derivedStyleIntToDouble[V](style: DerivedStyleProp[Int]): DerivedStyleProp[Double] = {
    // Safe because Int-s and Double-s have identical runtime representation in Scala.js
    style.asInstanceOf[DerivedStyleProp[Double]]
  }

  /** Allow both Int-s and Double-s in numeric style props */
  @inline implicit def styleEncoderIntToDouble[V](encoder: StyleEncoder[Int]): StyleEncoder[Double] = {
    // Safe because Int-s and Double-s have identical runtime representation in Scala.js
    encoder.asInstanceOf[StyleEncoder[Double]]
  }

  /** Add [[EventProcessor]] methods (mapToValue / filter / preventDefault / etc.) to event props (e.g. onClick) */
  @inline implicit def eventPropToProcessor[Ev <: dom.Event](eventProp: EventProp[Ev]): EventProcessor[Ev, Ev] = {
    EventProcessor.empty(eventProp)
  }

  /** Convert primitive renderable values (strings, numbers, booleans, etc.) to text nodes */
  implicit def textToTextNode[A](value: A)(implicit renderable: RenderableText[A]): TextNode = {
    new TextNode(renderable.asString(value))
  }

  /** Convert a custom component to Laminar DOM node */
  implicit def componentToNode[A](component: A)(implicit renderable: RenderableNode[A]): ChildNode.Base = {
    renderable.asNode(component)
  }

  // -- Methods to convert collections of Setter[El] to a single Setter[El] --

  /** Create a [[Setter]] that applies the optionally provided [[Setter]], or else does nothing. */
  implicit def optionToSetter[El <: ReactiveElement.Base](maybeSetter: Option[Setter[El]]): Setter[El] = {
    Setter(element => maybeSetter.foreach(_.apply(element)))
  }

  /** Combine aa Seq of [[Setter]]-s into a single [[Setter]] that applies them all. */
  implicit def seqToSetter[El <: ReactiveElement.Base](setters: collection.Seq[Setter[El]]): Setter[El] = {
    Setter(element => setters.foreach(_.apply(element)))
  }

  /** Combine an Array of [[Setter]]-s into a single [[Setter]] that applies them all. */
  implicit def arrayToSetter[El <: ReactiveElement.Base](setters: scala.Array[Setter[El]]): Setter[El] = {
    Setter(element => setters.foreach(_.apply(element)))
  }

  /** Combine an ew.JsArray of [[Setter]]-s into a single [[Setter]] that applies them all. */
  implicit def jsArrayToSetter[El <: ReactiveElement.Base](setters: ew.JsArray[Setter[El]]): Setter[El] = {
    Setter(element => setters.forEach(_.apply(element)))
  }

  /** Combine a js.Array of [[Setter]]-s into a single [[Setter]] that applies them all. */
  implicit def sjsArrayToSetter[El <: ReactiveElement.Base](setters: js.Array[Setter[El]]): Setter[El] = {
    Setter(element => setters.ew.forEach(_.apply(element)))
  }

  /** Create a binder that combines several binders */
  // This can only be implemented with significant caveats, I think. See https://gitter.im/Laminar_/Lobby?at=631a58b4cf6cfd27af7c96b4
  //implicit def seqToBinder[El <: ReactiveElement.Base](binders: collection.Seq[Binder[El]]): Binder[El] = {
  //  Binder[El] { ??? }
  //}

  // -- Methods to convert collections of Modifier[El]-like things to Modifier[El] --

  /** Create a modifier that applies an optional modifier, or does nothing if option is empty */
  implicit def optionToModifier[A, El <: ReactiveElement.Base](
    maybeModifier: Option[A]
  )(
    implicit asModifier: A => Modifier[El]
  ): Modifier[El] = {
    Modifier(element => maybeModifier.foreach(asModifier(_).apply(element)))
  }

  /** Create a modifier that applies each of the modifiers in a seq */
  implicit def seqToModifier[A, El <: ReactiveElement.Base](
    modifiers: collection.Seq[A]
  )(
    implicit asModifier: A => Modifier[El]
  ): Modifier[El] = {
    Modifier(element => modifiers.foreach(asModifier(_).apply(element)))
  }

  /** Create a modifier that applies each of the modifiers in an array */
  implicit def arrayToModifier[A, El <: ReactiveElement.Base](
    modifiers: scala.Array[A]
  )(
    implicit asModifier: A => Modifier[El]
  ): Modifier[El] = {
    Modifier(element => modifiers.foreach(asModifier(_).apply(element)))
  }

  /** Create a modifier that applies each of the modifiers in an array */
  implicit def jsArrayToModifier[A, El <: ReactiveElement.Base](
    modifiers: ew.JsArray[A]
  )(
    implicit asModifier: A => Modifier[El]
  ): Modifier[El] = {
    Modifier(element => modifiers.forEach(asModifier(_).apply(element)))
  }

  /** Create a modifier that applies each of the modifiers in an array */
  implicit def sjsArrayToModifier[A, El <: ReactiveElement.Base](
    modifiers: js.Array[A]
  )(
    implicit asModifier: A => Modifier[El]
  ): Modifier[El] = {
    jsArrayToModifier(modifiers.ew)
  }

  // The various collection-to-modifier conversions below are cheaper and better equivalents of
  // collection-to-inserter modifiers found in the `LowPriorityImplicits` trait below.
  // We have a test that will fail should the priority of implicits be wrong.

  // -- Methods to convert collections of nodes to modifiers --

  implicit def nodeOptionToModifier(nodes: Option[ChildNode.Base]): Modifier.Base = {
    Modifier(element => nodes.foreach(_.apply(element)))
  }

  implicit def nodeSeqToModifier(nodes: collection.Seq[ChildNode.Base]): Modifier.Base = {
    Modifier(element => nodes.foreach(_.apply(element)))
  }

  implicit def nodeArrayToModifier[N <: ChildNode.Base](nodes: scala.Array[N]): Modifier.Base = {
    Modifier(element => nodes.foreach(_.apply(element)))
  }

  implicit def nodeJsArrayToModifier[N <: ChildNode.Base](nodes: ew.JsArray[N]): Modifier.Base = {
    Modifier(element => nodes.forEach(_.apply(element)))
  }

  implicit def nodeSjsArrayToModifier[N <: ChildNode.Base](nodes: js.Array[N]): Modifier.Base = {
    Modifier(element => nodes.ew.forEach(_.apply(element)))
  }
}

object Implicits {

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

  /** Implicit conversions from X to Inserter are primarily needed for
    * convenience in `onMountInsert`, but they are an expensive overkill
    * in other contexts where converting all these types to regular
    * Modifiers is cheaper and just as functional, so the conversions
    * below are de-prioritized.
    */
  trait LowPriorityImplicits {

    // -- Methods to convert individual values / nodes / components to inserters --

    implicit def textToInserter[A](value: A)(implicit renderable: RenderableText[A]): Inserter.Base = {
      child.text <-- Val(value)
    }

    implicit def nodeToInserter(node: ChildNode.Base): Inserter.Base = {
      child <-- Val(node)
    }

    implicit def componentToInserter[Component](component: Component)(implicit renderable: RenderableNode[Component]): Inserter.Base = {
      // #TODO[Scala3] this could be simply `child <-- Val(component)`, but inside this
      //  particular method, Scala 3 fails to compile that pattern for some reason. WHY
      new RichChildReceiver(child) <-- Val(component)
    }

    // -- Methods to convert collections of nodes to inserters --

    implicit def nodeOptionToInserter(maybeNode: Option[ChildNode.Base]): Inserter.Base = {
      child.maybe <-- Val(maybeNode)
    }

    implicit def nodeSeqToInserter(nodes: collection.Seq[ChildNode.Base]): Inserter.Base = {
      children <-- Val(nodes.toList)
    }

    implicit def nodeArrayToInserter(nodes: scala.Array[ChildNode.Base]): Inserter.Base = {
      children <-- Val(nodes.toList)
    }
    
    implicit def nodeJsArrayToInserter[N <: ChildNode.Base](nodes: ew.JsArray[N]): Inserter.Base = {
      children <-- Val(nodes.asScalaJs.toList)
    }
    
    implicit def nodeSjsArrayToInserter[N <: ChildNode.Base](nodes: js.Array[N]): Inserter.Base = {
      children <-- Val(nodes.toList)
    }

    // -- Methods to convert collections of components to inserters --
    
    implicit def componentOptionToInserter[Component: RenderableNode](maybeComponent: Option[Component]): Inserter.Base = {
      // #TODO[Scala3] this could be simply `child.maybe <-- Val(maybeComponent)`, but inside this
      //  particular method, Scala 3 fails to compile that pattern for some reason. WHY
      new RichChildOptionReceiver(child.maybe) <-- Val(maybeComponent)
    }

    implicit def componentSeqToInserter[Component: RenderableNode](components: collection.Seq[Component]): Inserter.Base = {
      children <-- Val(components.toList)
    }

    implicit def componentArrayToInserter[Component: RenderableNode](components: scala.Array[Component]): Inserter.Base = {
      children <-- Val(components.toList)
    }

    implicit def componentJsArrayToInserter[Component: RenderableNode](components: ew.JsArray[Component]): Inserter.Base = {
      children <-- Val(components.asScalaJs.toList)
    }

    implicit def componentSjsArrayToInserter[Component: RenderableNode](components: js.Array[Component]): Inserter.Base = {
      children <-- Val(components.toList)
    }

  }

}
