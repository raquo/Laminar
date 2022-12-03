package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Position { this: StyleProp[_] =>

  /**
    * This keyword let the element use the normal behavior, that is it is laid
    * out in its current position in the flow.  The top, right, bottom, and left
    * properties do not apply.
    */
  lazy val static: StyleSetter = this := "static"

  /**
    * This keyword lays out all elements as though the element were not
    * positioned, and then adjust the element's position, without changing
    * layout (and thus leaving a gap for the element where it would have been
    * had it not been positioned). The effect of position:relative on
    * table-*-group, table-row, table-column, table-cell, and table-caption
    * elements is undefined.
    */
  lazy val relative: StyleSetter = this := "relative"

  /**
    * Do not leave space for the element. Instead, position it at a specified
    * position relative to its closest positioned ancestor or to the containing
    * block. Absolutely positioned boxes can have margins, they do not collapse
    * with any other margins.
    */
  lazy val absolute: StyleSetter = this := "absolute"

  /**
    * Do not leave space for the element. Instead, position it at a specified
    * position relative to the screen's viewport and doesn't move when scrolled.
    * When printing, position it at that fixed position on every page.
    */
  lazy val fixed: StyleSetter = this := "fixed"

}
