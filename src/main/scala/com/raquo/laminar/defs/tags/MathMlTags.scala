package com.raquo.laminar.defs.tags

import com.raquo.laminar.tags.MathMlTag

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait MathMlTags {


  /**
    * Create MathML tag
    *
    * Note: this simply creates an instance of MathMlTag.
    *  - This does not create the element (to do that, call .apply() on the returned tag instance)
    *
    * @param name - e.g. "mo" or "mtext"
    */
  def mathMlTag(name: String): MathMlTag = new MathMlTag(name)


  // -- MathML Tags --


  /**
    * The <annotation> MathML element is used to include annotations for a MathML expression.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/annotation annotation @ MDN]]
    */
  lazy val annotation: MathMlTag = mathMlTag("annotation")


  /**
    * The top-level element in MathML, representing a mathematical expression.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/math math @ MDN]]
    */
  lazy val mathTag: MathMlTag = mathMlTag("math")


  /**
    * The <merror> MathML element is used to display an error message in a mathematical expression.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/merror merror @ MDN]]
    */
  lazy val merror: MathMlTag = mathMlTag("merror")


  /**
    * Represents a fraction.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mfrac mfrac @ MDN]]
    */
  lazy val mfrac: MathMlTag = mathMlTag("mfrac")


  /**
    * Represents a mathematical identifier, such as a variable name.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mi mi @ MDN]]
    */
  lazy val mi: MathMlTag = mathMlTag("mi")


  /**
    * The <mmultiscripts> MathML element allows the specification of prescripts and postscripts to a base.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mmultiscripts mmultiscripts @ MDN]]
    */
  lazy val mmultiscripts: MathMlTag = mathMlTag("mmultiscripts")


  /**
    * Represents a number in a mathematical expression.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mn mn @ MDN]]
    */
  lazy val mn: MathMlTag = mathMlTag("mn")


  /**
    * Represents a mathematical operator.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mo mo @ MDN]]
    */
  lazy val mo: MathMlTag = mathMlTag("mo")


  /**
    * The <mover> MathML element is used to attach an accent or a limit over an expression. It uses the following syntax: <mover> base overscript </mover>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mover mover @ MDN]]
    */
  lazy val mover: MathMlTag = mathMlTag("mover")


  /**
    * The <mphantom> MathML element is used to hide its content, while still affecting the layout of the expression.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mphantom mphantom @ MDN]]
    */
  lazy val mphantom: MathMlTag = mathMlTag("mphantom")


  /**
    * The <mprescripts> MathML element is used to specify prescripts in a <mmultiscripts> element.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mprescripts mprescripts @ MDN]]
    */
  lazy val mprescripts: MathMlTag = mathMlTag("mprescripts")


  /**
    * Represents a root expression with a specified degree.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mroot mroot @ MDN]]
    */
  lazy val mroot: MathMlTag = mathMlTag("mroot")


  /**
    * The <mrow> MathML element is used to group sub-expressions, representing them horizontally.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mrow mrow @ MDN]]
    */
  lazy val mrow: MathMlTag = mathMlTag("mrow")


  /**
    * Represents a string literal.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/ms ms @ MDN]]
    */
  lazy val ms: MathMlTag = mathMlTag("ms")


  /**
    * The <mspace> MathML element is used to display a blank space, whose size is set by its attributes.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mspace mspace @ MDN]]
    */
  lazy val mspace: MathMlTag = mathMlTag("mspace")


  /**
    * Represents a square root expression.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/msqrt msqrt @ MDN]]
    */
  lazy val msqrt: MathMlTag = mathMlTag("msqrt")


  /**
    * The <mstyle> MathML element is used to change the style of its contents. It is similar to the <style> element in HTML.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mstyle mstyle @ MDN]]
    */
  lazy val mstyle: MathMlTag = mathMlTag("mstyle")


  /**
    * Represents a subscript expression.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/msub msub @ MDN]]
    */
  lazy val msub: MathMlTag = mathMlTag("msub")


  /**
    * Represents a base with both a subscript and a superscript.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/msubsup msubsup @ MDN]]
    */
  lazy val msubsup: MathMlTag = mathMlTag("msubsup")


  /**
    * Represents a superscript expression.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/msup msup @ MDN]]
    */
  lazy val msup: MathMlTag = mathMlTag("msup")


  /**
    * The <mtable> MathML element allows you to create tables or matrices. Its children are <mtr> elements (representing rows), each of them having <mtd> elements as its children (representing cells). These elements are similar to <table>, <tr> and <td> elements of HTML.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtable mtable @ MDN]]
    */
  lazy val mtable: MathMlTag = mathMlTag("mtable")


  /**
    * The <mtd> MathML element represents a cell in a table or a matrix. It may only appear in an <mtr> element.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtd mtd @ MDN]]
    */
  lazy val mtd: MathMlTag = mathMlTag("mtd")


  /**
    * Represents text within a mathematical expression.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtext mtext @ MDN]]
    */
  lazy val mtext: MathMlTag = mathMlTag("mtext")


  /**
    * The <mtr> MathML element represents a row in a table or a matrix. It may only appear in a <mtable> element and its children are <mtd> elements representing cells. This element is similar to the <tr> element of HTML.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/mtr mtr @ MDN]]
    */
  lazy val mtr: MathMlTag = mathMlTag("mtr")


  /**
    * The <munder> MathML element is used to attach an accent or a limit under an expression. It uses the following syntax: <munder> base underscript </munder>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/munder munder @ MDN]]
    */
  lazy val munder: MathMlTag = mathMlTag("munder")


  /**
    * The <munder> MathML element is used to attach an accent or a limit under an expression. It uses the following syntax: <munder> base underscript </munder>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/munderover munderover @ MDN]]
    */
  lazy val munderover: MathMlTag = mathMlTag("munderover")


  /**
    * The <semantics> MathML element is used to annotate a MathML expression with additional information, such as its meaning or its presentation.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/MathML/Element/semantics semantics @ MDN]]
    */
  lazy val semantics: MathMlTag = mathMlTag("semantics")


}
