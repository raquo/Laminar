package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait MixBlendMode extends Normal { this: StyleProp[_] =>

  lazy val multiply: StyleSetter = this := "multiply"

  lazy val screen: StyleSetter = this := "screen"

  lazy val overlay: StyleSetter = this := "overlay"

  lazy val darken: StyleSetter = this := "darken"

  lazy val lighten: StyleSetter = this := "lighten"

  lazy val colorDodge: StyleSetter = this := "color-dodge"

  lazy val colorBurn: StyleSetter = this := "color-burn"

  lazy val hardLight: StyleSetter = this := "hard-light"

  lazy val softLight: StyleSetter = this := "soft-light"

  lazy val difference: StyleSetter = this := "difference"

  lazy val exclusion: StyleSetter = this := "exclusion"

  lazy val hue: StyleSetter = this := "hue"

  lazy val saturation: StyleSetter = this := "saturation"

  lazy val color: StyleSetter = this := "color"

  lazy val luminosity: StyleSetter = this := "luminosity"

}
