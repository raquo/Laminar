package com.raquo.laminar.setters

import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.experimental.airstream.core.Observable
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

class FocusSetter($isFocused: Observable[Boolean])
  extends Modifier[ReactiveHtmlElement[dom.html.Element]] {

  // @TODO[Convenience] This could use some isMounted lifecycle hook or something so that we can have focused-on-creation elements
  // @TODO[API] Or maybe have a special Laminar-only focus prop?

  override def apply(element: ReactiveHtmlElement[dom.html.Element]): Unit = {

    element.subscribe($isFocused, onNext(_))

    def onNext(isFocused: Boolean): Unit = {
      if (isFocused) {
        element.ref.focus()
      } else {
        element.ref.blur()
      }
    }
  }
}
