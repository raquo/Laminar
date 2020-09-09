package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.keys.Key
import com.raquo.laminar.api.Laminar.{MapValueMapper, StringValueMapper}
import com.raquo.laminar.keys.CompositeAttr.CompositeValueMapper
import com.raquo.laminar.modifiers.{Binder, Setter}
import com.raquo.laminar.nodes.ReactiveElement

import scala.scalajs.js

// @TODO[Performance] We can eventually use classList for className attribute instead of splitting strings. That needs IE 10+
// @TODO[Performance] Will string splitting be faster with native JS method? How do we access it?

class CompositeAttr[Attr <: Key, El <: ReactiveElement.Base](val key: Attr, val separator: Char) {

  // @TODO[API] Should StringValueMapper be passed implicitly?
  @inline def apply(items: String): Setter[El] = {
    update(StringValueMapper.toDict(items, separator))
  }

  @inline def apply(items: Map[String, Boolean]): Setter[El] = {
    update(MapValueMapper.toDict(items, separator))
  }

  @inline def apply[V](items: V*)(implicit mapper: CompositeValueMapper[collection.Seq[V]]): Setter[El] = {
    update(mapper.toDict(items, separator))
  }

  @inline def :=(items: String): Setter[El] = {
    update(StringValueMapper.toDict(items, separator))
  }

  @inline def :=(items: Map[String, Boolean]): Setter[El] = {
    update(MapValueMapper.toDict(items, separator))
  }

  @inline def :=[V](value: V*)(implicit mapper: CompositeValueMapper[collection.Seq[V]]): Setter[El] = {
    update(mapper.toDict(value, separator))
  }

  /** This method provides standard magic-free behaviour, simply overriding the attribute with a new value */
  def set(newItems: String): Setter[El] = {
    Setter { element =>
      element.ref.setAttributeNS(namespaceURI = null, qualifiedName = key.name, newItems)
    }
  }

  def set(newItems: String*): Setter[El] = {
    set(newItems.mkString(separator.toString))
  }

  def remove(items: String*): Setter[El] = {
    update(js.Dictionary(items.map(item => (item -> false)): _*))
  }

  def toggle(items: String*): LockedCompositeAttr[Attr, El] = {
    new LockedCompositeAttr(this, items.toList)
  }

  def <--[V]($items: Observable[V])(implicit valueMapper: CompositeValueMapper[V]): Binder[El] = {
    // @TODO[update:scalajs] prevItems should be js.Dictionary, wait for https://github.com/scala-js/scala-js/pull/3991
    var prevItems = js.WrappedDictionary.empty[Boolean]
    Binder { element =>
      ReactiveElement.bindFn(element, $items) { items =>

        // Convert incoming value into a normalized map of chunks
        val nextItems = js.Dictionary[Boolean]()
        normalizeAndUpdateItems(nextItems, valueMapper.toDict(items, separator))

        // Mark for removal the previous chunks that are not needed anymore
        prevItems.foreach { prevClsTuple =>
          if (!nextItems.contains(prevClsTuple._1)) {
            nextItems.update(prevClsTuple._1, value = false)
          }
        }

        // @TODO[Performance] nextItems will be normalized again in this call, which is extraneous
        // Apply changes to the DOM
        update(nextItems)(element)

        // Update previous chunks
        prevItems = nextItems.filter(_._2)
      }
    }
  }

  private def update(newItems: js.Dictionary[Boolean]): Setter[El] = {
    Setter { element =>
      // @TODO[Elegance] We're talking to element.ref directly instead of using DomApi. Not ideal.
      val items = js.Dictionary.empty[Boolean]

      // Get current value from the DOM
      val domValue = element.ref.getAttributeNS(namespaceURI = null, localName = key.name)
      normalizeAndUpdateItems(items, if (domValue == null) "" else domValue, add = true)

      // Update value to desired state
      normalizeAndUpdateItems(items, newItems)

      // Remove keys that should not be present in the DOM
      items.keys.foreach { key =>
        if (!items(key)) items.remove(key)
      }

      // Write desired state to the Dom
      element.ref.setAttributeNS(namespaceURI = null, qualifiedName = key.name, items.keys.mkString(separator.toString))
    }
  }

  /** @param newItems non-normalized dictionary (contains non-normalized string keys) */
  @inline private def normalizeAndUpdateItems(items: js.Dictionary[Boolean], newItems: js.Dictionary[Boolean]): Unit = {
    newItems.foreach { itemTuple =>
      normalizeAndUpdateItems(items, newItems = itemTuple._1, add = itemTuple._2)
    }
  }

  /** @param newItems non-normalized string with one or more items separated by `separator`*/
  private def normalizeAndUpdateItems(items: js.Dictionary[Boolean], newItems: String, add: Boolean): Unit = {
    if (newItems.nonEmpty) {
      if (newItems.contains(separator)) {
        newItems.split(separator).foreach { newItem =>
          if (newItem.nonEmpty) {
            items.update(newItem, add)
          }
        }
      } else {
        items.update(newItems, add)
      }
    }
  }

}

object CompositeAttr {

  trait CompositeValueMapper[-V] {
    def toDict(value: V, separator: Char): js.Dictionary[Boolean]
  }

  trait CompositeValueMappers {

    implicit object StringValueMapper extends CompositeValueMapper[String] {
      override def toDict(item: String, separator: Char): js.Dictionary[Boolean] = {
        js.Dictionary(item -> true)
      }
    }

    implicit object StringSeqValueMapper extends CompositeValueMapper[collection.Seq[String]] {
      override def toDict(items: collection.Seq[String], separator: Char): js.Dictionary[Boolean] = {
        val dict = js.Dictionary.empty[Boolean]
        items.foreach(item => dict.update(item, true))
        dict
      }
    }

    implicit object StringSeqSeqValueMapper extends CompositeValueMapper[collection.Seq[collection.Seq[String]]] {
      override def toDict(items: collection.Seq[collection.Seq[String]], separator: Char): js.Dictionary[Boolean] = {
        val dict = js.Dictionary.empty[Boolean]
        items.flatten.foreach(item => dict.update(item, true))
        dict
      }
    }

    implicit object StringBooleanSeqValueMapper extends CompositeValueMapper[collection.Seq[(String, Boolean)]] {
      override def toDict(items: collection.Seq[(String, Boolean)], separator: Char): js.Dictionary[Boolean] = {
        val dict = js.Dictionary[Boolean]()
        items.foreach(itemTuple => dict.update(itemTuple._1, itemTuple._2))
        dict
      }
    }

    implicit object StringBooleanSeqSeqValueMapper extends CompositeValueMapper[collection.Seq[collection.Seq[(String, Boolean)]]] {
      override def toDict(items: collection.Seq[collection.Seq[(String, Boolean)]], separator: Char): js.Dictionary[Boolean] = {
        val dict = js.Dictionary[Boolean]()
        items.flatten.foreach(itemTuple => dict.update(itemTuple._1, itemTuple._2))
        dict
      }
    }

    implicit object MapValueMapper extends CompositeValueMapper[Map[String, Boolean]] {
      // @TODO[Performance] Check how `_*` is encoded in Scala.js, if it's too heavy we should review all usages of it.
      override def toDict(items: Map[String, Boolean], separator: Char): js.Dictionary[Boolean] = js.Dictionary(items.toList: _*)
    }

    implicit object JsDictionaryValueMapper extends CompositeValueMapper[js.Dictionary[Boolean]] {
      override def toDict(items: js.Dictionary[Boolean], separator: Char): js.Dictionary[Boolean] = items
    }

  }

}
