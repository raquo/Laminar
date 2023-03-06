package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.ew.ewArray
import com.raquo.laminar.api.L.{MapValueMapper, StringValueMapper}
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.keys.CompositeKey.{CompositeCodec, CompositeValueMapper}
import com.raquo.laminar.modifiers.{CompositeKeySetter, KeyUpdater}
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js
import scala.scalajs.js.JSStringOps._

// #TODO[Performance] Should we use classList for className attribute instead of splitting strings? That needs IE 10+ (also, complexity)

class CompositeKey[K <: Key, -El <: ReactiveElement.Base](
  override val name: String,
  private[laminar] val getRawDomValue: El => String,
  private[laminar] val setRawDomValue: (El, String) => Unit,
  val separator: String
) extends Key {

  val codec: CompositeCodec = new CompositeCodec(separator)

  def :=(items: String): CompositeKeySetter[K, El] = {
    addStaticItems(StringValueMapper.toNormalizedList(items, separator))
  }

  def :=(items: Map[String, Boolean]): CompositeKeySetter[K, El] = {
    addStaticItems(MapValueMapper.toNormalizedList(items, separator))
  }

  def :=[V](value: V*)(implicit mapper: CompositeValueMapper[collection.Seq[V]]): CompositeKeySetter[K, El] = {
    addStaticItems(mapper.toNormalizedList(value, separator))
  }

  @inline def apply(items: String): CompositeKeySetter[K, El] = {
    this := items
  }

  @inline def apply(items: Map[String, Boolean]): CompositeKeySetter[K, El] = {
    this := items
  }

  @inline def apply[V](items: V*)(implicit mapper: CompositeValueMapper[collection.Seq[V]]): CompositeKeySetter[K, El] = {
    this.:=(items: _*)
  }

  def toggle(items: String*): LockedCompositeKey[K, El] = {
    new LockedCompositeKey(this, items.toList)
  }

  def <--[V](items: Source[V])(implicit valueMapper: CompositeValueMapper[V]): KeyUpdater[El, this.type, V] = {
    new KeyUpdater[El, this.type, V](
      key = this,
      values = items.toObservable,
      update = (element, nextRawItems, thisBinder) => {
        val currentNormalizedItems = element.compositeValueItems(this, reason = thisBinder)
        val nextNormalizedItems = valueMapper.toNormalizedList(nextRawItems, separator)

        val itemsToAdd = nextNormalizedItems.filterNot(currentNormalizedItems.contains)
        val itemsToRemove = currentNormalizedItems.filterNot(nextNormalizedItems.contains)

        element.updateCompositeValue(
          key = this,
          reason = thisBinder,
          addItems = itemsToAdd,
          removeItems = itemsToRemove
        )
      }
    )
  }

  private def addStaticItems(normalizedItems: List[String]): CompositeKeySetter[K, El] = {
    new CompositeKeySetter(
      key = this,
      itemsToAdd = normalizedItems,
      itemsToRemove = Nil
    )
  }
}

object CompositeKey {

  // #TODO[Perf] Consider switching `normalize` from List-s to JsArrays.
  //  - Not sure if this would win us anything because user-facing API is based on scala collections

  class CompositeCodec(separator: String) extends Codec[Iterable[String], String] {

    override def decode(domValue: String): List[String] = {
      CompositeKey.normalize(domValue, separator)
    }

    override def encode(scalaValue: Iterable[String]): String = {
      scalaValue.mkString(separator)
    }
  }

  /** @param items non-normalized string with one or more items separated by `separator`
    *
    * @return individual values. Note that normalization does NOT ensure that the items are unique.
    */
  def normalize(items: String, separator: String): List[String] = {
    if (items.isEmpty) {
      Nil
    } else {
      items.jsSplit(separator).ew.filter(_.nonEmpty).asScalaJs.toList
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
