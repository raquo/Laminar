package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.airstream.core.Source
import com.raquo.laminar.api.Laminar.{MapValueMapper, StringValueMapper}
import com.raquo.laminar.keys.CompositeKey.CompositeValueMapper
import com.raquo.laminar.modifiers.{Binder, Setter}
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js
import scala.scalajs.js.JSStringOps._

// @TODO[Performance] We can eventually use classList for className attribute instead of splitting strings. That needs IE 10+

class CompositeKey[
  Key,
  -El <: ReactiveElement.Base
](
  val key: Key,
  private[laminar] val getDomValue: El => List[String],
  private[laminar] val setDomValue: (El, List[String]) => Unit,
  val separator: String
) { self =>

  // @TODO[API] Should StringValueMapper be passed implicitly?
  @inline def apply(items: String): Setter[El] = {
    addStaticItems(StringValueMapper.toNormalizedList(items, separator))
  }

  @inline def apply(items: Map[String, Boolean]): Setter[El] = {
    addStaticItems(MapValueMapper.toNormalizedList(items, separator))
  }

  @inline def apply[V](items: V*)(implicit mapper: CompositeValueMapper[collection.Seq[V]]): Setter[El] = {
    addStaticItems(mapper.toNormalizedList(items, separator))
  }

  @inline def :=(items: String): Setter[El] = {
    addStaticItems(StringValueMapper.toNormalizedList(items, separator))
  }

  @inline def :=(items: Map[String, Boolean]): Setter[El] = {
    addStaticItems(MapValueMapper.toNormalizedList(items, separator))
  }

  @inline def :=[V](value: V*)(implicit mapper: CompositeValueMapper[collection.Seq[V]]): Setter[El] = {
    addStaticItems(mapper.toNormalizedList(value, separator))
  }

  def toggle(items: String*): LockedCompositeKey[Key, El] = {
    new LockedCompositeKey(this, items.toList)
  }

  def <--[V]($items: Source[V])(implicit valueMapper: CompositeValueMapper[V]): Binder[El] = {
    Binder.withSelf[El] { (element, thisBinder) =>
      ReactiveElement.bindFn(element, $items.toObservable) { nextRawItems =>
        val currentNormalizedItems = element.compositeValueItems(self, thisBinder)
        val nextNormalizedItems = valueMapper.toNormalizedList(nextRawItems, separator)

        val itemsToAdd = nextNormalizedItems.filterNot(currentNormalizedItems.contains)
        val itemsToRemove = currentNormalizedItems.filterNot(nextNormalizedItems.contains)

        element.updateCompositeValue(
          key = self,
          reason = thisBinder,
          addItems = itemsToAdd,
          removeItems = itemsToRemove
        )
      }
    }
  }

  private def addStaticItems(normalizedItems: List[String]): Setter[El] = {
    if (normalizedItems.nonEmpty) {
      Setter { element =>
        element.updateCompositeValue(
          key = self,
          reason = null, // @Note using null to avoid keeping a reference to this setter
          addItems = normalizedItems,
          removeItems = Nil
        )
      }
    } else {
      Setter.noop
    }
  }
}

object CompositeKey {

  /** @param items non-normalized string with one or more items separated by `separator`
    *
    * @return individual values. Note that normalization does NOT ensure that the items are unique.
    */
  def normalize(items: String, separator: String): List[String] = {
    if (items.isEmpty) {
      Nil
    } else {
      items.jsSplit(separator).filter(_.nonEmpty).toList
    }
  }

  trait CompositeValueMapper[-V] {

    /** Note: normalization does not include deduplication */
    def toNormalizedList(value: V, separator: String): List[String]
  }

  trait CompositeValueMappers {

    implicit object StringValueMapper extends CompositeValueMapper[String] {

      override def toNormalizedList(item: String, separator: String): List[String] = {
        normalize(item, separator)
      }
    }

    implicit object StringSeqValueMapper extends CompositeValueMapper[collection.Seq[String]] {

      override def toNormalizedList(items: collection.Seq[String], separator: String): List[String] = {
        items.toList.flatMap(normalize(_, separator))
      }
    }

    implicit object StringSeqSeqValueMapper extends CompositeValueMapper[collection.Seq[collection.Seq[String]]] {

      override def toNormalizedList(items: collection.Seq[collection.Seq[String]], separator: String): List[String] = {
        items.toList.flatten.flatMap(normalize(_, separator))
      }
    }

    implicit object StringBooleanSeqValueMapper extends CompositeValueMapper[collection.Seq[(String, Boolean)]] {

      override def toNormalizedList(items: collection.Seq[(String, Boolean)], separator: String): List[String] = {
        items.filter(_._2).map(_._1).toList.flatMap(normalize(_, separator))
      }
    }

    implicit object StringBooleanSeqSeqValueMapper extends CompositeValueMapper[collection.Seq[collection.Seq[(String, Boolean)]]] {

      override def toNormalizedList(items: collection.Seq[collection.Seq[(String, Boolean)]], separator: String): List[String] = {
        items.toList.flatten.filter(_._2).map(_._1).flatMap(normalize(_, separator))
      }
    }

    implicit object MapValueMapper extends CompositeValueMapper[Map[String, Boolean]] {

      override def toNormalizedList(items: Map[String, Boolean], separator: String): List[String] = {
        items.filter(_._2).keys.toList.flatMap(normalize(_, separator))
      }
    }

    implicit object JsDictionaryValueMapper extends CompositeValueMapper[js.Dictionary[Boolean]] {

      override def toNormalizedList(items: js.Dictionary[Boolean], separator: String): List[String] = {
        items.filter(_._2).keys.toList.flatMap(normalize(_, separator))
      }
    }

  }

}
