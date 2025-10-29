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

  lazy val xxSmall: StyleSetter[String, String] = this := "xx-small"

  lazy val xSmall: StyleSetter[String, String] = this := "x-small"

  lazy val small: StyleSetter[String, String] = this := "small"

  lazy val medium: StyleSetter[String, String] = this := "medium"

  lazy val large: StyleSetter[String, String] = this := "large"

  lazy val xLarge: StyleSetter[String, String] = this := "x-large"

  lazy val xxLarge: StyleSetter[String, String] = this := "xx-large"

  /**
    * Larger than the parent element's font size, by roughly the ratio used to
    * separate the absolute size keywords above.
    */
  lazy val larger: StyleSetter[String, String] = this := "larger"

  /**
    * Smaller than the parent element's font size, by roughly the ratio used to
    * separate the absolute size keywords above.
    */
  lazy val smaller: StyleSetter[String, String] = this := "smaller"

}
