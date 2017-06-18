package com.raquo.laminar.builders

import com.raquo.dombuilder.generic
import com.raquo.laminar.nodes.ReactiveComment

object ReactiveCommentBuilder extends generic.builders.Builder[ReactiveComment] {

  override def build(): ReactiveComment = {
    new ReactiveComment("")
  }
}
