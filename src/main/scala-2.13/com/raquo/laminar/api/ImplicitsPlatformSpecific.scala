package com.raquo.laminar.api

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.ImplicitsPlatformSpecific.DomKeysExt
import com.raquo.laminar.api.StyleUnitsApi.StyleEncoder
import com.raquo.laminar.domapi.DomKeys
import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.nodes.ReactiveHtmlElement

import scala.scalajs.js.|

/** Scala 2 specific implicit conversions.
  *
  * Scala 2 does not have real union types, we use [[scala.scalajs.js.|]],
  * which is implemented in userland, and thus has limited capabilities.
  *
  * In contrast, in Scala 3, [[scala.scalajs.js.|]] is aliased to real
  * Scala 3 union types, which are smart enough to not need these conversions.
  */
trait ImplicitsPlatformSpecific {

  // #TODO[Scala3] – is it ok to have those available only in Scala 2?
  //  - It means we can't call these explicitly in code that needs to cross-compile.
  //  - I think it's ok, at least until someone complains.
  //  - There's no private magic here, they're just fancy asInstanceOf-s.

  @inline implicit def mergeUnion[A](u: A | A): A = u.merge

  @inline implicit def mergeUnion2[A, B](u: A | B | B): A | B = u.asInstanceOf[A | B]

  @inline implicit def unionOrNull[A, B](u: A | B): A | B | Null = u.asInstanceOf[A | B | Null]

  @inline implicit def expandCovariantIntoUnion2[A1, A2](u: Source[A1]): Source[A1 | A2] = u.asInstanceOf[Source[A1 | A2]]

  @inline implicit def expandCovariantIntoUnion3a[A1, A2, A3](u: Source[A1]): Source[A1 | A2 | A3] = u.asInstanceOf[Source[A1 | A2 | A3]]

  @inline implicit def expandCovariantIntoUnion3b[A1, A2, A3](u: Source[A2]): Source[A1 | A2 | A3] = u.asInstanceOf[Source[A1 | A2 | A3]]

  // @inline implicit def expandCovariantIntoUnion3c[A1, A2, A3](u: Source[A3]): Source[A1 | A2 | A3] = u.asInstanceOf[Source[A1 | A2 | A3]]

  // @inline implicit def foldCovariantUnion[A, B, C[+_]](u: C[A | B]): C[A] = u.merge

  @inline implicit def foldUnionSource[A](u: Source[A | A]): Source[A] = u.asInstanceOf[Source[A]]

  @inline implicit def styleEncoderUnionToInt2(encoder: StyleEncoder[Int | Double]): Int => String | String = {
    // Safe because Int-s and Double-s have identical runtime representation in Scala.js
    encoder.asInstanceOf[Int => String | String]
  }

  @inline implicit def styleEncoderUnionToDouble2(encoder: StyleEncoder[Int | Double]): Double => String | String = {
    // Safe because Int-s and Double-s have identical runtime representation in Scala.js
    encoder.asInstanceOf[Double => String | String]
  }

  implicit def domKeysExt(d: DomKeys): DomKeysExt = new DomKeysExt(d)
}

object ImplicitsPlatformSpecific {

  class DomKeysExt(val d: DomKeys) extends AnyVal {

    def setHtmlStyle[V](
      element: ReactiveHtmlElement.Base,
      prop: StyleProp[V],
      value: V | String
    ): Unit = {
      d.setHtmlStyle(element, prop, value.asInstanceOf[V | String | Null])
    }
  }
}
