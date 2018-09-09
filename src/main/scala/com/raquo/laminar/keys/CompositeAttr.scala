package com.raquo.laminar.keys

import com.raquo.airstream.core.Observable
import com.raquo.domtypes.generic.Modifier
import com.raquo.domtypes.generic.keys.Key
import com.raquo.laminar.api.Laminar.{HtmlElement, MapValueMapper, StringValueMapper}
import com.raquo.laminar.keys.CompositeAttr.CompositeValueMapper

import scala.scalajs.js
import scala.scalajs.js.Dictionary

// @TODO[Performance] We can eventually use classList for className attribute instead of splitting strings. That needs IE 10+
// @TODO[Performance] Will string splitting be faster with native JS method? How do we access it?

class CompositeAttr[Attr <: Key](val key: Attr, separator: Char) {

  // @TODO[API] Should StringValueMapper be passed implicitly?
  @inline def apply(items: String): Modifier[HtmlElement] = {
    update(StringValueMapper.toDict(items, separator))
  }

  @inline def apply(items: Map[String, Boolean]): Modifier[HtmlElement] = {
    update(MapValueMapper.toDict(items, separator))
  }

  @inline def apply[V](items: V*)(implicit mapper: CompositeValueMapper[Seq[V]]): Modifier[HtmlElement] = {
    update(mapper.toDict(items, separator))
  }

  @inline def :=(items: String): Modifier[HtmlElement] = {
    update(StringValueMapper.toDict(items, separator))
  }

  @inline def :=(items: Map[String, Boolean]): Modifier[HtmlElement] = {
    update(MapValueMapper.toDict(items, separator))
  }

  @inline def :=[V](value: V*)(implicit mapper: CompositeValueMapper[Seq[V]]): Modifier[HtmlElement] = {
    update(mapper.toDict(value, separator))
  }

  /** This method provides standard magic-free behaviour, simply overriding the attribute with a new value */
  def set(newItems: String): Modifier[HtmlElement] = {
    new Modifier[HtmlElement] {
      override def apply(element: HtmlElement): Unit = {
        element.ref.setAttributeNS(namespaceURI = null, qualifiedName = key.name, newItems)
      }
    }
  }

  def <--[V]($items: Observable[V])(implicit valueMapper: CompositeValueMapper[V]): Modifier[HtmlElement] = {
    var prevItems = js.Dictionary.empty[Boolean]
    new Modifier[HtmlElement] {
      override def apply(element: HtmlElement): Unit = {
        element.subscribe($items) { items =>

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
          prevItems = nextItems.filter(_._2).dict
        }
      }
    }
  }

  private def update(newItems: js.Dictionary[Boolean]): Modifier[HtmlElement] = {
    new Modifier[HtmlElement] {
      override def apply(element: HtmlElement): Unit = {
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
  }

  /** @param newItems non-normalized dictionary (contains non-normalized string keys) */
  @inline private def normalizeAndUpdateItems(items: js.Dictionary[Boolean], newItems: js.Dictionary[Boolean]): Unit = {
    newItems.foreach { itemTuple =>
      normalizeAndUpdateItems(items, newItems = itemTuple._1, add = itemTuple._2)
    }
  }

  /** @param newItems non-normalized string with one or more items separated by `separator` */
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
      override def toDict(item: String, separator: Char): Dictionary[Boolean] = {
        js.Dictionary(item -> true)
      }
    }

    implicit object StringSeqValueMapper extends CompositeValueMapper[Seq[String]] {
      override def toDict(items: Seq[String], separator: Char): Dictionary[Boolean] = {
        val dict = js.Dictionary.empty[Boolean]
        items.foreach(item => dict.update(item, true))
        dict
      }
    }

    implicit object StringSeqSeqValueMapper extends CompositeValueMapper[Seq[Seq[String]]] {
      override def toDict(items: Seq[Seq[String]], separator: Char): Dictionary[Boolean] = {
        val dict = js.Dictionary.empty[Boolean]
        items.flatten.foreach(item => dict.update(item, true))
        dict
      }
    }

    implicit object StringBooleanSeqValueMapper extends CompositeValueMapper[Seq[(String, Boolean)]] {
      // @TODO[Performance] Check how `_*` is encoded in Scala.js, if it's too heavy we should review all usages of it.
      override def toDict(items: Seq[(String, Boolean)], separator: Char): js.Dictionary[Boolean] = js.Dictionary(items: _*)
    }

    implicit object StringBooleanSeqSeqValueMapper extends CompositeValueMapper[Seq[Seq[(String, Boolean)]]] {
      // @TODO[Performance] Check how `_*` is encoded in Scala.js, if it's too heavy we should review all usages of it.
      override def toDict(items: Seq[Seq[(String, Boolean)]], separator: Char): js.Dictionary[Boolean] = js.Dictionary(items.flatten: _*)
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
