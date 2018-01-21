package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.InternalParentObserver
import com.raquo.laminar.experimental.airstream.features.CombineObservable

/** Stream that combines the latest values from two streams into a tuple.
  * Only fires after both streams have sent a value.
  *
  * Note: this stream forgets any previous values of parent streams once it's stopped
  */
class CombineEventStream2[A, B, O](
  parent1: EventStream[A],
  parent2: EventStream[B],
  combinator: (A, B) => O
) extends EventStream[O] with CombineObservable[O] {

  override protected[airstream] val topoRank: Int = (parent1.topoRank max parent2.topoRank) + 1

  private[this] var maybeLastParent1Value: Option[A] = None
  private[this] var maybeLastParent2Value: Option[B] = None

  parentObservers.push(
    InternalParentObserver[A](parent1, (nextParent1Value, transaction) => {
      // println(s"> updated p1 value to $nextParent1Value")
      maybeLastParent1Value = Some(nextParent1Value)
      maybeLastParent2Value.foreach { lastParent2Value =>
        internalObserver.onNext(combinator(nextParent1Value, lastParent2Value), transaction)
      }
    }),
    InternalParentObserver[B](parent2, (nextParent2Value, transaction) => {
      // println(s"> updated p1 value to $nextParent1Value")
      maybeLastParent2Value = Some(nextParent2Value)
      maybeLastParent1Value.foreach { lastParent1Value =>
        internalObserver.onNext(combinator(lastParent1Value, nextParent2Value), transaction)
      }
    })
  )

  override protected[this] def onStop(): Unit = {
    maybeLastParent1Value = None
    maybeLastParent2Value = None
    super.onStop()
  }

}
