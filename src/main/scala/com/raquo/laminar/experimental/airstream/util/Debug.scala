package com.raquo.laminar.experimental.airstream.util

import org.scalajs.dom

import scala.scalajs.js

/** A collection of convenience debug methods that you can plug into .map, for example:
  *
  * $numbers.map(Debug.log("numbers"))    this will log "numbers: 1" when the observable emits value `1`
  */
object Debug {

  private val always = (_: Any) => true

  def log[V](prefix: String = "event", when: V => Boolean = always): V => V = {
    value => {
      if (when(value)) {
        dom.console.log(prefix + ": ", value.asInstanceOf[js.Any])
      }
      value
    }
  }

  def break[V](when: V => Boolean = always): V => V = {
    value => {
      if (when(value)) {
        js.special.debugger()
      }
      value
    }
  }

  def spy[V](fn: V => Unit): V => V = {
    value => {
      fn(value)
      value
    }
  }
}
