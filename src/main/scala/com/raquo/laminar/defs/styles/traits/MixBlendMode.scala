package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait MixBlendMode extends Normal[String] { this: StyleProp[String] =>

  lazy val multiply: StyleSetter[String] = this := "multiply"

  lazy val screen: StyleSetter[String] = this := "screen"

  lazy val overlay: StyleSetter[String] = this := "overlay"

  lazy val darken: StyleSetter[String] = this := "darken"

  lazy val lighten: StyleSetter[String] = this := "lighten"

  lazy val colorDodge: StyleSetter[String] = this := "color-dodge"

  lazy val colorBurn: StyleSetter[String] = this := "color-burn"

  lazy val hardLight: StyleSetter[String] = this := "hard-light"

  lazy val softLight: StyleSetter[String] = this := "soft-light"

  lazy val difference: StyleSetter[String] = this := "difference"

  lazy val exclusion: StyleSetter[String] = this := "exclusion"

  lazy val hue: StyleSetter[String] = this := "hue"

  lazy val saturation: StyleSetter[String] = this := "saturation"

  lazy val color: StyleSetter[String] = this := "color"

  lazy val luminosity: StyleSetter[String] = this := "luminosity"

}
