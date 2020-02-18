package com.raquo.laminar.keys

import com.raquo.domtypes.generic.keys.EventProp
import org.scalajs.dom

class ReactiveEventProp[Ev <: dom.Event](override val name: String) extends EventProp[Ev](name)
