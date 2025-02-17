package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait FlexPosition extends Normal { this: StyleProp[_] =>

  lazy val flexStart: StyleSetter[_] = this := "flex-start"

  lazy val flexEnd: StyleSetter[_] = this := "flex-end"

  lazy val center: StyleSetter[_] = this := "center"

  lazy val start: StyleSetter[_] = this := "start"

  lazy val end: StyleSetter[_] = this := "end"

  lazy val selfStart: StyleSetter[_] = this := "self-start"

  lazy val selfEnd: StyleSetter[_] = this := "self-end"

  lazy val baseline: StyleSetter[_] = this := "baseline"

  lazy val firstBaseline: StyleSetter[_] = this := "first baseline"

  lazy val lastBaseline: StyleSetter[_] = this := "last baseline"

  lazy val stretch: StyleSetter[_] = this := "stretch"

}
