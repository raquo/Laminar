package com.raquo.laminar

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Style
import com.raquo.laminar.emitter.EventPropTransformation
import com.raquo.laminar.keys.CompositeAttr.CompositeValueMappers
import com.raquo.laminar.keys.{ReactiveEventProp, ReactiveStyle}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveText}
import org.scalajs.dom

import scala.scalajs.js.|

trait Implicits extends DomApi with CompositeValueMappers {

  implicit def eventPropToEventPropTransformation[Ev <: dom.Event, El <: ReactiveElement[dom.Element]](
    eventProp: ReactiveEventProp[Ev]
  ): EventPropTransformation[Ev, Ev] = {
    new EventPropTransformation(eventProp, useCapture = false, processor = Some(_))
  }

  @inline implicit def styleToReactiveStyle[V](style: Style[V]): ReactiveStyle[V] = {
    new ReactiveStyle[V](style)
  }

  @inline implicit def textToNode(text: String): ReactiveText = {
    new ReactiveText(text)
  }

  /** Create a modifier that applies each of the modifiers in a seq */
  implicit def seqToModifier[A, El <: ReactiveElement[dom.Element]](seq: Seq[A])(implicit evidence: A => Modifier[El]): Modifier[El] = {
    // @TODO[Performance] See if we might want a separate implicit conversion for cases when we don't need `evidence`
    new Modifier[El] {
      override def apply(el: El): Unit = {
        seq.foreach(evidence(_).apply(el))
      }
    }
  }

  /** Create a modifier that applies the modifier in an option, if it's defined, or does nothing otherwise */
  implicit def optionToModifier[A, El <: ReactiveElement[dom.Element]](option: Option[A])(implicit evidence: A => Modifier[El]): Modifier[El] = {
    // @TODO[Performance] See if we might want a separate implicit conversion for cases when we don't need `evidence`
    new Modifier[El] {
      override def apply(el: El): Unit = {
        option.foreach(evidence(_).apply(el))
      }
    }
  }

  // @TODO[IDE] This implicit conversion is actually never used by the compiler. However, this makes the Scala plugin for IntelliJ 2017.3 happy.
  @inline implicit def intellijStringObservableAsStringOrStringObservable(stringStream: Observable[String]): Observable[String | String] = {
    stringStream.asInstanceOf[Observable[String | String]]
  }
}
