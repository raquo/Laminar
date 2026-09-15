package com.raquo.laminar.api

import com.raquo.airstream.core.{Sink, Source}
import com.raquo.laminar.api.Implicits.{EventProcessorSyntaxFixType, SourceArrowSyntax, SourceSyntaxFixType}
import com.raquo.laminar.inserters._
import com.raquo.laminar.keys._
import com.raquo.laminar.modifiers._
import com.raquo.laminar.nodes._
import org.scalajs.dom

trait Implicits
extends ImplicitsPlatformSpecific
with Implicits.LowPriorityImplicits
with CompositeValueMapper.Implicits {

  // #TODO[Airstream] should probably provide this itself?
  /** Add --> methods to Observables */
  @inline implicit def arrowSyntax[A](source: Source[A]): SourceArrowSyntax[A] = {
    new SourceArrowSyntax(source)
  }

  //
  // -- Basic Laminar syntax --
  //

  /** Add [[EventProcessor]] methods (mapToValue / filter / preventDefault / etc.) to event props (e.g. onClick) */
  @inline implicit def eventPropToProcessor[Ev <: dom.Event](eventProp: EventProp[Ev]): EventProcessor[Ev, Ev] = {
    EventProcessor.empty(eventProp)
  }

  /** Convert primitive renderable values (strings, numbers, booleans, etc.) to text nodes */
  implicit def textToTextNode[A](value: A)(implicit r: RenderableText[A]): TextNode = {
    TextNode(value)(using r)
  }

  /** Convert a custom component to Laminar DOM node */
  implicit def componentToNode[A](component: A)(implicit r: RenderableNode[A]): ChildNode.Base = {
    r.asNode(component)
  }

  //
  // -- Methods to convert collections of Setter[El] to a single Setter[El] --
  //

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

  // #Note: the case of Collection[Component] is covered by `seqToModifier` above
  // implicit def nodeSeqToModifier[Collection[_]](
  //   nodes: Collection[ChildNode.Base]
  // )(implicit
  //   renderableSeq: RenderableSeq[Collection]
  // ): Modifier.Base = {
  //   Modifier { element =>
  //     renderableSeq.foreach(nodes)(_.apply(element))
  //   }
  // }

  // -- IDE helpers ---

  /** Add the `fixType` IDE helper method to Observables. See [[SourceSyntaxFixType]]. */
  @inline implicit def arrowSyntaxFixType[A](source: Source[A]): SourceSyntaxFixType[A] = {
    new SourceSyntaxFixType(source)
  }

  /** Add the `fixType` IDE helper method to event processors. See [[SourceSyntaxFixType]]. */
  @inline implicit def arrowSyntaxFixType[Ev <: dom.Event, V](processor: EventProcessor[Ev, V]): EventProcessorSyntaxFixType[Ev, V] = {
    new EventProcessorSyntaxFixType(processor)
  }

}

object Implicits {

  /** Some of these methods are redundant, but we need them for type inference to work. */

  class SourceArrowSyntax[A](private val source: Source[A]) extends AnyVal {

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

  // -- IDE helpers --

  /** Problem:  `observableOfTuple --> { (a, b) => ... }` is valid Scala 3 syntax,
    *           but IntelliJ fails to infer the types of `a` and `b`.
    * Solution: `observableOfTuple.fixType --> { (a, b) => ... }` works exactly the
    *           same, except IntelliJ infers the types just fine.
    *
    * This is not limited to tuples, e.g. this works too:
    *
    * `signalOfOption.fixType --> { case Some(x) => ... }`.
    *
    * There is no other point to this helper. You can omit `.fixType`, and Scala
    * itself will compile and work fine. It's just a workaround for an IDE issue.
    */
  class SourceSyntaxFixType[A](private val source: Source[A]) extends AnyVal {

    @inline def fixType: SourceArrowSyntaxFixType[A] = new SourceArrowSyntaxFixType(source)
  }

  class SourceArrowSyntaxFixType[A](private val source: Source[A]) extends AnyVal {

    @inline def -->(onNext: A => Unit): Binder.Base = new SourceArrowSyntax(source) --> onNext
  }

  /** Same as [[SourceSyntaxFixType]], but for event processors, e.g.
    * `onClick.mapTo(aTuple).fixType --> { (a, b) => ... }`.
    */
  class EventProcessorSyntaxFixType[Ev <: dom.Event, V](private val processor: EventProcessor[Ev, V]) extends AnyVal {

    @inline def fixType: EventProcessorArrowSyntaxFixType[Ev, V] = new EventProcessorArrowSyntaxFixType(processor)
  }

  class EventProcessorArrowSyntaxFixType[Ev <: dom.Event, V](private val processor: EventProcessor[Ev, V]) extends AnyVal {

    @inline def -->(onNext: V => Unit): EventListener[Ev, V] = processor --> onNext
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

    // -- Specialized methods for onMountInsert and Web Component Slot.apply --

    /** This is only used for Web Component Slots, which require `Inserter & Slotted`.
      * Regular code uses higher-priority [[componentToNode]].
      *
      * Note: this deliberately does not convert TextNode-s as those are not allowed
      * in slots, but in principle this is an abstraction leak.
      */
    implicit def componentToInserter[Component](
      component: Component
    )(implicit
      r: RenderableNode[Component]
    ): SlottableChildInserter = {
      SlottableChildInserter.noSlotName(r.asNode(component))
    }

    /** This is only used when:
      *  - onMountInsert's callback returns a Seq of elements of a single element, or
      *  - One of the items passed to Web Components Slot.apply is a Seq of elements.
      *
      * In those contexts, we need an Inserter (or Inserter & Slottable), and aside from
      * this low-priority conversion, we only have [[seqToModifier]] which gives us an
      * arbitrary Modifier – no other way to get an Inserter implicitly right now.
      */
    implicit def componentSeqToInserter[Collection[_], Component](
      components: Collection[Component]
    )(implicit
      renderableSeq: RenderableSeq[Collection],
      renderableNode: RenderableNode[Component]
    ): SlottableChildrenInserter = {
      SlottableChildrenInserter.noSlotName(components, renderableSeq, renderableNode)
    }
  }

}
