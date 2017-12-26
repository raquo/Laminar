package com.raquo.laminar.experimental.airstream

import com.raquo.laminar.experimental.airstream
import org.scalajs.dom

class Var[A](initialValue: A) extends Signal[A] {

  override protected var currentValue: A = initialValue

  dom.console.log(s"> Create $this")

  def update(newValue: A): Unit = {
    airstream.batchUpdate(Assignment(this, newValue)) // @TODO[Performance] can be denormalized to make more efficient
  }

  // The original stream can always be transformed to A
//  def updateFromStream(sourceStream: Stream[A])(implicit context: Context): Unit = {
//    sourceStream.foreach(this.update, context)
//  }

  /** @return whether value was changed */
  private[airstream] def recalculate(newValue: A): Boolean = {
    val valueChanged = newValue != currentValue
    if (valueChanged) {
      currentValue = newValue
    }
    valueChanged
  }

  override def toString: String = s"Var@${hashCode()}(value=$currentValue)"
}

