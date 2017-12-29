package com.raquo.laminar.experimental.airstream.stream

import com.raquo.laminar.experimental.airstream.observation.Observer

/** Stream that combines the latest values from two streams into a tuple.
  * Only fires after both streams have sent a value.
  *
  * Note: this stream forgets any previous values of parent streams once it's stopped
  */
class CombineStream2[A, B](
  parent1: Stream[A],
  parent2: Stream[B]
) extends Stream[(A, B)] {

  private[this] var maybeLastParent1Value: Option[A] = None
  private[this] var maybeLastParent2Value: Option[B] = None

  private[this] lazy val parent1Observer = Observer[A](nextParent1Value => {
    maybeLastParent1Value = Some(nextParent1Value)
    maybeLastParent2Value.foreach { lastParent2Value =>
      fire((nextParent1Value, lastParent2Value))
    }
  })

  private[this] lazy val parent2Observer = Observer[B](nextParent2Value => {
    maybeLastParent2Value = Some(nextParent2Value)
    maybeLastParent1Value.foreach { lastParent1Value =>
      fire((lastParent1Value, nextParent2Value))
    }
  })

  override protected[this] def onStart(): Unit = {
    parent1.addChildObserver(parent1Observer)
    parent2.addChildObserver(parent2Observer)
  }

  override protected[this] def onStop(): Unit = {
    maybeLastParent1Value = None
    maybeLastParent2Value = None
    parent1.removeChildObserver(parent1Observer)
    parent2.removeChildObserver(parent2Observer)
  }

}
