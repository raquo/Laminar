package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Visibility { this: StyleProp[_] =>

  /** Default value, the box is visible */
  lazy val visible: StyleSetter = this := "visible"

  /**
    * The box is invisible (fully transparent, nothing is drawn), but still
    * affects layout.  Descendants of the element will be visible if they have
    * visibility:visible
    */
  lazy val hidden: StyleSetter = this := "hidden"

  /**
    * For table rows, columns, column groups, and row groups the row(s) or
    * column(s) are hidden and the space they would have occupied is (as if
    * display: none were applied to the column/row of the table)
    */
  lazy val collapse: StyleSetter = this := "collapse"

}
