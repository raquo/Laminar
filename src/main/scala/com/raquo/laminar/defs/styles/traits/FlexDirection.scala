package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait FlexDirection { this: StyleProp[_] =>

  /**
    * The flex container's main-axis is the same as the block-axis.
    * The main-start and main-end points are the same as the before
    * and after points of the writing-mode.
    */
  lazy val column: StyleSetter = this := "column"

  /** Behaves the same as column but the main-start and main-end are permuted. */
  lazy val columnReverse: StyleSetter = this := "column-reverse"

  /**
    * The flex container's main-axis is defined to be the same as the text direction.
    * The main-start and main-end points are the same as the content direction.
    */
  lazy val row: StyleSetter = this := "row"

  /** Behaves the same as row but the main-start and main-end points are permuted. */
  lazy val rowReverse: StyleSetter = this := "row-reverse"

}
