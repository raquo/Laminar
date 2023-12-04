package com.raquo.laminar.defs.eventProps

import com.raquo.laminar.keys.EventProp
import org.scalajs.dom

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

/** Window-only Events */
trait WindowEventProps { this: GlobalEventProps =>




  // -- Window-only Events --


  /**
    * Script to be run after the document is printed
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Window/afterprint_event afterprint_event @ MDN]]
    */
  lazy val onAfterPrint: EventProp[dom.Event] = eventProp("afterprint")


  /**
    * Script to be run before the document is printed
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Window/beforeprint_event beforeprint_event @ MDN]]
    */
  lazy val onBeforePrint: EventProp[dom.Event] = eventProp("beforeprint")


  /**
    * Script to be run when the document is about to be unloaded
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/beforeunload_event beforeunload_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/BeforeUnloadEvent BeforeUnloadEvent @ MDN]]
    */
  lazy val onBeforeUnload: EventProp[dom.BeforeUnloadEvent] = eventProp("beforeunload")


  /**
    * Script to be run when there has been changes to the anchor part of the a URL
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/hashchange_event hashchange_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HashChangeEvent HashChangeEvent @ MDN]]
    */
  lazy val onHashChange: EventProp[dom.HashChangeEvent] = eventProp("hashchange")


  /**
    * Script to be run when an object receives a message
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/message_event message_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MessageEvent MessageEvent @ MDN]]
    */
  lazy val onMessage: EventProp[dom.MessageEvent] = eventProp("message")


  /**
    * Script to be run when an object receives a message that cannot be
    * deserialized and therefore raises an error
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/messageerror_event messageerror_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MessageEvent MessageEvent @ MDN]]
    */
  lazy val onMessageError: EventProp[dom.MessageEvent] = eventProp("messageerror")


  /**
    * Script to be run when the browser starts to work offline
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Window/offline_event offline_event @ MDN]]
    */
  lazy val onOffline: EventProp[dom.Event] = eventProp("offline")


  /**
    * Script to be run when the browser starts to work online
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Window/online_event online_event @ MDN]]
    */
  lazy val onOnline: EventProp[dom.Event] = eventProp("online")


  /**
    * Script to be run when a user navigates away from a page
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/pagehide_event pagehide_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PageTransitionEvent PageTransitionEvent @ MDN]]
    */
  lazy val onPageHide: EventProp[dom.PageTransitionEvent] = eventProp("pagehide")


  /**
    * Script to be run when a user navigates to a page
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/pageshow_event pageshow_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PageTransitionEvent PageTransitionEvent @ MDN]]
    */
  lazy val onPageShow: EventProp[dom.PageTransitionEvent] = eventProp("pageshow")


  /**
    * Script to be run when the window's history changes
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/popstate_event popstate_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PopStateEvent PopStateEvent @ MDN]]
    */
  lazy val onPopState: EventProp[dom.PopStateEvent] = eventProp("popstate")


  /**
    * Script to be run when a Web Storage area is updated
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/storage_event storage_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/StorageEvent StorageEvent @ MDN]]
    */
  lazy val onStorage: EventProp[dom.StorageEvent] = eventProp("storage")


  /**
    * Fires once a page has unloaded (or the browser window has been closed)
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/unload_event unload_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/UIEvent UIEvent @ MDN]]
    */
  lazy val onUnload: EventProp[dom.UIEvent] = eventProp("unload")


}
