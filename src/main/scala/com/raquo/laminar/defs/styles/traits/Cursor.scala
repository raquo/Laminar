package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.SimpleKeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Cursor extends Auto with None { this: StyleProp[_] =>

  /** Default cursor, typically an arrow. */
  lazy val default: StyleSetter[_] = this := "default"

  /** A context menu is available under the cursor. */
  lazy val contextMenu: StyleSetter[_] = this := "context-menu"

  /** Indicating help is available. */
  lazy val help: StyleSetter[_] = this := "help"

  /** E.g. used when hovering over links, typically a hand. */
  lazy val pointer: StyleSetter[_] = this := "pointer"

  /**
    * The program is busy in the background but the user can still interact
    * with the interface (unlike for wait).
    */
  lazy val progress: StyleSetter[_] = this := "progress"

  /** The program is busy (sometimes an hourglass or a watch). */
  lazy val waitCss: StyleSetter[_] = this := "wait"

  /** Indicating that cells can be selected. */
  lazy val cell: StyleSetter[_] = this := "cell"

  /** Cross cursor, often used to indicate selection in a bitmap. */
  lazy val crosshair: StyleSetter[_] = this := "crosshair"

  /** Indicating text can be selected, typically an I-beam. */
  lazy val text: StyleSetter[_] = this := "text"

  /** Indicating that vertical text can be selected, typically a sideways I-beam */
  lazy val verticalText: StyleSetter[_] = this := "vertical-text"

  /** Indicating an alias or shortcut is to be created. */
  lazy val alias: StyleSetter[_] = this := "alias"

  /** Indicating that something can be copied */
  lazy val copy: StyleSetter[_] = this := "copy"

  /** The hovered object may be moved. */
  lazy val move: StyleSetter[_] = this := "move"

  /** Cursor showing that a drop is not allowed at the current location. */
  lazy val noDrop: StyleSetter[_] = this := "no-drop"

  /** Cursor showing that something cannot be done. */
  lazy val notAllowed: StyleSetter[_] = this := "not-allowed"

  /** Cursor showing that something can be scrolled in any direction (panned). */
  lazy val allScroll: StyleSetter[_] = this := "all-scroll"

  /**
    * The item/column can be resized horizontally. Often rendered as arrows
    * pointing left and right with a vertical separating.
    */
  lazy val colResize: StyleSetter[_] = this := "col-resize"

  /**
    * The item/row can be resized vertically. Often rendered as arrows pointing
    * up and down with a horizontal bar separating them.
    */
  lazy val rowResize: StyleSetter[_] = this := "row-resize"

  /** The top edge is to be moved. */
  lazy val nResize: StyleSetter[_] = this := "n-resize"

  /** The right edge is to be moved. */
  lazy val eResize: StyleSetter[_] = this := "e-resize"

  /** The bottom edge is to be moved. */
  lazy val sResize: StyleSetter[_] = this := "s-resize"

  /** The left edge is to be moved. */
  lazy val wResize: StyleSetter[_] = this := "w-resize"

  /** The top-right corner is to be moved. */
  lazy val neResize: StyleSetter[_] = this := "ne-resize"

  /** The top-left corner is to be moved. */
  lazy val nwResize: StyleSetter[_] = this := "nw-resize"

  /** The bottom-right corner is to be moved. */
  lazy val seResize: StyleSetter[_] = this := "se-resize"

  /** The bottom-left corner is to be moved. */
  lazy val swResize: StyleSetter[_] = this := "sw-resize"

  /** The left and right edges are to be moved. */
  lazy val ewResize: StyleSetter[_] = this := "ew-resize"

  /** The top and bottom edges are to be moved. */
  lazy val nsResize: StyleSetter[_] = this := "ns-resize"

  /** The top right and bottom left corners are to be moved. */
  lazy val neswResize: StyleSetter[_] = this := "nesw-resize"

  /** The top left and bottom right corners are to be moved. */
  lazy val nwseResize: StyleSetter[_] = this := "nwse-resize"

  /** Indicates that something can be zoomed (magnified) in. */
  lazy val zoomIn: StyleSetter[_] = this := "zoom-in"

  /** Indicates that something can be zoomed (magnified) out. */
  lazy val zoomOut: StyleSetter[_] = this := "zoom-out"

  /** Indicates that something can be grabbed (dragged to be moved). */
  lazy val grab: StyleSetter[_] = this := "grab"

  /** Indicates that something can be grabbed (dragged to be moved). */
  lazy val grabbing: StyleSetter[_] = this := "grabbing"

}
