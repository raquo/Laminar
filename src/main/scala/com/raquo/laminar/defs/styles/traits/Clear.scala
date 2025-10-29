package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Clear extends None[String] { this: StyleProp[String] =>

  /** The element is moved down to clear past left floats. */
  lazy val left: StyleSetter[String, String] = this := "left"

  /** The element is moved down to clear past right floats. */
  lazy val right: StyleSetter[String, String] = this := "right"

  /** The element is moved down to clear past both left and right floats. */
  lazy val both: StyleSetter[String, String] = this := "both"

}
