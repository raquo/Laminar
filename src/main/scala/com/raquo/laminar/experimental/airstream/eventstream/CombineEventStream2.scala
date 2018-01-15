package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.features.CombineObservable2
import com.raquo.laminar.experimental.airstream.core.{Observable, Observer}

/** Stream that combines the latest values from two streams into a tuple.
  * Only fires after both streams have sent a value.
  *
  * Note: this stream forgets any previous values of parent streams once it's stopped
  */
class CombineEventStream2[A, B](
  override protected[this] val parent1: EventStream[A],
  override protected[this] val parent2: EventStream[B]
) extends EventStream[(A, B)] with CombineObservable2[A, B] {

  private[this] var maybeLastParent1Value: Option[A] = None
  private[this] var maybeLastParent2Value: Option[B] = None

  override protected[this] lazy val parent1Observer: Observer[A] = Observer(nextParent1Value => {
    // println(s"> updated p1 value to $nextParent1Value")
    maybeLastParent1Value = Some(nextParent1Value)
    maybeLastParent2Value.foreach { lastParent2Value =>
      fire((nextParent1Value, lastParent2Value))
    }
  })

  override protected[this] lazy val parent2Observer: Observer[B] = Observer(nextParent2Value => {
    // println(s"> updated p2 value to $nextParent2Value")
    maybeLastParent2Value = Some(nextParent2Value)
    maybeLastParent1Value.foreach { lastParent1Value =>
      fire((lastParent1Value, nextParent2Value))
    }
  })

  // @TODO[API] This should be in an implicit value class, I think.
  def map2[C](project: (A, B) => C): EventStream[C] = {
    new MapEventStream[(A, B), C](
      parent = this,
      combinedValue => project(combinedValue._1, combinedValue._2)
    )
  }

  override protected[this] def onStop(): Unit = {
    maybeLastParent1Value = None
    maybeLastParent2Value = None
    super.onStop()
  }

}
