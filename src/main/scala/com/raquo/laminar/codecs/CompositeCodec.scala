package com.raquo.laminar.codecs

import com.raquo.ew.ewArray

import scala.scalajs.js.JSStringOps._

/** Such codecs are used for space-separated or comma-separated values, e.g. in the `cls` attr. */
class CompositeCodec(separator: String) extends Codec[Iterable[String], String] {

  override def decode(domValue: String): List[String] = {
    CompositeCodec.normalize(domValue, separator)
  }

  override def encode(scalaValue: Iterable[String]): String = {
    scalaValue.mkString(separator)
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
    } else {
      items.jsSplit(separator).ew.filter(_.nonEmpty).asScalaJs.toList
    }
  }
}
