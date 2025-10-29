package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait BorderCollapse { this: StyleProp[String] =>

  /** Use separated-border table rendering model. This is the default. */
  lazy val separate: StyleSetter[String, String] = this := "separate"

  /** Use collapsed-border table rendering model. */
  lazy val collapse: StyleSetter[String, String] = this := "collapse"

}
