package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter
import com.raquo.laminar.defs.styles.{units => u}
import com.raquo.laminar.keys.DerivedStyleProp

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait VerticalAlign extends u.Length[DerivedStyleProp] { this: StyleProp[String] =>

  /**
    * Aligns the baseline of the element with the baseline of its parent. The
    * baseline of some replaced elements, like textarea is not specified by
    * the HTML specification, meaning that their behavior with this keyword may
    * change from one browser to the other.
    */
  lazy val baseline: StyleSetter[String] = this := "baseline"

  /**
    * Aligns the baseline of the element with the subscript-baseline of its
    * parent.
    */
  lazy val sub: StyleSetter[String] = this := "sub"

  /**
    * Aligns the baseline of the element with the superscript-baseline of its
    * parent.
    */
  lazy val `super`: StyleSetter[String] = this := "super"

  /** Aligns the top of the element with the top of the parent element's font. */
  lazy val textTop: StyleSetter[String] = this := "text-top"

  /**
    * Aligns the bottom of the element with the bottom of the parent element's
    * font.
    */
  lazy val textBottom: StyleSetter[String] = this := "text-bottom"

  /**
    * Aligns the middle of the element with the middle of lowercase letters in
    * the parent.
    */
  lazy val middle: StyleSetter[String] = this := "middle"

  /**
    * Aligns the top of the element and its descendants with the top of the
    * entire line.
    */
  lazy val top: StyleSetter[String] = this := "top"

  /**
    * Aligns the bottom of the element and its descendants with the bottom of
    * the entire line.
    */
  lazy val bottom: StyleSetter[String] = this := "bottom"

}
