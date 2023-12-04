package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Color { this: StyleProp[_] =>

  /**
    * The `currentcolor` keyword represents the value of an element's color property.
    * This lets you use the color value on properties that do not receive it by default.
    */
  lazy val currentColor: StyleSetter = this := "currentcolor"

  lazy val aqua: StyleSetter = this := "aqua"

  lazy val black: StyleSetter = this := "black"

  lazy val blue: StyleSetter = this := "blue"

  lazy val cyan: StyleSetter = this := "cyan"

  lazy val fuschia: StyleSetter = this := "fuschia"

  lazy val gray: StyleSetter = this := "gray"

  lazy val green: StyleSetter = this := "green"

  lazy val lime: StyleSetter = this := "lime"

  lazy val maroon: StyleSetter = this := "maroon"

  lazy val navy: StyleSetter = this := "navy"

  lazy val olive: StyleSetter = this := "olive"

  lazy val purple: StyleSetter = this := "purple"

  lazy val silver: StyleSetter = this := "silver"

  lazy val teal: StyleSetter = this := "teal"

  lazy val red: StyleSetter = this := "red"

  lazy val white: StyleSetter = this := "white"

  lazy val yellow: StyleSetter = this := "yellow"

}
