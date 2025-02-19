package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait PageBreak extends Auto[String] { this: StyleProp[String] =>

  /** Always force page breaks. */
  lazy val always: StyleSetter[String] = this := "always"

  /** Avoid page breaks. */
  lazy val avoid: StyleSetter[String] = this := "avoid"

  /** Force page breaks so that the next page is formatted as a left page. */
  lazy val left: StyleSetter[String] = this := "left"

  /** Force page breaks so that the next page is formatted as a right page. */
  lazy val right: StyleSetter[String] = this := "right"

}
