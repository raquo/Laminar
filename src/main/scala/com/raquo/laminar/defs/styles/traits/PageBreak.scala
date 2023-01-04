package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait PageBreak extends Auto { this: StyleProp[_] =>

  /** Always force page breaks. */
  lazy val always: StyleSetter = this := "always"

  /** Avoid page breaks. */
  lazy val avoid: StyleSetter = this := "avoid"

  /** Force page breaks so that the next page is formatted as a left page. */
  lazy val left: StyleSetter = this := "left"

  /** Force page breaks so that the next page is formatted as a right page. */
  lazy val right: StyleSetter = this := "right"

}
