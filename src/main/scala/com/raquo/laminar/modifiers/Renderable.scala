package com.raquo.laminar.modifiers

import com.raquo.laminar.nodes.TextNode

import scala.annotation.implicitNotFound

@implicitNotFound("Unable to convert ${A} to string / TextNode: no implicit instance of Renderable found for ${A}. If you want to render a non-primitive type as a string, define an implicit Renderable instance for it.")
trait Renderable[A] {

  def asString(value: A): String

  def asTextNode(value: A): TextNode = new TextNode(asString(value))
}

object Renderable {

  def apply[A](render: A => String): Renderable[A] = new Renderable[A] {
    override def asString(value: A): String = render(value)
  }

  // #Note[API] if you want Renderable[TextNode], please let me know why.

  implicit val stringRenderable: Renderable[String] = Renderable[String](identity)

  implicit val intRenderable: Renderable[Int] = Renderable[Int](_.toString)

  implicit val doubleRenderable: Renderable[Double] = Renderable[Double](_.toString)

  implicit val boolRenderable: Renderable[Boolean] = Renderable[Boolean](_.toString)

  // --

  implicit lazy val charRenderable: Renderable[Char] = Renderable[Char](_.toString)

  implicit lazy val byteRenderable: Renderable[Byte] = Renderable[Byte](_.toString)

  implicit lazy val shortRenderable: Renderable[Short] = Renderable[Short](_.toString)

  implicit lazy val longRenderable: Renderable[Long] = Renderable[Long](_.toString)

  implicit lazy val floatRenderable: Renderable[Float] = Renderable[Float](_.toString)
}
