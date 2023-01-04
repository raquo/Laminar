package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Cursor extends Auto with None { this: StyleProp[_] =>

  /** Default cursor, typically an arrow. */
  lazy val default: StyleSetter = this := "default"

  /** A context menu is available under the cursor. */
  lazy val contextMenu: StyleSetter = this := "context-menu"

  /** Indicating help is available. */
  lazy val help: StyleSetter = this := "help"

  /** E.g. used when hovering over links, typically a hand. */
  lazy val pointer: StyleSetter = this := "pointer"

  /**
    * The program is busy in the background but the user can still interact
    * with the interface (unlike for wait).
    */
  lazy val progress: StyleSetter = this := "progress"

  /** The program is busy (sometimes an hourglass or a watch). */
  lazy val waitCss: StyleSetter = this := "wait"

  /** Indicating that cells can be selected. */
  lazy val cell: StyleSetter = this := "cell"

  /** Cross cursor, often used to indicate selection in a bitmap. */
  lazy val crosshair: StyleSetter = this := "crosshair"

  /** Indicating text can be selected, typically an I-beam. */
  lazy val text: StyleSetter = this := "text"

  /** Indicating that vertical text can be selected, typically a sideways I-beam */
  lazy val verticalText: StyleSetter = this := "vertical-text"

  /** Indicating an alias or shortcut is to be created. */
  lazy val alias: StyleSetter = this := "alias"

  /** Indicating that something can be copied */
  lazy val copy: StyleSetter = this := "copy"

  /** The hovered object may be moved. */
  lazy val move: StyleSetter = this := "move"

  /** Cursor showing that a drop is not allowed at the current location. */
  lazy val noDrop: StyleSetter = this := "no-drop"

  /** Cursor showing that something cannot be done. */
  lazy val notAllowed: StyleSetter = this := "not-allowed"

  /** Cursor showing that something can be scrolled in any direction (panned). */
  lazy val allScroll: StyleSetter = this := "all-scroll"

  /**
    * The item/column can be resized horizontally. Often rendered as arrows
    * pointing left and right with a vertical separating.
    */
  lazy val colResize: StyleSetter = this := "col-resize"

  /**
    * The item/row can be resized vertically. Often rendered as arrows pointing
    * up and down with a horizontal bar separating them.
    */
  lazy val rowResize: StyleSetter = this := "row-resize"

  /** The top edge is to be moved. */
  lazy val nResize: StyleSetter = this := "n-resize"

  /** The right edge is to be moved. */
  lazy val eResize: StyleSetter = this := "e-resize"

  /** The bottom edge is to be moved. */
  lazy val sResize: StyleSetter = this := "s-resize"

  /** The left edge is to be moved. */
  lazy val wResize: StyleSetter = this := "w-resize"

  /** The top-right corner is to be moved. */
  lazy val neResize: StyleSetter = this := "ne-resize"

  /** The top-left corner is to be moved. */
  lazy val nwResize: StyleSetter = this := "nw-resize"

  /** The bottom-right corner is to be moved. */
  lazy val seResize: StyleSetter = this := "se-resize"

  /** The bottom-left corner is to be moved. */
  lazy val swResize: StyleSetter = this := "sw-resize"

  /** The left and right edges are to be moved. */
  lazy val ewResize: StyleSetter = this := "ew-resize"

  /** The top and bottom edges are to be moved. */
  lazy val nsResize: StyleSetter = this := "ns-resize"

  /** The top right and bottom left corners are to be moved. */
  lazy val neswResize: StyleSetter = this := "nesw-resize"

  /** The top left and bottom right corners are to be moved. */
  lazy val nwseResize: StyleSetter = this := "nwse-resize"

  /** Indicates that something can be zoomed (magnified) in. */
  lazy val zoomIn: StyleSetter = this := "zoom-in"

  /** Indicates that something can be zoomed (magnified) out. */
  lazy val zoomOut: StyleSetter = this := "zoom-out"

  /** Indicates that something can be grabbed (dragged to be moved). */
  lazy val grab: StyleSetter = this := "grab"

  /** Indicates that something can be grabbed (dragged to be moved). */
  lazy val grabbing: StyleSetter = this := "grabbing"

}
