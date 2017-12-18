package com.raquo.laminar.receivers

import com.raquo.domtypes.generic.keys.Prop
import com.raquo.laminar.setters.PropSetter
import com.raquo.xstream.XStream

class PropReceiver[V, DomV](val prop: Prop[V, DomV]) extends AnyVal {

  @inline def <--($value: XStream[V]): PropSetter[V, DomV] = {
    new PropSetter(prop, $value)
  }
}
