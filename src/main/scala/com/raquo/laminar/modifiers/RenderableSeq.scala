package com.raquo.laminar.modifiers

import com.raquo.ew.{JsArray, JsVector}
import com.raquo.laminar

import scala.scalajs.js

// #TODO[Naming] - LSeq + RenderableSeq?   RenderableSeq + IsRenderableSeq? // #nc
trait RenderableSeq[-Collection[_]] {

  def toSeq[A](values: Collection[A]): laminar.Seq[A]
}

object RenderableSeq {

  implicit object collectionSeqRenderable extends RenderableSeq[collection.Seq] {
    override def toSeq[A](values: collection.Seq[A]): laminar.Seq[A] = {
      laminar.Seq.from(values)
    }
  }

  implicit object scalaArrayRenderable extends RenderableSeq[scala.Array] {
    override def toSeq[A](values: scala.Array[A]): laminar.Seq[A] = {
      laminar.Seq.from(values)
    }
  }

  implicit object jsArrayRenderable extends RenderableSeq[JsArray] {
    override def toSeq[A](values: JsArray[A]): laminar.Seq[A] = {
      laminar.Seq.from(values)
    }
  }

  implicit object sjsArrayRenderable extends RenderableSeq[js.Array] {
    override def toSeq[A](values: js.Array[A]): laminar.Seq[A] = {
      laminar.Seq.from(values)
    }
  }

  implicit object jsVectorRenderable extends RenderableSeq[JsVector] {
    override def toSeq[A](values: JsVector[A]): laminar.Seq[A] = {
      laminar.Seq.from(values)
    }
  }

  implicit object laminarSeqRenderable extends RenderableSeq[laminar.Seq] {
    override def toSeq[A](values: laminar.Seq[A]): laminar.Seq[A] = {
      values
    }
  }

  // object optionRenderable extends RenderableSeq[Option] {
  //   override def toSeq[A](maybeValue: Option[A]): laminar.Seq[A] = {
  //     laminar.Seq.from(maybeValue.toList)
  //   }
  // }
  //
  // object jsUndefOrRenderable extends RenderableSeq[js.UndefOr] {
  //   override def toSeq[A](maybeValue: js.UndefOr[A]): laminar.Seq[A] = {
  //     laminar.Seq.from(JsArray.from(maybeValue))
  //   }
  // }

}
