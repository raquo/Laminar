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
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/html
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLHtmlElement
    */
  lazy val htmlRootTag: HtmlTag[dom.HTMLHtmlElement] = htmlTag("html")


  /**
    * Represents a collection of metadata about the document, including links to,
    * or definitions of, scripts and style sheets.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/head
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadElement
    */
  lazy val headTag: HtmlTag[dom.HTMLHeadElement] = htmlTag("head")


  /**
    * Defines the base URL for relative URLs in the page.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/base
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLBaseElement
    */
  lazy val baseTag: HtmlTag[dom.HTMLBaseElement] = htmlTag("base", void = true)


  /**
    * Used to link JavaScript and external CSS with the current HTML document.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/link
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLLinkElement
    */
  lazy val linkTag: HtmlTag[dom.HTMLLinkElement] = htmlTag("link", void = true)


  /**
    * Defines metadata that can't be defined using another HTML element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/meta
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMetaElement
    */
  lazy val metaTag: HtmlTag[dom.HTMLMetaElement] = htmlTag("meta", void = true)


  /**
    * Defines either an internal script or a link to an external script. The
    * script language is JavaScript.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/script
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLScriptElement
    */
  lazy val scriptTag: HtmlTag[dom.HTMLScriptElement] = htmlTag("script")


  /**
    * Defines alternative content to display when the browser doesn't support
    * scripting.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/noscript
    */
  lazy val noScriptTag: HtmlTag[dom.HTMLElement] = htmlTag("noscript")


  // -- Embed Tags --


  /**
    * Represents an image.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/img
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLImageElement
    */
  lazy val img: HtmlTag[dom.HTMLImageElement] = htmlTag("img", void = true)


  /**
    * Represents a nested browsing context, that is an embedded HTML document.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/iframe
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLIFrameElement
    */
  lazy val iframe: HtmlTag[dom.HTMLIFrameElement] = htmlTag("iframe")


  /**
    * Represents a integration point for an external, often non-HTML, application
    * or interactive content.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/embed
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLEmbedElement
    */
  lazy val embedTag: HtmlTag[dom.HTMLEmbedElement] = htmlTag("embed", void = true)


  /**
    * Represents an external resource, which is treated as an image, an HTML
    * sub-document, or an external resource to be processed by a plug-in.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/object
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLObjectElement
    */
  lazy val objectTag: HtmlTag[dom.HTMLObjectElement] = htmlTag("object")


  /**
    * Defines parameters for use by plug-ins invoked by object elements.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/param
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLParamElement
    */
  lazy val paramTag: HtmlTag[dom.HTMLParamElement] = htmlTag("param", void = true)


  /**
    * Represents a video, and its associated audio files and captions, with the
    * necessary interface to play it.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/video
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLVideoElement
    */
  lazy val videoTag: HtmlTag[dom.HTMLVideoElement] = htmlTag("video")


  /**
    * Represents a sound or an audio stream.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/audio
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLAudioElement
    */
  lazy val audioTag: HtmlTag[dom.HTMLAudioElement] = htmlTag("audio")


  /**
    * Allows the authors to specify alternate media resources for media elements
    * like video or audio
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/source
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLSourceElement
    */
  lazy val sourceTag: HtmlTag[dom.HTMLSourceElement] = htmlTag("source", void = true)


  /**
    * Allows authors to specify timed text track for media elements like video or
    * audio
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/track
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTrackElement
    */
  lazy val trackTag: HtmlTag[dom.HTMLTrackElement] = htmlTag("track", void = true)


  /**
    * Represents a bitmap area that scripts can use to render graphics like graphs,
    * games or any visual images on the fly.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/canvas
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLCanvasElement
    */
  lazy val canvasTag: HtmlTag[dom.HTMLCanvasElement] = htmlTag("canvas")


  /**
    * In conjunction with area, defines an image map.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/map
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMapElement
    */
  lazy val mapTag: HtmlTag[dom.HTMLMapElement] = htmlTag("map")


  /**
    * In conjunction with map, defines an image map
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/area
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLAreaElement
    */
  lazy val areaTag: HtmlTag[dom.HTMLAreaElement] = htmlTag("area", void = true)


  // -- Section Tags --


  /**
    * Represents the content of an HTML document. There is only one body
    *   element in a document.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/body
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLBodyElement
    */
  lazy val bodyTag: HtmlTag[dom.HTMLBodyElement] = htmlTag("body")


  /**
    * Defines the header of a page or section. It often contains a logo, the
    * title of the Web site, and a navigational table of content.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/header
    */
  lazy val headerTag: HtmlTag[dom.HTMLElement] = htmlTag("header")


  /**
    * Defines the footer for a page or section. It often contains a copyright
    * notice, some links to legal information, or addresses to give feedback.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/footer
    */
  lazy val footerTag: HtmlTag[dom.HTMLElement] = htmlTag("footer")


  /**
    * Heading level 1
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h1
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement
    */
  lazy val h1: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h1")


  /**
    * Heading level 2
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h2
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement
    */
  lazy val h2: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h2")


  /**
    * Heading level 3
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h3
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement
    */
  lazy val h3: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h3")


  /**
    * Heading level 4
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h4
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement
    */
  lazy val h4: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h4")


  /**
    * Heading level 5
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h5
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement
    */
  lazy val h5: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h5")


  /**
    * Heading level 6
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/h6
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLHeadingElement
    */
  lazy val h6: HtmlTag[dom.HTMLHeadingElement] = htmlTag("h6")


  // -- Text Tags --


  /**
    * Represents a hyperlink, linking to another resource.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLAnchorElement
    */
  lazy val a: HtmlTag[dom.HTMLAnchorElement] = htmlTag("a")


  /**
    * Represents emphasized text.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/em
    */
  lazy val em: HtmlTag[dom.HTMLElement] = htmlTag("em")


  /**
    * Represents especially important text.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/strong
    */
  lazy val strong: HtmlTag[dom.HTMLElement] = htmlTag("strong")


  /**
    * Represents a side comment; text like a disclaimer or copyright, which is not
    * essential to the comprehension of the document.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/small
    */
  lazy val small: HtmlTag[dom.HTMLElement] = htmlTag("small")


  /**
    * Strikethrough element, used for that is no longer accurate or relevant.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/s
    */
  lazy val s: HtmlTag[dom.HTMLElement] = htmlTag("s")


  /**
    * Represents the title of a work being cited.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/cite
    */
  lazy val cite: HtmlTag[dom.HTMLElement] = htmlTag("cite")


  /**
    * Represents computer code.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/code
    */
  lazy val code: HtmlTag[dom.HTMLElement] = htmlTag("code")


  /**
    * Subscript tag
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/sub
    */
  lazy val sub: HtmlTag[dom.HTMLElement] = htmlTag("sub")


  /**
    * Superscript tag.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/sup
    */
  lazy val sup: HtmlTag[dom.HTMLElement] = htmlTag("sup")


  /**
    * Italicized text.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/i
    */
  lazy val i: HtmlTag[dom.HTMLElement] = htmlTag("i")


  /**
    * Bold text.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/b
    */
  lazy val b: HtmlTag[dom.HTMLElement] = htmlTag("b")


  /**
    * Underlined text.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/u
    */
  lazy val u: HtmlTag[dom.HTMLElement] = htmlTag("u")


  /**
    * Represents text with no specific meaning. This has to be used when no other
    * text-semantic element conveys an adequate meaning, which, in this case, is
    * often brought by global attributes like class, lang, or dir.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/span
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLSpanElement
    */
  lazy val span: HtmlTag[dom.HTMLSpanElement] = htmlTag("span")


  /**
    * Represents a line break.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/br
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLBRElement
    */
  lazy val br: HtmlTag[dom.HTMLBRElement] = htmlTag("br", void = true)


  /**
    * Represents a line break opportunity, that is a suggested point for wrapping
    * text in order to improve readability of text split on several lines.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/wbr
    */
  lazy val wbr: HtmlTag[dom.HTMLElement] = htmlTag("wbr", void = true)


  /**
    * Defines an addition to the document.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/ins
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLModElement
    */
  lazy val ins: HtmlTag[dom.HTMLModElement] = htmlTag("ins")


  /**
    * Defines a remolazy val from the document.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/del
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLModElement
    */
  lazy val del: HtmlTag[dom.HTMLModElement] = htmlTag("del")


  // -- Form Tags --


  /**
    * Represents a form, consisting of controls, that can be submitted to a
    * server for processing.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/form
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLFormElement
    */
  lazy val form: HtmlTag[dom.HTMLFormElement] = htmlTag("form")


  /**
    * A set of fields.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/fieldset
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLFieldSetElement
    */
  lazy val fieldSet: HtmlTag[dom.HTMLFieldSetElement] = htmlTag("fieldset")


  /**
    * The caption for a fieldset.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/legend
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLLegendElement
    */
  lazy val legend: HtmlTag[dom.HTMLLegendElement] = htmlTag("legend")


  /**
    * The caption of a single field
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/label
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLLabelElement
    */
  lazy val label: HtmlTag[dom.HTMLLabelElement] = htmlTag("label")


  /**
    * A typed data field allowing the user to input data.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLInputElement
    */
  lazy val input: HtmlTag[dom.HTMLInputElement] = htmlTag("input", void = true)


  /**
    * A button
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/button
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLButtonElement
    */
  lazy val button: HtmlTag[dom.HTMLButtonElement] = htmlTag("button")


  /**
    * A control that allows the user to select one of a set of options.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/select
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLSelectElement
    */
  lazy val select: HtmlTag[dom.HTMLSelectElement] = htmlTag("select")


  /**
    * A set of predefined options for other controls.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/datalist
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLDataListElement
    */
  lazy val dataList: HtmlTag[dom.HTMLDataListElement] = htmlTag("datalist")


  /**
    * A set of options, logically grouped.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/optgroup
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLOptGroupElement
    */
  lazy val optGroup: HtmlTag[dom.HTMLOptGroupElement] = htmlTag("optgroup")


  /**
    * An option in a select element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/option
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLOptionElement
    */
  lazy val option: HtmlTag[dom.HTMLOptionElement] = htmlTag("option")


  /**
    * A multiline text edit control.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/textarea
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTextAreaElement
    */
  lazy val textArea: HtmlTag[dom.HTMLTextAreaElement] = htmlTag("textarea")


  // -- Grouping Tags --


  /**
    * Defines a portion that should be displayed as a paragraph.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/p
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLParagraphElement
    */
  lazy val p: HtmlTag[dom.HTMLParagraphElement] = htmlTag("p")


  /**
    * Represents a thematic break between paragraphs of a section or article or
    * any longer content.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/hr
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLHRElement
    */
  lazy val hr: HtmlTag[dom.HTMLHRElement] = htmlTag("hr", void = true)


  /**
    * Indicates that its content is preformatted and that this format must be
    * preserved.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/pre
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLPreElement
    */
  lazy val pre: HtmlTag[dom.HTMLPreElement] = htmlTag("pre")


  /**
    * Represents a content that is quoted from another source.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/blockquote
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLQuoteElement
    */
  lazy val blockQuote: HtmlTag[dom.HTMLQuoteElement] = htmlTag("blockquote")


  /**
    * Defines an ordered list of items.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/ol
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLOListElement
    */
  lazy val ol: HtmlTag[dom.HTMLOListElement] = htmlTag("ol")


  /**
    * Defines an unordered list of items.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/ul
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLUListElement
    */
  lazy val ul: HtmlTag[dom.HTMLUListElement] = htmlTag("ul")


  /**
    * Defines an item of an list.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/li
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLLIElement
    */
  lazy val li: HtmlTag[dom.HTMLLIElement] = htmlTag("li")


  /**
    * Defines a definition list; a list of terms and their associated definitions.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/dl
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLDListElement
    */
  lazy val dl: HtmlTag[dom.HTMLDListElement] = htmlTag("dl")


  /**
    * Represents a term defined by the next dd
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/dt
    */
  lazy val dt: HtmlTag[dom.HTMLElement] = htmlTag("dt")


  /**
    * Represents the definition of the terms immediately listed before it.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/dd
    */
  lazy val dd: HtmlTag[dom.HTMLElement] = htmlTag("dd")


  /**
    * Represents a figure illustrated as part of the document.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/figure
    */
  lazy val figure: HtmlTag[dom.HTMLElement] = htmlTag("figure")


  /**
    * Represents the legend of a figure.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/figcaption
    */
  lazy val figCaption: HtmlTag[dom.HTMLElement] = htmlTag("figcaption")


  /**
    * Represents a generic container with no special meaning.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/div
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLDivElement
    */
  lazy val div: HtmlTag[dom.HTMLDivElement] = htmlTag("div")


  // -- Table Tags --


  /**
    * Represents data with more than one dimension.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/table
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableElement
    */
  lazy val table: HtmlTag[dom.HTMLTableElement] = htmlTag("table")


  /**
    * The title of a table.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/caption
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableCaptionElement
    */
  lazy val caption: HtmlTag[dom.HTMLTableCaptionElement] = htmlTag("caption")


  /**
    * A set of columns.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/colgroup
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableColElement
    */
  lazy val colGroup: HtmlTag[dom.HTMLTableColElement] = htmlTag("colgroup")


  /**
    * A single column.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/col
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableColElement
    */
  lazy val col: HtmlTag[dom.HTMLTableColElement] = htmlTag("col", void = true)


  /**
    * The table body.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/tbody
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableSectionElement
    */
  lazy val tbody: HtmlTag[dom.HTMLTableSectionElement] = htmlTag("tbody")


  /**
    * The table headers.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/thead
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableSectionElement
    */
  lazy val thead: HtmlTag[dom.HTMLTableSectionElement] = htmlTag("thead")


  /**
    * The table footer.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/tfoot
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableSectionElement
    */
  lazy val tfoot: HtmlTag[dom.HTMLTableSectionElement] = htmlTag("tfoot")


  /**
    * A single row in a table.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/tr
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableRowElement
    */
  lazy val tr: HtmlTag[dom.HTMLTableRowElement] = htmlTag("tr")


  /**
    * A single cell in a table.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/td
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableCellElement
    */
  lazy val td: HtmlTag[dom.HTMLTableCellElement] = htmlTag("td")


  /**
    * A header cell in a table.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/th
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableCellElement
    */
  lazy val th: HtmlTag[dom.HTMLTableCellElement] = htmlTag("th")


  // -- Misc Tags --


  /**
    * Defines the title of the document, shown in a browser's title bar or on the
    * page's tab. It can only contain text and any contained tags are not
    * interpreted.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/title
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLTitleElement
    */
  lazy val titleTag: HtmlTag[dom.HTMLTitleElement] = htmlTag("title")


  /**
    * Used to write inline CSS.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/style
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLStyleElement
    */
  lazy val styleTag: HtmlTag[dom.HTMLStyleElement] = htmlTag("style")


  /**
    * Represents a generic section of a document, i.e., a thematic grouping of
    * content, typically with a heading.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/section
    */
  lazy val sectionTag: HtmlTag[dom.HTMLElement] = htmlTag("section")


  /**
    * Represents a section of a page that links to other pages or to parts within
    * the page: a section with navigation links.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/nav
    */
  lazy val navTag: HtmlTag[dom.HTMLElement] = htmlTag("nav")


  /**
    * Defines self-contained content that could exist independently of the rest
    * of the content.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/article
    */
  lazy val articleTag: HtmlTag[dom.HTMLElement] = htmlTag("article")


  /**
    * Defines some content loosely related to the page content. If it is removed,
    * the remaining content still makes sense.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/aside
    */
  lazy val asideTag: HtmlTag[dom.HTMLElement] = htmlTag("aside")


  /**
    * Defines a section containing contact information.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/address
    */
  lazy val addressTag: HtmlTag[dom.HTMLElement] = htmlTag("address")


  /**
    * Defines the main or important content in the document. There is only one
    * main element in the document.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/main
    */
  lazy val mainTag: HtmlTag[dom.HTMLElement] = htmlTag("main")


  /**
    * An inline quotation.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/q
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLQuoteElement
    */
  lazy val q: HtmlTag[dom.HTMLQuoteElement] = htmlTag("q")


  /**
    * Represents a term whose definition is contained in its nearest ancestor
    * content.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/dfn
    */
  lazy val dfn: HtmlTag[dom.HTMLElement] = htmlTag("dfn")


  /**
    * An abbreviation or acronym; the expansion of the abbreviation can be
    * represented in the title attribute.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/abbr
    */
  lazy val abbr: HtmlTag[dom.HTMLElement] = htmlTag("abbr")


  /**
    * Associates to its content a machine-readable equivalent.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/data
    */
  lazy val dataTag: HtmlTag[dom.HTMLElement] = htmlTag("data")


  /**
    * Represents a date and time value; the machine-readable equivalent can be
    * represented in the datetime attribute
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/time
    */
  lazy val timeTag: HtmlTag[dom.HTMLElement] = htmlTag("time")


  /**
    * Represents a variable.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/var
    */
  lazy val varTag: HtmlTag[dom.HTMLElement] = htmlTag("var")


  /**
    * Represents the output of a program or a computer.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/samp
    */
  lazy val samp: HtmlTag[dom.HTMLElement] = htmlTag("samp")


  /**
    * Represents user input, often from a keyboard, but not necessarily.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/kbd
    */
  lazy val kbd: HtmlTag[dom.HTMLElement] = htmlTag("kbd")


  /**
    * Defines a mathematical formula.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/math
    */
  lazy val mathTag: HtmlTag[dom.HTMLElement] = htmlTag("math")


  /**
    * Represents text highlighted for reference purposes, that is for its
    * relevance in another context.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/mark
    */
  lazy val mark: HtmlTag[dom.HTMLElement] = htmlTag("mark")


  /**
    * Represents content to be marked with ruby annotations, short runs of text
    * presented alongside the text. This is often used in conjunction with East
    * Asian language where the annotations act as a guide for pronunciation, like
    * the Japanese furigana .
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/ruby
    */
  lazy val ruby: HtmlTag[dom.HTMLElement] = htmlTag("ruby")


  /**
    * Represents the text of a ruby annotation.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/rt
    */
  lazy val rt: HtmlTag[dom.HTMLElement] = htmlTag("rt")


  /**
    * Represents parenthesis around a ruby annotation, used to display the
    * annotation in an alternate way by browsers not supporting the standard
    * display for annotations.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/rp
    */
  lazy val rp: HtmlTag[dom.HTMLElement] = htmlTag("rp")


  /**
    * Represents text that must be isolated from its surrounding for bidirectional
    * text formatting. It allows embedding a span of text with a different, or
    * unknown, directionality.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/bdi
    */
  lazy val bdi: HtmlTag[dom.HTMLElement] = htmlTag("bdi")


  /**
    * Represents the directionality of its children, in order to explicitly
    * override the Unicode bidirectional algorithm.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/bdo
    */
  lazy val bdo: HtmlTag[dom.HTMLElement] = htmlTag("bdo")


  /**
    * A key-pair generator control.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/keygen
    */
  lazy val keyGenTag: HtmlTag[dom.HTMLElement] = htmlTag("keygen")


  /**
    * The result of a calculation
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/output
    */
  lazy val outputTag: HtmlTag[dom.HTMLElement] = htmlTag("output")


  /**
    * A progress completion bar
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/progress
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLProgressElement
    */
  lazy val progressTag: HtmlTag[dom.HTMLProgressElement] = htmlTag("progress")


  /**
    * A scalar measurement within a known range.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/meter
    */
  lazy val meterTag: HtmlTag[dom.HTMLElement] = htmlTag("meter")


  /**
    * A widget from which the user can obtain additional information
    * or controls.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/details
    */
  lazy val detailsTag: HtmlTag[dom.HTMLElement] = htmlTag("details")


  /**
    * A summary, caption, or legend for a given details.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/summary
    */
  lazy val summaryTag: HtmlTag[dom.HTMLElement] = htmlTag("summary")


  /**
    * A command that the user can invoke.
    * 
    * @see https://www.w3.org/TR/2011/WD-html5-author-20110809/the-command-element.html
    */
  lazy val commandTag: HtmlTag[dom.HTMLElement] = htmlTag("command")


  /**
    * A list of commands
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/menu
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMenuElement
    */
  lazy val menuTag: HtmlTag[dom.HTMLMenuElement] = htmlTag("menu")


  /**
    * Dialog box or other interactive component, such as a dismissible alert,
    * inspector, or subwindow.
    * 
    * Note: The tabindex attribute must not be used on the `<dialog>` element
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/dialog
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLDialogElement
    */
  lazy val dialogTag: HtmlTag[dom.HTMLDialogElement] = htmlTag("dialog")


}
