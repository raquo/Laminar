package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait MixBlendMode extends Normal { this: StyleProp[_] =>

  lazy val multiply: StyleSetter[_] = this := "multiply"

  lazy val screen: StyleSetter[_] = this := "screen"

  lazy val overlay: StyleSetter[_] = this := "overlay"

  lazy val darken: StyleSetter[_] = this := "darken"

  lazy val lighten: StyleSetter[_] = this := "lighten"

  lazy val colorDodge: StyleSetter[_] = this := "color-dodge"

  lazy val colorBurn: StyleSetter[_] = this := "color-burn"

  lazy val hardLight: StyleSetter[_] = this := "hard-light"

  lazy val softLight: StyleSetter[_] = this := "soft-light"

  lazy val difference: StyleSetter[_] = this := "difference"

  lazy val exclusion: StyleSetter[_] = this := "exclusion"

  lazy val hue: StyleSetter[_] = this := "hue"

  lazy val saturation: StyleSetter[_] = this := "saturation"

  lazy val color: StyleSetter[_] = this := "color"

  lazy val luminosity: StyleSetter[_] = this := "luminosity"

}
