package com.raquo.laminar

package object codecs {

  @deprecated("This used to be a subtrait of Codec, but now it's just a type alias. It will be removed later.")
  type AsIsCodec[V] = Codec[V, V]

  @deprecated("Moved to Codec.asIsCodec", "18.0.0-M1")
  def AsIsCodec[V](): Codec[V, V] = Codec.asIsCodec()

  // String Codecs

  @deprecated("Moved to Codec.stringAsIs", "18.0.0-M1")
  lazy val StringAsIsCodec: Codec[String, String] = Codec.stringAsIs

  // Int Codecs

  @deprecated("Moved to Codec.intAsIs", "18.0.0-M1")
  lazy val IntAsIsCodec: Codec[Int, Int] = Codec.intAsIs

  @deprecated("Moved to Codec.intAsString", "18.0.0-M1")
  lazy val IntAsStringCodec: Codec[Int, String] = Codec.intAsString

  // Double Codecs

  @deprecated("Moved to Codec.doubleAsIs", "18.0.0-M1")
  lazy val DoubleAsIsCodec: Codec[Double, Double] = Codec.doubleAsIs

  @deprecated("Moved to Codec.doubleAsString", "18.0.0-M1")
  lazy val DoubleAsStringCodec: Codec[Double, String] = Codec.doubleAsString

  // Boolean Codecs

  @deprecated("Moved to Codec.booleanAsIs", "18.0.0-M1")
  lazy val BooleanAsIsCodec: Codec[Boolean, Boolean] = Codec.booleanAsIs

  @deprecated("Moved to Codec.booleanAsAttrPresenceCodec", "18.0.0-M1")
  lazy val BooleanAsAttrPresenceCodec: Codec[Boolean, String] = Codec.booleanAsAttrPresenceCodec

  @deprecated("Moved to Codec.booleanAsTrueFalseString", "18.0.0-M1")
  lazy val BooleanAsTrueFalseStringCodec: Codec[Boolean, String] = Codec.booleanAsTrueFalseString

  @deprecated("Moved to Codec.booleanAsYesNoString", "18.0.0-M1")
  lazy val BooleanAsYesNoStringCodec: Codec[Boolean, String] = Codec.booleanAsYesNoString

  @deprecated("Moved to Codec.booleanAsOnOffString", "18.0.0-M1")
  lazy val BooleanAsOnOffStringCodec: Codec[Boolean, String] = Codec.booleanAsOnOffString
}
