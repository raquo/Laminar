package com.raquo.laminar.experimental.airstream.util

// @TODO this is pretty much unused, consider removing
trait GlobalCounter {

  private var lastCreatedId: Int = 0

  protected[airstream] def nextId(): Int = {
    lastCreatedId += 1
    lastCreatedId
  }

}
