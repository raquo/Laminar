package com.raquo.laminar.receivers

import com.raquo.domtypes.generic.keys.Attr
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.xstream.XStream
import org.scalajs.dom

/** An Attribute receiver that is locked to a particular element */
class LockedAttrReceiver[V](
  attr: Attr[V],
  element: ReactiveElement[dom.Element]
) {

  def <--($value: XStream[V]): Unit = {
    (new AttrReceiver(attr) <-- $value)(element)
  }
}
