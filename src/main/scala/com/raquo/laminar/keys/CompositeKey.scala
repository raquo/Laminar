package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.api.{MapValueMapper, StringSeqValueMapper, StringValueMapper}
import com.raquo.laminar.codecs.CompositeCodec
import com.raquo.laminar.modifiers.{CompositeKeySetter, CompositeKeyUpdater}
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js.|

// #TODO[Performance] Should we use classList for className attribute instead of splitting strings? That needs IE 10+ (also, complexity)

/** An attribute or property that we can add multiple values to,
  * often in the shape of space-separated strings, e.g. `class` (`cls` in Scala).
  *
  * In Laminar there is only one subtype: [[CompositeAttr]].
  *
  * In principle, you may easily define a CompositeHtmlProp subclass if you need it,
  * but I don't think any HTML props would need it, and Web Component libraries
  * typically work with attributes rather than props.
  */
abstract class CompositeKey[ //
  +Self <: CompositeKey[Self, El],
  -El <: ReactiveElement.Base
] { this: Self =>

  val name: String

  private[laminar] def getRawDomValue(element: El): String | Unit

  private[laminar] def setRawDomValue(element: El, value: String): Unit

  val separator: String

  val codec: CompositeCodec = new CompositeCodec(separator)

  def :=(items: String): CompositeKeySetter[Self, El] = {
    addStaticItems(StringValueMapper.toNormalizedList(items, separator))
  }

  def :=(items: Map[String, Boolean]): CompositeKeySetter[Self, El] = {
    addStaticItems(MapValueMapper.toNormalizedList(items, separator))
  }

  def :=[V](value: V*)(implicit mapper: CompositeValueMapper[collection.Seq[V]]): CompositeKeySetter[Self, El] = {
    addStaticItems(mapper.toNormalizedList(value, separator))
  }

  @inline def apply(items: String): CompositeKeySetter[Self, El] = {
    this := items
  }

  @inline def apply(items: Map[String, Boolean]): CompositeKeySetter[Self, El] = {
    this := items
  }

  @inline def apply[V](items: V*)(implicit mapper: CompositeValueMapper[collection.Seq[V]]): CompositeKeySetter[Self, El] = {
    this.:= (items: _*)
  }

  @deprecated(
    """cls.toggle("foo") attribute method is not necessary anymore: use cls("foo"), it now supports everything that toggle supported.""",
    since = "17.0.0-M1"
  )
  def toggle(items: String*): CompositeKeySetter[Self, El] = {
    this.:= (items: _*)
  }

  def <--[V](items: Source[V])(implicit valueMapper: CompositeValueMapper[V]): CompositeKeyUpdater[Self, V, El] = {
    new CompositeKeyUpdater[Self, V, El](
      key = this,
      values = items.toObservable,
      valueMapper = valueMapper
    )
  }

  private def addStaticItems(normalizedItems: List[String]): CompositeKeySetter[Self, El] = {
    new CompositeKeySetter(
      key = this,
      itemsToAdd = normalizedItems
    )
  }
}
