package com.raquo.laminar.experimental.airstream.eventstream

import com.raquo.laminar.experimental.airstream.core.SyncObservable

class SyncEventStream[A](
  override protected[this] val parent: EventStream[A],
  override val isSoft: Boolean
) extends EventStream[A] with SyncObservable[A]
