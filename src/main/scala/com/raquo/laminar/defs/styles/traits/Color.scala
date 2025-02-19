package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Color { this: StyleProp[String] =>

  /**
    * The `currentcolor` keyword represents the value of an element's color property.
    * This lets you use the color value on properties that do not receive it by default.
    */
  lazy val currentColor: StyleSetter[String] = this := "currentcolor"

  lazy val aqua: StyleSetter[String] = this := "aqua"

  lazy val black: StyleSetter[String] = this := "black"

  lazy val blue: StyleSetter[String] = this := "blue"

  lazy val cyan: StyleSetter[String] = this := "cyan"

  lazy val fuchsia: StyleSetter[String] = this := "fuchsia"

  lazy val gray: StyleSetter[String] = this := "gray"

  lazy val green: StyleSetter[String] = this := "green"

  lazy val lime: StyleSetter[String] = this := "lime"

  lazy val maroon: StyleSetter[String] = this := "maroon"

  lazy val navy: StyleSetter[String] = this := "navy"

  lazy val olive: StyleSetter[String] = this := "olive"

  lazy val purple: StyleSetter[String] = this := "purple"

  lazy val silver: StyleSetter[String] = this := "silver"

  lazy val teal: StyleSetter[String] = this := "teal"

  lazy val red: StyleSetter[String] = this := "red"

  lazy val white: StyleSetter[String] = this := "white"

  lazy val yellow: StyleSetter[String] = this := "yellow"

}
