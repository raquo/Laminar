package com.raquo.laminar.defs.attrs

import com.raquo.laminar.keys.MathMlAttr
import com.raquo.laminar.codecs.Codec

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait MathMlAttrs {


  /**
    * Create MathML attribute
    *
    * @param name  - name of the attribute, e.g. "stretchy"
    * @param codec - used to encode V into String, e.g. Codec.stringAsIs
    *
    * @tparam V    - value type for this attr in Scala
    */
  def mathMlAttr[V](name: String, codec: Codec[V, String]): MathMlAttr[V] = new MathMlAttr(name, codec)


  @inline protected def boolAsPresenceMathMlAttr(name: String): MathMlAttr[Boolean] = mathMlAttr(name, Codec.booleanAsAttrPresence)

  @inline protected def intMathMlAttr(name: String): MathMlAttr[Int] = mathMlAttr(name, Codec.intAsString)

  @inline protected def stringMathMlAttr(name: String): MathMlAttr[String] = mathMlAttr(name, Codec.stringAsIs)



  /**
    * A Boolean indicating whether the operator should be treated as an accent
    * when used as an under- or over-script.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mo#accent mo#accent @ MDN]]
    */
  lazy val accent: MathMlAttr[Boolean] = boolAsPresenceMathMlAttr("accent")


  /**
    * A Boolean indicating whether the under script should be treated as an accent.
    *
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/munder#accentunder munder#accentunder @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/munderover#accentunder munderover#accentunder @ MDN]]
    */
  lazy val accentUnder: MathMlAttr[Boolean] = boolAsPresenceMathMlAttr("accentunder")


  /**
    * Specifies vertical alignment of the table with respect to its environment.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtable#align mtable#align @ MDN]]
    */
  lazy val align: MathMlAttr[String] = stringMathMlAttr("align")


  /**
    * Specifies the horizontal alignment of table cells.
    *
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtable#columnalign mtable#columnalign @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtd#columnalign mtd#columnalign @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtr#columnalign mtr#columnalign @ MDN]]
    */
  lazy val columnAlign: MathMlAttr[String] = stringMathMlAttr("columnalign")


  /**
    * Specifies table column borders.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtable#columnlines mtable#columnlines @ MDN]]
    */
  lazy val columnLines: MathMlAttr[String] = stringMathMlAttr("columnlines")


  /**
    * Specifies the space between table columns.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtable#columnspacing mtable#columnspacing @ MDN]]
    */
  lazy val columnSpacing: MathMlAttr[String] = stringMathMlAttr("columnspacing")


  /**
    * A non-negative integer value that indicates over how many table columns the cell extends.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtd#columnspan mtd#columnspan @ MDN]]
    */
  lazy val columnSpan: MathMlAttr[Int] = intMathMlAttr("columnspan")


  /**
    * A length-percentage indicating the desired depth (below the baseline).
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mpadded#depth mpadded#depth @ MDN]]
    */
  lazy val depth: MathMlAttr[String] = stringMathMlAttr("depth")


  /**
    * The text direction. Possible values are either ltr (left to right) or rtl (right to left).
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Global_attributes/dir dir @ MDN]]
    */
  lazy val dir: MathMlAttr[String] = stringMathMlAttr("dir")


  /**
    * Specifies the rendering mode. The values block and inline are allowed.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/math#display math#display @ MDN]]
    */
  lazy val display: MathMlAttr[String] = stringMathMlAttr("display")


  /**
    * A Boolean specifying whether to set the math-style to normal (if true) or compact (otherwise).
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Global_attributes/displaystyle displaystyle @ MDN]]
    */
  lazy val displayStyle: MathMlAttr[Boolean] = boolAsPresenceMathMlAttr("displaystyle")


  /**
    * A Boolean specifying whether the operator is a fence (such as parentheses).
    * There is no visual effect for this attribute.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mo#fence mo#fence @ MDN]]
    */
  lazy val fence: MathMlAttr[Boolean] = boolAsPresenceMathMlAttr("fence")


  /**
    * Specifies borders of an entire mtable. Possible values are: none (default), solid and dashed.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtable#frame mtable#frame @ MDN]]
    */
  lazy val frame: MathMlAttr[String] = stringMathMlAttr("frame")


  /**
    * Specifies additional space added between the table and frame.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtable#framespacing mtable#framespacing @ MDN]]
    */
  lazy val frameSpacing: MathMlAttr[String] = stringMathMlAttr("framespacing")


  /**
    * A length-percentage indicating the desired height (above the baseline).
    *
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mpadded#height mpadded#height @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mspace#height mspace#height @ MDN]]
    */
  lazy val height: MathMlAttr[String] = stringMathMlAttr("height")


  /**
    * Used to set a hyperlink to a specified URI.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Global_attributes/href href @ MDN]]
    */
  lazy val href: MathMlAttr[String] = stringMathMlAttr("href")


  /**
    * A length-percentage indicating the thickness of the horizontal fraction line.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mfrac#linethickness mfrac#linethickness @ MDN]]
    */
  lazy val lineThickness: MathMlAttr[String] = stringMathMlAttr("linethickness")


  /**
    * A length-percentage indicating amount of space before the operator.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mo#lspace mo#lspace @ MDN]]
    */
  lazy val lSpace: MathMlAttr[String] = stringMathMlAttr("lspace")


  /**
    * A background-color for the element.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Global_attributes/mathbackground mathbackground @ MDN]]
    */
  lazy val mathBackground: MathMlAttr[String] = stringMathMlAttr("mathbackground")


  /**
    * A color for the element.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Global_attributes/mathcolor mathcolor @ MDN]]
    */
  lazy val mathColor: MathMlAttr[String] = stringMathMlAttr("mathcolor")


  /**
    * A length-percentage used as a font-size for the element.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Global_attributes/mathsize mathsize @ MDN]]
    */
  lazy val mathSize: MathMlAttr[String] = stringMathMlAttr("mathsize")


  /**
    * The logical class of token elements, which varies in typography.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Global_attributes/mathvariant mathvariant @ MDN]]
    */
  lazy val mathVariant: MathMlAttr[String] = stringMathMlAttr("mathvariant")


  /**
    * A length-percentage indicating the maximum size of the operator when it is stretchy.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mo#maxsize mo#maxsize @ MDN]]
    */
  lazy val maxSize: MathMlAttr[String] = stringMathMlAttr("maxsize")


  /**
    * A length-percentage indicating the minimum size of the operator when it is stretchy.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mo#minsize mo#minsize @ MDN]]
    */
  lazy val minsize: MathMlAttr[String] = stringMathMlAttr("minSize")


  /**
    * A Boolean indicating whether attached under- and overscripts move to sub-
    * and superscript positions when math-style is set to compact.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mo#movablelimits mo#movablelimits @ MDN]]
    */
  lazy val movableLimits: MathMlAttr[Boolean] = boolAsPresenceMathMlAttr("movablelimits")


  /**
    * A list of notations, separated by white space, to apply to the child elements.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/menclose#notation menclose#notation @ MDN]]
    */
  lazy val notation: MathMlAttr[String] = stringMathMlAttr("notation")


  /**
    * Specifies the vertical alignment of table cells.
    *
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtable#rowalign mtable#rowalign @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtd#rowalign mtd#rowalign @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtr#rowalign mtr#rowalign @ MDN]]
    */
  lazy val rowAlign: MathMlAttr[String] = stringMathMlAttr("rowalign")


  /**
    * Specifies table row borders.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtable#rowlines mtable#rowlines @ MDN]]
    */
  lazy val rowLines: MathMlAttr[String] = stringMathMlAttr("rowlines")


  /**
    * Specifies the space between table rows.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtable#rowspacing mtable#rowspacing @ MDN]]
    */
  lazy val rowSpacing: MathMlAttr[String] = stringMathMlAttr("rowspacing")


  /**
    * A non-negative integer value that indicates on how many rows does the cell extend.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtd#rowspan mtd#rowspan @ MDN]]
    */
  lazy val rowSpan: MathMlAttr[Int] = intMathMlAttr("rowspan")


  /**
    * A length-percentage indicating the amount of space after the operator.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mo#rspace mo#rspace @ MDN]]
    */
  lazy val rSpace: MathMlAttr[String] = stringMathMlAttr("rspace")


  /**
    * Specifies a math-depth for the element. See the scriptlevel page for accepted values and mapping.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Global_attributes/scriptlevel scriptlevel @ MDN]]
    */
  lazy val scriptLevel: MathMlAttr[Int] = intMathMlAttr("scriptlevel")


  /**
    * A Boolean specifying whether the operator is a separator (such as commas).
    * There is no visual effect for this attribute.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mo#separator mo#separator @ MDN]]
    */
  lazy val separator: MathMlAttr[Boolean] = boolAsPresenceMathMlAttr("separator")


  /**
    * A Boolean indicating whether the operator stretches to the size of the adjacent element.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mo#stretchy mo#stretchy @ MDN]]
    */
  lazy val stretchy: MathMlAttr[Boolean] = boolAsPresenceMathMlAttr("stretchy")


  /**
    * A Boolean indicating whether a stretchy operator should be vertically symmetric
    * around the imaginary math axis (centered fraction line).
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mo#symmetric mo#symmetric @ MDN]]
    */
  lazy val symmetric: MathMlAttr[Boolean] = boolAsPresenceMathMlAttr("symmetric")


  /**
    * A length-percentage indicating the vertical location of the positioning point
    * of the child content with respect to the positioning point of the element.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mpadded#voffset mpadded#voffset @ MDN]]
    */
  lazy val vOffset: MathMlAttr[String] = stringMathMlAttr("voffset")


  /**
    * A length-percentage indicating the desired width.
    *
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mpadded#width mpadded#width @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mspace#width mspace#width @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtable#width mtable#width @ MDN]]
    */
  lazy val width: MathMlAttr[String] = stringMathMlAttr("width")


  /**
    * Specifies the URI for the MathML namespace (http://www.w3.org/1998/Math/MathML).
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/math math @ MDN]]
    */
  lazy val xmlns: MathMlAttr[String] = stringMathMlAttr("xmlns")


}
