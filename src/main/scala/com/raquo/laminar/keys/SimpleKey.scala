package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.L.seqToSetter
import com.raquo.laminar.domapi.KeyDomApi
import com.raquo.laminar.modifiers.{Setter, SimpleKeySetter, SimpleKeyUpdater}
import com.raquo.laminar.nodes.ReactiveElement

/**
  * This class represents a Key typically found on the left hand side of the key-value pair `key := value`
  *
  * Example would be a particular attribute or a property (without the corresponding value), e.g. "href"
  */
trait SimpleKey[V, DomV, -El <: ReactiveElement.Base] { self =>

  // #nc naming
  /** `this` must be an instance of Self[V] */
  type Self[VV] <: SimpleKey[VV, DomV, El]

  // type Self = SelfKind[V]

  val name: String

  val domApi: KeyDomApi[Self, El]

  def :=(value: V): SimpleKeySetter[V, DomV, El] = {
    val _value = value
    val _self = self.asInstanceOf[Self[V]]  // #nc asinstanceof - safe?
    new SimpleKeySetter[V, DomV, El] {
      override val key: Self[V] = _self
      override val value: V = _value

      override def set(el: El): Unit = {
        domApi.set[V](el, _self, _value)
      }

      override def remove(el: El): Unit = {
        domApi.remove[V](el, _self)
      }
    }
  }

  @inline def apply(value: V): SimpleKeySetter[V, DomV, El] = {
    this := value
  }

  def maybe(value: Option[V]): Setter[El] = {
    seqToSetter[Option, El](value.map(v => this := v))
  }

  def <--(values: Source[V]): SimpleKeyUpdater[El, Self[V], V] = {
    new SimpleKeyUpdater[El, Self[V], V](
      key = self.asInstanceOf[Self[V]],
      values = values.toObservable,
      update = (el, value, reason) => {
        domApi.set(el, self.asInstanceOf[Self[V]], value)
      }
    )
  }

}
