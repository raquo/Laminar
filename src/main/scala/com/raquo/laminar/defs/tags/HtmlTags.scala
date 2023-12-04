package com.raquo.laminar.defs.tags

import com.raquo.laminar.tags.HtmlTag
import org.scalajs.dom

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait HtmlTags {


  /**
    * Create HTML tag
    * 
    * Note: this simply creates an instance of HtmlTag.
    *  - This does not create the element (to do that, call .apply() on the returned tag instance)
    *  - This does not register this tag name as a custom element
    *    - See https://developer.mozilla.org/en-US/docs/Web/Web_Components/Using_custom_elements
    * 
    * @param name - e.g. "div" or "mwc-input"
    * 
    * @tparam Ref - type of elements with this tag, e.g. dom.html.Input for "input" tag
    */
  def htmlTag[Ref <: dom.html.Element](name: String, void: Boolean = false): HtmlTag[Ref] = new HtmlTag(name, void)


  // -- Document Tags --


  /**
    * Represents the root of an HTML or XHTML document. All other elements must
    * be descendants of this element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/html html @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLHtmlElement HTMLHtmlElement @ MDN]]
    */
  lazy val htmlRootTag: HtmlTag[dom.HTMLHtmlElement] = htmlTag("html")


  /**
    * Represents a collection of metadata about the document, including links to,
    * or definitions of, scripts and style sheets.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/head head @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadElement HTMLHeadElement @ MDN]]
    */
  lazy val headTag: HtmlTag[dom.HTMLHeadElement] = htmlTag("head")


  /**
    * Defines the base URL for relative URLs in the page.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/base base @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLBaseElement HTMLBaseElement @ MDN]]
    */
  lazy val baseTag: HtmlTag[dom.HTMLBaseElement] = htmlTag("base", void = true)


  /**
    * Used to link JavaScript and external CSS with the current HTML document.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/link link @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLLinkElement HTMLLinkElement @ MDN]]
    */
  lazy val linkTag: HtmlTag[dom.HTMLLinkElement] = htmlTag("link", void = true)


  /**
    * Defines metadata that can't be defined using another HTML element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/meta meta @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMetaElement HTMLMetaElement @ MDN]]
    */
  lazy val metaTag: HtmlTag[dom.HTMLMetaElement] = htmlTag("meta", void = true)


  /**
    * Defines either an internal script or a link to an external script. The
    * script language is JavaScript.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/script script @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLScriptElement HTMLScriptElement @ MDN]]
    */
  lazy val scriptTag: HtmlTag[dom.HTMLScriptElement] = htmlTag("script")


  /**
    * Defines alternative content to display when the browser doesn't support
    * scripting.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/noscript noscript @ MDN]]
    */
  lazy val noScriptTag: HtmlTag[dom.HTMLElement] = htmlTag("noscript")


  // -- Embed Tags --


  /**
    * Represents an image.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/img img @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLImageElement HTMLImageElement @ MDN]]
    */
  lazy val img: HtmlTag[dom.HTMLImageElement] = htmlTag("img", void = true)


  /**
    * Represents a nested browsing context, that is an embedded HTML document.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/iframe iframe @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLIFrameElement HTMLIFrameElement @ MDN]]
    */
  lazy val iframe: HtmlTag[dom.HTMLIFrameElement] = htmlTag("iframe")


  /**
    * Represents a integration point for an external, often non-HTML, application
    * or interactive content.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/embed embed @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLEmbedElement HTMLEmbedElement @ MDN]]
    */
  lazy val embedTag: HtmlTag[dom.HTMLEmbedElement] = htmlTag("embed", void = true)


  /**
    * Represents an external resource, which is treated as an image, an HTML
    * sub-document, or an external resource to be processed by a plug-in.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/object object @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLObjectElement HTMLObjectElement @ MDN]]
    */
  lazy val objectTag: HtmlTag[dom.HTMLObjectElement] = htmlTag("object")


  /**
    * Defines parameters for use by plug-ins invoked by object elements.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/param param @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLParamElement HTMLParamElement @ MDN]]
    */
  lazy val paramTag: HtmlTag[dom.HTMLParamElement] = htmlTag("param", void = true)


  /**
    * Represents a video, and its associated audio files and captions, with the
    * necessary interface to play it.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/video video @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLVideoElement HTMLVideoElement @ MDN]]
    */
  lazy val videoTag: HtmlTag[dom.HTMLVideoElement] = htmlTag("video")


  /**
    * Represents a sound or an audio stream.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/audio audio @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLAudioElement HTMLAudioElement @ MDN]]
    */
  lazy val audioTag: HtmlTag[dom.HTMLAudioElement] = htmlTag("audio")


  /**
    * Allows the authors to specify alternate media resources for media elements
    * like video or audio
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/source source @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLSourceElement HTMLSourceElement @ MDN]]
    */
  lazy val sourceTag: HtmlTag[dom.HTMLSourceElement] = htmlTag("source", void = true)


  /**
    * Allows authors to specify timed text track for media elements like video or
    * audio
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/track track @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTrackElement HTMLTrackElement @ MDN]]
    */
  lazy val trackTag: HtmlTag[dom.HTMLTrackElement] = htmlTag("track", void = true)


  /**
    * Represents a bitmap area that scripts can use to render graphics like graphs,
    * games or any visual images on the fly.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/canvas canvas @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLCanvasElement HTMLCanvasElement @ MDN]]
    */
  lazy val canvasTag: HtmlTag[dom.HTMLCanvasElement] = htmlTag("canvas")


  /**
    * In conjunction with area, defines an image map.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/map map @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMapElement HTMLMapElement @ MDN]]
    */
  lazy val mapTag: HtmlTag[dom.HTMLMapElement] = htmlTag("map")


  /**
    * In conjunction with map, defines an image map
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/area area @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLAreaElement HTMLAreaElement @ MDN]]
    */
  lazy val areaTag: HtmlTag[dom.HTMLAreaElement] = htmlTag("area", void = true)


  // -- Section Tags --


  /**
    * Represents the content of an HTML document. There is only one body
    *   element in a document.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/body body @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLBodyElement HTMLBodyElement @ MDN]]
    */
  lazy val bodyTag: HtmlTag[dom.HTMLBodyElement] = htmlTag("body")


  /**
    * Defines the header of a page or section. It often contains a logo, the
    * title of the Web site, and a navigational table of content.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/header header @ MDN]]
    */
  lazy val headerTag: HtmlTag[dom.HTMLElement] = htmlTag("header")


  /**
    * Defines the footer for a page or section. It often contains a copyright
    * notice, some links to legal information, or addresses to give feedback.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/footer footer @ MDN]]
    */
  lazy val footerTag: HtmlTag[dom.HTMLElement] = htmlTag("footer")


  /**
    * Heading level 1
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h1 h1 @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement HTMLHeadingElement @ MDN]]
    */
  lazy val h1: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h1")


  /**
    * Heading level 2
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h2 h2 @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement HTMLHeadingElement @ MDN]]
    */
  lazy val h2: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h2")


  /**
    * Heading level 3
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h3 h3 @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement HTMLHeadingElement @ MDN]]
    */
  lazy val h3: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h3")


  /**
    * Heading level 4
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h4 h4 @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement HTMLHeadingElement @ MDN]]
    */
  lazy val h4: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h4")


  /**
    * Heading level 5
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h5 h5 @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement HTMLHeadingElement @ MDN]]
    */
  lazy val h5: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h5")


  /**
    * Heading level 6
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h6 h6 @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement HTMLHeadingElement @ MDN]]
    */
  lazy val h6: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h6")


  // -- Text Tags --


  /**
    * Represents a hyperlink, linking to another resource.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a a @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLAnchorElement HTMLAnchorElement @ MDN]]
    */
  lazy val a: HtmlTag[dom.HTMLAnchorElement] = htmlTag("a")


  /**
    * Represents emphasized text.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/em em @ MDN]]
    */
  lazy val em: HtmlTag[dom.HTMLElement] = htmlTag("em")


  /**
    * Represents especially important text.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/strong strong @ MDN]]
    */
  lazy val strong: HtmlTag[dom.HTMLElement] = htmlTag("strong")


  /**
    * Represents a side comment; text like a disclaimer or copyright, which is not
    * essential to the comprehension of the document.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/small small @ MDN]]
    */
  lazy val small: HtmlTag[dom.HTMLElement] = htmlTag("small")


  /**
    * Strikethrough element, used for that is no longer accurate or relevant.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/s s @ MDN]]
    */
  lazy val s: HtmlTag[dom.HTMLElement] = htmlTag("s")


  /**
    * Represents the title of a work being cited.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/cite cite @ MDN]]
    */
  lazy val cite: HtmlTag[dom.HTMLElement] = htmlTag("cite")


  /**
    * Represents computer code.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/code code @ MDN]]
    */
  lazy val code: HtmlTag[dom.HTMLElement] = htmlTag("code")


  /**
    * Subscript tag
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/sub sub @ MDN]]
    */
  lazy val sub: HtmlTag[dom.HTMLElement] = htmlTag("sub")


  /**
    * Superscript tag.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/sup sup @ MDN]]
    */
  lazy val sup: HtmlTag[dom.HTMLElement] = htmlTag("sup")


  /**
    * Italicized text.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/i i @ MDN]]
    */
  lazy val i: HtmlTag[dom.HTMLElement] = htmlTag("i")


  /**
    * Bold text.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/b b @ MDN]]
    */
  lazy val b: HtmlTag[dom.HTMLElement] = htmlTag("b")


  /**
    * Underlined text.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/u u @ MDN]]
    */
  lazy val u: HtmlTag[dom.HTMLElement] = htmlTag("u")


  /**
    * Represents text with no specific meaning. This has to be used when no other
    * text-semantic element conveys an adequate meaning, which, in this case, is
    * often brought by global attributes like class, lang, or dir.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/span span @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLSpanElement HTMLSpanElement @ MDN]]
    */
  lazy val span: HtmlTag[dom.HTMLSpanElement] = htmlTag("span")


  /**
    * Represents a line break.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/br br @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLBRElement HTMLBRElement @ MDN]]
    */
  lazy val br: HtmlTag[dom.HTMLBRElement] = htmlTag("br", void = true)


  /**
    * Represents a line break opportunity, that is a suggested point for wrapping
    * text in order to improve readability of text split on several lines.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/wbr wbr @ MDN]]
    */
  lazy val wbr: HtmlTag[dom.HTMLElement] = htmlTag("wbr", void = true)


  /**
    * Defines an addition to the document.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/ins ins @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLModElement HTMLModElement @ MDN]]
    */
  lazy val ins: HtmlTag[dom.HTMLModElement] = htmlTag("ins")


  /**
    * Defines a remolazy val from the document.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/del del @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLModElement HTMLModElement @ MDN]]
    */
  lazy val del: HtmlTag[dom.HTMLModElement] = htmlTag("del")


  // -- Form Tags --


  /**
    * Represents a form, consisting of controls, that can be submitted to a
    * server for processing.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/form form @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLFormElement HTMLFormElement @ MDN]]
    */
  lazy val form: HtmlTag[dom.HTMLFormElement] = htmlTag("form")


  /**
    * A set of fields.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/fieldset fieldset @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLFieldSetElement HTMLFieldSetElement @ MDN]]
    */
  lazy val fieldSet: HtmlTag[dom.HTMLFieldSetElement] = htmlTag("fieldset")


  /**
    * The caption for a fieldset.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/legend legend @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLLegendElement HTMLLegendElement @ MDN]]
    */
  lazy val legend: HtmlTag[dom.HTMLLegendElement] = htmlTag("legend")


  /**
    * The caption of a single field
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/label label @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLLabelElement HTMLLabelElement @ MDN]]
    */
  lazy val label: HtmlTag[dom.HTMLLabelElement] = htmlTag("label")


  /**
    * A typed data field allowing the user to input data.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input input @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLInputElement HTMLInputElement @ MDN]]
    */
  lazy val input: HtmlTag[dom.HTMLInputElement] = htmlTag("input", void = true)


  /**
    * A button
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/button button @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLButtonElement HTMLButtonElement @ MDN]]
    */
  lazy val button: HtmlTag[dom.HTMLButtonElement] = htmlTag("button")


  /**
    * A control that allows the user to select one of a set of options.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/select select @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLSelectElement HTMLSelectElement @ MDN]]
    */
  lazy val select: HtmlTag[dom.HTMLSelectElement] = htmlTag("select")


  /**
    * A set of predefined options for other controls.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/datalist datalist @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLDataListElement HTMLDataListElement @ MDN]]
    */
  lazy val dataList: HtmlTag[dom.HTMLDataListElement] = htmlTag("datalist")


  /**
    * A set of options, logically grouped.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/optgroup optgroup @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLOptGroupElement HTMLOptGroupElement @ MDN]]
    */
  lazy val optGroup: HtmlTag[dom.HTMLOptGroupElement] = htmlTag("optgroup")


  /**
    * An option in a select element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/option option @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLOptionElement HTMLOptionElement @ MDN]]
    */
  lazy val option: HtmlTag[dom.HTMLOptionElement] = htmlTag("option")


  /**
    * A multiline text edit control.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/textarea textarea @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTextAreaElement HTMLTextAreaElement @ MDN]]
    */
  lazy val textArea: HtmlTag[dom.HTMLTextAreaElement] = htmlTag("textarea")


  // -- Grouping Tags --


  /**
    * Defines a portion that should be displayed as a paragraph.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/p p @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLParagraphElement HTMLParagraphElement @ MDN]]
    */
  lazy val p: HtmlTag[dom.HTMLParagraphElement] = htmlTag("p")


  /**
    * Represents a thematic break between paragraphs of a section or article or
    * any longer content.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/hr hr @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLHRElement HTMLHRElement @ MDN]]
    */
  lazy val hr: HtmlTag[dom.HTMLHRElement] = htmlTag("hr", void = true)


  /**
    * Indicates that its content is preformatted and that this format must be
    * preserved.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/pre pre @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLPreElement HTMLPreElement @ MDN]]
    */
  lazy val pre: HtmlTag[dom.HTMLPreElement] = htmlTag("pre")


  /**
    * Represents a content that is quoted from another source.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/blockquote blockquote @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLQuoteElement HTMLQuoteElement @ MDN]]
    */
  lazy val blockQuote: HtmlTag[dom.HTMLQuoteElement] = htmlTag("blockquote")


  /**
    * Defines an ordered list of items.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/ol ol @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLOListElement HTMLOListElement @ MDN]]
    */
  lazy val ol: HtmlTag[dom.HTMLOListElement] = htmlTag("ol")


  /**
    * Defines an unordered list of items.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/ul ul @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLUListElement HTMLUListElement @ MDN]]
    */
  lazy val ul: HtmlTag[dom.HTMLUListElement] = htmlTag("ul")


  /**
    * Defines an item of an list.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/li li @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLLIElement HTMLLIElement @ MDN]]
    */
  lazy val li: HtmlTag[dom.HTMLLIElement] = htmlTag("li")


  /**
    * Defines a definition list; a list of terms and their associated definitions.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/dl dl @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLDListElement HTMLDListElement @ MDN]]
    */
  lazy val dl: HtmlTag[dom.HTMLDListElement] = htmlTag("dl")


  /**
    * Represents a term defined by the next dd
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/dt dt @ MDN]]
    */
  lazy val dt: HtmlTag[dom.HTMLElement] = htmlTag("dt")


  /**
    * Represents the definition of the terms immediately listed before it.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/dd dd @ MDN]]
    */
  lazy val dd: HtmlTag[dom.HTMLElement] = htmlTag("dd")


  /**
    * Represents a figure illustrated as part of the document.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/figure figure @ MDN]]
    */
  lazy val figure: HtmlTag[dom.HTMLElement] = htmlTag("figure")


  /**
    * Represents the legend of a figure.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/figcaption figcaption @ MDN]]
    */
  lazy val figCaption: HtmlTag[dom.HTMLElement] = htmlTag("figcaption")


  /**
    * Represents a generic container with no special meaning.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/div div @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLDivElement HTMLDivElement @ MDN]]
    */
  lazy val div: HtmlTag[dom.HTMLDivElement] = htmlTag("div")


  // -- Table Tags --


  /**
    * Represents data with more than one dimension.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/table table @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableElement HTMLTableElement @ MDN]]
    */
  lazy val table: HtmlTag[dom.HTMLTableElement] = htmlTag("table")


  /**
    * The title of a table.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/caption caption @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableCaptionElement HTMLTableCaptionElement @ MDN]]
    */
  lazy val caption: HtmlTag[dom.HTMLTableCaptionElement] = htmlTag("caption")


  /**
    * A set of columns.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/colgroup colgroup @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableColElement HTMLTableColElement @ MDN]]
    */
  lazy val colGroup: HtmlTag[dom.HTMLTableColElement] = htmlTag("colgroup")


  /**
    * A single column.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/col col @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableColElement HTMLTableColElement @ MDN]]
    */
  lazy val col: HtmlTag[dom.HTMLTableColElement] = htmlTag("col", void = true)


  /**
    * The table body.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/tbody tbody @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableSectionElement HTMLTableSectionElement @ MDN]]
    */
  lazy val tbody: HtmlTag[dom.HTMLTableSectionElement] = htmlTag("tbody")


  /**
    * The table headers.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/thead thead @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableSectionElement HTMLTableSectionElement @ MDN]]
    */
  lazy val thead: HtmlTag[dom.HTMLTableSectionElement] = htmlTag("thead")


  /**
    * The table footer.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/tfoot tfoot @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableSectionElement HTMLTableSectionElement @ MDN]]
    */
  lazy val tfoot: HtmlTag[dom.HTMLTableSectionElement] = htmlTag("tfoot")


  /**
    * A single row in a table.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/tr tr @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableRowElement HTMLTableRowElement @ MDN]]
    */
  lazy val tr: HtmlTag[dom.HTMLTableRowElement] = htmlTag("tr")


  /**
    * A single cell in a table.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/td td @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableCellElement HTMLTableCellElement @ MDN]]
    */
  lazy val td: HtmlTag[dom.HTMLTableCellElement] = htmlTag("td")


  /**
    * A header cell in a table.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/th th @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableCellElement HTMLTableCellElement @ MDN]]
    */
  lazy val th: HtmlTag[dom.HTMLTableCellElement] = htmlTag("th")


  // -- Misc Tags --


  /**
    * Defines the title of the document, shown in a browser's title bar or on the
    * page's tab. It can only contain text and any contained tags are not
    * interpreted.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/title title @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLTitleElement HTMLTitleElement @ MDN]]
    */
  lazy val titleTag: HtmlTag[dom.HTMLTitleElement] = htmlTag("title")


  /**
    * Used to write inline CSS.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/style style @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLStyleElement HTMLStyleElement @ MDN]]
    */
  lazy val styleTag: HtmlTag[dom.HTMLStyleElement] = htmlTag("style")


  /**
    * Represents a generic section of a document, i.e., a thematic grouping of
    * content, typically with a heading.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/section section @ MDN]]
    */
  lazy val sectionTag: HtmlTag[dom.HTMLElement] = htmlTag("section")


  /**
    * Represents a section of a page that links to other pages or to parts within
    * the page: a section with navigation links.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/nav nav @ MDN]]
    */
  lazy val navTag: HtmlTag[dom.HTMLElement] = htmlTag("nav")


  /**
    * Defines self-contained content that could exist independently of the rest
    * of the content.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/article article @ MDN]]
    */
  lazy val articleTag: HtmlTag[dom.HTMLElement] = htmlTag("article")


  /**
    * Defines some content loosely related to the page content. If it is removed,
    * the remaining content still makes sense.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/aside aside @ MDN]]
    */
  lazy val asideTag: HtmlTag[dom.HTMLElement] = htmlTag("aside")


  /**
    * Defines a section containing contact information.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/address address @ MDN]]
    */
  lazy val addressTag: HtmlTag[dom.HTMLElement] = htmlTag("address")


  /**
    * Defines the main or important content in the document. There is only one
    * main element in the document.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/main main @ MDN]]
    */
  lazy val mainTag: HtmlTag[dom.HTMLElement] = htmlTag("main")


  /**
    * An inline quotation.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/q q @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLQuoteElement HTMLQuoteElement @ MDN]]
    */
  lazy val q: HtmlTag[dom.HTMLQuoteElement] = htmlTag("q")


  /**
    * Represents a term whose definition is contained in its nearest ancestor
    * content.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/dfn dfn @ MDN]]
    */
  lazy val dfn: HtmlTag[dom.HTMLElement] = htmlTag("dfn")


  /**
    * An abbreviation or acronym; the expansion of the abbreviation can be
    * represented in the title attribute.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/abbr abbr @ MDN]]
    */
  lazy val abbr: HtmlTag[dom.HTMLElement] = htmlTag("abbr")


  /**
    * Associates to its content a machine-readable equivalent.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/data data @ MDN]]
    */
  lazy val dataTag: HtmlTag[dom.HTMLElement] = htmlTag("data")


  /**
    * Represents a date and time value; the machine-readable equivalent can be
    * represented in the datetime attribute
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/time time @ MDN]]
    */
  lazy val timeTag: HtmlTag[dom.HTMLElement] = htmlTag("time")


  /**
    * Represents a variable.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/var var @ MDN]]
    */
  lazy val varTag: HtmlTag[dom.HTMLElement] = htmlTag("var")


  /**
    * Represents the output of a program or a computer.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/samp samp @ MDN]]
    */
  lazy val samp: HtmlTag[dom.HTMLElement] = htmlTag("samp")


  /**
    * Represents user input, often from a keyboard, but not necessarily.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/kbd kbd @ MDN]]
    */
  lazy val kbd: HtmlTag[dom.HTMLElement] = htmlTag("kbd")


  /**
    * Defines a mathematical formula.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/math math @ MDN]]
    */
  lazy val mathTag: HtmlTag[dom.HTMLElement] = htmlTag("math")


  /**
    * Represents text highlighted for reference purposes, that is for its
    * relevance in another context.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/mark mark @ MDN]]
    */
  lazy val mark: HtmlTag[dom.HTMLElement] = htmlTag("mark")


  /**
    * Represents content to be marked with ruby annotations, short runs of text
    * presented alongside the text. This is often used in conjunction with East
    * Asian language where the annotations act as a guide for pronunciation, like
    * the Japanese furigana .
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/ruby ruby @ MDN]]
    */
  lazy val ruby: HtmlTag[dom.HTMLElement] = htmlTag("ruby")


  /**
    * Represents the text of a ruby annotation.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/rt rt @ MDN]]
    */
  lazy val rt: HtmlTag[dom.HTMLElement] = htmlTag("rt")


  /**
    * Represents parenthesis around a ruby annotation, used to display the
    * annotation in an alternate way by browsers not supporting the standard
    * display for annotations.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/rp rp @ MDN]]
    */
  lazy val rp: HtmlTag[dom.HTMLElement] = htmlTag("rp")


  /**
    * Represents text that must be isolated from its surrounding for bidirectional
    * text formatting. It allows embedding a span of text with a different, or
    * unknown, directionality.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/bdi bdi @ MDN]]
    */
  lazy val bdi: HtmlTag[dom.HTMLElement] = htmlTag("bdi")


  /**
    * Represents the directionality of its children, in order to explicitly
    * override the Unicode bidirectional algorithm.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/bdo bdo @ MDN]]
    */
  lazy val bdo: HtmlTag[dom.HTMLElement] = htmlTag("bdo")


  /**
    * A key-pair generator control.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/keygen keygen @ MDN]]
    */
  lazy val keyGenTag: HtmlTag[dom.HTMLElement] = htmlTag("keygen")


  /**
    * The result of a calculation
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/output output @ MDN]]
    */
  lazy val outputTag: HtmlTag[dom.HTMLElement] = htmlTag("output")


  /**
    * A progress completion bar
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/progress progress @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLProgressElement HTMLProgressElement @ MDN]]
    */
  lazy val progressTag: HtmlTag[dom.HTMLProgressElement] = htmlTag("progress")


  /**
    * A scalar measurement within a known range.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/meter meter @ MDN]]
    */
  lazy val meterTag: HtmlTag[dom.HTMLElement] = htmlTag("meter")


  /**
    * A widget from which the user can obtain additional information
    * or controls.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/details details @ MDN]]
    */
  lazy val detailsTag: HtmlTag[dom.HTMLElement] = htmlTag("details")


  /**
    * A summary, caption, or legend for a given details.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/summary summary @ MDN]]
    */
  lazy val summaryTag: HtmlTag[dom.HTMLElement] = htmlTag("summary")


  /**
    * A command that the user can invoke.
    * 
    * [[https://www.w3.org/TR/2011/WD-html5-author-20110809/the-command-element.html the-command-element.html @ W3C]]
    */
  lazy val commandTag: HtmlTag[dom.HTMLElement] = htmlTag("command")


  /**
    * A list of commands
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/menu menu @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMenuElement HTMLMenuElement @ MDN]]
    */
  lazy val menuTag: HtmlTag[dom.HTMLMenuElement] = htmlTag("menu")


  /**
    * Dialog box or other interactive component, such as a dismissible alert,
    * inspector, or subwindow.
    * 
    * Note: The tabindex attribute must not be used on the `<dialog>` element
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/HTML/Element/dialog dialog @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLDialogElement HTMLDialogElement @ MDN]]
    */
  lazy val dialogTag: HtmlTag[dom.HTMLDialogElement] = htmlTag("dialog")


}
