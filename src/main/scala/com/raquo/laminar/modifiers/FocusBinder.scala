package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Observable
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}

object FocusBinder {

  def apply(isFocusedObservable: Observable[Boolean]): Binder[ReactiveHtmlElement.Base] = {
    Binder { element =>
      ReactiveElement.bindFn(element, isFocusedObservable) { isFocused =>
        if (isFocused) element.ref.focus() else element.ref.blur()
      }
    }
  }
}
