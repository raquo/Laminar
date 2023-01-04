package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait ListStyleType extends None { this: StyleProp[_] =>

  /** A filled circle (default value) */
  lazy val disc: StyleSetter = this := "disc"

  /** A hollow circle */
  lazy val circle: StyleSetter = this := "circle"

  /** A filled square */
  lazy val square: StyleSetter = this := "square"

  /** Decimal numbers begining with 1 */
  lazy val decimal: StyleSetter = this := "decimal"

  /** Han decimal numbers */
  lazy val cjkDecimal: StyleSetter = this := "cjk-decimal"

  /** Decimal numbers padded by initial zeros */
  lazy val decimalLeadingZero: StyleSetter = this := "decimal-leading-zero"

  /** Lowercase roman numerals */
  lazy val lowerRoman: StyleSetter = this := "lower-roman"

  /** Uppercase roman numerals */
  lazy val upperRoman: StyleSetter = this := "upper-roman"

  /** Lowercase classical greek */
  lazy val lowerGreek: StyleSetter = this := "lower-greek"

  /** Lowercase ASCII letters */
  lazy val lowerAlpha: StyleSetter = this := "lower-alpha"

  /** Lowercase ASCII letters */
  lazy val lowerLatin: StyleSetter = this := "lower-latin"

  /** Uppercase ASCII letters */
  lazy val upperAlpha: StyleSetter = this := "upper-alpha"

  /** Uppercase ASCII letters */
  lazy val upperLatin: StyleSetter = this := "upper-latin"

  /** Traditional Armenian numbering */
  lazy val armenian: StyleSetter = this := "armenian"

  /** Traditional Georgian numbering */
  lazy val georgian: StyleSetter = this := "georgian"

  /** Traditional Hebrew numbering */
  lazy val hebrew: StyleSetter = this := "hebrew"

  /** Japanese Hiragana */
  lazy val hiragana: StyleSetter = this := "hiragana"

  /** Japanese Hiragana. Iroha is the old japanese ordering of syllables */
  lazy val hiraganaIroha: StyleSetter = this := "hiragana-iroha"

  /** Japanese Katakana */
  lazy val katakana: StyleSetter = this := "katakana"

  /** Japanese Katakana. Iroha is the old japanese ordering of syllables */
  lazy val katakanaIroha: StyleSetter = this := "katakana-iroha"

}
