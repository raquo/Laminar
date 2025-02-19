package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait FlexPosition extends Normal[String] { this: StyleProp[String] =>

  lazy val flexStart: StyleSetter[String] = this := "flex-start"

  lazy val flexEnd: StyleSetter[String] = this := "flex-end"

  lazy val center: StyleSetter[String] = this := "center"

  lazy val start: StyleSetter[String] = this := "start"

  lazy val end: StyleSetter[String] = this := "end"

  lazy val selfStart: StyleSetter[String] = this := "self-start"

  lazy val selfEnd: StyleSetter[String] = this := "self-end"

  lazy val baseline: StyleSetter[String] = this := "baseline"

  lazy val firstBaseline: StyleSetter[String] = this := "first baseline"

  lazy val lastBaseline: StyleSetter[String] = this := "last baseline"

  lazy val stretch: StyleSetter[String] = this := "stretch"

}
