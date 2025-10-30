package com.raquo.laminar.api

trait LaminarPlatformSpecific { this: LaminarAliases =>

  /** Returns a Modifier that applies another Modifier if `condition` is true
    *
    * Note:
    *  - The inner Modifier is evaluated only if `condition` is `true`.
    *    - In evaluated at all, it is evaluated immediately.
    *  - Scala 3 version of `when` supports passing multiple modifiers.
    */
  def when[El <: Element](condition: Boolean)(mod: => Modifier[El]): Modifier[El] = {
    if (condition) {
      mod // implicitly converted to a single modifier
    } else {
      Modifier.empty
    }
  }

  /** Returns a Modifier that applies another Modifier if `condition` is true.
    *
    * Note:
    *  - The inner Modifier is evaluated only if `condition` is `false`.
    *    - In evaluated at all, it is evaluated immediately.
    *  - Scala 3 version of `when` supports passing multiple modifiers.
    */
  @inline def whenNot[El <: Element](condition: Boolean)(mod: => Modifier[El]): Modifier[El] = when(!condition)(mod)
}
