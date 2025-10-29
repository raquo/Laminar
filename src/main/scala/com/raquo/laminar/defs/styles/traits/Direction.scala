package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Direction { this: StyleProp[String] =>

  /** Text and other elements go from left to right. */
  lazy val ltr: StyleSetter[String, String] = this := "ltr"

  /** Text and other elements go from right to left. */
  lazy val rtl: StyleSetter[String, String] = this := "rtl"

}
