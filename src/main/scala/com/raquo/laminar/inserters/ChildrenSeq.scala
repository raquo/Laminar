package com.raquo.laminar.inserters

import com.raquo.ew.{JsArray, JsVector, ewArray}

import scala.scalajs.js

/**
  * `ChildrenSeq[A]` is what Laminar needs to render sequences of things.
  * It is less powerful than real collection types, and that lets us
  * abstract away their differences, such as the lack of Scala semantics
  * in JsVector, or the need for ClassTag when mapping a scala.Array.
  *
  * The purpose of this class is to allow users to provide arbitrary
  * collection types to Laminar methods like `children <--`, including
  * types that have better performance than native Scala collections,
  * such as JsVector, or even a mutable JsArray.
  *
  * As a Laminar user, you shouldn't need to instantiate this class
  * yourself, Laminar takes care of that behind the scenes.
  *
  * If you have a custom collection type that you want Laminar to
  * understand, provide an implicit instance of
  * [[com.raquo.laminar.modifiers.RenderableSeq]] for it.
  *
  * Note: Internally, only one of `seq` / `scalaArray` / `jsVector`
  *       members contains a value, all the other ones are null.
  * Note: Mapping over ChildrenSeq may internally translate the source
  *       collection to another collection type (see comments below).
  */
class ChildrenSeq[+A] private (
  seq: collection.Seq[A], // nullable
  scalaArray: scala.Array[A], // nullable
  jsArray: JsArray[A] // nullable
) {

  def map[B](project: A => B): ChildrenSeq[B] = {
    // #TODO[Performance] May want to check this, if we don't get rid of this `map` method first.
    // I'm pretty sure that native JS arrays are faster,
    // so we convert to JsArray instead of creating a new
    // Scala collection instance.
    // Also, mapping over an array requires a ClassTag,
    // and we don't want to require that.
    if (seq ne null) {
      val jsArr = JsArray[B]()
      seq.foreach(v => jsArr.push(project(v)))
      ChildrenSeq.from(jsArr)
    } else if (jsArray ne null) {
      ChildrenSeq.from(jsArray.map(project))
    } else {
      val jsArr = JsArray[B]()
      scalaArray.foreach(v => jsArr.push(project(v)))
      ChildrenSeq.from(jsArr)
    }
  }

  def foreach(f: A => Unit): Unit = {
    if (seq ne null) {
      seq.foreach(f)
    } else if (jsArray ne null) {
      jsArray.forEach(f)
    } else {
      scalaArray.foreach(f)
    }
  }
}

object ChildrenSeq {

  private val _empty: ChildrenSeq[Nothing] = from(Nil)

  @inline def empty[A]: ChildrenSeq[A] = _empty

  def from[A](seq: collection.Seq[A]): ChildrenSeq[A] = {
    new ChildrenSeq(seq, null, null)
  }

  def from[A](array: scala.Array[A]): ChildrenSeq[A] = {
    new ChildrenSeq(null, array, null)
  }

  def from[A](jsArray: JsArray[A]): ChildrenSeq[A] = {
    new ChildrenSeq(null, null, jsArray)
  }

  def from[A](sjsArray: js.Array[A]): ChildrenSeq[A] = {
    new ChildrenSeq(null, null, sjsArray.ew)
  }

  def from[A](jsVector: JsVector[A]): ChildrenSeq[A] = {
    new ChildrenSeq(null, null, jsVector.unsafeAsScalaJs.ew)
  }
}
