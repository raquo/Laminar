package com.raquo.laminar.emitter

import com.raquo.xstream.MemoryStream

class ReactiveVar[A](initialValue: A) extends EventBus[A] {

  // @TODO[Integrity] Is this "$ = $" ok? We're essentially getting `super.$` this way, but scala does not allow that explicitly. Looks sketchy...
  override val $: MemoryStream[A] = $.startWith[A](initialValue)
}
