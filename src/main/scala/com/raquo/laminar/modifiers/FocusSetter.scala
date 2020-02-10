package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}

object FocusSetter {

  def apply(isFocusedObservable: Observable[Boolean]): Setter[ReactiveHtmlElement.Base] = {
    Setter { element =>
      ReactiveElement.bindFn(element, isFocusedObservable) { isFocused =>
        if (isFocused) element.ref.focus() else element.ref.blur()
      }
    }
  }
}
