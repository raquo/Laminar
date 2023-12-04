package com.raquo.laminar.defs.eventProps

import com.raquo.laminar.keys.EventProp
import org.scalajs.dom

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

/** Document-only Events */
trait DocumentEventProps { this: GlobalEventProps =>




  // -- Document-only Events --


  /**
    * The `DOMContentLoaded` event is fired when the initial HTML document has been completely loaded and parsed,
    * without waiting for stylesheets, images, and subframes to finish loading. A very different event `load`
    * should be used only to detect a fully-loaded page.
    * 
    * It is an incredibly common mistake to use load where DOMContentLoaded would be much more appropriate,
    * so be cautious.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Document/DOMContentLoaded_event DOMContentLoaded_event @ MDN]]
    */
  lazy val onDomContentLoaded: EventProp[dom.Event] = eventProp("DOMContentLoaded")


  /**
    * The fullscreenchange event is fired immediately after the browser switches into or out of full-screen mode.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Document/fullscreenchange_event fullscreenchange_event @ MDN]]
    */
  lazy val onFullScreenChange: EventProp[dom.Event] = eventProp("fullscreenchange")


  /**
    * The fullscreenerror event is fired when the browser cannot switch to full-screen mode.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Document/fullscreenerror_event fullscreenerror_event @ MDN]]
    */
  lazy val onFullScreenError: EventProp[dom.Event] = eventProp("fullscreenerror")


  /**
    * The selectionchange event is fired when the current Selection of a Document is changed.
    * This event is not cancelable and does not bubble.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Document/selectionchange_event selectionchange_event @ MDN]]
    */
  lazy val onSelectionChange: EventProp[dom.Event] = eventProp("selectionchange")


  /**
    * The visibilitychange event is fired when the content of a tab has become visible or has been hidden.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Document/visibilitychange_event visibilitychange_event @ MDN]]
    */
  lazy val onVisibilityChange: EventProp[dom.Event] = eventProp("visibilitychange")


}
