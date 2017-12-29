package com.raquo.laminar.experimental.airstream.stream

/** Stream that combines the latest values from two streams into a tuple.
  * Only fires after both streams have sent a value.
  *
  * Note: this stream forgets any previous values of parent streams once it's stopped
  */
class CombineStream2[A, B](
  parent1: Stream[A],
  parent2: Stream[B]
) extends Stream[(A, B)] {

  var maybeLastParent1Value: Option[A] = None
  var maybeLastParent2Value: Option[B] = None

  override def onStart(): Unit = {
    parent1.onChildStarted(parent1Action)
    parent2.onChildStarted(parent2Action)
  }

  override def onStop(): Unit = {
    maybeLastParent1Value = None
    maybeLastParent2Value = None
    parent1.onChildStopped(parent1Action)
    parent2.onChildStopped(parent2Action)
  }

  val parent1Action: A => Unit = { nextParent1Value =>
    maybeLastParent1Value = Some(nextParent1Value)
    maybeLastParent2Value.foreach { lastParent2Value =>
      fire((nextParent1Value, lastParent2Value))
    }
  }

  val parent2Action: B => Unit = { nextParent2Value =>
    maybeLastParent2Value = Some(nextParent2Value)
    maybeLastParent1Value.foreach { lastParent1Value =>
      fire((lastParent1Value, nextParent2Value))
    }
  }

}
