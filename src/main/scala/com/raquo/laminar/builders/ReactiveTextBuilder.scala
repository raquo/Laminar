package com.raquo.laminar.builders

import com.raquo.domtypes.generic
import com.raquo.laminar.nodes.ReactiveText

object ReactiveTextBuilder extends generic.builders.Builder[ReactiveText] {

  override def build(): ReactiveText = {
    new ReactiveText("")
  }
}

