package com.raquo.laminar.modifiers

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.StringSeqValueMapper
import com.raquo.laminar.keys.{CompositeAttr, CompositeKey}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}

/**
  * This is like [[SimpleKeySetter]], but for composite attributes like `cls` and `role`.
  *
  * CompositeKeySetter can not be a subtype of [[SimpleKeySetter]] because we can not implement
  * `val value` – the actual value to be set is dynamic, depending on the current
  * value of the element's composite attribute.
  *
  * Also, if you call `cls := "class2"` after calling `cls := "class1"`, you end up
  * with two classes instead of just "class2", which is different from [[SimpleKeySetter]]
  * semantics.
  *
  * @param itemsToAdd NOTE: must be normalized (no empty strings; one value per item)
  */
class CompositeKeySetter[+K <: CompositeKey[K, El], -El <: ReactiveElement.Base](
  val key: K,
  val itemsToAdd: List[String]
) extends Setter[El] {

  /** This is called by Laminar when composite key setter is used as a modifier,
    * i.e. when you say `div(cls := "foo")` – much like the usual KeySetter-s.
    */
  override def apply(element: El): Unit = {
    if (itemsToAdd.nonEmpty) {
      element.updateCompositeValue(
        key = key,
        reason = null, // @Note using null to avoid keeping a reference to this setter
        addItems = itemsToAdd,
        removeItems = Nil
      )
    }
  }

  // The methods below let us use this CompositeKeySetter as a key,
  // instead of as a setter, supporting syntax like:
  //
  //     cls("foo") := myBoolean
  //     cls("foo", "bar") <-- myBooleanStream
  //
  // which sets one or more classes conditionally.

  /** If `include` is true, the items will be added, if false, they will not be added */
  def :=(include: Boolean): CompositeKeySetter[K, El] = {
    if (include) {
      key(itemsToAdd: _*)
    } else {
      key()
    }
  }

  /** If `include` is true, the items will be added, if false, they will not be added */
  @inline def apply(include: Boolean): CompositeKeySetter[K, El] = {
    this := include
  }

  /** If the `include` observable emits true, value(s) will be added,
    * if it emits false, they will be removed (but only if they were
    * previously added - see docs).
    */
  def <--(include: Source[Boolean]): CompositeKeyUpdater[K, List[String], El] = {
    key <-- include.toObservable.map(include => if (include) itemsToAdd else Nil)
  }
}

object CompositeKeySetter {

  type CompositeAttrSetter = CompositeKeySetter[CompositeAttr.Base, ReactiveElement.Base]

  type HtmlCompositeAttrSetter = CompositeKeySetter[CompositeAttr.HtmlCompositeAttr, ReactiveHtmlElement.Base]

  // type SvgCompositeAttrSetter = CompositeKeySetter[CompositeAttr.SvgCompositeAttr, ReactiveSvgElement.Base]
}
