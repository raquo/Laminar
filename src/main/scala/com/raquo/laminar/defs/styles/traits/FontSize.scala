package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter
import com.raquo.laminar.defs.styles.{units => u}
import com.raquo.laminar.keys.DerivedStyleProp

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait FontSize extends u.Length[DerivedStyleProp, Int] { this: StyleProp[_] =>

  lazy val xxSmall: StyleSetter[_] = this := "xx-small"

  lazy val xSmall: StyleSetter[_] = this := "x-small"

  lazy val small: StyleSetter[_] = this := "small"

  lazy val medium: StyleSetter[_] = this := "medium"

  lazy val large: StyleSetter[_] = this := "large"

  lazy val xLarge: StyleSetter[_] = this := "x-large"

  lazy val xxLarge: StyleSetter[_] = this := "xx-large"

  /**
    * Larger than the parent element's font size, by roughly the ratio used to
    * separate the absolute size keywords above.
    */
  lazy val larger: StyleSetter[_] = this := "larger"

  /**
    * Smaller than the parent element's font size, by roughly the ratio used to
    * separate the absolute size keywords above.
    */
  lazy val smaller: StyleSetter[_] = this := "smaller"

}
