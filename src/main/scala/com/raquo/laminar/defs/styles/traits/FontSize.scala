package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter
import com.raquo.laminar.defs.styles.{units => u}
import com.raquo.laminar.keys.DerivedStyleProp

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait FontSize extends u.Length[DerivedStyleProp] { this: StyleProp[String] =>

  lazy val xxSmall: StyleSetter[String] = this := "xx-small"

  lazy val xSmall: StyleSetter[String] = this := "x-small"

  lazy val small: StyleSetter[String] = this := "small"

  lazy val medium: StyleSetter[String] = this := "medium"

  lazy val large: StyleSetter[String] = this := "large"

  lazy val xLarge: StyleSetter[String] = this := "x-large"

  lazy val xxLarge: StyleSetter[String] = this := "xx-large"

  /**
    * Larger than the parent element's font size, by roughly the ratio used to
    * separate the absolute size keywords above.
    */
  lazy val larger: StyleSetter[String] = this := "larger"

  /**
    * Smaller than the parent element's font size, by roughly the ratio used to
    * separate the absolute size keywords above.
    */
  lazy val smaller: StyleSetter[String] = this := "smaller"

}
