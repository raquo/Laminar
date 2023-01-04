package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Line { this: StyleProp[_] =>



  /**
    * Displays a series of rounded dots. The spacing of the dots are not
    * defined by the specification and are implementation-specific. The radius
    * of the dots is half the calculated border-right-width.
    */
  lazy val dotted: StyleSetter = this := "dotted"

  /**
    * Displays a series of short square-ended dashes or line segments. The exact
    * size and Length of the segments are not defined by the specification and
    * are implementation-specific.
    */
  lazy val dashed: StyleSetter = this := "dashed"

  /** Displays a single, straight, solid line. */
  lazy val solid: StyleSetter = this := "solid"



  /**
    * Displays two straight lines that add up to the pixel amount defined as
    * border-width or border-right-width.
    */
  lazy val double: StyleSetter = this := "double"



  /** Displays a border leading to a carved effect. It is the opposite of ridge. */
  lazy val groove: StyleSetter = this := "groove"

  /**
    * Displays a border with a 3D effect, like if it is coming out of the page.
    * It is the opposite of groove.
    */
  lazy val ridge: StyleSetter = this := "ridge"



  /**
    * Displays a border that makes the box appear embedded. It is the opposite
    * of outset. When applied to a table cell with border-collapse set to
    * collapsed, this value behaves like groove.
    */
  lazy val inset: StyleSetter = this := "inset"

  /**
    * Displays a border that makes the box appear in 3D, embossed. It is the
    * opposite of inset. When applied to a table cell with border-collapse set
    * to collapsed, this value behaves like ridge.
    */
  lazy val outset: StyleSetter = this := "outset"

}
