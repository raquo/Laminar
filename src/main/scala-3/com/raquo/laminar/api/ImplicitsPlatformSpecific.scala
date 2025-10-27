package com.raquo.laminar.api

/**
  * Scala 3 specific implicit conversions.
  */
trait ImplicitsPlatformSpecific {

  // Does not need to be implicit. It's implicit in Scala 2, and here it's only to help cross compile Laminar.
  @inline def unionOrNull[A, B](u: A | B): A | B | Null = u

}
