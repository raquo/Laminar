package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.L.seqToSetter
import com.raquo.laminar.modifiers.{Setter, SimpleKeySetter, SimpleKeyUpdater}
import com.raquo.laminar.nodes.ReactiveElement

/**
  * This class represents a Key typically found on the left hand side of the key-value pair `key := value`
  *
  * Example would be a particular attribute or a property (without the corresponding value), e.g. "href"
  */
abstract class SimpleKey[V, DomV, -El <: ReactiveElement.Base] {

  val name: String

  def :=(value: V): SimpleKeySetter.Of[V, DomV, El]

  @inline def apply(value: V): SimpleKeySetter.Of[V, DomV, El] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[El] = {
    seqToSetter[Option, El](value.map(v => this := v))
  }

  def <--(values: Source[V]): SimpleKeyUpdater[El, ? <: SimpleKey[V, DomV, El], V]

}
