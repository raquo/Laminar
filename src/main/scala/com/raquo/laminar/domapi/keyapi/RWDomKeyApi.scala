package com.raquo.laminar.domapi.keyapi

import com.raquo.laminar.keys.SimpleKey
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js.|

trait RWDomKeyApi[-K[_] <: SimpleKey[?, _, El], -El <: ReactiveElement.Base]
extends DomKeyApi[K, El] {

  def get[V](
    element: El,
    key: K[V]
  ): V | Unit

}
