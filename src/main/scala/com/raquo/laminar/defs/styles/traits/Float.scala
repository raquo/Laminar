package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Float extends None[String] { this: StyleProp[String] =>

  /** Element must float on the left side of its containing block. */
  lazy val left: StyleSetter[String, String] = this := "left"

  /** Element must float on the right side of its containing block. */
  lazy val right: StyleSetter[String, String] = this := "right"

}
