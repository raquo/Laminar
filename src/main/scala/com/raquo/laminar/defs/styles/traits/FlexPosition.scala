package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait FlexPosition extends Normal { this: StyleProp[_] =>

  lazy val flexStart: StyleSetter = this := "flex-start"

  lazy val flexEnd: StyleSetter = this := "flex-end"

  lazy val center: StyleSetter = this := "center"

  lazy val start: StyleSetter = this := "start"

  lazy val end: StyleSetter = this := "end"

  lazy val selfStart: StyleSetter = this := "self-start"

  lazy val selfEnd: StyleSetter = this := "self-end"

  lazy val baseline: StyleSetter = this := "baseline"

  lazy val firstBaseline: StyleSetter = this := "first baseline"

  lazy val lastBaseline: StyleSetter = this := "last baseline"

  lazy val stretch: StyleSetter = this := "stretch"

}
