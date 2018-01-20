package com.raquo.laminar.experimental.airstream.eventstream

class Tuple2EventStream[A, B](val tuple2Stream: EventStream[(A, B)]) extends AnyVal {

  def map2[C](project: (A, B) => C): EventStream[C] = {
    new MapEventStream[(A, B), C](
      parent = tuple2Stream,
      combinedValue => project(combinedValue._1, combinedValue._2)
    )
  }
}
