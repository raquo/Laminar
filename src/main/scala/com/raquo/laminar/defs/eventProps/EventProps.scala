package com.raquo.laminar.defs.eventProps

import com.raquo.laminar.keys.EventProp
import org.scalajs.dom

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait EventProps {


  def eventProp[Ev <: dom.Event](key: String): EventProp[Ev] = new EventProp(key)


  // -- Mouse Events --


  /**
    * The click event is raised when the user clicks on an element. The click
    * event will occur after the mousedown and mouseup events.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/click_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent
    */
  lazy val onClick: EventProp[dom.MouseEvent] = eventProp("click")


  /**
    * The dblclick event is fired when a pointing device button (usually a
    * mouse button) is clicked twice on a single element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/dblclick_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent
    */
  lazy val onDblClick: EventProp[dom.MouseEvent] = eventProp("dblclick")


  /**
    * The mousedown event is raised when the user presses the mouse button.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/mousedown_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent
    */
  lazy val onMouseDown: EventProp[dom.MouseEvent] = eventProp("mousedown")


  /**
    * The mousemove event is raised when the user moves the mouse.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/mousemove_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent
    */
  lazy val onMouseMove: EventProp[dom.MouseEvent] = eventProp("mousemove")


  /**
    * The mouseout event is raised when the mouse leaves an element (e.g, when
    * the mouse moves off of an image in the web page, the mouseout event is
    * raised for that image element).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/mouseout_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent
    */
  lazy val onMouseOut: EventProp[dom.MouseEvent] = eventProp("mouseout")


  /**
    * The mouseover event is raised when the user moves the mouse over a
    * particular element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/mouseover_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent
    */
  lazy val onMouseOver: EventProp[dom.MouseEvent] = eventProp("mouseover")


  /**
    * The mouseleave event is fired when the pointer of a pointing device (usually a mouse) is
    * moved out of an element.
    * 
    * mouseleave and mouseout are similar but differ in that mouseleave does not bubble and mouseout does.
    * 
    * This means that mouseleave is fired when the pointer has exited the element and all of its descendants,
    * whereas mouseout is fired when the pointer leaves the element or leaves one of the element's
    * descendants (even if the pointer is still within the element).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/mouseleave_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent
    */
  lazy val onMouseLeave: EventProp[dom.MouseEvent] = eventProp("mouseleave")


  /**
    * The mouseenter event is fired when a pointing device (usually a mouse) is moved over
    * the element that has the listener attached.
    * 
    * Similar to mouseover, it differs in that it doesn't bubble and that it isn't sent
    * when the pointer is moved from one of its descendants' physical space to its own physical space.
    * 
    * With deep hierarchies, the amount of mouseenter events sent can be quite huge and cause
    * significant performance problems. In such cases, it is better to listen for mouseover events.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/mouseenter_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent
    */
  lazy val onMouseEnter: EventProp[dom.MouseEvent] = eventProp("mouseenter")


  /**
    * The mouseup event is raised when the user releases the mouse button.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/mouseup_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent
    */
  lazy val onMouseUp: EventProp[dom.MouseEvent] = eventProp("mouseup")


  /**
    * Fires when the mouse wheel rolls up or down over an element
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/wheel_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/WheelEvent
    */
  lazy val onWheel: EventProp[dom.WheelEvent] = eventProp("wheel")


  /**
    * Script to be run when a context menu is triggered
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/contextmenu_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent
    */
  lazy val onContextMenu: EventProp[dom.MouseEvent] = eventProp("contextmenu")


  /**
    * Script to be run when an element is dragged
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/drag_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/DragEvent
    */
  lazy val onDrag: EventProp[dom.DragEvent] = eventProp("drag")


  /**
    * Script to be run at the end of a drag operation
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dragend_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/DragEvent
    */
  lazy val onDragEnd: EventProp[dom.DragEvent] = eventProp("dragend")


  /**
    * Script to be run when an element has been dragged to a valid drop target
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dragenter_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/DragEvent
    */
  lazy val onDragEnter: EventProp[dom.DragEvent] = eventProp("dragenter")


  /**
    * Script to be run when an element leaves a valid drop target
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dragleave_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/DragEvent
    */
  lazy val onDragLeave: EventProp[dom.DragEvent] = eventProp("dragleave")


  /**
    * Script to be run when an element is being dragged over a valid drop target
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dragover_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/DragEvent
    */
  lazy val onDragOver: EventProp[dom.DragEvent] = eventProp("dragover")


  /**
    * Script to be run at the start of a drag operation
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dragstart_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/DragEvent
    */
  lazy val onDragStart: EventProp[dom.DragEvent] = eventProp("dragstart")


  /**
    * Script to be run when dragged element is being dropped
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/drop_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/DragEvent
    */
  lazy val onDrop: EventProp[dom.DragEvent] = eventProp("drop")


  // -- Pointer Events --


  /**
    * fired when a pointing device is moved into an element's hit test boundaries.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerover_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent
    */
  lazy val onPointerOver: EventProp[dom.PointerEvent] = eventProp("pointerover")


  /**
    * fired when a pointing device is moved into the hit test boundaries of an element
    * or one of its descendants, including as a result of a pointerdown event
    * from a device that does not support hover (see pointerdown).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerenter_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent
    */
  lazy val onPointerEnter: EventProp[dom.PointerEvent] = eventProp("pointerenter")


  /**
    * fired when a pointer becomes active.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerdown_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent
    */
  lazy val onPointerDown: EventProp[dom.PointerEvent] = eventProp("pointerdown")


  /**
    * fired when a pointer changes coordinates.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/pointermove_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent
    */
  lazy val onPointerMove: EventProp[dom.PointerEvent] = eventProp("pointermove")


  /**
    * fired when a pointer is no longer active.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerup_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent
    */
  lazy val onPointerUp: EventProp[dom.PointerEvent] = eventProp("pointerup")


  /**
    * a browser fires this event if it concludes the pointer will no longer be able
    * to generate events (for example the related device is deactived).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/pointercancel_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent
    */
  lazy val onPointerCancel: EventProp[dom.PointerEvent] = eventProp("pointercancel")


  /**
    * fired for several reasons including: pointing device is moved out of
    * the hit test boundaries of an element;
    * firing the pointerup event for a device that does not support hover (see pointerup);
    * after firing the pointercancel event (see pointercancel);
    * when a pen stylus leaves the hover range detectable by the digitizer.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerout_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent
    */
  lazy val onPointerOut: EventProp[dom.PointerEvent] = eventProp("pointerout")


  /**
    * fired when a pointing device is moved out of the hit test boundaries of an element.
    * For pen devices, this event is fired when the stylus leaves the hover range detectable by the digitizer.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerleave_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent
    */
  lazy val onPointerLeave: EventProp[dom.PointerEvent] = eventProp("pointerleave")


  /**
    * fired when an element receives pointer capture.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/gotpointercapture_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent
    */
  lazy val gotPointerCapture: EventProp[dom.PointerEvent] = eventProp("gotpointercapture")


  /**
    * Fired after pointer capture is released for a pointer.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/lostpointercapture_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent
    */
  lazy val lostPointerCapture: EventProp[dom.PointerEvent] = eventProp("lostpointercapture")


  // -- Form Events --


  /**
    * The change event is fired for input, select, and textarea elements
    * when a change to the element's value is committed by the user.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/change_event
    */
  lazy val onChange: EventProp[dom.Event] = eventProp("change")


  /**
    * The select event only fires when text inside a text input or textarea is
    * selected. The event is fired after the text has been selected.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/select_event
    */
  lazy val onSelect: EventProp[dom.Event] = eventProp("select")


  /**
    * The DOM beforeinput event fires when the value of an <input>, or <textarea>
    * element is about to be modified. The event also applies to elements with
    * contenteditable enabled, and to any element when designMode is turned on.
    * 
    * 
    * @note IE does not support this event.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/beforeinput_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/InputEvent
    */
  lazy val onBeforeInput: EventProp[dom.InputEvent] = eventProp("beforeinput")


  /**
    * The input event is fired for input, select, textarea, and
    * contentEditable elements when it gets user input.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/input_event
    */
  lazy val onInput: EventProp[dom.Event] = eventProp("input")


  /**
    * The blur event is raised when an element loses focus.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/blur_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/FocusEvent
    */
  lazy val onBlur: EventProp[dom.FocusEvent] = eventProp("blur")


  /**
    * The focus event is raised when the user sets focus on the given element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/focus_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/FocusEvent
    */
  lazy val onFocus: EventProp[dom.FocusEvent] = eventProp("focus")


  /**
    * The submit event is fired when the user clicks a submit button in a form
    * (<input type="submit"/>).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLFormElement/submit_event
    */
  lazy val onSubmit: EventProp[dom.Event] = eventProp("submit")


  /**
    * The reset event is fired when a form is reset.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLFormElement/reset_event
    */
  lazy val onReset: EventProp[dom.Event] = eventProp("reset")


  /**
    * Script to be run when an element is invalid
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLInputElement/invalid_event
    */
  lazy val onInvalid: EventProp[dom.Event] = eventProp("invalid")


  /**
    * Fires when the user writes something in a search field (for <input="search">)
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLInputElement/search_event
    */
  lazy val onSearch: EventProp[dom.Event] = eventProp("search")


  // -- Keyboard Events --


  /**
    * The keydown event is raised when the user presses a keyboard key.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/keydown_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent
    */
  lazy val onKeyDown: EventProp[dom.KeyboardEvent] = eventProp("keydown")


  /**
    * The keyup event is raised when the user releases a key that's been pressed.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/keyup_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent
    */
  lazy val onKeyUp: EventProp[dom.KeyboardEvent] = eventProp("keyup")


  /**
    * The keypress event should be raised when the user presses a key on the keyboard.
    * However, not all browsers fire keypress events for certain keys.
    * 
    * Webkit-based browsers (Google Chrome and Safari, for example) do not fire keypress events on the arrow keys.
    * Firefox does not fire keypress events on modifier keys like SHIFT.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/keypress_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent
    */
  lazy val onKeyPress: EventProp[dom.KeyboardEvent] = eventProp("keypress")


  // -- Clipboard Events --


  /**
    * Fires when the user copies the content of an element
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/copy_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/ClipboardEvent
    */
  lazy val onCopy: EventProp[dom.ClipboardEvent] = eventProp("copy")


  /**
    * Fires when the user cuts the content of an element
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/cut_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/ClipboardEvent
    */
  lazy val onCut: EventProp[dom.ClipboardEvent] = eventProp("cut")


  /**
    * Fires when the user pastes some content in an element
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/paste_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/ClipboardEvent
    */
  lazy val onPaste: EventProp[dom.ClipboardEvent] = eventProp("paste")


  // -- Media Events --


  /**
    * Script to be run on abort
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/abort_event
    */
  lazy val onAbort: EventProp[dom.Event] = eventProp("abort")


  /**
    * Script to be run when a file is ready to start playing (when it has buffered enough to begin)
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/canplay_event
    */
  lazy val onCanPlay: EventProp[dom.Event] = eventProp("canplay")


  /**
    * Script to be run when a file can be played all the way to the end without pausing for buffering
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/canplaythrough_event
    */
  lazy val onCanPlayThrough: EventProp[dom.Event] = eventProp("canplaythrough")


  /**
    * Script to be run when the cue changes in a <track> element
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/TextTrack/cuechange_event
    */
  lazy val onCueChange: EventProp[dom.Event] = eventProp("cuechange")


  /**
    * Script to be run when the length of the media changes
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/durationchange_event
    */
  lazy val onDurationChange: EventProp[dom.Event] = eventProp("durationchange")


  /**
    * Script to be run when something bad happens and the file is suddenly unavailable (like unexpectedly disconnects)
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/emptied_event
    */
  lazy val onEmptied: EventProp[dom.Event] = eventProp("emptied")


  /**
    * Script to be run when the media has reach the end (a useful event for messages like "thanks for listening")
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/ended_event
    */
  lazy val onEnded: EventProp[dom.Event] = eventProp("ended")


  /**
    * Script to be run when media data is loaded
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/loadeddata_event
    */
  lazy val onLoadedData: EventProp[dom.Event] = eventProp("loadeddata")


  /**
    * Script to be run when meta data (like dimensions and duration) are loaded
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/loadedmetadata_event
    */
  lazy val onLoadedMetadata: EventProp[dom.Event] = eventProp("loadedmetadata")


  /**
    * Script to be run just as the file begins to load before anything is actually loaded
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/loadstart_event
    */
  lazy val onLoadStart: EventProp[dom.Event] = eventProp("loadstart")


  /**
    * Script to be run when the media is paused either by the user or programmatically
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/pause_event
    */
  lazy val onPause: EventProp[dom.Event] = eventProp("pause")


  /**
    * Script to be run when the media is ready to start playing
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/play_event
    */
  lazy val onPlay: EventProp[dom.Event] = eventProp("play")


  /**
    * Script to be run when the media actually has started playing
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/playing_event
    */
  lazy val onPlaying: EventProp[dom.Event] = eventProp("playing")


  /**
    * Script to be run when the browser is in the process of getting the media data
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/progress_event
    */
  lazy val onProgress: EventProp[dom.Event] = eventProp("progress")


  /**
    * Script to be run each time the playback rate changes (like when a user switches to a slow motion or fast forward mode)
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/ratechange_event
    */
  lazy val onRateChange: EventProp[dom.Event] = eventProp("ratechange")


  /**
    * Script to be run when the seeking attribute is set to false indicating that seeking has ended
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/seeked_event
    */
  lazy val onSeeked: EventProp[dom.Event] = eventProp("seeked")


  /**
    * Script to be run when the seeking attribute is set to true indicating that seeking is active
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/seeking_event
    */
  lazy val onSeeking: EventProp[dom.Event] = eventProp("seeking")


  /**
    * Script to be run when the browser is unable to fetch the media data for whatever reason
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/stalled_event
    */
  lazy val onStalled: EventProp[dom.Event] = eventProp("stalled")


  /**
    * Script to be run when fetching the media data is stopped before it is completely loaded for whatever reason
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/suspend_event
    */
  lazy val onSuspend: EventProp[dom.Event] = eventProp("suspend")


  /**
    * Script to be run when the playing position has changed (like when the user fast forwards to a different point in the media)
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/timeupdate_event
    */
  lazy val onTimeUpdate: EventProp[dom.Event] = eventProp("timeupdate")


  /**
    * Script to be run each time the volume is changed which (includes setting the volume to "mute")
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/volumechange_event
    */
  lazy val onVolumeChange: EventProp[dom.Event] = eventProp("volumechange")


  /**
    * Script to be run when the media has paused but is expected to resume (like when the media pauses to buffer more data)
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/waiting_event
    */
  lazy val onWaiting: EventProp[dom.Event] = eventProp("waiting")


  // -- Misc Events --


  /**
    * The animationend event is event fires when a CSS animation reaches the end of its active period.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/animationend_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/AnimationEvent
    */
  lazy val onAnimationEnd: EventProp[dom.AnimationEvent] = eventProp("animationend")


  /**
    * The animationiteration event is sent when a CSS animation reaches the end of an iteration. An iteration ends
    * when a single pass through the sequence of animation instructions is completed by executing the last
    * animation step.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/animationiteration_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/AnimationEvent
    */
  lazy val onAnimationIteration: EventProp[dom.AnimationEvent] = eventProp("animationiteration")


  /**
    * The animationstart event is sent when a CSS Animation starts to play.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/animationstart_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/AnimationEvent
    */
  lazy val onAnimationStart: EventProp[dom.AnimationEvent] = eventProp("animationstart")


  /**
    * The onload property of the GlobalEventHandlers mixin is an event handler
    * for the load event of a Window, XMLHttpRequest, <img> element, etc.,
    * which fires when the resource has loaded.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/load_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/UIEvent
    */
  lazy val onLoad: EventProp[dom.UIEvent] = eventProp("load")


  /**
    * The GlobalEventHandlers.onresize property contains an EventHandler
    * triggered when a resize event is received.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/resize_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/UIEvent
    */
  lazy val onResize: EventProp[dom.UIEvent] = eventProp("resize")


  /**
    * An event handler for scroll events on element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/scroll_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/UIEvent
    */
  lazy val onScroll: EventProp[dom.UIEvent] = eventProp("scroll")


  /**
    * Fires when a <menu> element is shown as a context menu
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/show_event
    */
  lazy val onShow: EventProp[dom.Event] = eventProp("show")


  /**
    * Fires when the user opens or closes the <details> element
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/toggle_event
    */
  lazy val onToggle: EventProp[dom.Event] = eventProp("toggle")


  /**
    * The `transitionend` event is sent to when a CSS transition completes.
    * 
    * @note If the transition is removed from its target node before the transition completes execution, the
    *       `transitionend` event won't be generated. One way this can happen is by changing the value of the
    *       `transition-property` attribute which applies to the target. Another is if the `display` attribute is set to
    *       `none`.
    * 
    * @see [[https://developer.mozilla.org/en-US/docs/Web/API/GlobalEventHandlers/ontransitionend MDN]]
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/transitionend_event
    */
  lazy val onTransitionEnd: EventProp[dom.Event] = eventProp("transitionend")


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


  // -- Window-only Events --


  /**
    * Script to be run after the document is printed
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/afterprint_event
    */
  lazy val onAfterPrint: EventProp[dom.Event] = eventProp("afterprint")


  /**
    * Script to be run before the document is printed
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/beforeprint_event
    */
  lazy val onBeforePrint: EventProp[dom.Event] = eventProp("beforeprint")


  /**
    * Script to be run when the document is about to be unloaded
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/beforeunload_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/BeforeUnloadEvent
    */
  lazy val onBeforeUnload: EventProp[dom.BeforeUnloadEvent] = eventProp("beforeunload")


  /**
    * Script to be run when there has been changes to the anchor part of the a URL
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/hashchange_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/HashChangeEvent
    */
  lazy val onHashChange: EventProp[dom.HashChangeEvent] = eventProp("hashchange")


  /**
    * Script to be run when an object receives a message
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/message_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MessageEvent
    */
  lazy val onMessage: EventProp[dom.MessageEvent] = eventProp("message")


  /**
    * Script to be run when an object receives a message that cannot be
    * deserialized and therefore raises an error
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/messageerror_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/MessageEvent
    */
  lazy val onMessageError: EventProp[dom.MessageEvent] = eventProp("messageerror")


  /**
    * Script to be run when the browser starts to work offline
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/offline_event
    */
  lazy val onOffline: EventProp[dom.Event] = eventProp("offline")


  /**
    * Script to be run when the browser starts to work online
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/online_event
    */
  lazy val onOnline: EventProp[dom.Event] = eventProp("online")


  /**
    * Script to be run when a user navigates away from a page
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/pagehide_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PageTransitionEvent
    */
  lazy val onPageHide: EventProp[dom.PageTransitionEvent] = eventProp("pagehide")


  /**
    * Script to be run when a user navigates to a page
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/pageshow_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PageTransitionEvent
    */
  lazy val onPageShow: EventProp[dom.PageTransitionEvent] = eventProp("pageshow")


  /**
    * Script to be run when the window's history changes
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/popstate_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/PopStateEvent
    */
  lazy val onPopState: EventProp[dom.PopStateEvent] = eventProp("popstate")


  /**
    * Script to be run when a Web Storage area is updated
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/storage_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/StorageEvent
    */
  lazy val onStorage: EventProp[dom.StorageEvent] = eventProp("storage")


  /**
    * Fires once a page has unloaded (or the browser window has been closed)
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Window/unload_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/UIEvent
    */
  lazy val onUnload: EventProp[dom.UIEvent] = eventProp("unload")


  // -- Error Events --


  /**
    * Script to be run when an error occurs when the file is being loaded
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/error_event
    * @see https://developer.mozilla.org/en-US/docs/Web/API/ErrorEvent
    */
  lazy val onError: EventProp[dom.ErrorEvent] = eventProp("error")


}
