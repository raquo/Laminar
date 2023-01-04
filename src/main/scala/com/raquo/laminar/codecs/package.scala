package com.raquo.laminar

package object codecs {

  // String Codecs

  object StringAsIsCodec extends AsIsCodec[String]

  // Int Codecs

  object IntAsIsCodec extends AsIsCodec[Int]

  object IntAsStringCodec extends Codec[Int, String] {
    override def decode(domValue: String): Int = domValue.toInt // @TODO this can throw exception. How do we handle this?

    override def encode(scalaValue: Int): String = scalaValue.toString
  }

  // Double Codecs

  object DoubleAsIsCodec extends AsIsCodec[Double]

  object DoubleAsStringCodec extends Codec[Double, String] {
    override def decode(domValue: String): Double = domValue.toDouble // @TODO this can throw exception. How do we handle this?

    override def encode(scalaValue: Double): String = scalaValue.toString
  }

  // Boolean Codecs

  object BooleanAsIsCodec extends AsIsCodec[Boolean]

  object BooleanAsAttrPresenceCodec extends Codec[Boolean, String] {
    override def decode(domValue: String): Boolean = domValue != null

    override def encode(scalaValue: Boolean): String = if (scalaValue) "" else null
  }

  object BooleanAsTrueFalseStringCodec extends Codec[Boolean, String] {
    override def decode(domValue: String): Boolean = domValue == "true"

    override def encode(scalaValue: Boolean): String = if (scalaValue) "true" else "false"
  }

  object BooleanAsYesNoStringCodec extends Codec[Boolean, String] {
    override def decode(domValue: String): Boolean = domValue == "yes"

    override def encode(scalaValue: Boolean): String = if (scalaValue) "yes" else "no"
  }

  object BooleanAsOnOffStringCodec extends Codec[Boolean, String] {
    override def decode(domValue: String): Boolean = domValue == "on"

    override def encode(scalaValue: Boolean): String = if (scalaValue) "on" else "off"
  }

  // Iterable Codecs

  object IterableAsSpaceSeparatedStringCodec extends Codec[Iterable[String], String] { // use for e.g. className
    override def decode(domValue: String): Iterable[String] = if (domValue == "") Nil else domValue.split(' ')

    override def encode(scalaValue: Iterable[String]): String = scalaValue.mkString(" ")
  }

  object IterableAsCommaSeparatedStringCodec extends Codec[Iterable[String], String] { // use for lists of IDs
    override def decode(domValue: String): Iterable[String] = if (domValue == "") Nil else domValue.split(',')

    override def encode(scalaValue: Iterable[String]): String = scalaValue.mkString(",")
  }
}
