package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Overflow extends Auto { this: StyleProp[_] =>

  /**
    * Default value. Content is not clipped, it may be rendered outside the
    * content box.
    */
  lazy val visible: StyleSetter = this := "visible"

  /** The content is clipped and no scrollbars are provided. */
  lazy val hidden: StyleSetter = this := "hidden"

  /**
    * The content is clipped and desktop browsers use scrollbars, whether or
    * not any content is clipped. This avoids any problem with scrollbars
    * appearing and disappearing in a dynamic environment. Printers may print
    * overflowing content.
    */
  lazy val scroll: StyleSetter = this := "scroll"

}
