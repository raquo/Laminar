package com.raquo.laminar.modifiers

import com.raquo.ew.{JsArray, JsVector}
import com.raquo.laminar.inserters.ChildrenSeq

import scala.scalajs.js

// #TODO[Naming] - LSeq + RenderableSeq?   RenderableSeq + IsRenderableSeq? // #nc
trait RenderableSeq[-Collection[_]] {

  def toChildrenSeq[A](values: Collection[A]): ChildrenSeq[A]
}

object RenderableSeq {

  implicit object collectionSeqRenderable extends RenderableSeq[collection.Seq] {
    override def toChildrenSeq[A](values: collection.Seq[A]): ChildrenSeq[A] = {
      ChildrenSeq.from(values)
    }
  }

  implicit object scalaArrayRenderable extends RenderableSeq[scala.Array] {
    override def toChildrenSeq[A](values: scala.Array[A]): ChildrenSeq[A] = {
      ChildrenSeq.from(values)
    }
  }

  implicit object jsArrayRenderable extends RenderableSeq[JsArray] {
    override def toChildrenSeq[A](values: JsArray[A]): ChildrenSeq[A] = {
      ChildrenSeq.from(values)
    }
  }

  implicit object sjsArrayRenderable extends RenderableSeq[js.Array] {
    override def toChildrenSeq[A](values: js.Array[A]): ChildrenSeq[A] = {
      ChildrenSeq.from(values)
    }
  }

  implicit object jsVectorRenderable extends RenderableSeq[JsVector] {
    override def toChildrenSeq[A](values: JsVector[A]): ChildrenSeq[A] = {
      ChildrenSeq.from(values)
    }
  }

  implicit object childrenSeqRenderable extends RenderableSeq[ChildrenSeq] {
    override def toChildrenSeq[A](values: ChildrenSeq[A]): ChildrenSeq[A] = {
      values
    }
  }

  // object optionRenderable extends RenderableSeq[Option] {
  //   override def toChildrenSeq[A](maybeValue: Option[A]): ChildrenSeq[A] = {
  //     ChildrenSeq.from(maybeValue.toList)
  //   }
  // }
  //
  // object jsUndefOrRenderable extends RenderableSeq[js.UndefOr] {
  //   override def toChildrenSeq[A](maybeValue: js.UndefOr[A]): ChildrenSeq[A] = {
  //     ChildrenSeq.from(JsArray.from(maybeValue))
  //   }
  // }

}
