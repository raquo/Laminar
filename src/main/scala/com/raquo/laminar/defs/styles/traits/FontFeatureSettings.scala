package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait FontFeatureSettings extends Normal[String] { this: StyleProp[String] =>

  /** Render fractions as literals (e.g. ½, ¾) */
  lazy val frac: StyleSetter[String, String] = this := "\"frac\""

  /** Render tabular numerals: all digit characters will have the same width */
  lazy val tnum: StyleSetter[String, String] = this := "\"tnum\""

  /** Render zero characters as slashed (Ø) */
  lazy val zero: StyleSetter[String, String] = this := "\"zero\""

}
