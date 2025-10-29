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
  lazy val currentColor: StyleSetter[String, String] = this := "currentcolor"

  lazy val aqua: StyleSetter[String, String] = this := "aqua"

  lazy val black: StyleSetter[String, String] = this := "black"

  lazy val blue: StyleSetter[String, String] = this := "blue"

  lazy val cyan: StyleSetter[String, String] = this := "cyan"

  lazy val fuchsia: StyleSetter[String, String] = this := "fuchsia"

  lazy val gray: StyleSetter[String, String] = this := "gray"

  lazy val green: StyleSetter[String, String] = this := "green"

  lazy val lime: StyleSetter[String, String] = this := "lime"

  lazy val maroon: StyleSetter[String, String] = this := "maroon"

  lazy val navy: StyleSetter[String, String] = this := "navy"

  lazy val olive: StyleSetter[String, String] = this := "olive"

  lazy val purple: StyleSetter[String, String] = this := "purple"

  lazy val silver: StyleSetter[String, String] = this := "silver"

  lazy val teal: StyleSetter[String, String] = this := "teal"

  lazy val red: StyleSetter[String, String] = this := "red"

  lazy val white: StyleSetter[String, String] = this := "white"

  lazy val yellow: StyleSetter[String, String] = this := "yellow"

}
