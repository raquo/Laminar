package com.raquo.laminar.receivers

import com.raquo.domtypes.generic.keys.Prop
import com.raquo.laminar.experimental.airstream.core.Observable
import com.raquo.laminar.setters.PropSetter

class PropReceiver[V, DomV](val prop: Prop[V, DomV]) extends AnyVal {

  @inline def <--($value: Observable[V]): PropSetter[V, DomV] = {
    new PropSetter(prop, $value)
  }
}
