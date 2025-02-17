package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Color { this: StyleProp[_] =>

  /**
    * The `currentcolor` keyword represents the value of an element's color property.
    * This lets you use the color value on properties that do not receive it by default.
    */
  lazy val currentColor: StyleSetter[_] = this := "currentcolor"

  lazy val aqua: StyleSetter[_] = this := "aqua"

  lazy val black: StyleSetter[_] = this := "black"

  lazy val blue: StyleSetter[_] = this := "blue"

  lazy val cyan: StyleSetter[_] = this := "cyan"

  lazy val fuchsia: StyleSetter[_] = this := "fuchsia"

  lazy val gray: StyleSetter[_] = this := "gray"

  lazy val green: StyleSetter[_] = this := "green"

  lazy val lime: StyleSetter[_] = this := "lime"

  lazy val maroon: StyleSetter[_] = this := "maroon"

  lazy val navy: StyleSetter[_] = this := "navy"

  lazy val olive: StyleSetter[_] = this := "olive"

  lazy val purple: StyleSetter[_] = this := "purple"

  lazy val silver: StyleSetter[_] = this := "silver"

  lazy val teal: StyleSetter[_] = this := "teal"

  lazy val red: StyleSetter[_] = this := "red"

  lazy val white: StyleSetter[_] = this := "white"

  lazy val yellow: StyleSetter[_] = this := "yellow"

}
