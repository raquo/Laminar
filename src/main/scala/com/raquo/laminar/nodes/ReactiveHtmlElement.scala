package com.raquo.laminar.nodes

import com.raquo.airstream.core.{Observable, Observer}
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{BooleanTransformer, StringTransformer}
import com.raquo.laminar.builders.HtmlTag
import com.raquo.laminar.inputs.{CheckedController, ValueController}
import org.scalajs.dom

import scala.scalajs.js

class ReactiveHtmlElement[+Ref <: dom.html.Element](val tag: HtmlTag[Ref])
  extends ReactiveElement[Ref] {

  final override val ref: Ref = DomApi.createHtmlElement(this)

  // -- `value` prop controller

  private[this] var valueController: js.UndefOr[ValueController] = js.undefined

  private[laminar] def hasValueController: Boolean = valueController.nonEmpty

  private[laminar] var hasValueBinder: Boolean = false

  private[laminar] def setValueController(
    source: Observable[String],
    processInput: StringTransformer,
    observer: Observer[String]
  ): Unit = {
    valueController = js.defined(new ValueController(this, source, processInput, observer))
  }

  // -- `checked` prop controller

  private[this] var checkedController: js.UndefOr[CheckedController] = js.undefined

  private[laminar] def hasCheckedController: Boolean = checkedController.nonEmpty

  private[laminar] var hasCheckedBinder: Boolean = false

  private[laminar] def setCheckedController(
    source: Observable[Boolean],
    processInput: BooleanTransformer,
    observer: Observer[Boolean]
  ): Unit = {
    checkedController = js.defined(new CheckedController(this, source, processInput, observer))
  }

  // --

  override def toString: String = {
    // `ref` is not available inside ReactiveElement's constructor due to initialization order, so fall back to `tag`.
    s"ReactiveHtmlElement(${ if (ref != null) ref.outerHTML else s"tag=${tag.name}"})"
  }
}

object ReactiveHtmlElement {

  type Base = ReactiveHtmlElement[dom.html.Element]
}
