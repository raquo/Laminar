package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait FontWeight extends Normal[String] { this: StyleProp[String] =>

  /** Normal font weight. Same as 400. */
  override lazy val normal: StyleSetter[String] = this := "normal"

  /** Bold font weight. Same as 700. */
  lazy val bold: StyleSetter[String] = this := "bold"

  /**
    * One font weight lighter than the parent element (among the available
    * weights of the font).
    */
  lazy val lighter: StyleSetter[String] = this := "lighter"

  /**
    * One font weight darker than the parent element (among the available
    * weights of the font)
    */
  lazy val bolder: StyleSetter[String] = this := "bolder"

}
