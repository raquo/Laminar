package com.raquo.laminar.codecs

/** Use this codec when you don't need any data transformation */
trait AsIsCodec[T] extends Codec[T, T] {
  override def decode(domValue: T): T = domValue
  override def encode(scalaValue: T): T = scalaValue
}

object AsIsCodec {

  /** Note: We already have several AsIsCodec instances in codecs/package.scala */
  def apply[T]: AsIsCodec[T] = new AsIsCodec[T] {}
}
