package com.raquo.laminar.domapi

import com.raquo.laminar.api.L.svg
import com.raquo.laminar.tags.{HtmlTag, SvgTag}
import org.scalajs.dom

import scala.scalajs.js

trait DomParser { this: DomTags =>

  private val htmlParserContainer: dom.HTMLTemplateElement = {
    dom.document.createElement("template").asInstanceOf[dom.HTMLTemplateElement]
  }

  private val svgParserContainer: dom.svg.Element = {
    createSvgElement(svg.svg)
  }

  /** #WARNING: Only use on trusted HTML strings.
    *  HTML strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    *
    * Note: this expects the html string to contain one HTML element,
    * and will throw otherwise (e.g. if given a text node, an SVG, or multiple elements)
    */
  def unsafeParseHtmlString(
    dangerousHtmlString: String
  ): dom.html.Element = {
    val nodes = unsafeParseHtmlStringIntoNodeArray(dangerousHtmlString)
    val clue = "Error parsing HTML string"
    val node = assertSingleNode(nodes, clue)
    assertHtmlElement(node, clue)
  }

  /** #WARNING: Only use on trusted HTML strings.
    *  HTML strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    *
    * Note: this expects the html string to contain one element matching the
    * tag name, and will throw otherwise (e.g. if given a text node, an
    * element with a different tag name, or multiple elements)
    */
  def unsafeParseHtmlString[Ref <: dom.html.Element](
    tag: HtmlTag[Ref],
    dangerousHtmlString: String
  ): Ref = {
    val nodes = unsafeParseHtmlStringIntoNodeArray(dangerousHtmlString)
    val clue = "Error parsing HTML string"
    val node = assertSingleNode(nodes, clue)
    assertTagMatches(tag, node, clue)
  }

  /** #WARNING: Only use on trusted SVG strings.
    *  SVG strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    *
    * Note: this expects the svg string to contain one HTML element,
    * and will throw otherwise (e.g. if given a text node, HTML, or
    * multiple elements)
    */
  def unsafeParseSvgString(
    dangerousSvgString: String
  ): dom.svg.Element = {
    val nodes = unsafeParseSvgStringIntoNodeArray(dangerousSvgString)
    val clue = "Error parsing SVG string"
    val node = assertSingleNode(nodes, clue)
    assertSvgElement(node, clue)
  }

  /** #WARNING: Only use on trusted SVG strings.
    *  SVG strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    *
    * Note: this expects the svg string to contain one element matching the
    * tag name, and will throw otherwise (e.g. if given a text node, an
    * element with a different tag name, or multiple elements)
    */
  def unsafeParseSvgString[Ref <: dom.svg.Element](
    tag: SvgTag[Ref],
    dangerousSvgString: String
  ): Ref = {
    val nodes = unsafeParseSvgStringIntoNodeArray(dangerousSvgString)
    val clue = "Error parsing SVG string"
    val node = assertSingleNode(nodes, clue)
    assertTagMatches(tag, node, clue)
  }

  /** #WARNING: Only use on trusted HTML strings.
    *  HTML strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    *
    * Note: This method does not work in Internet Explorer.
    * See https://stackoverflow.com/q/10585029 for the issues with various approaches.
    * Use this if you need IE support: https://gist.github.com/Munawwar/6e6362dbdf77c7865a99
    */
  def unsafeParseHtmlStringIntoNodeArray(
    dangerousHtmlString: String
  ): js.Array[dom.Node] = {
    htmlParserContainer.innerHTML = dangerousHtmlString
    // #TODO add to `ew`: js.Array.from(nodeList)
    val arr = js.Array.from(htmlParserContainer.content.childNodes.asInstanceOf[js.Iterable[dom.Node]])
    htmlParserContainer.innerHTML = ""
    arr
  }

  /** #WARNING: Only use on trusted SVG strings.
    *  SVG strings can contain embedded Javascript code,
    *  which this function will execute blindly!
    */
  def unsafeParseSvgStringIntoNodeArray(
    dangerousSvgString: String
  ): js.Array[dom.Node] = {
    svgParserContainer.innerHTML = dangerousSvgString
    // #TODO add to `ew`: js.Array.from(nodeList)
    val arr = js.Array.from(svgParserContainer.childNodes.asInstanceOf[js.Iterable[dom.Node]])
    svgParserContainer.innerHTML = ""
    arr
  }
}
