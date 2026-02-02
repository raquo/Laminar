package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{SimpleKeySetter, SimpleKeyUpdater}
import com.raquo.laminar.nodes.ReactiveElement

/** Scala 2 version: no ThisV <: V bound (emulated unions can't prove subtyping) */
trait SimpleKey[
  +Self <: SimpleKey[Self, V, El],
  -V,
  -El <: ReactiveElement.Base
] { self: Self =>

  val name: String

  def :=[ThisV <: V](value: ThisV): SimpleKeySetter[Self, ThisV, El]

  @inline def apply[ThisV <: V](value: ThisV): SimpleKeySetter[Self, ThisV, El] = {
    this := value
  }

  def maybe: SimpleKey[_ <: SimpleKey[_, Option[V], El], Option[V], El]

  final def <--[ThisV](values: Source[ThisV])(implicit ev: ThisV => V): SimpleKeyUpdater[Self, ThisV, El] =
    bindSource(values, ev)

  protected def bindSource[ThisV](values: Source[ThisV], ev: ThisV => V): SimpleKeyUpdater[Self, ThisV, El]
}
