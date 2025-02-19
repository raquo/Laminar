package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait TextDecoration extends None[String] { this: StyleProp[String] =>

  /** Each line of text is underlined. */
  lazy val underline: StyleSetter[String] = this := "underline"

  /** Each line of text has a line above it. */
  lazy val overline: StyleSetter[String] = this := "overline"

  /** Each line of text has a line through the middle. */
  lazy val lineThrough: StyleSetter[String] = this := "line-through"

}
