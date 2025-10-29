package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait ListStyleType extends None[String] { this: StyleProp[String] =>

  /** A filled circle (default value) */
  lazy val disc: StyleSetter[String, String] = this := "disc"

  /** A hollow circle */
  lazy val circle: StyleSetter[String, String] = this := "circle"

  /** A filled square */
  lazy val square: StyleSetter[String, String] = this := "square"

  /** Decimal numbers begining with 1 */
  lazy val decimal: StyleSetter[String, String] = this := "decimal"

  /** Han decimal numbers */
  lazy val cjkDecimal: StyleSetter[String, String] = this := "cjk-decimal"

  /** Decimal numbers padded by initial zeros */
  lazy val decimalLeadingZero: StyleSetter[String, String] = this := "decimal-leading-zero"

  /** Lowercase roman numerals */
  lazy val lowerRoman: StyleSetter[String, String] = this := "lower-roman"

  /** Uppercase roman numerals */
  lazy val upperRoman: StyleSetter[String, String] = this := "upper-roman"

  /** Lowercase classical greek */
  lazy val lowerGreek: StyleSetter[String, String] = this := "lower-greek"

  /** Lowercase ASCII letters */
  lazy val lowerAlpha: StyleSetter[String, String] = this := "lower-alpha"

  /** Lowercase ASCII letters */
  lazy val lowerLatin: StyleSetter[String, String] = this := "lower-latin"

  /** Uppercase ASCII letters */
  lazy val upperAlpha: StyleSetter[String, String] = this := "upper-alpha"

  /** Uppercase ASCII letters */
  lazy val upperLatin: StyleSetter[String, String] = this := "upper-latin"

  /** Traditional Armenian numbering */
  lazy val armenian: StyleSetter[String, String] = this := "armenian"

  /** Traditional Georgian numbering */
  lazy val georgian: StyleSetter[String, String] = this := "georgian"

  /** Traditional Hebrew numbering */
  lazy val hebrew: StyleSetter[String, String] = this := "hebrew"

  /** Japanese Hiragana */
  lazy val hiragana: StyleSetter[String, String] = this := "hiragana"

  /** Japanese Hiragana. Iroha is the old japanese ordering of syllables */
  lazy val hiraganaIroha: StyleSetter[String, String] = this := "hiragana-iroha"

  /** Japanese Katakana */
  lazy val katakana: StyleSetter[String, String] = this := "katakana"

  /** Japanese Katakana. Iroha is the old japanese ordering of syllables */
  lazy val katakanaIroha: StyleSetter[String, String] = this := "katakana-iroha"

}
