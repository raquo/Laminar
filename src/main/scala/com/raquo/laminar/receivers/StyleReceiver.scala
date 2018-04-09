package com.raquo.laminar.receivers

import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Style
import com.raquo.laminar.experimental.airstream.core.Observable
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.setters.StyleSetter
import org.scalajs.dom

import scala.scalajs.js.|

class StyleReceiver[V](val style: Style[V]) extends AnyVal {

  // @TODO[API] this needs better API than `|`
  def <--($value: Observable[V | String]): Modifier[ReactiveHtmlElement[dom.html.Element]] = {
    // @TODO[Integrity] Get rid of .asInstanceOf
    new StyleSetter[V | String](style.asInstanceOf[Style[V | String]], $value)
  }
}
