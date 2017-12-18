package com.raquo.laminar.setters

import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.xstream.{Listener, XStream}
import org.scalajs.dom

class FocusSetter($isFocused: XStream[Boolean])
  extends Modifier[ReactiveElement[dom.html.Element]] {

  // @TODO[Convenience] This could use some isMounted lifecycle hook or something so that we can have focused-on-creation elements
  // @TODO[API] Or maybe have a special Laminar-only focus prop?

  override def apply(element: ReactiveElement[dom.html.Element]): Unit = {

    element.subscribe($isFocused, Listener(onNext = onNext))

    def onNext(isFocused: Boolean): Unit = {
      if (isFocused) {
        element.ref.focus()
      } else {
        element.ref.blur()
      }
    }
  }
}
