package com.raquo.laminar.receivers

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.keys.Attr
import com.raquo.laminar.setters.AttrSetter

class AttrReceiver[V](val attr: Attr[V]) extends AnyVal {

  @inline def <--($value: Observable[V]): AttrSetter[V] = {
    new AttrSetter(attr, $value)
  }
}
