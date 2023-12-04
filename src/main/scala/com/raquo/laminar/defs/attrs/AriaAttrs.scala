package com.raquo.laminar.defs.attrs

import com.raquo.laminar.keys.AriaAttr
import com.raquo.laminar.codecs._

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait AriaAttrs {


  /**
    * Create ARIA attribute (Note: for HTML attrs, use L.htmlAttr)
    * 
    * @param name  - suffix of the attribute, without "aria-" prefix, e.g. "labelledby"
    * @param codec - used to encode V into String, e.g. StringAsIsCodec
    * 
    * @tparam V    - value type for this attr in Scala
    */
  def ariaAttr[V](name: String, codec: Codec[V, String]): AriaAttr[V] = new AriaAttr(name, codec)


  @inline protected def boolAsTrueFalseAriaAttr(name: String): AriaAttr[Boolean] = ariaAttr(name, BooleanAsTrueFalseStringCodec)

  @inline protected def doubleAriaAttr(name: String): AriaAttr[Double] = ariaAttr(name, DoubleAsStringCodec)

  @inline protected def intAriaAttr(name: String): AriaAttr[Int] = ariaAttr(name, IntAsStringCodec)

  @inline protected def stringAriaAttr(name: String): AriaAttr[String] = ariaAttr(name, StringAsIsCodec)



  /**
    * Identifies the currently active descendant of a composite widget.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-activedescendant aria-activedescendant @ MDN]]
    */
  lazy val activeDescendant: AriaAttr[String] = stringAriaAttr("activedescendant")


  /**
    * Indicates whether assistive technologies will present all, or only parts of, the
    * changed region based on the change notifications defined by the aria-relevant
    * attribute. See related [[relevant]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-atomic aria-atomic @ MDN]]
    */
  lazy val atomic: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("atomic")


  /**
    * Indicates whether user input completion suggestions are provided.
    * 
    * Enumerated: "inline" | "list" | "both" | "none" (default)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-autocomplete aria-autocomplete @ MDN]]
    */
  lazy val autoComplete: AriaAttr[String] = stringAriaAttr("autocomplete")


  /**
    * Indicates whether an element, and its subtree, are currently being updated.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-busy aria-busy @ MDN]]
    */
  lazy val busy: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("busy")


  /**
    * Indicates the current "checked" state of checkboxes, radio buttons, and other
    * widgets. See related [[pressed]] and [[selected]].
    * 
    * Enumerated: Tristate – "true" | "false" | "mixed" | undefined (default)
    * - undefined means the element does not support being checked
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-checked aria-checked @ MDN]]
    */
  lazy val checked: AriaAttr[String] = stringAriaAttr("checked")


  /**
    * Identifies the element (or elements) whose contents or presence are controlled
    * by the current element. See related [[owns]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-controls aria-controls @ MDN]]
    */
  lazy val controls: AriaAttr[String] = stringAriaAttr("controls")


  /**
    * Indicates the element that represents the current item within a container
    * or set of related elements.
    * 
    * Enumerated:
    * "page" | "step" | "location" | "date" | "time" | "true" | "false" (default)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-current aria-current @ MDN]]
    */
  lazy val current: AriaAttr[String] = stringAriaAttr("current")


  /**
    * Identifies the element (or elements) that describes the object.
    * See related [[labelledBy]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-describedby aria-describedby @ MDN]]
    */
  lazy val describedBy: AriaAttr[String] = stringAriaAttr("describedby")


  /**
    * Indicates that the element is perceivable but disabled, so it is not editable
    * or otherwise operable. See related [[hidden]] and [[readOnly]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-disabled aria-disabled @ MDN]]
    */
  lazy val disabled: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("disabled")


  /**
    * Indicates what functions can be performed when the dragged object is released
    * on the drop target. This allows assistive technologies to convey the possible
    * drag options available to users, including whether a pop-up menu of choices is
    * provided by the application. Typically, drop effect functions can only be
    * provided once an object has been grabbed for a drag operation as the drop
    * effect functions available are dependent on the object being dragged.
    * 
    * Enumerated: "copy" | "move" | "link" | "execute" | "popup" | "none" (default)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-dropeffect aria-dropeffect @ MDN]]
    */
  lazy val dropEffect: AriaAttr[String] = stringAriaAttr("dropeffect")


  /**
    * Indicates whether the element, or another grouping element it controls, is
    * currently expanded or collapsed.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-expanded aria-expanded @ MDN]]
    */
  lazy val expanded: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("expanded")


  /**
    * Identifies the next element (or elements) in an alternate reading order of
    * content which, at the user's discretion, allows assistive technology to
    * override the general default of reading in document source order.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-flowto aria-flowto @ MDN]]
    */
  lazy val flowTo: AriaAttr[String] = stringAriaAttr("flowto")


  /**
    * Indicates an element's "grabbed" state in a drag-and-drop operation.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-grabbed aria-grabbed @ MDN]]
    */
  lazy val grabbed: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("grabbed")


  /**
    * Indicates that the element has a popup context menu or sub-level menu.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-haspopup aria-haspopup @ MDN]]
    */
  lazy val hasPopup: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("haspopup")


  /**
    * Indicates that the element and all of its descendants are not visible or
    * perceivable to any user as implemented by the author.
    * See related [[disabled]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-hidden aria-hidden @ MDN]]
    */
  lazy val hidden: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("hidden")


  /**
    * Indicates the entered value does not conform to the format expected by the
    * application.
    * 
    * Enumerated: "grammar" | "spelling" | "true" | "false" (default)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-invalid aria-invalid @ MDN]]
    */
  lazy val invalid: AriaAttr[String] = stringAriaAttr("invalid")


  /**
    * Defines a string value that labels the current element.
    * See related [[labelledBy]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-label aria-label @ MDN]]
    */
  lazy val label: AriaAttr[String] = stringAriaAttr("label")


  /**
    * Identifies the element (or elements) that labels the current element.
    * See related [[label]] and [[describedBy]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-labelledby aria-labelledby @ MDN]]
    */
  lazy val labelledBy: AriaAttr[String] = stringAriaAttr("labelledby")


  /**
    * Defines the hierarchical level of an element within a structure.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-level aria-level @ MDN]]
    */
  lazy val level: AriaAttr[Int] = intAriaAttr("level")


  /**
    * Indicates that an element will be updated, and describes the types of updates the
    * user agents, assistive technologies, and user can expect from the live region.
    * 
    * Enumerated: "polite" | "assertive" | "off" (default)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-live aria-live @ MDN]]
    */
  lazy val live: AriaAttr[String] = stringAriaAttr("live")


  /**
    * Indicates whether a text box accepts multiple lines of input or only a single line.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-multiline aria-multiline @ MDN]]
    */
  lazy val multiLine: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("multiline")


  /**
    * Indicates that the user may select more than one item from the current selectable descendants.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-multiselectable aria-multiselectable @ MDN]]
    */
  lazy val multiSelectable: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("multiselectable")


  /**
    * Indicates whether the element and orientation is horizontal or vertical.
    * 
    * Enumerated: "vertical" | "horizontal" (default)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-orientation aria-orientation @ MDN]]
    */
  lazy val orientation: AriaAttr[String] = stringAriaAttr("orientation")


  /**
    * Identifies an element (or elements) in order to define a visual, functional, or
    * contextual parent/child relationship between DOM elements where the DOM hierarchy
    * cannot be used to represent the relationship. See related [[controls]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-owns aria-owns @ MDN]]
    */
  lazy val owns: AriaAttr[String] = stringAriaAttr("owns")


  /**
    * Defines an element's number or position in the current set of listitems or treeitems.
    * Not required if all elements in the set are present in the DOM. See related [[setSize]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-posinset aria-posinset @ MDN]]
    */
  lazy val posInSet: AriaAttr[Int] = intAriaAttr("posinset")


  /**
    * Indicates the current "pressed" state of toggle buttons. See related [[checked]] and [[selected]].
    * 
    * Enumerated: Tristate – "true" | "false" | "mixed" | undefined (default)
    * - undefined means the element does not support being pressed
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-pressed aria-pressed @ MDN]]
    */
  lazy val pressed: AriaAttr[String] = stringAriaAttr("pressed")


  /**
    * Indicates that the element is not editable, but is otherwise operable. See related [[disabled]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-readonly aria-readonly @ MDN]]
    */
  lazy val readOnly: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("readonly")


  /**
    * Indicates what user agent change notifications (additions, removals, etc.)
    * assistive technologies will receive within a live region. See related [[atomic]].
    * 
    * Enumerated: "additions" | "removals" | "text" | "all" | "additions text" (default)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-relevant aria-relevant @ MDN]]
    */
  lazy val relevant: AriaAttr[String] = stringAriaAttr("relevant")


  /**
    * Indicates that user input is required on the element before a form may be submitted.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-required aria-required @ MDN]]
    */
  lazy val required: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("required")


  /**
    * Indicates the current "selected" state of various widgets.
    * See related [[checked]] and [[pressed]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-selected aria-selected @ MDN]]
    */
  lazy val selected: AriaAttr[Boolean] = boolAsTrueFalseAriaAttr("selected")


  /**
    * Defines the number of items in the current set of listitems or treeitems.
    * Not required if all elements in the set are present in the DOM.
    * See related [[posInSet]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-setsize aria-setsize @ MDN]]
    */
  lazy val setSize: AriaAttr[Int] = intAriaAttr("setsize")


  /**
    * Indicates if items in a table or grid are sorted in ascending or descending order.
    * 
    * Enumerated: "ascending" | "descending" | "other" | "none" (default)
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-sort aria-sort @ MDN]]
    */
  lazy val sort: AriaAttr[String] = stringAriaAttr("sort")


  /**
    * Defines the maximum allowed value for a range widget.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-valuemax aria-valuemax @ MDN]]
    */
  lazy val valueMax: AriaAttr[Double] = doubleAriaAttr("valuemax")


  /**
    * Defines the minimum allowed value for a range widget.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-valuemin aria-valuemin @ MDN]]
    */
  lazy val valueMin: AriaAttr[Double] = doubleAriaAttr("valuemin")


  /**
    * Defines the current value for a range widget. See related [[valueText]].
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-valuenow aria-valuenow @ MDN]]
    */
  lazy val valueNow: AriaAttr[Double] = doubleAriaAttr("valuenow")


  /**
    * Defines the human readable text alternative of aria-valuenow for a range widget.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Attributes/aria-valuetext aria-valuetext @ MDN]]
    */
  lazy val valueText: AriaAttr[String] = stringAriaAttr("valuetext")


}
