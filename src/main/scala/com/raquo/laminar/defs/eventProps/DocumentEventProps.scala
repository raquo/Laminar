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
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Document/DOMContentLoaded_event
    */
  lazy val onDomContentLoaded: EventProp[dom.Event] = eventProp("DOMContentLoaded")


  /**
    * The fullscreenchange event is fired immediately after the browser switches into or out of full-screen mode.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Document/fullscreenchange_event
    */
  lazy val onFullScreenChange: EventProp[dom.Event] = eventProp("fullscreenchange")


  /**
    * The fullscreenerror event is fired when the browser cannot switch to full-screen mode.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Document/fullscreenerror_event
    */
  lazy val onFullScreenError: EventProp[dom.Event] = eventProp("fullscreenerror")


  /**
    * The visibilitychange event is fired when the content of a tab has become visible or has been hidden.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Document/visibilitychange_event
    */
  lazy val onVisibilityChange: EventProp[dom.Event] = eventProp("visibilitychange")


}
