package com.raquo.laminar.receivers

import com.raquo.domtypes.generic.keys.Attr
import com.raquo.laminar.setters.AttrSetter
import com.raquo.xstream.XStream

class AttrReceiver[V](val attr: Attr[V]) extends AnyVal {

  @inline def <--($value: XStream[V]): AttrSetter[V] = {
    new AttrSetter(attr, $value)
  }
}
