package com.raquo.laminar.modifiers

import com.raquo.ew.{JsArray, JsVector}
import com.raquo.laminar

import scala.scalajs.js

// #TODO[Naming] - LSeq + RenderableSeq?   RenderableSeq + IsRenderableSeq? // #nc
trait RenderableSeq[-Collection[_]] {

  def toSeq[A](values: Collection[A]): laminar.Seq[A]

  def foreach[A](values: Collection[A])(f: A => Unit): Unit
}

object RenderableSeq {

  implicit object collectionSeqRenderable extends RenderableSeq[collection.Seq] {
    override def toSeq[A](values: collection.Seq[A]): laminar.Seq[A] = {
      laminar.Seq.from(values)
    }

    override def foreach[A](values: collection.Seq[A])(f: A => Unit): Unit = {
      values.foreach(f)
    }
  }

  implicit object scalaArrayRenderable extends RenderableSeq[scala.Array] {
    override def toSeq[A](values: scala.Array[A]): laminar.Seq[A] = {
      laminar.Seq.from(values)
    }

    override def foreach[A](values: Array[A])(f: A => Unit): Unit = {
      values.foreach(f)
    }
  }

  implicit object jsArrayRenderable extends RenderableSeq[JsArray] {
    override def toSeq[A](values: JsArray[A]): laminar.Seq[A] = {
      laminar.Seq.from(values)
    }

    override def foreach[A](values: JsArray[A])(f: A => Unit): Unit = {
      values.forEach(f)
    }
  }

  implicit object sjsArrayRenderable extends RenderableSeq[js.Array] {
    override def toSeq[A](values: js.Array[A]): laminar.Seq[A] = {
      laminar.Seq.from(values)
    }

    override def foreach[A](values: js.Array[A])(f: A => Unit): Unit = {
      values.foreach(f)
    }
  }

  implicit object jsVectorRenderable extends RenderableSeq[JsVector] {
    override def toSeq[A](values: JsVector[A]): laminar.Seq[A] = {
      laminar.Seq.from(values)
    }

    override def foreach[A](values: JsVector[A])(f: A => Unit): Unit = {
      values.forEach(f)
    }
  }

  implicit object laminarSeqRenderable extends RenderableSeq[laminar.Seq] {
    override def toSeq[A](values: laminar.Seq[A]): laminar.Seq[A] = {
      values
    }

    override def foreach[A](values: laminar.Seq[A])(f: A => Unit): Unit = {
      values.foreach(f)
    }
  }

  implicit object optionRenderable extends RenderableSeq[Option] {
    override def toSeq[A](maybeValue: Option[A]): laminar.Seq[A] = {
      laminar.Seq.from(maybeValue.toList)
    }

    override def foreach[A](values: Option[A])(f: A => Unit): Unit = {
      values.foreach(f)
    }
  }

  implicit object jsUndefOrRenderable extends RenderableSeq[js.UndefOr] {
    override def toSeq[A](maybeValue: js.UndefOr[A]): laminar.Seq[A] = {
      laminar.Seq.from(JsArray.from(maybeValue))
    }

    override def foreach[A](values: js.UndefOr[A])(f: A => Unit): Unit = {
      values.foreach(f)
    }
  }

}
