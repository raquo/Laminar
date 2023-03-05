package com.raquo.laminar.defs.props

import com.raquo.laminar.keys.HtmlProp
import com.raquo.laminar.codecs._

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait HtmlProps {


  /**
    * Create custom HTML element property
    * 
    * @param name  - name of the prop in JS, e.g. "value"
    * @param codec - used to encode V into DomV, e.g. StringAsIsCodec,
    * 
    * @tparam V    - value type for this prop in Scala
    * @tparam DomV - value type for this prop in the underlying JS DOM.
    */
  def htmlProp[V, DomV](name: String, codec: Codec[V, DomV]): HtmlProp[V, DomV] = new HtmlProp(name, codec)


  @inline protected def boolProp(name: String): HtmlProp[Boolean, Boolean] = htmlProp(name, BooleanAsIsCodec)

  @inline protected def doubleProp(name: String): HtmlProp[Double, Double] = htmlProp(name, DoubleAsIsCodec)

  @inline protected def intProp(name: String): HtmlProp[Int, Int] = htmlProp(name, IntAsIsCodec)

  @inline protected def stringProp(name: String): HtmlProp[String, String] = htmlProp(name, StringAsIsCodec)



  // -- Props --


  /**
    * In addition to the checked and unchecked states, there is a third state
    * a checkbox can be in: indeterminate. This is a state in which it's
    * impossible to say whether the item is toggled on or off.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/checkbox#Indeterminate_state_checkboxes
    */
  lazy val indeterminate: HtmlProp[Boolean, Boolean] = boolProp("indeterminate")


  /**
    * When the value of the type attribute is "radio" or "checkbox", this property
    * determines whether it is checked or not.
    * This is different from `checked` _attribute_,
    * which contains the _initial_ checked status of the element.
    * More info: https://stackoverflow.com/a/6004028/2601788 (`checked` behaves similar to `value`)
    * 
    * See also: defaultChecked prop / attribute
    */
  lazy val checked: HtmlProp[Boolean, Boolean] = boolProp("checked")


  /**
    * Indicates whether an `<option>` element is _currently_ selected.
    * This is different from `selected` _attribute_,
    * which contains the _initial_ selected status of the element.
    * More info: https://stackoverflow.com/a/6004028/2601788 (`selected` behaves similar to `value`)
    * 
    * See also: defaultSelected prop / attribute
    */
  lazy val selected: HtmlProp[Boolean, Boolean] = boolProp("selected")


  /**
    * Current value of the element. This is different from `value` _attribute_,
    * which contains the _initial_ value of the element.
    * More info: https://stackoverflow.com/a/6004028/2601788
    * 
    * See also: defaultValue prop / attribute
    */
  lazy val value: HtmlProp[String, String] = stringProp("value")


  // -- Reflected Attributes --


  /**
    * If the value of the type attribute is file, this attribute indicates the
    * types of files that the server accepts; otherwise it is ignored.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/accept
    */
  lazy val accept: HtmlProp[String, String] = stringProp("accept")


  /**
    * The URI of a program that processes the information submitted via the form.
    * This value can be overridden by a [[formAction]] attribute on a button or
    * input element.
    * 
    * 
    * Only applies to [[FormTags.form]]
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/action
    */
  lazy val action: HtmlProp[String, String] = stringProp("action")


  /**
    * Specifies a shortcut key to activate/focus an element
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/accesskey
    */
  lazy val accessKey: HtmlProp[String, String] = stringProp("accessKey")


  /**
    * This attribute defines the alternative text describing the image. Users
    * will see this displayed if the image URL is wrong, the image is not in one
    * of the supported formats, or until the image is downloaded.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/alt
    */
  lazy val alt: HtmlProp[String, String] = stringProp("alt")


  /**
    * This is a nonstandard attribute used by Chrome and iOS Safari Mobile, which
    * controls whether and how the text value should be automatically capitalized
    * as it is entered/edited by the user.
    * 
    * Possible values: "none" | "sentences" | "words" | "characters"
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/autocapitalize
    */
  lazy val autoCapitalize: HtmlProp[String, String] = stringProp("autocapitalize")


  /**
    * This attribute indicates whether the value of the control can be
    * automatically completed by the browser. This attribute is ignored if the
    * value of the type attribute is hidden, checkbox, radio, file, or a button
    * type (button, submit, reset, image).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/autocomplete
    */
  lazy val autoComplete: HtmlProp[String, String] = stringProp("autocomplete")


  /**
    * This Boolean attribute lets you specify that a form control should have
    * input focus when the page loads, unless the user overrides it, for example
    * by typing in a different control. Only one form element in a document can
    * have the autofocus attribute, which is a Boolean. It cannot be applied if
    * the type attribute is set to hidden (that is, you cannot automatically set
    * focus to a hidden control).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/autofocus
    */
  lazy val autoFocus: HtmlProp[Boolean, Boolean] = boolProp("autofocus")


  /**
    * The visible width of text input or `<textArea>`, in average character widths.
    * If it is specified, it must be a positive integer.
    * If it is not specified, the default value is 20 (HTML5).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/textarea#attr-cols
    */
  lazy val cols: HtmlProp[Int, Int] = intProp("cols")


  /**
    * This attribute contains a non-negative integer value that indicates for
    * how many columns the cell extends. Its default value is 1; if its value
    * is set to 0, it extends until the end of the `<colgroup>`, even if implicitly
    * defined, that the cell belongs to. Values higher than 1000 will be considered
    * as incorrect and will be set to the default value (1).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/td#attr-colspan
    */
  lazy val colSpan: HtmlProp[Int, Int] = intProp("colSpan")


  /**
    * This attribute gives the value associated with the [[name]] or [[httpEquiv]] attribute,
    * of a `<meta>` element, depending on which of those attributes is defined on that element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/meta#attr-content
    */
  lazy val contentAttr: HtmlProp[String, String] = stringProp("content")


  /**
    * When the value of the type attribute is "radio" or "checkbox", the presence of
    * this Boolean attribute indicates that the control is selected **by default**;
    * otherwise it is ignored.
    * 
    * See [[Props.checked]]
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/checkbox#attr-checked
    */
  lazy val defaultChecked: HtmlProp[Boolean, Boolean] = boolProp("defaultChecked")


  /**
    * Indicates whether this `<option>` is initially selected
    * in an option list of a `<select>` element.
    * 
    * See [[Props.selected]]
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/option#attr-selected
    */
  lazy val defaultSelected: HtmlProp[Boolean, Boolean] = boolProp("defaultSelected")


  /**
    * The initial value of the control. This attribute is optional except when
    * the value of the type attribute is radio or checkbox.
    * 
    * See also [[Props.value]]
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#value
    */
  lazy val defaultValue: HtmlProp[String, String] = stringProp("defaultValue")


  /**
    * Specifies the text direction for the content in an element. The valid values are:
    * 
    * - `ltr` Default. Left-to-right text direction
    * 
    * - `rtl` Right-to-left text direction
    * 
    * - `auto` Let the browser figure out the text direction, based on the content,
    *          (only recommended if the text direction is unknown)
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/dir
    */
  lazy val dir: HtmlProp[String, String] = stringProp("dir")


  /**
    * This Boolean attribute indicates that the form control is not available for
    * interaction. In particular, the click event will not be dispatched on
    * disabled controls. Also, a disabled control's value isn't submitted with
    * the form.
    * 
    * This attribute is ignored if the value of the type attribute is hidden.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/disabled
    */
  lazy val disabled: HtmlProp[Boolean, Boolean] = boolProp("disabled")


  /**
    * Prompts the user to save the linked URL instead of navigating to it. Can be used with or without a value:
    * 
    * - Without a value, the browser will suggest a filename/extension, generated from various sources:
    *   - The Content-Disposition HTTP header
    *   - The final segment in the URL path
    *   - The media type (from the Content-Type header, the start of a data: URL, or Blob.type for a blob: URL)
    * - Defining a value suggests it as the filename. / and \ characters are converted to underscores (_). Filesystems may forbid other characters in filenames, so browsers will adjust the suggested name if necessary.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a#attr-download
    */
  lazy val download: HtmlProp[String, String] = stringProp("download")


  /**
    * Specifies whether an element is draggable or not
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/draggable
    */
  lazy val draggable: HtmlProp[Boolean, Boolean] = boolProp("draggable")


  /**
    * The `enctype` attribute provides the encoding type of the form when it is
    * submitted (for forms with a method of "POST").
    * 
    * Only applies to [[FormTags.form]]
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/form#attr-enctype
    */
  lazy val encType: HtmlProp[String, String] = stringProp("enctype")


  /**
    * Describes an element which belongs to this one. Used on labels and output elements.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/for
    */
  lazy val forId: HtmlProp[String, String] = stringProp("htmlFor")


  /**
    * The `formenctype` attribute provides the encoding type of the form when it is
    * submitted (for forms with a method of "POST") and overrides the default
    * `enctype` attribute of the `form` element. This should be used only with the
    * `input` elements of `type` "submit" or "image"
    * 
    * Enumerated: "multipart/form-data" | "text/plain" | "application/x-www-form-urlencoded" (default)
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#formenctype
    */
  lazy val formEncType: HtmlProp[String, String] = stringProp("formEnctype")


  /**
    * The `formmethod` attribute specifies the HTTP Method the form should use when
    * it is submitted and overrides the default `method` attribute of the `form`
    * element. This should be used only with the `input` elements of `type` "submit"
    * or "image".
    * 
    * Enumerated: "post" | "get"
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#formmethod
    */
  lazy val formMethod: HtmlProp[String, String] = stringProp("formMethod")


  /**
    * The `formnovalidate` Boolean attribute specifies that the input of the form
    * should not be validated upon submit and overrides the default `novalidate`
    * attribute of the `form`. This should only be used with `input` elements of
    * of `type` "submit".
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#formnovalidate
    */
  lazy val formNoValidate: HtmlProp[Boolean, Boolean] = boolProp("formNoValidate")


  /**
    * The `formtarget` provides a name or keyword that indicates where to display
    * the response that is received after submitting the form and overrides the
    * `target` attribute of them `form` element. This should only be used with
    * the `input` elements of `type` "submit" or "image"
    * 
    * Enumerated: "_blank" | "_parent" | "_top" | "_self" (default)
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#formtarget
    */
  lazy val formTarget: HtmlProp[String, String] = stringProp("formTarget")


  /**
    * Specifies that an element is not yet, or is no longer, relevant and
    * consequently hidden from view of the user.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/hidden
    */
  lazy val hidden: HtmlProp[Boolean, Boolean] = boolProp("hidden")


  /**
    * For use in &lt;meter&gt; tags.
    * 
    * @see https://css-tricks.com/html5-meter-element/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/meter#attr-high
    */
  lazy val high: HtmlProp[Double, Double] = doubleProp("high")


  /**
    * This enumerated attribute defines the pragma that can alter servers and
    * user-agents behavior. The value of the pragma is defined using the content
    * attribute and can be one of the following:
    * 
    *   - content-language
    *   - content-type
    *   - default-style
    *   - refresh
    *   - set-cookie
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/meta#attr-http-equiv
    */
  lazy val httpEquiv: HtmlProp[String, String] = stringProp("httpEquiv")


  /**
    * This attribute defines a unique identifier (ID) which must be unique in
    * the whole document. Its purpose is to identify the element when linking
    * (using a fragment identifier), scripting, or styling (with CSS).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/id
    */
  lazy val idAttr: HtmlProp[String, String] = stringProp("id")


  /**
    * The inputmode attribute hints at the type of data that might be entered by
    * the user while editing the element or its contents. This allows a browser
    * to display an appropriate virtual keyboard.
    * 
    * Acceptable values:
    * `none` | `text` (default value) | `decimal` | `numeric` | `tel` | `search` | `email` | `url`
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/inputmode
    */
  lazy val inputMode: HtmlProp[String, String] = stringProp("inputMode")


  /**
    * For `optgroup` elements, specifies the name of the group of options, which the browser can
    * use when labeling the options in the user interface.
    */
  lazy val labelAttr: HtmlProp[String, String] = stringProp("label")


  /**
    * This attribute participates in defining the language of the element, the
    * language that non-editable elements are written in or the language that
    * editable elements should be written in. The tag contains one single entry
    * value in the format defines in the Tags for Identifying Languages (BCP47)
    * IETF document. If the tag content is the empty string the language is set
    * to unknown; if the tag content is not valid, regarding to BCP47, it is set
    * to invalid.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/lang
    */
  lazy val lang: HtmlProp[String, String] = stringProp("lang")


  /**
    * Indicates how the browser should load the image:
    * 
    * "eager": Loads the image immediately, regardless of whether or not the image is currently
    * within the visible viewport (this is the default value).
    * 
    * "lazy": Defers loading the image until it reaches a calculated distance from the viewport,
    * as defined by the browser. The intent is to avoid the network and storage bandwidth needed
    * to handle the image until it's reasonably certain that it will be needed. This generally
    * improves the performance of the content in most typical use cases.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/img#attr-loading
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/iframe#attr-loading
    */
  lazy val loadingAttr: HtmlProp[String, String] = stringProp("loading")


  /**
    * For use in &lt;meter&gt; tags.
    * 
    * @see https://css-tricks.com/html5-meter-element/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/meter#attr-low
    */
  lazy val low: HtmlProp[Double, Double] = doubleProp("low")


  /**
    * If the type of the input element is text, email, search, password, tel, or
    * url, this attribute specifies the minimum number of characters (in Unicode
    * code points) that the user can enter. For other control types, it is ignored.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/minlength
    */
  lazy val minLength: HtmlProp[Int, Int] = intProp("minLength")


  /**
    * The maximum allowed length for the input field. This attribute forces the input control
    * to accept no more than the allowed number of characters. It does not produce any
    * feedback to the user; you must write Javascript to make that happen.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/maxlength
    */
  lazy val maxLength: HtmlProp[Int, Int] = intProp("maxLength")


  /**
    * This attribute specifies the media which the linked resource applies to.
    * Its value must be a media query. This attribute is mainly useful when
    * linking to external stylesheets by allowing the user agent to pick
    * the best adapted one for the device it runs on.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/link#attr-media
    */
  lazy val media: HtmlProp[String, String] = stringProp("media")


  /**
    * The HTTP method that the browser uses to submit the form. Possible values are:
    * 
    * - post: Corresponds to the HTTP POST method ; form data are included in the
    *   body of the form and sent to the server.
    * 
    * - get: Corresponds to the HTTP GET method; form data are appended to the
    *   action attribute URI with a '?' as a separator, and the resulting URI is
    *   sent to the server. Use this method when the form has no side-effects and
    *   contains only ASCII characters.
    * 
    * This value can be overridden by a formmethod attribute on a button or
    * input element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/form#attr-method
    */
  lazy val method: HtmlProp[String, String] = stringProp("method")


  /**
    * This Boolean attribute specifies, when present/true, that the user is allowed
    * to enter more than one value for the `<input>` element for types "email" or "file".
    * It can also be provided to the `<select>` element to allow selecting more than one
    * option.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#multiple
    */
  lazy val multiple: HtmlProp[Boolean, Boolean] = boolProp("multiple")


  /**
    * On form elements (input etc.):
    *   Name of the element. For example used by the server to identify the fields
    *   in form submits.
    * 
    * On the meta tag:
    *   This attribute defines the name of a document-level metadata.
    *   This document-level metadata name is associated with a value, contained by
    *   the content attribute.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#name
    */
  lazy val nameAttr: HtmlProp[String, String] = stringProp("name")


  /**
    * This Boolean attribute indicates that the form is not to be validated when
    * submitted. If this attribute is not specified (and therefore the form is
    * validated), this default setting can be overridden by a formnovalidate
    * attribute on a `<button>` or `<input>` element belonging to the form.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/form#attr-novalidate
    */
  lazy val noValidate: HtmlProp[Boolean, Boolean] = boolProp("noValidate")


  /**
    * For use in &lt;meter&gt; tags.
    * 
    * @see https://css-tricks.com/html5-meter-element/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/meter#attr-optimum
    */
  lazy val optimum: HtmlProp[Double, Double] = doubleProp("optimum")


  /**
    * Specifies a regular expression to validate the input. The pattern attribute
    * works with the following input types: text, search, url, tel, email, and
    * password. Use the `title` attribute to describe the pattern to the user.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/pattern
    */
  lazy val pattern: HtmlProp[String, String] = stringProp("pattern")


  /**
    * A hint to the user of what can be entered in the control. The placeholder
    * text must not contain carriage returns or line-feeds. This attribute
    * applies when the value of the type attribute is text, search, tel, url or
    * email; otherwise it is ignored.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#placeholder
    */
  lazy val placeholder: HtmlProp[String, String] = stringProp("placeholder")


  /**
    * This Boolean attribute indicates that the user cannot modify the value of
    * the control. This attribute is ignored if the value of the type attribute
    * is hidden, range, color, checkbox, radio, file, or a button type.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/readonly
    */
  lazy val readOnly: HtmlProp[Boolean, Boolean] = boolProp("readOnly")


  /**
    * This attribute specifies that the user must fill in a value before
    * submitting a form. It cannot be used when the type attribute is hidden,
    * image, or a button type (submit, reset, or button). The :optional and
    * :required CSS pseudo-classes will be applied to the field as appropriate.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/required
    */
  lazy val required: HtmlProp[Boolean, Boolean] = boolProp("required")


  /**
    * The number of visible text lines for a text control.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/textarea#attr-rows
    */
  lazy val rows: HtmlProp[Int, Int] = intProp("rows")


  /**
    * This attribute contains a non-negative integer value that indicates for how many
    * rows the cell extends. Its default value is 1; if its value is set to 0, it extends
    * until the end of the table section (`<thead>`, `<tbody>`, `<tfoot>`, even if implicitly
    * defined, that the cell belongs to. Values higher than 65534 are clipped down to 65534.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/td#attr-rowspan
    */
  lazy val rowSpan: HtmlProp[Int, Int] = intProp("rowSpan")


  /**
    * For use in &lt;style&gt; tags.
    * 
    * If this attribute is present, then the style applies only to its parent element.
    * If absent, the style applies to the whole document.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLStyleElement/scoped
    */
  lazy val scoped: HtmlProp[Boolean, Boolean] = boolProp("scoped")


  /**
    * The initial size of the control. This value is in pixels unless the value
    * of the type attribute is text or password, in which case, it is an integer
    * number of characters. Starting in HTML5, this attribute applies only when
    * the type attribute is set to text, search, tel, url, email, or password;
    * otherwise it is ignored. In addition, the size must be greater than zero.
    * If you don't specify a size, a default value of 20 is used.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/size
    */
  lazy val size: HtmlProp[Int, Int] = intProp("size")


  /**
    * The slot global attribute assigns a slot in a shadow DOM shadow tree to an element.
    * 
    * MDN – https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/slot
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/slot
    */
  lazy val slot: HtmlProp[String, String] = stringProp("slot")


  /**
    * Defines whether the element may be checked for spelling errors.
    * 
    * MDN – https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/spellcheck
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/spellcheck
    */
  lazy val spellCheck: HtmlProp[Boolean, Boolean] = boolProp("spellcheck")


  /**
    * This integer attribute indicates if the element can take input focus (is
    * focusable), if it should participate to sequential keyboard navigation, and
    * if so, at what position. It can takes several values:
    * 
    * - a negative value means that the element should be focusable, but should
    *   not be reachable via sequential keyboard navigation;
    * - 0 means that the element should be focusable and reachable via sequential
    *   keyboard navigation, but its relative order is defined by the platform
    *   convention;
    * - a positive value which means should be focusable and reachable via
    *   sequential keyboard navigation; its relative order is defined by the value
    *   of the attribute: the sequential follow the increasing number of the
    *   tabindex. If several elements share the same tabindex, their relative order
    *   follows their relative position in the document).
    * 
    * An element with a 0 value, an invalid value, or no tabindex value should be placed after elements with a positive tabindex in the sequential keyboard navigation order.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/tabindex
    */
  lazy val tabIndex: HtmlProp[Int, Int] = intProp("tabIndex")


  /**
    * A name or keyword indicating where to display the response that is received
    * after submitting the form. In HTML 4, this is the name of, or a keyword
    * for, a frame. In HTML5, it is a name of, or keyword for, a browsing context
    * (for example, tab, window, or inline frame). The following keywords have
    * special meanings:
    * 
    * - _self: Load the response into the same HTML 4 frame (or HTML5 browsing
    *   context) as the current one. This value is the default if the attribute
    *   is not specified.
    * - _blank: Load the response into a new unnamed HTML 4 window or HTML5
    *   browsing context.
    * - _parent: Load the response into the HTML 4 frameset parent of the current
    *   frame or HTML5 parent browsing context of the current one. If there is no
    *   parent, this option behaves the same way as _self.
    * - _top: HTML 4: Load the response into the full, original window, canceling
    *   all other frames. HTML5: Load the response into the top-level browsing
    *   context (that is, the browsing context that is an ancestor of the current
    *   one, and has no parent). If there is no parent, this option behaves the
    *   same way as _self.
    * - iframename: The response is displayed in a named iframe.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a#attr-target
    */
  lazy val target: HtmlProp[String, String] = stringProp("target")


  /**
    * This attribute contains a text representing advisory information related to
    * the element it belongs too. Such information can typically, but not
    * necessarily, be presented to the user as a tooltip.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/title
    */
  lazy val title: HtmlProp[String, String] = stringProp("title")


  /**
    * Specifies whether the content of an element should be translated or not
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/translate
    */
  lazy val translate: HtmlProp[Boolean, Boolean] = boolProp("translate")


  /** Specifies XML namespace for the document */
  lazy val xmlns: HtmlProp[String, String] = stringProp("xmlns")


}
