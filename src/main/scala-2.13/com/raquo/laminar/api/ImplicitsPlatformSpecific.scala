package com.raquo.laminar.api

import com.raquo.laminar.api.StyleUnitsApi.StyleEncoder

import scala.scalajs.js.|

/** Scala 2 specific implicit conversions.
  *
  * Scala 2 does not have real union types, we use [[scala.scalajs.js.|]],
  * which is implemented in userland, and thus has limited capabilities
  * because its suptyping is emulated via implicit conversions.
  *
  * In contrast, in Scala 3, [[scala.scalajs.js.|]] is aliased to real
  * Scala 3 union types, for which the compiler supports subtyping properly.
  */
trait ImplicitsPlatformSpecific {

  // #TODO[Scala3] – is it ok to have those available only in Scala 2?
  //  - It means we can't call these explicitly in code that needs to cross-compile.
  //  - I think it's ok, at least until someone complains.
  //  - There's no private magic here, they're just fancy asInstanceOf-s.

  @inline implicit def styleEncoderUnionToInt(encoder: StyleEncoder[Int | Double]): Int => String = {
    // Safe because Int-s and Double-s have identical runtime representation in Scala.js
    encoder.asInstanceOf[Int => String]
  }

  @inline implicit def styleEncoderUnionToDouble(encoder: StyleEncoder[Int | Double]): Double => String = {
    // Safe because Int-s and Double-s have identical runtime representation in Scala.js
    encoder.asInstanceOf[Double => String]
  }
}
