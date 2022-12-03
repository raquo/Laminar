package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait TextDecoration extends None { this: StyleProp[_] =>

  /** Each line of text is underlined. */
  lazy val underline: StyleSetter = this := "underline"

  /** Each line of text has a line above it. */
  lazy val overline: StyleSetter = this := "overline"

  /** Each line of text has a line through the middle. */
  lazy val lineThrough: StyleSetter = this := "line-through"

}
