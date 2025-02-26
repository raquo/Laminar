package com.raquo.laminar.domapi.keyapi

import com.raquo.laminar.keys._
import com.raquo.laminar.nodes.ReactiveElement

// #TODO[Naming]
trait DomKeyApi[-K[_] <: SimpleKey[?, _, El], -El <: ReactiveElement.Base] {

  def set[V](
    element: El,
    key: K[V],
    value: V
  ): Unit

  def remove[V](
    element: El,
    key: K[V],
  ): Unit = {
    set(element, key, value = null.asInstanceOf[V])
  }
}
