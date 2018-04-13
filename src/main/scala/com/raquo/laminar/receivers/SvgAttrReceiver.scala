package com.raquo.laminar.receivers

import com.raquo.domtypes.generic.keys.SvgAttr
import com.raquo.laminar.experimental.airstream.core.Observable
import com.raquo.laminar.setters.SvgAttrSetter

class SvgAttrReceiver[V](val attr: SvgAttr[V]) extends AnyVal {

  @inline def <--($value: Observable[V]): SvgAttrSetter[V] = {
    new SvgAttrSetter(attr, $value)
  }
}
