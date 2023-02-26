package com.raquo.laminar

package object codecs {

  @deprecated("This used to be a subtrait of Codec, but now it's just a type alias. It will be removed later.")
  type AsIsCodec[V] = Codec[V, V]

  def AsIsCodec[V](): Codec[V, V] = new Codec[V, V] {
    override def encode(scalaValue: V): V = scalaValue

    override def decode(domValue: V): V = domValue
  }

  // String Codecs

  val StringAsIsCodec: Codec[String, String] = AsIsCodec()

  // Int Codecs

  val IntAsIsCodec: Codec[Int, Int] = AsIsCodec()

  lazy val IntAsStringCodec: Codec[Int, String] = new Codec[Int, String] {

    override def decode(domValue: String): Int = domValue.toInt // @TODO this can throw exception. How do we handle this?

    override def encode(scalaValue: Int): String = scalaValue.toString
  }

  // Double Codecs

  lazy val DoubleAsIsCodec: Codec[Double, Double] = AsIsCodec()

  lazy val DoubleAsStringCodec: Codec[Double, String] = new Codec[Double, String] {

    override def decode(domValue: String): Double = domValue.toDouble // @TODO this can throw exception. How do we handle this?

    override def encode(scalaValue: Double): String = scalaValue.toString
  }

  // Boolean Codecs

  val BooleanAsIsCodec: Codec[Boolean, Boolean] = AsIsCodec()

  val BooleanAsAttrPresenceCodec: Codec[Boolean, String] = new Codec[Boolean, String] {

    override def decode(domValue: String): Boolean = domValue != null

    override def encode(scalaValue: Boolean): String = if (scalaValue) "" else null
  }

  lazy val BooleanAsTrueFalseStringCodec: Codec[Boolean, String] = new Codec[Boolean, String] {

    override def decode(domValue: String): Boolean = domValue == "true"

    override def encode(scalaValue: Boolean): String = if (scalaValue) "true" else "false"
  }

  lazy val BooleanAsYesNoStringCodec: Codec[Boolean, String] = new Codec[Boolean, String] {

    override def decode(domValue: String): Boolean = domValue == "yes"

    override def encode(scalaValue: Boolean): String = if (scalaValue) "yes" else "no"
  }

  lazy val BooleanAsOnOffStringCodec: Codec[Boolean, String] = new Codec[Boolean, String] {

    override def decode(domValue: String): Boolean = domValue == "on"

    override def encode(scalaValue: Boolean): String = if (scalaValue) "on" else "off"
  }

  // Iterable Codecs

  @deprecated("Laminar no longer uses IterableAsSpaceSeparatedStringCodec so I plan to remove it; if you need it, please let me know", "15.0.0-M7")
  lazy val IterableAsSpaceSeparatedStringCodec: Codec[Iterable[String], String] = new Codec[Iterable[String], String] { // could use for e.g. className

    override def decode(domValue: String): Iterable[String] = if (domValue == "") Nil else domValue.split(' ')

    override def encode(scalaValue: Iterable[String]): String = scalaValue.mkString(" ")
  }

  @deprecated("Laminar no longer uses IterableAsCommaSeparatedStringCodec so I plan to remove it; if you need it, please let me know", "15.0.0-M7")
  lazy val IterableAsCommaSeparatedStringCodec: Codec[Iterable[String], String] = new Codec[Iterable[String], String] { // could use for lists of IDs

    override def decode(domValue: String): Iterable[String] = if (domValue == "") Nil else domValue.split(',')

    override def encode(scalaValue: Iterable[String]): String = scalaValue.mkString(",")
  }
}
