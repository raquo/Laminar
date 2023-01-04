package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait TextOverflow { this: StyleProp[_] =>

  /**
    * This keyword value indicates to truncate the text at the limit of the
    * content area, therefore the truncation can happen in the middle of a
    * character. To truncate at the transition between two characters, the
    * empty string value must be used. The value clip is the default for
    * this property.
    */
  lazy val clip: StyleSetter = this := "clip"

  /**
    * This keyword value indicates to display an ellipsis ('…', U+2026 HORIZONTAL
    * ELLIPSIS) to represent clipped text. The ellipsis is displayed inside the
    * content area, decreasing the amount of text displayed. If there is not
    * enough space to display the ellipsis, it is clipped.
    */
  lazy val ellipsis: StyleSetter = this := "ellipsis"

}
