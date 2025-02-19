package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait FontStyle extends Normal[String] { this: StyleProp[String] =>

  /**
    * Selects a font that is labeled italic, if that is not available,
    * one labeled oblique
    */
  lazy val italic: StyleSetter[String] = this := "italic"

  /** Selects a font that is labeled oblique */
  lazy val oblique: StyleSetter[String] = this := "oblique"

}
