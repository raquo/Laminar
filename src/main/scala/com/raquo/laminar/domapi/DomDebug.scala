package com.raquo.laminar.domapi

import com.raquo.ew._

import org.scalajs.dom

import scala.annotation.tailrec
import scala.scalajs.js

trait DomDebug {

  private val classNamesSeparatorRegex = new js.RegExp(" ", flags = "g")

  /** @return hierarchical path describing the position and identity of this node, starting with the root. */
  @tailrec final def debugPath(element: dom.Node, initial: List[String] = Nil): List[String] = {
    element match {
      case null => initial
      case _ => debugPath(element.parentNode, debugNodeDescription(element) :: initial)
    }
  }

  /** @return e.g. a, div#mainSection, span.sideNote.sizeSmall */
  def debugNodeDescription(node: dom.Node): String = {
    node match {
      case el: dom.html.Element =>
        val id = el.id
        val suffixStr = if (id.nonEmpty) {
          "#" + id
        } else {
          val classes = el.className
          if (classes.nonEmpty) {
            "." + classes.ew.replace(classNamesSeparatorRegex, ".").str
          } else {
            ""
          }
        }
        el.tagName.ew.toLowerCase().str + suffixStr

      case _ => node.nodeName
    }
  }

  def debugNodeOuterHtml(node: dom.Node): String = {
    node match {
      case el: dom.Element => el.outerHTML
      case el: dom.Text => s"Text(${el.textContent})"
      case el: dom.Comment => s"Comment(${el.textContent})"
      case null => "<null>"
      case other => s"OtherNode(${other.toString})"
    }
  }

  def debugNodeInnerHtml(node: dom.Node): String = {
    node match {
      case el: dom.Element => el.innerHTML
      case el: dom.Text => s"Text(${el.textContent})"
      case el: dom.Comment => s"Comment(${el.textContent})"
      case null => "<null>"
      case other => s"OtherNode(${other.toString})"
    }
  }
}
