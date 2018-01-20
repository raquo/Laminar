package com.raquo.laminar.experimental.airstream.util

trait GlobalCounter {

  private var lastCreatedId: Int = 1

  protected[airstream] def nextId(): Int = {
    lastCreatedId += 1
    lastCreatedId
  }

}
