package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait MixBlendMode extends Normal[String] { this: StyleProp[String] =>

  lazy val multiply: StyleSetter[String, String] = this := "multiply"

  lazy val screen: StyleSetter[String, String] = this := "screen"

  lazy val overlay: StyleSetter[String, String] = this := "overlay"

  lazy val darken: StyleSetter[String, String] = this := "darken"

  lazy val lighten: StyleSetter[String, String] = this := "lighten"

  lazy val colorDodge: StyleSetter[String, String] = this := "color-dodge"

  lazy val colorBurn: StyleSetter[String, String] = this := "color-burn"

  lazy val hardLight: StyleSetter[String, String] = this := "hard-light"

  lazy val softLight: StyleSetter[String, String] = this := "soft-light"

  lazy val difference: StyleSetter[String, String] = this := "difference"

  lazy val exclusion: StyleSetter[String, String] = this := "exclusion"

  lazy val hue: StyleSetter[String, String] = this := "hue"

  lazy val saturation: StyleSetter[String, String] = this := "saturation"

  lazy val color: StyleSetter[String, String] = this := "color"

  lazy val luminosity: StyleSetter[String, String] = this := "luminosity"

}
