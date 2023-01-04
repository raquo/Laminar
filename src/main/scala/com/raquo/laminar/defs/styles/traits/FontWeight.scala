package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait FontWeight extends Normal { this: StyleProp[_] =>

  /** Normal font weight. Same as 400. */
  override lazy val normal: StyleSetter = this := "normal"

  /** Bold font weight. Same as 700. */
  lazy val bold: StyleSetter = this := "bold"

  /**
    * One font weight lighter than the parent element (among the available
    * weights of the font).
    */
  lazy val lighter: StyleSetter = this := "lighter"

  /**
    * One font weight darker than the parent element (among the available
    * weights of the font)
    */
  lazy val bolder: StyleSetter = this := "bolder"

}
