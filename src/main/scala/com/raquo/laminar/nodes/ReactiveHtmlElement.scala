package com.raquo.laminar.nodes

import com.raquo.airstream.ownership.DynamicSubscription
import com.raquo.ew.JsArray
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L
import com.raquo.laminar.inputs.InputController
import com.raquo.laminar.keys.{HtmlProp, Key}
import com.raquo.laminar.modifiers.{EventListener, KeyUpdater}
import com.raquo.laminar.tags.{CustomHtmlTag, HtmlTag}
import org.scalajs.dom

import scala.scalajs.js

class ReactiveHtmlElement[+Ref <: dom.html.Element](
  override val tag: HtmlTag[Ref],
  final override val ref: Ref
) extends ReactiveElement[Ref] {

  // -- `value` prop controller

  // #nc do we actually need multiple controllers per element? Don't need it for HTML elements, if only for weird web components, maybe
  //  - actually as long as our custom elements API allows only one pair, this is moot, so make a choice here.

  /** List of value controllers installed on this element */
  private[this] var controllers: js.UndefOr[JsArray[InputController[_, _]]] = js.undefined

  /** List of binders for props that are controllable */
  private[this] var controllablePropBinders: js.UndefOr[JsArray[String]] = js.undefined

  private[this] def appendValueController(controller: InputController[_, _]): Unit = {
    controllers.fold {
      controllers = js.defined(JsArray(controller))
    }(_.push(controller))
  }

  private[this] def appendControllablePropBinder(propDomName: String): Unit = {
    controllablePropBinders.fold {
      controllablePropBinders = js.defined(JsArray(propDomName))
    }(_.push(propDomName))
  }

  private[laminar] def hasBinderForControllableProp(domPropName: String): Boolean = {
    controllablePropBinders.exists(_.includes(domPropName))
  }

  private[laminar] def hasOtherControllerForSameProp(thisController: InputController[_, _]): Boolean = {
    controllers.exists(_.asScalaJs.exists { otherController =>
      otherController.propDomName == thisController.propDomName && otherController != thisController
    })
  }

  private[laminar] def bindController(controller: InputController[_, _]): DynamicSubscription = {
    val dynSub = controller.bind()
    appendValueController(controller)
    dynSub
  }

  // private[laminar] def setValueController[A](
  //   updater: KeyUpdater[ReactiveHtmlElement.Base, HtmlProp[String, _], String],
  //   listener: EventListener[_ <: dom.Event, A]
  // ): DynamicSubscription = {
  //   val controller = new InputController[String, A](
  //     initialValue = "",
  //     getDomValue = DomApi.getValue(_).getOrElse(""),
  //     setDomValue = DomApi.setValue,
  //     element = this,
  //     updater,
  //     listener
  //   )
  //   val dynSub = controller.bind()
  //   appendValueController(controller)
  //   dynSub
  // }

  // -- `checked` prop controller

  // private[laminar] def setCheckedController[A](
  //   updater: KeyUpdater[ReactiveHtmlElement.Base, HtmlProp[Boolean, _], Boolean],
  //   listener: EventListener[_ <: dom.Event, A]
  // ): DynamicSubscription = {
  //   val controller = new InputController[Boolean, A](
  //     initialValue = false,
  //     getDomValue = DomApi.getChecked(_).getOrElse(false),
  //     setDomValue = DomApi.setChecked,
  //     element = this,
  //     updater,
  //     listener
  //   )
  //   val dynSub = controller.bind()
  //   appendValueController(controller)
  //   dynSub
  // }

  // --

  private[laminar] def controllableProps: js.UndefOr[JsArray[String]] = {
    if (DomApi.isCustomElement(ref)) {
      tag match {
        case t: CustomHtmlTag[Ref@unchecked] => t.allowableControlProps
        case _ => js.undefined
      }
    } else {
      InputController.htmlControllableProps
    }
  }

  private[laminar] def isControllableProp(propDomName: String): Boolean = {
    controllableProps.exists(_.includes(propDomName))
  }

  private def hasController(propDomName: String): Boolean = {
    controllers.exists(_.asScalaJs.exists(_.propDomName == propDomName))
  }

  override private[laminar] def onAddKeyUpdater(key: Key): Unit = {
    key match {
      case p: HtmlProp[_, _] =>
        if (isControllableProp(p.name)) {
          if (hasController(p.name)) {
            throw new Exception(s"Can not add a `${p.name} <-- ???` binder to an element that already has a `${p.name}` controller: ${DomApi.debugNodeDescription(ref)}")
          } else {
            appendControllablePropBinder(p.name)
          }
        }
      case _ => ()
    }
  }

  override def toString: String = {
    // `ref` is not available inside ReactiveElement's constructor due to initialization order, so fall back to `tag`.
    s"ReactiveHtmlElement(${if (ref != null) ref.outerHTML else s"tag=${tag.name}"})"
  }
}

object ReactiveHtmlElement {

  type Base = ReactiveHtmlElement[dom.html.Element]
}
