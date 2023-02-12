package com.raquo.laminar.defs.styles.traits

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait Display extends None { this: StyleProp[_] =>


  // -- Outside values --

  /** The element generates a block element box */
  lazy val block: StyleSetter = this := "block"

  /** The element generates one or more inline element boxes */
  lazy val inline: StyleSetter = this := "inline"


  // -- Inside values (except `table`, see below) --

  /**
    * The element lays out its contents using flow layout (block-and-inline layout).
    * 
    * If its outer display type is inline or run-in, and it is participating in a
    * block or inline formatting context, then it generates an inline box.
    * Otherwise it generates a block container box.
    * 
    * Depending on the value of other properties (such as position, float, or overflow)
    * and whether it is itself participating in a block or inline formatting context,
    * it either establishes a new block formatting context (BFC) for its contents or
    * integrates its contents into its parent formatting context.
    */
  lazy val flow: StyleSetter = this := "flow"

  /**
    * The element generates a block element box that establishes a new block
    * formatting context, defining where the formatting root lies
    */
  lazy val flowRoot: StyleSetter = this := "flow-root"

  /**
    * The element behaves like a block element and lays out its content according
    * to the flexbox model
    */
  lazy val flex: StyleSetter = this := "flex"

  /**
    * The element behaves like a block element and lays out its content according
    * to the grid model
    */
  lazy val grid: StyleSetter = this := "grid"

  /**
    * The element behaves like an inline element and lays out its content according
    * to the ruby formatting model. It behaves like the corresponding HTML `<ruby>`
    * elements.
    */
  lazy val ruby: StyleSetter = this := "ruby"


  // -- Legacy values --

  /**
    * The element generates a block element box that will be flowed with
    * surrounding content as if it were a single inline box.
    */
  lazy val inlineBlock: StyleSetter = this := "inline-block"

  /**
    * The element behaves like an inline element and lays out its content
    * according to the flexbox model
    */
  lazy val inlineFlex: StyleSetter = this := "inline-flex"

  /**
    * The element behaves like an inline element and lays out its content
    * according to the grid model
    */
  lazy val inlineGrid: StyleSetter = this := "inline-grid"

  /**
    * The inline-table value does not have a direct mapping in HTML. It behaves
    * like a table HTML element, but as an inline box, rather than a
    * block-level box. Inside the table box is a block-level context
    */
  lazy val inlineTable: StyleSetter = this := "inline-table"


  // -- Box generation --

  /**
    * Turns off the display of an element (it has no effect on layout); all
    * descendant elements also have their display turned off. The document is
    * rendered as though the element did not exist.
    * 
    * To render an element box's dimensions, yet have its contents be invisible,
    * see the visibility property.
    */
  override lazy val none: StyleSetter = this := "none"

  /**
    * These elements don't produce a specific box by themselves.
    * They are replaced by their pseudo-box and their child boxes.
    */
  lazy val contents: StyleSetter = this := "contents"


  // -- List item --

  /**
    * The element generates a block box for the content and a separate list-item
    * inline box
    */
  lazy val listItem: StyleSetter = this := "list-item"


  // -- Table --

  /** Behaves like the table HTML element. It defines a block-level box */
  lazy val table: StyleSetter = this := "table"

  /** Behaves like the caption HTML element */
  lazy val tableCaption: StyleSetter = this := "table-caption"

  /** Behaves like the td HTML element */
  lazy val tableCell: StyleSetter = this := "table-cell"

  /** These elements behave like the corresponding col HTML elements */
  lazy val tableColumn: StyleSetter = this := "table-column"

  /** These elements behave like the corresponding colgroup HTML elements */
  lazy val tableColumnGroup: StyleSetter = this := "table-column-group"

  /** These elements behave like the corresponding tfoot HTML elements */
  lazy val tableFooterGroup: StyleSetter = this := "table-footer-group"

  /** These elements behave like the corresponding thead HTML elements */
  lazy val tableHeaderGroup: StyleSetter = this := "table-header-group"

  /** Behaves like the tr HTML element */
  lazy val tableRow: StyleSetter = this := "table-row"

  /** These elements behave like the corresponding tbody HTML elements */
  lazy val tableRowGroup: StyleSetter = this := "table-row-group"

}
