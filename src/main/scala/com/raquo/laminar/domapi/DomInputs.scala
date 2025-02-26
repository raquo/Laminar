package com.raquo.laminar.domapi

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.|

trait DomInputs { this: DomTags =>

  def getChecked(element: dom.Element): Boolean | Unit = {
    element match {
      case input: dom.html.Input if input.`type` == "checkbox" || input.`type` == "radio" =>
        input.checked
      case el if isCustomElement(el) =>
        el.asInstanceOf[js.Dynamic]
          .selectDynamic("checked")
          .asInstanceOf[Any | Unit]
          .collect { case b: Boolean => b }
      case _ =>
        ()
    }
  }

  /** @return whether the operation succeeded */
  def setChecked(element: dom.Element, checked: Boolean): Boolean = {
    element match {
      case input: dom.html.Input if input.`type` == "checkbox" || input.`type` == "radio" =>
        input.checked = checked
        true
      case el if isCustomElement(el) =>
        el.asInstanceOf[js.Dynamic].updateDynamic("checked")(checked)
        true
      case _ =>
        false
    }
  }

  def getValue(element: dom.Element): js.UndefOr[String] = {
    element match {
      case input: dom.html.Input =>
        // @TODO[API,Warn] is there a legitimate use case for this on checkbox / radio inputs?
        input.value
      case textarea: dom.html.TextArea =>
        textarea.value
      case select: dom.html.Select =>
        select.value
      case button: dom.html.Button =>
        button.value
      case option: dom.html.Option =>
        option.value
      case el if isCustomElement(el) =>
        el.asInstanceOf[js.Dynamic]
          .selectDynamic("value")
          .asInstanceOf[js.UndefOr[Any]]
          .collect { case s: String => s }
      case _ =>
        js.undefined
    }
  }

  /** @return whether the operation succeeded */
  def setValue(element: dom.Element, value: String): Boolean = {
    element match {
      case input: dom.html.Input =>
        // @TODO[API,Warn] is there a legitimate use case for this on checkbox / radio inputs?
        input.value = value
        true
      case textarea: dom.html.TextArea =>
        textarea.value = value
        true
      case select: dom.html.Select =>
        select.value = value
        true
      case button: dom.html.Button =>
        button.value = value
        true
      case option: dom.html.Option =>
        option.value = value
        true
      case el if isCustomElement(el) =>
        el.asInstanceOf[js.Dynamic]
          .updateDynamic("value")(value)
        true
      case _ =>
        false
    }
  }

  /** @see https://developer.mozilla.org/en-US/docs/Web/API/File_API/Using_files_from_web_applications */
  def getFiles(element: dom.Element): js.UndefOr[List[dom.File]] = {
    element match {
      case input: dom.html.Input if input.`type` == "file" =>
        var result: List[dom.File] = Nil
        var ix = input.files.length - 1
        while (ix >= 0) {
          result = input.files(ix) :: result
          ix -= 1
        }
        result
      case _ => js.undefined
    }
  }
}
