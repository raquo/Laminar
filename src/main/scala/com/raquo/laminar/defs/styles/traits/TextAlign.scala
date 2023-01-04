package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait TextAlign { this: StyleProp[_] =>

  /** `left` if direction is left-to-right and `right` otherwise. */
  lazy val start: StyleSetter = this := "start"

  /** `right` if direction is left-to-right and `left` otherwise. */
  lazy val end: StyleSetter = this := "end"

  /** The inline contents are aligned to the left edge of the line box. */
  lazy val left: StyleSetter = this := "left"

  /** The inline contents are aligned to the right edge of the line box. */
  lazy val right: StyleSetter = this := "right"

  /** The inline contents are centered within the line box. */
  lazy val center: StyleSetter = this := "center"

  /**
    * The text is justified. Text should line up their left and right edges to
    * the left and right content edges of the paragraph.
    */
  lazy val justify: StyleSetter = this := "justify"

}
