package com.raquo.laminar.api

import com.raquo.airstream.core.{Sink, Source}
import com.raquo.laminar.api.Implicits.RichSource
import com.raquo.laminar.api.StyleUnitsApi.StyleEncoder
import com.raquo.laminar.inserters._
import com.raquo.laminar.keys._
import com.raquo.laminar.keys.CompositeKey.CompositeValueMappers
import com.raquo.laminar.modifiers._
import com.raquo.laminar.modifiers.SimpleKeyUpdater.StyleUpdater
import com.raquo.laminar.nodes._
import org.scalajs.dom

trait Implicits extends Implicits.LowPriorityImplicits with CompositeValueMappers {

  /** Add --> methods to Observables */
  @inline implicit def enrichSource[A](source: Source[A]): RichSource[A] = {
    new RichSource(source)
  }

  //
  // -- CSS Styles --
  //

  /** Allow both Int-s and Double-s in numeric style props */
  @inline implicit def derivedStyleIntToDouble(style: DerivedStyleProp[Int]): DerivedStyleProp[Double] = {
    // Safe because Int-s and Double-s have identical runtime representation in Scala.js
    style.asInstanceOf[DerivedStyleProp[Double]]
  }

  /** Allow both Int-s and Double-s in numeric style props */
  @inline implicit def styleEncoderIntToDouble(encoder: StyleEncoder[Int]): StyleEncoder[Double] = {
    // Safe because Int-s and Double-s have identical runtime representation in Scala.js
    encoder.asInstanceOf[StyleEncoder[Double]]
  }

  // #TODO[Scala2] This is only needed in Scala 2, for type inference
  implicit class RichStyleProp1[V](val prop: StyleProp[V]) {
    def <--(values: Source[V]): StyleUpdater[V] = prop <-- values
  }

  //
  // -- Basic laminar syntax --
  //

  /** Add [[EventProcessor]] methods (mapToValue / filter / preventDefault / etc.) to event props (e.g. onClick) */
  @inline implicit def eventPropToProcessor[Ev <: dom.Event](eventProp: EventProp[Ev]): EventProcessor[Ev, Ev] = {
    EventProcessor.empty(eventProp)
  }

  /** Convert primitive renderable values (strings, numbers, booleans, etc.) to text nodes */
  implicit def textToTextNode[A](value: A)(implicit r: RenderableText[A]): TextNode = {
    new TextNode(r.asString(value))
  }

  /** Convert a custom component to Laminar DOM node */
  implicit def componentToNode[A](component: A)(implicit r: RenderableNode[A]): ChildNode.Base = {
    r.asNode(component)
  }

  //
  // -- Methods to convert collections of Setter[El] to a single Setter[El] --
  //

  /** Create a [[Setter]] that applies the optionally provided [[Setter]], or else does nothing. */
  // implicit def optionToSetter[El <: ReactiveElement.Base](maybeSetter: Option[Setter[El]]): Setter[El] = {
  //   Setter(element => maybeSetter.foreach(_.apply(element)))
  // }

  /** Combine a js.Array of [[Setter]]-s into a single [[Setter]] that applies them all. */
  implicit def seqToSetter[Collection[_], El <: ReactiveElement.Base](
    setters: Collection[Setter[El]]
  )(implicit
    renderableSeq: RenderableSeq[Collection]
  ): Setter[El] = {
    Setter { element =>
      renderableSeq.foreach(setters)(_.apply(element))
    }
  }

  /** Create a binder that combines several binders */
  // This can only be implemented with significant caveats, I think. See https://gitter.im/Laminar_/Lobby?at=631a58b4cf6cfd27af7c96b4
  // implicit def seqToBinder[El <: ReactiveElement.Base](binders: collection.Seq[Binder[El]]): Binder[El] = {
  //   Binder[El] { ??? }
  // }

  // -- Methods to convert collections of Modifier[El]-like things to Modifier[El] --

  /** Create a modifier that applies an optional modifier, or does nothing if option is empty */
  // implicit def optionToModifier[A, El <: ReactiveElement.Base](
  //   maybeModifier: Option[A]
  // )(implicit
  //   asModifier: A => Modifier[El]
  // ): Modifier[El] = {
  //   Modifier(element => maybeModifier.foreach(asModifier(_).apply(element)))
  // }

  /** Create a modifier that applies each of the modifiers in a seq */
  implicit def seqToModifier[A, Collection[_], El <: ReactiveElement.Base](
    modifiers: Collection[A]
  )(implicit
    asModifier: A => Modifier[El],
    renderableSeq: RenderableSeq[Collection]
  ): Modifier[El] = {
    Modifier(element => renderableSeq.foreach(modifiers)(asModifier(_).apply(element)))
  }

  // The various collection-to-modifier conversions below are cheaper and better equivalents of
  // collection-to-inserter modifiers found in the `LowPriorityImplicits` trait below.
  // We have a test that will fail should the priority of implicits be wrong.
  // #Note ^^ That comment is outdated as of v17. We have a test that ensures that the selected
  //  implicits don't create unnecessary subscriptions, but the implicits in LowPriorityImplicits
  //  don't do that anymore, so the test does not catch using them. But, that is also not a problem.
  // #TODO[Elegance] We should simplify the implicits even further
  //  - I think `nodeOptionToModifier` and `nodeSeqToModifier` are not needed anymore
  //  - Possibly other non-Component versions of the things in LowPriorityImplicits as well
  //  - But then we should probably move that Component stuff out of LowPriorityImplicits,
  //    I hope we don't get any conflicts. I'll leave this for v18.

  // -- Methods to convert collections of nodes to modifiers --

  // implicit def nodeOptionToModifier(nodes: Option[ChildNode.Base]): Modifier.Base = {
  //   Modifier(element => nodes.foreach(_.apply(element)))
  // }

  // #Note: the case of Collection[Component] is covered by `seqToModifier` above
  implicit def nodeSeqToModifier[Collection[_]](
    nodes: Collection[ChildNode.Base]
  )(implicit
    renderableSeq: RenderableSeq[Collection]
  ): Modifier.Base = {
    Modifier { element =>
      renderableSeq.foreach(nodes)(_.apply(element))
    }
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
    * `onMountInsert`, but they are relatively expensive compared to simpler
    * alternatives when a mere Modifier would suffice. And so, the conversions
    * below are de-prioritized.
    *
    * #Note: Prior to v17, these conversions involved dynamic inserters with
    *  inefficient implementations like `children <-- Val(nodes.toList)`.
    *  Now, they use static inserters, and don't have such a significant
    *  inefficiency.
    * #TODO Simplify this! See the other #TODO comment above about moving stuff out of LowPriorityImplicits.
    */
  trait LowPriorityImplicits {

    // -- CSS Styles --

    // #TODO[Scala2] This is only needed in Scala 2, for type inference
    implicit class RichStyleProp2[V](val prop: StyleProp[V]) {
      def <--(values: Source[String]): StyleUpdater[V] = prop <-- values
    }

    // -- Methods to convert individual values / nodes / components to inserters --

    implicit def textToInserter[TextLike](value: TextLike)(implicit r: RenderableText[TextLike]): StaticInserter = {
      if (r == RenderableText.textNodeRenderable) {
        StaticChildInserter.noHooks(value.asInstanceOf[TextNode])
      } else {
        new StaticTextInserter(r.asString(value))
      }
    }

    implicit def componentToInserter[Component: RenderableNode](component: Component): StaticChildInserter = {
      StaticChildInserter.noHooksC(component)
    }

    // -- Methods to convert collections of nodes and components to inserters --

    // implicit def componentOptionToInserter[Component](
    //   maybeComponent: Option[Component]
    // )(implicit
    //   renderableNode: RenderableNode[Component]
    // ): StaticChildrenInserter = {
    //   componentSeqToInserter(maybeComponent.toList)
    // }

    implicit def componentSeqToInserter[Collection[_], Component](
      components: Collection[Component]
    )(implicit
      renderableSeq: RenderableSeq[Collection],
      renderableNode: RenderableNode[Component]
    ): StaticChildrenInserter = {
      StaticChildrenInserter.noHooks(components, renderableSeq, renderableNode)
    }
  }

}
