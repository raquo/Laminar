package com.raquo.laminar.defs.eventProps

import com.raquo.laminar.keys.EventProp
import org.scalajs.dom

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait GlobalEventProps {


  /**
    * Create custom event property
    * 
    * @param name - event type in JS, e.g. "click"
    * 
    * @tparam Ev - event type in JS, e.g. dom.MouseEvent
    */
  def eventProp[Ev <: dom.Event](name: String): EventProp[Ev] = new EventProp(name)


  // -- Mouse Events --


  /**
    * The click event is raised when the user clicks on an element. The click
    * event will occur after the mousedown and mouseup events.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/click_event click_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent MouseEvent @ MDN]]
    */
  lazy val onClick: EventProp[dom.MouseEvent] = eventProp("click")


  /**
    * The dblclick event is fired when a pointing device button (usually a
    * mouse button) is clicked twice on a single element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/dblclick_event dblclick_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent MouseEvent @ MDN]]
    */
  lazy val onDblClick: EventProp[dom.MouseEvent] = eventProp("dblclick")


  /**
    * The mousedown event is raised when the user presses the mouse button.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/mousedown_event mousedown_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent MouseEvent @ MDN]]
    */
  lazy val onMouseDown: EventProp[dom.MouseEvent] = eventProp("mousedown")


  /**
    * The mousemove event is raised when the user moves the mouse.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/mousemove_event mousemove_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent MouseEvent @ MDN]]
    */
  lazy val onMouseMove: EventProp[dom.MouseEvent] = eventProp("mousemove")


  /**
    * The mouseout event is raised when the mouse leaves an element (e.g, when
    * the mouse moves off of an image in the web page, the mouseout event is
    * raised for that image element).
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/mouseout_event mouseout_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent MouseEvent @ MDN]]
    */
  lazy val onMouseOut: EventProp[dom.MouseEvent] = eventProp("mouseout")


  /**
    * The mouseover event is raised when the user moves the mouse over a
    * particular element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/mouseover_event mouseover_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent MouseEvent @ MDN]]
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
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/mouseleave_event mouseleave_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent MouseEvent @ MDN]]
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
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/mouseenter_event mouseenter_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent MouseEvent @ MDN]]
    */
  lazy val onMouseEnter: EventProp[dom.MouseEvent] = eventProp("mouseenter")


  /**
    * The mouseup event is raised when the user releases the mouse button.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/mouseup_event mouseup_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent MouseEvent @ MDN]]
    */
  lazy val onMouseUp: EventProp[dom.MouseEvent] = eventProp("mouseup")


  /**
    * Fires when the mouse wheel rolls up or down over an element
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/wheel_event wheel_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/WheelEvent WheelEvent @ MDN]]
    */
  lazy val onWheel: EventProp[dom.WheelEvent] = eventProp("wheel")


  /**
    * Script to be run when a context menu is triggered
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/contextmenu_event contextmenu_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent MouseEvent @ MDN]]
    */
  lazy val onContextMenu: EventProp[dom.MouseEvent] = eventProp("contextmenu")


  /**
    * Script to be run when an element is dragged
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/drag_event drag_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/DragEvent DragEvent @ MDN]]
    */
  lazy val onDrag: EventProp[dom.DragEvent] = eventProp("drag")


  /**
    * Script to be run at the end of a drag operation
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dragend_event dragend_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/DragEvent DragEvent @ MDN]]
    */
  lazy val onDragEnd: EventProp[dom.DragEvent] = eventProp("dragend")


  /**
    * Script to be run when an element has been dragged to a valid drop target
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dragenter_event dragenter_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/DragEvent DragEvent @ MDN]]
    */
  lazy val onDragEnter: EventProp[dom.DragEvent] = eventProp("dragenter")


  /**
    * Script to be run when an element leaves a valid drop target
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dragleave_event dragleave_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/DragEvent DragEvent @ MDN]]
    */
  lazy val onDragLeave: EventProp[dom.DragEvent] = eventProp("dragleave")


  /**
    * Script to be run when an element is being dragged over a valid drop target
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dragover_event dragover_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/DragEvent DragEvent @ MDN]]
    */
  lazy val onDragOver: EventProp[dom.DragEvent] = eventProp("dragover")


  /**
    * Script to be run at the start of a drag operation
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dragstart_event dragstart_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/DragEvent DragEvent @ MDN]]
    */
  lazy val onDragStart: EventProp[dom.DragEvent] = eventProp("dragstart")


  /**
    * Script to be run when dragged element is being dropped
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/drop_event drop_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/DragEvent DragEvent @ MDN]]
    */
  lazy val onDrop: EventProp[dom.DragEvent] = eventProp("drop")


  // -- Pointer Events --


  /**
    * fired when a pointing device is moved into an element's hit test boundaries.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerover_event pointerover_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent PointerEvent @ MDN]]
    */
  lazy val onPointerOver: EventProp[dom.PointerEvent] = eventProp("pointerover")


  /**
    * fired when a pointing device is moved into the hit test boundaries of an element
    * or one of its descendants, including as a result of a pointerdown event
    * from a device that does not support hover (see pointerdown).
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerenter_event pointerenter_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent PointerEvent @ MDN]]
    */
  lazy val onPointerEnter: EventProp[dom.PointerEvent] = eventProp("pointerenter")


  /**
    * fired when a pointer becomes active.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerdown_event pointerdown_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent PointerEvent @ MDN]]
    */
  lazy val onPointerDown: EventProp[dom.PointerEvent] = eventProp("pointerdown")


  /**
    * fired when a pointer changes coordinates.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/pointermove_event pointermove_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent PointerEvent @ MDN]]
    */
  lazy val onPointerMove: EventProp[dom.PointerEvent] = eventProp("pointermove")


  /**
    * fired when a pointer is no longer active.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerup_event pointerup_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent PointerEvent @ MDN]]
    */
  lazy val onPointerUp: EventProp[dom.PointerEvent] = eventProp("pointerup")


  /**
    * a browser fires this event if it concludes the pointer will no longer be able
    * to generate events (for example the related device is deactived).
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/pointercancel_event pointercancel_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent PointerEvent @ MDN]]
    */
  lazy val onPointerCancel: EventProp[dom.PointerEvent] = eventProp("pointercancel")


  /**
    * fired for several reasons including: pointing device is moved out of
    * the hit test boundaries of an element;
    * firing the pointerup event for a device that does not support hover (see pointerup);
    * after firing the pointercancel event (see pointercancel);
    * when a pen stylus leaves the hover range detectable by the digitizer.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerout_event pointerout_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent PointerEvent @ MDN]]
    */
  lazy val onPointerOut: EventProp[dom.PointerEvent] = eventProp("pointerout")


  /**
    * fired when a pointing device is moved out of the hit test boundaries of an element.
    * For pen devices, this event is fired when the stylus leaves the hover range detectable by the digitizer.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/pointerleave_event pointerleave_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent PointerEvent @ MDN]]
    */
  lazy val onPointerLeave: EventProp[dom.PointerEvent] = eventProp("pointerleave")


  /**
    * fired when an element receives pointer capture.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/gotpointercapture_event gotpointercapture_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent PointerEvent @ MDN]]
    */
  lazy val gotPointerCapture: EventProp[dom.PointerEvent] = eventProp("gotpointercapture")


  /**
    * Fired after pointer capture is released for a pointer.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/lostpointercapture_event lostpointercapture_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/PointerEvent PointerEvent @ MDN]]
    */
  lazy val lostPointerCapture: EventProp[dom.PointerEvent] = eventProp("lostpointercapture")


  // -- Form Events --


  /**
    * The change event is fired for input, select, and textarea elements
    * when a change to the element's value is committed by the user.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Element/change_event change_event @ MDN]]
    */
  lazy val onChange: EventProp[dom.Event] = eventProp("change")


  /**
    * The select event only fires when text inside a text input or textarea is
    * selected. The event is fired after the text has been selected.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Element/select_event select_event @ MDN]]
    */
  lazy val onSelect: EventProp[dom.Event] = eventProp("select")


  /**
    * The DOM beforeinput event fires when the value of an `<input>`, or `<textarea>`
    * element is about to be modified. The event also applies to elements with
    * contenteditable enabled, and to any element when designMode is turned on.
    * 
    * 
    * @note IE does not support this event.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/beforeinput_event beforeinput_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/InputEvent InputEvent @ MDN]]
    */
  lazy val onBeforeInput: EventProp[dom.InputEvent] = eventProp("beforeinput")


  /**
    * The input event is fired for input, select, textarea, and
    * contentEditable elements when it gets user input.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Element/input_event input_event @ MDN]]
    */
  lazy val onInput: EventProp[dom.Event] = eventProp("input")


  /**
    * The blur event is raised when an element loses focus.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/blur_event blur_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/FocusEvent FocusEvent @ MDN]]
    */
  lazy val onBlur: EventProp[dom.FocusEvent] = eventProp("blur")


  /**
    * The focus event is raised when the user sets focus on the given element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/focus_event focus_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/FocusEvent FocusEvent @ MDN]]
    */
  lazy val onFocus: EventProp[dom.FocusEvent] = eventProp("focus")


  /**
    * The submit event is fired when the user clicks a submit button in a form
    * (`<input type="submit"/>`).
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLFormElement/submit_event submit_event @ MDN]]
    */
  lazy val onSubmit: EventProp[dom.Event] = eventProp("submit")


  /**
    * The reset event is fired when a form is reset.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLFormElement/reset_event reset_event @ MDN]]
    */
  lazy val onReset: EventProp[dom.Event] = eventProp("reset")


  /**
    * Script to be run when an element is invalid
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLInputElement/invalid_event invalid_event @ MDN]]
    */
  lazy val onInvalid: EventProp[dom.Event] = eventProp("invalid")


  /**
    * Fires when the user writes something in a search field (for `<input="search">`)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLInputElement/search_event search_event @ MDN]]
    */
  lazy val onSearch: EventProp[dom.Event] = eventProp("search")


  // -- Keyboard Events --


  /**
    * The keydown event is raised when the user presses a keyboard key.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/keydown_event keydown_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent KeyboardEvent @ MDN]]
    */
  lazy val onKeyDown: EventProp[dom.KeyboardEvent] = eventProp("keydown")


  /**
    * The keyup event is raised when the user releases a key that's been pressed.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/keyup_event keyup_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent KeyboardEvent @ MDN]]
    */
  lazy val onKeyUp: EventProp[dom.KeyboardEvent] = eventProp("keyup")


  /**
    * The keypress event should be raised when the user presses a key on the keyboard.
    * However, not all browsers fire keypress events for certain keys.
    * 
    * Webkit-based browsers (Google Chrome and Safari, for example) do not fire keypress events on the arrow keys.
    * Firefox does not fire keypress events on modifier keys like SHIFT.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/keypress_event keypress_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent KeyboardEvent @ MDN]]
    */
  lazy val onKeyPress: EventProp[dom.KeyboardEvent] = eventProp("keypress")


  // -- Clipboard Events --


  /**
    * Fires when the user copies the content of an element
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/copy_event copy_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/ClipboardEvent ClipboardEvent @ MDN]]
    */
  lazy val onCopy: EventProp[dom.ClipboardEvent] = eventProp("copy")


  /**
    * Fires when the user cuts the content of an element
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/cut_event cut_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/ClipboardEvent ClipboardEvent @ MDN]]
    */
  lazy val onCut: EventProp[dom.ClipboardEvent] = eventProp("cut")


  /**
    * Fires when the user pastes some content in an element
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/paste_event paste_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/ClipboardEvent ClipboardEvent @ MDN]]
    */
  lazy val onPaste: EventProp[dom.ClipboardEvent] = eventProp("paste")


  // -- Media Events --


  /**
    * Script to be run on abort
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/abort_event abort_event @ MDN]]
    */
  lazy val onAbort: EventProp[dom.Event] = eventProp("abort")


  /**
    * Script to be run when a file is ready to start playing (when it has buffered enough to begin)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/canplay_event canplay_event @ MDN]]
    */
  lazy val onCanPlay: EventProp[dom.Event] = eventProp("canplay")


  /**
    * Script to be run when a file can be played all the way to the end without pausing for buffering
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/canplaythrough_event canplaythrough_event @ MDN]]
    */
  lazy val onCanPlayThrough: EventProp[dom.Event] = eventProp("canplaythrough")


  /**
    * Script to be run when the cue changes in a `<track>` element
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/TextTrack/cuechange_event cuechange_event @ MDN]]
    */
  lazy val onCueChange: EventProp[dom.Event] = eventProp("cuechange")


  /**
    * Script to be run when the length of the media changes
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/durationchange_event durationchange_event @ MDN]]
    */
  lazy val onDurationChange: EventProp[dom.Event] = eventProp("durationchange")


  /**
    * Script to be run when something bad happens and the file is suddenly unavailable (like unexpectedly disconnects)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/emptied_event emptied_event @ MDN]]
    */
  lazy val onEmptied: EventProp[dom.Event] = eventProp("emptied")


  /**
    * Script to be run when the media has reach the end (a useful event for messages like "thanks for listening")
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/ended_event ended_event @ MDN]]
    */
  lazy val onEnded: EventProp[dom.Event] = eventProp("ended")


  /**
    * Script to be run when media data is loaded
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/loadeddata_event loadeddata_event @ MDN]]
    */
  lazy val onLoadedData: EventProp[dom.Event] = eventProp("loadeddata")


  /**
    * Script to be run when meta data (like dimensions and duration) are loaded
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/loadedmetadata_event loadedmetadata_event @ MDN]]
    */
  lazy val onLoadedMetadata: EventProp[dom.Event] = eventProp("loadedmetadata")


  /**
    * Script to be run just as the file begins to load before anything is actually loaded
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/loadstart_event loadstart_event @ MDN]]
    */
  lazy val onLoadStart: EventProp[dom.Event] = eventProp("loadstart")


  /**
    * Script to be run when the media is paused either by the user or programmatically
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/pause_event pause_event @ MDN]]
    */
  lazy val onPause: EventProp[dom.Event] = eventProp("pause")


  /**
    * Script to be run when the media is ready to start playing
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/play_event play_event @ MDN]]
    */
  lazy val onPlay: EventProp[dom.Event] = eventProp("play")


  /**
    * Script to be run when the media actually has started playing
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/playing_event playing_event @ MDN]]
    */
  lazy val onPlaying: EventProp[dom.Event] = eventProp("playing")


  /**
    * Script to be run when the browser is in the process of getting the media data
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/progress_event progress_event @ MDN]]
    */
  lazy val onProgress: EventProp[dom.Event] = eventProp("progress")


  /**
    * Script to be run each time the playback rate changes (like when a user switches to a slow motion or fast forward mode)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/ratechange_event ratechange_event @ MDN]]
    */
  lazy val onRateChange: EventProp[dom.Event] = eventProp("ratechange")


  /**
    * Script to be run when the seeking attribute is set to false indicating that seeking has ended
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/seeked_event seeked_event @ MDN]]
    */
  lazy val onSeeked: EventProp[dom.Event] = eventProp("seeked")


  /**
    * Script to be run when the seeking attribute is set to true indicating that seeking is active
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/seeking_event seeking_event @ MDN]]
    */
  lazy val onSeeking: EventProp[dom.Event] = eventProp("seeking")


  /**
    * Script to be run when the browser is unable to fetch the media data for whatever reason
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/stalled_event stalled_event @ MDN]]
    */
  lazy val onStalled: EventProp[dom.Event] = eventProp("stalled")


  /**
    * Script to be run when fetching the media data is stopped before it is completely loaded for whatever reason
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/suspend_event suspend_event @ MDN]]
    */
  lazy val onSuspend: EventProp[dom.Event] = eventProp("suspend")


  /**
    * Script to be run when the playing position has changed (like when the user fast forwards to a different point in the media)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/timeupdate_event timeupdate_event @ MDN]]
    */
  lazy val onTimeUpdate: EventProp[dom.Event] = eventProp("timeupdate")


  /**
    * Script to be run each time the volume is changed which (includes setting the volume to "mute")
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/volumechange_event volumechange_event @ MDN]]
    */
  lazy val onVolumeChange: EventProp[dom.Event] = eventProp("volumechange")


  /**
    * Script to be run when the media has paused but is expected to resume (like when the media pauses to buffer more data)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/HTMLMediaElement/waiting_event waiting_event @ MDN]]
    */
  lazy val onWaiting: EventProp[dom.Event] = eventProp("waiting")


  // -- Animation Events --


  /**
    * The animationend event is event fires when a CSS animation reaches the end of its active period.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/animationend_event animationend_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/AnimationEvent AnimationEvent @ MDN]]
    */
  lazy val onAnimationEnd: EventProp[dom.AnimationEvent] = eventProp("animationend")


  /**
    * The animationiteration event is sent when a CSS animation reaches the end of an iteration. An iteration ends
    * when a single pass through the sequence of animation instructions is completed by executing the last
    * animation step.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/animationiteration_event animationiteration_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/AnimationEvent AnimationEvent @ MDN]]
    */
  lazy val onAnimationIteration: EventProp[dom.AnimationEvent] = eventProp("animationiteration")


  /**
    * The animationstart event is sent when a CSS Animation starts to play.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/animationstart_event animationstart_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/AnimationEvent AnimationEvent @ MDN]]
    */
  lazy val onAnimationStart: EventProp[dom.AnimationEvent] = eventProp("animationstart")


  /**
    * The `transitionend` event is sent to when a CSS transition completes.
    * 
    * Note: If the transition is removed from its target node before the transition completes execution, the
    * `transitionend` event won't be generated. One way this can happen is by changing the value of the
    * `transition-property` attribute which applies to the target. Another is if the `display` attribute is set to
    * `none`.
    * 
    * @see [[https://developer.mozilla.org/en-US/docs/Web/API/GlobalEventHandlers/ontransitionend MDN]]
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Element/transitionend_event transitionend_event @ MDN]]
    */
  lazy val onTransitionEnd: EventProp[dom.Event] = eventProp("transitionend")


  // -- Misc Events --


  /**
    * The onload property of the GlobalEventHandlers mixin is an event handler
    * for the load event of a Window, XMLHttpRequest, `<img>` element, etc.,
    * which fires when the resource has loaded.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/load_event load_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/UIEvent UIEvent @ MDN]]
    */
  lazy val onLoad: EventProp[dom.UIEvent] = eventProp("load")


  /**
    * The GlobalEventHandlers.onresize property contains an EventHandler
    * triggered when a resize event is received.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/resize_event resize_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/UIEvent UIEvent @ MDN]]
    */
  lazy val onResize: EventProp[dom.UIEvent] = eventProp("resize")


  /**
    * An event handler for scroll events on element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Window/scroll_event scroll_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/UIEvent UIEvent @ MDN]]
    */
  lazy val onScroll: EventProp[dom.UIEvent] = eventProp("scroll")


  /**
    * Fires when a user starts a new selection.
    * If the event is canceled, the selection is not changed.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Node/selectstart_event selectstart_event @ MDN]]
    */
  lazy val onSelectStart: EventProp[dom.Event] = eventProp("selectstart")


  /**
    * Fires when a `<menu>` element is shown as a context menu
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Element/show_event show_event @ MDN]]
    */
  lazy val onShow: EventProp[dom.Event] = eventProp("show")


  /**
    * Fires when the user opens or closes the `<details>` element
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/API/Element/toggle_event toggle_event @ MDN]]
    */
  lazy val onToggle: EventProp[dom.Event] = eventProp("toggle")


  // -- Error Events --


  /**
    * Script to be run when an error occurs when the file is being loaded
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/Element/error_event error_event @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/ErrorEvent ErrorEvent @ MDN]]
    */
  lazy val onError: EventProp[dom.ErrorEvent] = eventProp("error")


}
