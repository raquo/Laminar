package com.raquo.laminar.inputs

import com.raquo.airstream.core.{Observable, Observer, Transaction}
import com.raquo.airstream.ownership.Owner
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.Laminar.{BooleanTransformer, onClick, onMountBind}
import com.raquo.laminar.api.{enrichObservable, eventPropToEventPropTransformation}
import com.raquo.laminar.nodes.ReactiveHtmlElement

class CheckedController(
  element: ReactiveHtmlElement.Base,
  source: Observable[Boolean],
  processInput: BooleanTransformer,
  observer: Observer[Boolean]
) {

  if (element.hasCheckedController) {
    throw new Exception(s"Can not add another `checked` controller to an element that already has one: ${DomApi.debugNodeDescription(element.ref)}")
  }

  if (element.hasCheckedBinder) {
    throw new Exception(s"Can not add a `checked` controller to an element that already has a `checked <-- ???` binder: ${DomApi.debugNodeDescription(element.ref)}")
  }

  private var prevChecked: Boolean = {
    // @TODO[Elegance] Is there a non-ugly way to pattern match js.UndefOr?
    DomApi.getChecked(element.ref).toOption match {
      case Some(checked) =>
        checked
      case None =>
        val extraHelpText = if (element.ref.tagName == "INPUT") {
          "You need to add `typ := \"checkbox\"` or `typ := \"radio\"` to this input element."
        }  else {
          ""
        }
        throw new Exception(s"Can not add a `checked` controller to this element: ${DomApi.debugNodeDescription(element.ref)}. $extraHelpText")
    }
  }

  // Override the `defaultChecked` prop.
  // If source is Signal, its initial value will in turn override this,
  // but if it's a stream, this will remain the effective initial value.
  //DomApi.setChecked(element.ref, checked = false)
  setChecked(false)

  private def setChecked(nextChecked: Boolean): Unit = {
    DomApi.setChecked(element.ref, nextChecked)
    prevChecked = nextChecked
  }

  private val transform = processInput(onClick.useCapture.mapToChecked).mapRaw {
    case (_, v @ Some(c)) =>
      // Value emitted – do nothing, resetObserver will handle it.
      println(s"- `checked` emitted from EPT. emitted = $c")
      v
    case (ev, _) =>
      // Value filtered out – stop propagation and reset to previous value
      //ev.stopPropagation()
      println(s"- `checked` filtered out in EPT. setting = $prevChecked now (that's `prevValue`)")
      setChecked(prevChecked)
      None
  }

  private def combinedObserver(owner: Owner): Observer[Boolean] = {
    var latestSourceValue: Option[Boolean] = None
    source.foreach { sourceValue =>
      println(s"- observed source update. setting = $sourceValue now")
      latestSourceValue = Some(sourceValue)
      setChecked(sourceValue)
    }(owner)
    val resetObserver = Observer[Boolean] { _ =>
      //new Transaction(_ => {
      //  latestSourceValue match {
      //    case Some(checked) => setChecked(checked)
      //    case None => setChecked(prevChecked)
      //  }
      //})
      val resetCheckedTo = latestSourceValue.getOrElse(prevChecked)
      println(s"- observed reset. setting = $resetCheckedTo now (that's latestSourceValue.getOrElse(prevChecked))")
      setChecked(resetCheckedTo)
    }
    Observer.combine(observer, resetObserver)
  }

  element.amend(
    onMountBind { ctx =>
      transform --> combinedObserver(ctx.owner)
    },
    //source --> { newSourceValue =>
    //  setChecked(newSourceValue)
    //},
  )
}
