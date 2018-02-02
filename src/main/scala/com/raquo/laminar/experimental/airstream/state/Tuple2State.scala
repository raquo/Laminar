package com.raquo.laminar.experimental.airstream.state

class Tuple2State[A, B](val tuple2State: State[(A, B)]) extends AnyVal {

  def map2[C](project: (A, B) => C): State[C] = {
    new MapState[(A, B), C](
      parent = tuple2State,
      combinedValue => project(combinedValue._1, combinedValue._2),
      tuple2State.owner
    )
  }
}

