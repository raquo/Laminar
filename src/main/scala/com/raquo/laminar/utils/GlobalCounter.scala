package com.raquo.laminar.utils

// @TODO[API] Remove this, or put in Snabbdom Test utils. Only used for debug
object GlobalCounter {

  private var count = 0

  def current(): Int = count

  def next(): Int = {
    count += 1
    count
  }
}
