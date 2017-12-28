package com.raquo.laminar.experimental.airstream.signal
import com.raquo.laminar.experimental.airstream.ownership.Owner

class Val[A](value: A)(override protected implicit val owner: Owner) extends Signal[A] {

  override protected var currentValue: A = value
}
