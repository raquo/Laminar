package com.raquo.laminar.inputs

import com.raquo.airstream.core.{Observable, Observer, Transaction}
import com.raquo.airstream.ownership.Owner
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{StringTransformer, defaultValue, onInput, onMountBind}
import com.raquo.laminar.api.{enrichObservable, eventPropToEventPropTransformation}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

import scala.scalajs.js

class ValueController(
  element: ReactiveHtmlElement.Base,
  source: Observable[String],
  processInput: StringTransformer,
  observer: Observer[String]
) {

  if (element.hasValueController) {
    throw new Exception(s"Can not add another `value` controller to an element that already has one: ${DomApi.debugNodeDescription(element.ref)}")
  }

  if (element.hasValueBinder) {
    throw new Exception(s"Can not add a `value` controller to an element that already has a `value <-- ???` binder: ${DomApi.debugNodeDescription(element.ref)}")
  }

  private var prevValue: String = {
    // @TODO[Elegance] Is there a non-ugly way to pattern match js.UndefOr?
    DomApi.getValue(element.ref).toOption match {
      case Some(value) =>
        value
      case None =>
        throw new Exception(s"Can not add a `value` controller to this element: ${DomApi.debugNodeDescription(element.ref)}")
    }
  }

  // Override the `defaultValue` prop.
  // If source is Signal, its initial value will in turn override this,
  // but if it's a stream, this will remain the effective initial value.
  //DomApi.setValue(element.ref, "")
  setValue("") // this also sets prevValue

  private def setValue(nextValue: String): Unit = {
    DomApi.setValue(element.ref, nextValue)
    prevValue = nextValue
  }

  private val transform = processInput(onInput.useCapture.mapToValue).mapRaw {
    case (ev, v @ Some(_)) =>
      // Value emitted – do nothing, resetObserver will handle it.
      v
    case (ev, _) =>
      // Value filtered out – stop propagation and reset to previous value
      //ev.stopPropagation()
      setValue(prevValue)
      None
  }

  private def combinedObserver(owner: Owner): Observer[String] = {
    var latestSourceValue: Option[String] = None

    source.foreach { sourceValue =>
      latestSourceValue = Some(sourceValue)
      setValue(sourceValue)
    }(owner)

    val resetObserver = Observer[String] { _ =>
      // Not sure if the transaction is necessary. I think browser events are always fired
      // outside of the transaction, so wrapping in Transaction is not required
      //new Transaction(_ => {
      setValue(latestSourceValue.getOrElse(prevValue))
      //})
    }
    Observer.combine(observer, resetObserver)
  }

  element.amend(
    onMountBind { ctx =>
      transform --> combinedObserver(ctx.owner)
    },
    //source --> { newSourceValue =>
    //  setValue(newSourceValue)
    //},
  )
}
