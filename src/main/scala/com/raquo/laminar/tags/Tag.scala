package com.raquo.laminar.tags

import com.raquo.laminar.nodes.ReactiveElement

trait Tag[+El <: ReactiveElement.Base] {

  val name: String

  val void: Boolean
}

object Tag {

  type Base = Tag[ReactiveElement.Base]
}
