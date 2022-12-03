package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Direction { this: StyleProp[_] =>

  /** Text and other elements go from left to right. */
  lazy val ltr: StyleSetter = this := "ltr"

  /** Text and other elements go from right to left. */
  lazy val rtl: StyleSetter = this := "rtl"

}
