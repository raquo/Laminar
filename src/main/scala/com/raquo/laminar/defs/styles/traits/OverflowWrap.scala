package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait OverflowWrap extends Normal { this: StyleProp[_] =>

  /** Indicates that lines may only break at normal word break points. */
  override lazy val normal: StyleSetter = this := "normal"

  /**
    * Indicates that normally unbreakable words may be broken at arbitrary
    * points if there are no otherwise acceptable break points in the line.
    */
  lazy val breakWord: StyleSetter = this := "break-word"

}
