package com.raquo.laminar.experimental.airstream.fixtures

import com.raquo.laminar.experimental.airstream.observation.Observer
import com.raquo.laminar.experimental.airstream.signal.{ComputedSignal, Signal}

import scala.scalajs.js

trait TestableSignal[A] extends Signal[A] {

  // Make these fields public for testing

  def _testChildren: Seq[ComputedSignal[_]] = linkedChildren

  def _testObservers: js.Array[Observer[A]] = externalObservers

}
