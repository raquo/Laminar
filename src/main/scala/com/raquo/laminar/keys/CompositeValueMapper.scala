package com.raquo.laminar.keys

import scala.scalajs.js

trait CompositeValueMapper[-V] {

  /** Note: normalization does not include deduplication */
  def toNormalizedList(value: V, separator: String): List[String]
}

object CompositeValueMapper {

  import com.raquo.laminar.codecs.CompositeCodec.normalize

  trait Implicits {

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
