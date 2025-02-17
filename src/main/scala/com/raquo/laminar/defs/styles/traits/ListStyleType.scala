package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait ListStyleType extends None { this: StyleProp[_] =>

  /** A filled circle (default value) */
  lazy val disc: StyleSetter[_] = this := "disc"

  /** A hollow circle */
  lazy val circle: StyleSetter[_] = this := "circle"

  /** A filled square */
  lazy val square: StyleSetter[_] = this := "square"

  /** Decimal numbers begining with 1 */
  lazy val decimal: StyleSetter[_] = this := "decimal"

  /** Han decimal numbers */
  lazy val cjkDecimal: StyleSetter[_] = this := "cjk-decimal"

  /** Decimal numbers padded by initial zeros */
  lazy val decimalLeadingZero: StyleSetter[_] = this := "decimal-leading-zero"

  /** Lowercase roman numerals */
  lazy val lowerRoman: StyleSetter[_] = this := "lower-roman"

  /** Uppercase roman numerals */
  lazy val upperRoman: StyleSetter[_] = this := "upper-roman"

  /** Lowercase classical greek */
  lazy val lowerGreek: StyleSetter[_] = this := "lower-greek"

  /** Lowercase ASCII letters */
  lazy val lowerAlpha: StyleSetter[_] = this := "lower-alpha"

  /** Lowercase ASCII letters */
  lazy val lowerLatin: StyleSetter[_] = this := "lower-latin"

  /** Uppercase ASCII letters */
  lazy val upperAlpha: StyleSetter[_] = this := "upper-alpha"

  /** Uppercase ASCII letters */
  lazy val upperLatin: StyleSetter[_] = this := "upper-latin"

  /** Traditional Armenian numbering */
  lazy val armenian: StyleSetter[_] = this := "armenian"

  /** Traditional Georgian numbering */
  lazy val georgian: StyleSetter[_] = this := "georgian"

  /** Traditional Hebrew numbering */
  lazy val hebrew: StyleSetter[_] = this := "hebrew"

  /** Japanese Hiragana */
  lazy val hiragana: StyleSetter[_] = this := "hiragana"

  /** Japanese Hiragana. Iroha is the old japanese ordering of syllables */
  lazy val hiraganaIroha: StyleSetter[_] = this := "hiragana-iroha"

  /** Japanese Katakana */
  lazy val katakana: StyleSetter[_] = this := "katakana"

  /** Japanese Katakana. Iroha is the old japanese ordering of syllables */
  lazy val katakanaIroha: StyleSetter[_] = this := "katakana-iroha"

}
