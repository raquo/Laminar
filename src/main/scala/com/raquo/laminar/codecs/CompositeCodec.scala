package com.raquo.laminar.codecs

import scala.scalajs.js.|
import scala.scalajs.js.JSStringOps._

/** Such codecs are used for space-separated or comma-separated values, e.g. in the `cls` attr. */
class CompositeCodec(separator: String) extends Codec[Iterable[String], String] {

  override def decode(domValue: String): List[String] = {
    CompositeCodec.normalize(domValue, separator)
  }

  override def encode(scalaValue: Iterable[String]): String = {
    scalaValue.mkString(separator)
  }

  /** Updates a composite value using this codec's decode and encode methods.
    *
    * @param domValue raw DOM value, `()` if the attribute is not set
    * @param addItems must be normalized
    */
  def encodeUpdated(
    domValue: String | Unit,
    removeItems: List[String],
    addItems: List[String]
  ): String = {
    encode(domValue.map(decode).getOrElse(Nil).filterNot(removeItems.contains) ++ addItems)
  }
}

final private[laminar] class DefaultCompositeCodec(separator: String) extends CompositeCodec(separator) {

  override def encodeUpdated(
    domValue: String | Unit,
    removeItems: List[String],
    addItems: List[String]
  ): String = {
    var result = ""
    var isFirst = true
    val append = (item: String) => {
      if (isFirst) {
        result = item
        isFirst = false
      } else {
        result = result + separator + item
      }
    }
    domValue.foreach { value =>
      if (value.nonEmpty) {
        val items = value.jsSplit(separator)
        var i = 0
        while (i < items.length) {
          val item = items(i)
          if (item.nonEmpty && !removeItems.contains(item)) {
            append(item)
          }
          i += 1
        }
      }
    }
    addItems.foreach(append)
    result
  }
}

object CompositeCodec {

  // #TODO[Perf] Consider switching `normalize` from List-s to JsArrays.
  //  - Not sure if this would win us anything because user-facing API is based on scala collections
  //  - Note that this won't work on the JVM

  /** @param items non-normalized string with one or more items separated by `separator`
    *
    * @return individual values. Note that normalization does NOT ensure that the items are unique.
    */
  def normalize(items: String, separator: String): List[String] = {
    if (items.isEmpty) {
      Nil
    } else if (items.indexOf(separator) == -1) {
      // Fast path for the most common case: a single item, e.g. `cls := "active"`
      items :: Nil
    } else {
      val splitItems = items.jsSplit(separator)
      // Iterate backwards to build the List by prepending, preserving order
      var result: List[String] = Nil
      var i = splitItems.length - 1
      while (i >= 0) {
        val item = splitItems(i)
        if (item.nonEmpty) {
          result = item :: result
        }
        i -= 1
      }
      result
    }
  }
}
