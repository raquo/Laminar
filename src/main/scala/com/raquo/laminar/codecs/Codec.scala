package com.raquo.laminar.codecs

import scala.scalajs.js.|

/** This trait represents a way to encode and decode HTML attribute or DOM property values.
  *
  * It is needed because attributes encode all values as strings regardless of their type,
  * and then there are also multiple ways to encode e.g. boolean values. Some attributes
  * encode those as "true" / "false" strings, others as presence or absence of the element,
  * and yet others use "yes" / "no" or "on" / "off" strings, and properties encode booleans
  * as actual booleans.
  *
  * Scala DOM Types hides all this mess from you using codecs. All those pseudo-boolean
  * attributes would be simply `Attr[Boolean](name, codec)` in your code.
  */
trait Codec[ScalaType, DomType] { self =>

  /** Convert the result of a `dom.Node.getAttribute` call to appropriate Scala type.
    *
    * Note: HTML Attributes are generally optional, and `dom.Node.getAttribute` will return
    * `null` if an attribute is not defined on a given DOM node. However, this decoder is
    * only intended for cases when the attribute is defined.
    */
  def decode(domValue: DomType): ScalaType

  /** Convert desired attribute value to appropriate DOM type. The resulting value should
    * be passed to `dom.Node.setAttribute` call, EXCEPT when resulting value is a `null`.
    * In that case you should call `dom.Node.removeAttribute` instead.
    *
    * We use `null` instead of [[Option]] here to reduce overhead in JS land. This method
    * should not be called by end users anyway, it's the consuming library's job to
    * call this method under the hood.
    */
  def encode(scalaValue: ScalaType): DomType

  lazy val optAsNull: Codec[Option[ScalaType], DomType] = {
    new Codec[Option[ScalaType], DomType] {

      override def decode(domValue: DomType): Option[ScalaType] = {
        Option(domValue).map(self.decode)
      }

      override def encode(scalaValue: Option[ScalaType]): DomType = {
        // #Safe – `encode` is SUPPOSED to return `null` (remove-key command) when input scalaValue is None.
        Codec.getOrForceNull(scalaValue.map(self.encode))
      }
    }
  }
}

object Codec {

  def getOrForceNull[A](valueOpt: Option[A]): A = {
    valueOpt.getOrElse(null).asInstanceOf[A]
  }

  def mapNullable[A, B](value: A | Null, project: A => B): B | Null = {
    if (value == null) null else project(value.asInstanceOf[A])
  }

  @inline def foldNullable[A, B](value: A | Null)(@inline ifNull: => B, @inline ifValue: A => B): B = {
    if (value == null) {
      ifNull
    } else {
      ifValue(value.asInstanceOf[A])
    }
  }

  /** "as-is" codecs are identity functions – they read / write the value directly.
    *
    * Note: Codec.asIsCodec[Foo] creates a new codec instance at every invocation.
    */
  def asIsCodec[V]: Codec[V, V] = new Codec[V, V] {
    override def encode(scalaValue: V): V = scalaValue

    override def decode(domValue: V): V = domValue
  }

  val stringAsIs: Codec[String, String] = asIsCodec

  // --

  val intAsIs: Codec[Int, Int] = asIsCodec

  lazy val intAsString: Codec[Int, String] = new Codec[Int, String] {

    override def decode(domValue: String): Int = domValue.toInt // @TODO this can throw exception. How do we handle this?

    override def encode(scalaValue: Int): String = scalaValue.toString
  }

  // --

  lazy val doubleAsIs: Codec[Double, Double] = asIsCodec

  lazy val doubleAsString: Codec[Double, String] = new Codec[Double, String] {

    override def decode(domValue: String): Double = domValue.toDouble // @TODO this can throw exception. How do we handle this?

    override def encode(scalaValue: Double): String = scalaValue.toString
  }

  // --

  val booleanAsIs: Codec[Boolean, Boolean] = asIsCodec

  /** Codec for certain HTML attributes.
    *  - If you set `true` in Scala, attribute will be added with empty value.
    *  - If you set `false` in Scala, attribute will be removed from the DOM
    */
  val booleanAsAttrPresence: Codec[Boolean, String] = new Codec[Boolean, String] {

    override def decode(domValue: String): Boolean = domValue != null

    override def encode(scalaValue: Boolean): String = if (scalaValue) "" else null
  }

  lazy val booleanAsTrueFalseString: Codec[Boolean, String] = new Codec[Boolean, String] {

    override def decode(domValue: String): Boolean = domValue == "true"

    override def encode(scalaValue: Boolean): String = if (scalaValue) "true" else "false"
  }

  lazy val booleanAsYesNoString: Codec[Boolean, String] = new Codec[Boolean, String] {

    override def decode(domValue: String): Boolean = domValue == "yes"

    override def encode(scalaValue: Boolean): String = if (scalaValue) "yes" else "no"
  }

  lazy val booleanAsOnOffString: Codec[Boolean, String] = new Codec[Boolean, String] {

    override def decode(domValue: String): Boolean = domValue == "on"

    override def encode(scalaValue: Boolean): String = if (scalaValue) "on" else "off"
  }
}
