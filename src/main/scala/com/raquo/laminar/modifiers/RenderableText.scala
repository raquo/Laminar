package com.raquo.laminar.modifiers

import com.raquo.laminar.nodes.TextNode

import scala.annotation.implicitNotFound
import scala.scalajs.js.|

/** `RenderableText[A]` is evidence that you can convert a value of type A to
  * a string for the purpose of rendering it as a TextNode.
  *
  * If you have an implicit val of RenderableText[A], Laminar can render your
  * `A` type values by converting them to strings (and ultimately into
  * [[com.raquo.laminar.nodes.TextNode]]), and will accept your values as
  * a valid [[Modifier]], and in `text <--`.
  *
  * See also – [[RenderableNode]]
  */
@implicitNotFound("Implicit instance of RenderableText[${TextLike}] not found. If you want to render `${TextLike}` as a string, define an implicit `RenderableText[${TextLike}]` instance for it.")
trait RenderableText[-TextLike] {

  // override this default value, it's only here to prevent this from qualifying as a SAM trait
  def asString(value: TextLike): String = ""

  // lazy val toOptionRenderable: RenderableText[Option[TextLike]] = {
  //   new RenderableText[Option[TextLike]] {
  //     override def asString(value: Option[TextLike]): String =
  //       value.fold(ifEmpty = "")(self.asString)
  //   }
  // }
  //
  // lazy val toJsUndefOrRenderable: RenderableText[js.UndefOr[TextLike]] = {
  //   new RenderableText[js.UndefOr[TextLike]] {
  //     override def asString(value: js.UndefOr[TextLike]): String =
  //       value.fold(ifEmpty = "")(self.asString)
  //   }
  // }
}

object RenderableText {

  type TextLikeBuiltIn = String | Int | Double | Boolean | Char | Byte | Short | Long | Float

  def apply[A](render: A => String): RenderableText[A] = new RenderableText[A] {
    override def asString(value: A): String = render(value)
  }

  implicit val stringRenderable: RenderableText[String] = RenderableText[String](identity)

  implicit val intRenderable: RenderableText[Int] = RenderableText[Int](_.toString)

  implicit val doubleRenderable: RenderableText[Double] = RenderableText[Double](_.toString)

  implicit val boolRenderable: RenderableText[Boolean] = RenderableText[Boolean](_.toString)

  // --

  /** #Warning: Using this naively in ChildTextInserter is not efficient.
    *  When we encounter this renderable instance, we use ChildInserter instead.
    */
  implicit val textNodeRenderable: RenderableText[TextNode] = RenderableText[TextNode](_.text)

  // --

  implicit lazy val charRenderable: RenderableText[Char] = RenderableText[Char](_.toString)

  implicit lazy val byteRenderable: RenderableText[Byte] = RenderableText[Byte](_.toString)

  implicit lazy val shortRenderable: RenderableText[Short] = RenderableText[Short](_.toString)

  implicit lazy val longRenderable: RenderableText[Long] = RenderableText[Long](_.toString)

  implicit lazy val floatRenderable: RenderableText[Float] = RenderableText[Float](_.toString)

  // implicit def optionRenderable[A](implicit r: RenderableText[A]): RenderableText[Option[A]] = {
  //   r.toOptionRenderable
  // }
  //
  // implicit def jsUndefOrRenderable[A](implicit r: RenderableText[A]): RenderableText[js.UndefOr[A]] = {
  //   r.toJsUndefOrRenderable
  // }

  implicit def unionRenderable2[A <: TextLikeBuiltIn, B <: TextLikeBuiltIn]: RenderableText[A | B] = {
    RenderableText[A | B](_.toString)
  }

  // implicit def unionRenderable3[A <: TextLikeBuiltIn, B <: TextLikeBuiltIn, C <: TextLikeBuiltIn]: RenderableText[A | B | C] = {
  //   RenderableText[A | B | C](_.toString)
  // }

  // implicit def unionRenderable4[A <: TextLikeBuiltIn, B <: TextLikeBuiltIn, C <: TextLikeBuiltIn, D <: TextLikeBuiltIn]: RenderableText[A | B | C | D] = {
  //   RenderableText[A | B | C | D](_.toString)
  // }
  //
  // implicit def unionRenderable5[A <: TextLikeBuiltIn, B <: TextLikeBuiltIn, C <: TextLikeBuiltIn, D <: TextLikeBuiltIn, E <: TextLikeBuiltIn]: RenderableText[A | B | C | D | E] = {
  //   RenderableText[A | B | C | D | E](_.toString)
  // }
}
