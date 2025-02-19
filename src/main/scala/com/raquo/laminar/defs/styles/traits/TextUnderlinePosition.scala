package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait TextUnderlinePosition extends Auto[String] { this: StyleProp[String] =>

  /**
    * This keyword allows the browser to use an algorithm to choose between
    * under and alphabetic.
    */
  override lazy val auto: StyleSetter[String] = this := "auto"

  /**
    * This keyword forces the line to be set below the alphabetic baseline, at
    * a position where it won't cross any descender. This is useful to prevent
    * chemical or mathematical formulas, which make a large use of subscripts,
    * to be illegible.
    */
  lazy val under: StyleSetter[String] = this := "under"

  /**
    * In vertical writing-modes, this keyword forces the line to be placed on
    * the left of the characters. In horizontal writing-modes, it is a synonym
    * of under.
    */
  lazy val left: StyleSetter[String] = this := "left"

  /**
    * In vertical writing-modes, this keyword forces the line to be placed on
    * the right of the characters. In horizontal writing-modes, it is a synonym
    * of under.
    */
  lazy val right: StyleSetter[String] = this := "right"

  lazy val underLeft: StyleSetter[String] = this := "under left"

  lazy val underRight: StyleSetter[String] = this := "under right"

}
