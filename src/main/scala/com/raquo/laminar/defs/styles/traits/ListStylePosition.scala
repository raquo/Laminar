package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait ListStylePosition { this: StyleProp[_] =>

  /** The marker box is outside the principal block box. */
  lazy val outside: StyleSetter = this := "outside"

  /**
    * The marker box is the first inline box in the principal block box, after
    * which the element's content flows.
    */
  lazy val inside: StyleSetter = this := "inside"

}
