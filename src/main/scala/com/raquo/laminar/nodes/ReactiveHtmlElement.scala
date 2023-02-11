package com.raquo.laminar.nodes

import com.raquo.airstream.ownership.DynamicSubscription
import com.raquo.laminar.DomApi
import com.raquo.laminar.inputs.ValueController
import com.raquo.laminar.keys.HtmlProp
import com.raquo.laminar.modifiers.{EventListener, KeyUpdater}
import com.raquo.laminar.tags.HtmlTag
import org.scalajs.dom

import scala.scalajs.js

class ReactiveHtmlElement[+Ref <: dom.html.Element](
  override val tag: HtmlTag[Ref],
  final override val ref: Ref
) extends ReactiveElement[Ref] {

  // -- `value` prop controller

  private[this] var valueController: js.UndefOr[ValueController[_, _]] = js.undefined

  private[laminar] def hasValueController: Boolean = valueController.nonEmpty

  private[laminar] def hasOtherValueController(thisController: ValueController[_, _]): Boolean = {
    valueController.exists(_ != thisController)
  }

  private[laminar] var hasValueBinder: Boolean = false

  private[laminar] def setValueController[A](
    updater: KeyUpdater[ReactiveHtmlElement.Base, HtmlProp[String, _], String],
    listener: EventListener[_ <: dom.Event, A]
  ): DynamicSubscription = {
    val controller = new ValueController[String, A](
      initialValue = "",
      getDomValue = DomApi.getValue(_).getOrElse(""),
      setDomValue = DomApi.setValue,
      element = this,
      updater,
      listener
    )
    val dynSub = controller.bind()
    valueController = controller
    dynSub
  }

  // -- `checked` prop controller

  private[this] var checkedController: js.UndefOr[ValueController[_, _]] = js.undefined

  private[laminar] def hasCheckedController: Boolean = checkedController.nonEmpty

  private[laminar] def hasOtherCheckedController(thisController: ValueController[_, _]): Boolean = {
    checkedController.exists(_ != thisController)
  }

  private[laminar] var hasCheckedBinder: Boolean = false

  private[laminar] def setCheckedController[A](
    updater: KeyUpdater[ReactiveHtmlElement.Base, HtmlProp[Boolean, _], Boolean],
    listener: EventListener[_ <: dom.Event, A]
  ): DynamicSubscription = {
    val controller = new ValueController[Boolean, A](
      initialValue = false,
      getDomValue = DomApi.getChecked(_).getOrElse(false),
      setDomValue = DomApi.setChecked,
      element = this,
      updater,
      listener
    )
    val dynSub = controller.bind()
    checkedController = controller
    dynSub
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
