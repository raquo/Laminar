package com.raquo.laminar.defs.complex

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.keys
import com.raquo.laminar.keys.{CompositeAttr, HtmlAttr}
import com.raquo.laminar.keys.CompositeAttr.HtmlCompositeAttr
import com.raquo.laminar.nodes.ReactiveHtmlElement

trait ComplexHtmlKeys extends ComplexGlobalKeys {

  // #Note: we use attrs instead of props here because of https://github.com/raquo/Laminar/issues/136

  /**
    * This attribute contains the part name(s) which allow CSS to select and style
    * specific elements in a shadow tree via the ::part pseudo-element.
    *
    * See also: [[exportParts]]
    *
    * See: [[https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Global_attributes/part @ MDN]]
    */
  lazy val partAttr: HtmlCompositeAttr = compositeHtmlAttr("part", separator = " ")

  /**
    * This attribute allows you to select and style elements existing in nested
    * shadow trees, by exporting their part names.
    *
    * Part name(s) are defined with [[partAttr]] when authoring the Web Component.
    *
    * See: [[https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Global_attributes/exportparts @ MDN]]
    */
  lazy val exportParts: HtmlCompositeAttr = compositeHtmlAttr("exportparts", separator = ",")

  /**
    * This attribute names a relationship of the linked document to the current
    * document. The attribute must be a space-separated list of the link types
    * values. The most common use of this attribute is to specify a link to an
    * external style sheet: the rel attribute is set to stylesheet, and the href
    * attribute is set to the URL of an external style sheet to format the page.
    *
    * See: [[https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Attributes/rel @ MDN]]
    */
  lazy val rel: HtmlCompositeAttr = compositeHtmlAttr("rel", separator = " ")

  /**
    * This attribute contains CSS styling declarations to be applied to the
    * element. Note that it is recommended for styles to be defined in a separate
    * file or files. This attribute and the style element have mainly the
    * purpose of allowing for quick styling, for example for testing purposes.
    */
  lazy val styleAttr: HtmlAttr[String] = new HtmlAttr("style", Codec.stringAsIs)

  // --

  protected def compositeHtmlAttr(name: String, separator: String): HtmlCompositeAttr = {
    new CompositeAttr[ReactiveHtmlElement.Base](name, separator)
  }
}

object ComplexHtmlKeys {

  @deprecated("CompositeHtmlProp is dropped for lack of need in favor of HtmlCompositeAttr (available on api.L.)", "18.0.0-M1")
  type CompositeHtmlProp = keys.CompositeAttr.HtmlCompositeAttr

  @deprecated("CompositeHtmlAttr type was moved to com.raquo.laminar.keys.CompositeAttr.HtmlCompositeAttr (also available on L.)", "18.0.0-M1")
  type CompositeHtmlAttr = keys.CompositeAttr.HtmlCompositeAttr
}
