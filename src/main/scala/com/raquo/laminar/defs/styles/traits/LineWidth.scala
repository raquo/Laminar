package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter
import com.raquo.laminar.defs.styles.{units => u}
import com.raquo.laminar.keys.DerivedStyleProp

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait LineWidth extends u.Length[DerivedStyleProp, Int] { this: StyleProp[_] =>

  /** Typically 1px in desktop browsers like Firefox. */
  lazy val thin: StyleSetter = this := "thin"

  /** Typically 3px in desktop browsers like Firefox. */
  lazy val medium: StyleSetter = this := "medium"

  /** Typically 5px in desktop browsers like Firefox. */
  lazy val thick: StyleSetter = this := "thick"

}
