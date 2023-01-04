package com.raquo.laminar.defs.attrs

import com.raquo.laminar.keys.HtmlAttr
import com.raquo.laminar.codecs._

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait HtmlAttrs {


  /**
    * Create HTML attribute (Note: for SVG attrs, use L.svg.svgAttr)
    * 
    * @param key   - name of the attribute, e.g. "value"
    * @param codec - used to encode V into String, e.g. StringAsIsCodec
    * 
    * @tparam V    - value type for this attr in Scala
    */
  def htmlAttr[V](key: String, codec: Codec[V, String]): HtmlAttr[V] = new HtmlAttr(key, codec)


  @inline protected def boolAsOnOffHtmlAttr(key: String): HtmlAttr[Boolean] = htmlAttr(key, BooleanAsOnOffStringCodec)

  @inline protected def boolAsTrueFalseHtmlAttr(key: String): HtmlAttr[Boolean] = htmlAttr(key, BooleanAsTrueFalseStringCodec)

  @inline protected def intHtmlAttr(key: String): HtmlAttr[Int] = htmlAttr(key, IntAsStringCodec)

  @inline protected def stringHtmlAttr(key: String): HtmlAttr[String] = htmlAttr(key, StringAsIsCodec)



  /**
    * Declares the character encoding of the page or script. Used on meta and
    * script elements.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/object#attr-charset
    */
  lazy val charset: HtmlAttr[String] = stringHtmlAttr("charset")


  /**
    * Indicates whether the element should be editable by the user.
    * If so, the browser modifies its widget to allow editing.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/contentEditable
    */
  lazy val contentEditable: HtmlAttr[Boolean] = boolAsTrueFalseHtmlAttr("contenteditable")


  /**
    * Specifies a context menu for an element by its element id.
    * The context menu appears when a user right-clicks on the element
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/contextmenu
    */
  lazy val contextMenuId: HtmlAttr[String] = stringHtmlAttr("contextmenu")


  /**
    * Specifies whether the dragged data is copied, moved, or linked, when dropped
    * Acceptable values: `copy` | `move` | `link`
    */
  lazy val dropZone: HtmlAttr[String] = stringHtmlAttr("dropzone")


  /** The form attribute specifies an ID of the form an `<input>` element belongs to. */
  lazy val formId: HtmlAttr[String] = stringHtmlAttr("form")


  /**
    * The `height` attribute specifies the pixel height of the following elements:
    * `<canvas>, <embed>, <iframe>, <img>, <input type="image">, <object>, <video>`
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/object#attr-height
    */
  lazy val heightAttr: HtmlAttr[Int] = intHtmlAttr("height")


  /**
    * Identifies a list of pre-defined options to suggest to the user. The value must be
    * the id of a [[FormTags.dataList]] element in
    * the same document. The browser displays only options that are valid values for this
    * input element. This attribute is ignored when the type attribute's value is hidden,
    * checkbox, radio, file, or a button type.
    */
  lazy val listId: HtmlAttr[String] = stringHtmlAttr("list")


  /**
    * The max attribute specifies the maximum value for an <input> element of type
    * number, range, date, datetime, datetime-local, month, time, or week.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/max
    */
  lazy val maxAttr: HtmlAttr[String] = stringHtmlAttr("max")


  /**
    * The min attribute specifies the minimum value for an <input> element of type
    * number, range, date, datetime, datetime-local, month, time, or week.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/min
    */
  lazy val minAttr: HtmlAttr[String] = stringHtmlAttr("min")


  /**
    * The step attribute specifies the numeric intervals for an <input> element
    * that should be considered legal for the input. For example, if step is 2
    * on a number typed <input> then the legal numbers could be -2, 0, 2, 4, 6
    * etc. The step attribute should be used in conjunction with the min and
    * max attributes to specify the full range and interval of the legal values.
    * The step attribute is applicable to <input> elements of the following
    * types: number, range, date, datetime, datetime-local, month, time and week.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/step
    */
  lazy val stepAttr: HtmlAttr[String] = stringHtmlAttr("step")


  /**
    * This attribute is used to define the type of the content linked to. The
    * value of the attribute should be a MIME type such as text/html, text/css,
    * and so on. The common use of this attribute is to define the type of style
    * sheet linked and the most common current value is text/css, which indicates
    * a Cascading Style Sheet format. You can use tpe as an alias for this
    * attribute so you don't have to backtick-escape this attribute.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/object#attr-type
    */
  lazy val `type`: HtmlAttr[String] = stringHtmlAttr("type")


  lazy val typ: HtmlAttr[String] = `type`


  lazy val tpe: HtmlAttr[String] = `type`


  /** IE-specific property to prevent user selection */
  lazy val unselectable: HtmlAttr[Boolean] = boolAsOnOffHtmlAttr("unselectable")


  /**
    * The `width` attribute specifies the pixel width of the following elements:
    * `<canvas>, <embed>, <iframe>, <img>, <input type="image">, <object>, <video>`
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/object#attr-width
    */
  lazy val widthAttr: HtmlAttr[Int] = intHtmlAttr("width")


}
