package com.raquo.laminar.setters

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.SvgAttr
import com.raquo.laminar.DomApi
import com.raquo.laminar.nodes.ReactiveSvgElement
import org.scalajs.dom

class SvgAttrSetter[V](
  val attr: SvgAttr[V],
  $value: Observable[V]
) extends Modifier[ReactiveSvgElement[dom.svg.Element]] {

  override def apply(element: ReactiveSvgElement[dom.svg.Element]): Unit = {
    element.subscribe(
      $value,
      (value: V) => DomApi.svgElementApi.setSvgAttribute(element, attr, value)
    )
  }
}
