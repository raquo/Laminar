package com.raquo.laminar.keys

import org.scalajs.dom

/**
  * This class represents an HTML Element Event Property. Meaning the key that can be set, not a key-value pair.
  *
  * An example would be an "onclick" property.
  *
  * Note: ReactiveEventProp is implicitly converted to [[EventProcessor]]. See all the useful methods there.
  *
  * @tparam Ev type of DOM Events that the event handler callback for this event prop accepts
  */
class EventProp[Ev <: dom.Event](override val name: String) extends Key
