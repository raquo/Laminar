package com.raquo.laminar.api

trait LaminarPlatformSpecific { this: LaminarAliases =>

  /** Returns a Modifier that applies one or more Modifiers if `condition` is `true`.
    * Note: The inner Modifier-s are evaluated only if the condition is `true`.
    */
  def when[El <: Element](condition: Boolean)(mods: => Modifier[El]*): Modifier[El] = {
    if (condition) {
      mods // implicitly converted to a single modifier
    } else {
      Modifier.empty
    }
  }

  /** Returns a Modifier that applies one or more modifiers if `condition` is `true`.
    * Note: The inner Modifier-s are evaluated only if the condition is `false`.
    */
  @inline def whenNot[El <: Element](condition: Boolean)(mods: => Modifier[El]*): Modifier[El] = when(!condition)(mods)
}
