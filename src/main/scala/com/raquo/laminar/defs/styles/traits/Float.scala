package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Float extends None { this: StyleProp[_] =>

  /** Element must float on the left side of its containing block. */
  lazy val left: StyleSetter = this := "left"

  /** Element must float on the right side of its containing block. */
  lazy val right: StyleSetter = this := "right"

}
