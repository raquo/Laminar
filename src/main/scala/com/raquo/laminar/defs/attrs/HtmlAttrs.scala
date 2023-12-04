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
    * @param name  - name of the attribute, e.g. "value"
    * @param codec - used to encode V into String, e.g. StringAsIsCodec
    * 
    * @tparam V    - value type for this attr in Scala
    */
  def htmlAttr[V](name: String, codec: Codec[V, String]): HtmlAttr[V] = new HtmlAttr(name, codec)


  @inline protected def boolAsOnOffHtmlAttr(name: String): HtmlAttr[Boolean] = htmlAttr(name, BooleanAsOnOffStringCodec)

  @inline protected def boolAsTrueFalseHtmlAttr(name: String): HtmlAttr[Boolean] = htmlAttr(name, BooleanAsTrueFalseStringCodec)

  @inline protected def intHtmlAttr(name: String): HtmlAttr[Int] = htmlAttr(name, IntAsStringCodec)

  @inline protected def stringHtmlAttr(name: String): HtmlAttr[String] = htmlAttr(name, StringAsIsCodec)



  /**
    * Declares the character encoding of the page or script. Used on meta and
    * script elements.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/meta#charset meta#charset @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/script#charset script#charset @ MDN]]
    */
  lazy val charset: HtmlAttr[String] = stringHtmlAttr("charset")


  /**
    * Indicates whether the element should be editable by the user.
    * If so, the browser modifies its widget to allow editing.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/contentEditable contentEditable @ MDN]]
    */
  lazy val contentEditable: HtmlAttr[Boolean] = boolAsTrueFalseHtmlAttr("contenteditable")


  /**
    * Specifies a context menu for an element by its element id.
    * The context menu appears when a user right-clicks on the element
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/contextmenu contextmenu @ MDN]]
    */
  lazy val contextMenuId: HtmlAttr[String] = stringHtmlAttr("contextmenu")


  /**
    * Specifies whether the dragged data is copied, moved, or linked, when dropped
    * Acceptable values: `copy` | `move` | `link`
    */
  lazy val dropZone: HtmlAttr[String] = stringHtmlAttr("dropzone")


  /**
    * The `formaction` attribute provides the URL that will process the input control 
    * when the form is submitted and overrides the default `action` attribute of the 
    * `form` element. This should be used only with `input` elements of `type` 
    * submit or image.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#formaction input#formaction @ MDN]]
    */
  lazy val formAction: HtmlAttr[String] = stringHtmlAttr("formaction")


  /** The form attribute specifies an ID of the form an `<input>` element belongs to. */
  lazy val formId: HtmlAttr[String] = stringHtmlAttr("form")


  /**
    * The `height` attribute specifies the pixel height of the following elements:
    * `<canvas>, <embed>, <iframe>, <img>, <input type="image">, <object>, <video>`
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/object#height object#height @ MDN]]
    */
  lazy val heightAttr: HtmlAttr[Int] = intHtmlAttr("height")


  /**
    * This is the single required attribute for anchors defining a hypertext
    * source link. It indicates the link target, either a URL or a URL fragment.
    * A URL fragment is a name preceded by a hash mark (#), which specifies an
    * internal target location (an ID) within the current document. URLs are not
    * restricted to Web (HTTP)-based documents. URLs might use any protocol
    * supported by the browser. For example, file, ftp, and mailto work in most
    * user agents.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a#href a#href @ MDN]]
    */
  lazy val href: HtmlAttr[String] = stringHtmlAttr("href")


  /**
    * Identifies a list of pre-defined options to suggest to the user. The value must be
    * the id of a [[FormTags.dataList]] element in
    * the same document. The browser displays only options that are valid values for this
    * input element. This attribute is ignored when the type attribute's value is hidden,
    * checkbox, radio, file, or a button type.
    */
  lazy val listId: HtmlAttr[String] = stringHtmlAttr("list")


  /**
    * The max attribute specifies the maximum value for an `<input>` element of type
    * number, range, date, datetime, datetime-local, month, time, or week.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/max max @ MDN]]
    */
  lazy val maxAttr: HtmlAttr[String] = stringHtmlAttr("max")


  /**
    * The min attribute specifies the minimum value for an `<input>` element of type
    * number, range, date, datetime, datetime-local, month, time, or week.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/min min @ MDN]]
    */
  lazy val minAttr: HtmlAttr[String] = stringHtmlAttr("min")


  /**
    * Specifies the URL of an image for `<img>` tag, for `type="image"` input buttons, 
    * or the URL of some other network resources like `<iframe>`.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/img#src img#src @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#src input#src @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/iframe#src iframe#src @ MDN]]
    */
  lazy val src: HtmlAttr[String] = stringHtmlAttr("src")


  /**
    * The step attribute specifies the numeric intervals for an `<input>` element
    * that should be considered legal for the input. For example, if step is 2
    * on a number typed `<input>` then the legal numbers could be -2, 0, 2, 4, 6
    * etc. The step attribute should be used in conjunction with the min and
    * max attributes to specify the full range and interval of the legal values.
    * The step attribute is applicable to `<input>` elements of the following
    * types: number, range, date, datetime, datetime-local, month, time and week.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/step step @ MDN]]
    */
  lazy val stepAttr: HtmlAttr[String] = stringHtmlAttr("step")


  /**
    * This attribute has several meanings depending on what element it's applied to. 
    * It could indicate the type of a button, an input, a script, a stylesheet, etc.
    * 
    * Aliases: [[typ]], [[tpe]]
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#input_types input#input_types @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/button#type button#type @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/object#type object#type @ MDN]]
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
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/object#width object#width @ MDN]]
    */
  lazy val widthAttr: HtmlAttr[Int] = intHtmlAttr("width")


}
