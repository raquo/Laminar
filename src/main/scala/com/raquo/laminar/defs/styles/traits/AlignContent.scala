package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait AlignContent extends FlexPosition { this: StyleProp[_] =>

  lazy val spaceBetween: StyleSetter[_] = this := "space-between"

  lazy val spaceAround: StyleSetter[_] = this := "space-around"

  lazy val spaceEvenly: StyleSetter[_] = this := "space-evenly"

}
