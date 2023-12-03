package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source.EventSource
import com.raquo.laminar.modifiers.Binder
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}

object FocusReceiver {

  def <--(isFocused: EventSource[Boolean]): Binder[ReactiveHtmlElement.Base] = {
    Binder { element =>
      ReactiveElement.bindFn(element, isFocused.toObservable) { shouldFocus =>
        if (shouldFocus) element.ref.focus() else element.ref.blur()
      }
    }
  }
}
