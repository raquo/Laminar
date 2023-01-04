package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait WhiteSpace extends Normal { this: StyleProp[_] =>

  /**
    * Sequences of whitespace are collapsed. Newline characters in the source
    * are handled as other whitespace. Breaks lines as necessary to fill line
    * boxes.
    */
  override lazy val normal: StyleSetter = this := "normal"

  /**
    * Collapses whitespace as for normal, but suppresses line breaks (text
    * wrapping) within text.
    */
  lazy val nowrap: StyleSetter = this := "nowrap"

  /**
    * Sequences of whitespace are preserved, lines are only broken at newline
    * characters in the source and at br elements.
    */
  lazy val pre: StyleSetter = this := "pre"

  /**
    * Sequences of whitespace are preserved. Lines are broken at newline
    * characters, at br, and as necessary to fill line boxes.
    */
  lazy val preWrap: StyleSetter = this := "pre-wrap"

  /**
    * Sequences of whitespace are collapsed. Lines are broken at newline
    * characters, at br, and as necessary to fill line boxes.
    */
  lazy val preLine: StyleSetter = this := "pre-line"

}
