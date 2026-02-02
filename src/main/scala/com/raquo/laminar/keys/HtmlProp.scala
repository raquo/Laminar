package com.raquo.laminar.keys

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.nodes.ReactiveHtmlElement

import scala.scalajs.js.|

/**
  * This class represents a DOM Element Property. Meaning the key that can be set, not a key-value pair.
  *
  * Note: following the Javascript DOM Spec, Properties are distinct from Attributes even when they share a name.
  *
  * @tparam V type of values that this Property can be set to
  */
trait HtmlProp[V]
extends SimpleKey[HtmlProp[V], V, ReactiveHtmlElement.Base] { self =>

  /** This prop's type in the native JS DOM */
  type DomV

  val reflectedAttrName: Option[String]

  val codec: Codec[V, DomV]

  override lazy val maybe: HtmlProp[Option[V]] = {
    HtmlProp(name, reflectedAttrName, self.codec.optAsNull)
  }

  override protected def set(el: ReactiveHtmlElement.Base, value: V | Null): Unit = {
    DomApi.setHtmlProperty(el, this, value)
  }
}

object HtmlProp {

  @deprecated("HtmlProp.Of[V] is not needed anymore – use HtmlProp[V]", since = "18.0.0-M1")
  type Of[V] = HtmlProp[V]

  def apply[V, _DomV](
    name: String,
    reflectedAttrName: Option[String],
    codec: Codec[V, _DomV]
  ): HtmlProp[V] { type DomV = _DomV } = {
    if (name == "value") {
      // Special case to fix bug in Safari – see impl.
      new ValueHtmlProp(name, reflectedAttrName, codec)
    } else {
      val _name = name
      val _reflectedAttrName = reflectedAttrName
      val _codec = codec
      new HtmlProp[V] {
        type DomV = _DomV
        override val name: String = _name
        override val reflectedAttrName: Option[String] = _reflectedAttrName
        override val codec: Codec[V, DomV] = _codec
      }
    }
  }

  class ValueHtmlProp[V, _DomV](
    override val name: String,
    override val reflectedAttrName: Option[String],
    override val codec: Codec[V, _DomV]
  ) extends HtmlProp[V] {
    type DomV = _DomV

    override protected def set(el: ReactiveHtmlElement.Base, value: V | Null): Unit = {
      // Deduplicating updates against current DOM value prevents
      // cursor position reset in Safari https://github.com/raquo/Laminar/issues/110
      // #nc[Test] Verify that I didn't break this in Safari (I switched to non-raw DOM API). Also verify that Safari still needs this.
      // #nc[Test] Verify that Safari actually needs this fix.
      if (!DomApi.getHtmlProperty(el, this).contains(value)) {
        DomApi.setHtmlProperty(el, this, value)
      }
    }
  }
}
