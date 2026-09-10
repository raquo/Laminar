package com.raquo.laminar

import com.raquo.ew.{ewArray, JsArray, JsVector}

import scala.collection.immutable
import scala.scalajs.js

/**
  * `laminar.Seq[A]` is what Laminar needs to render sequences of things.
  * It is less powerful than real collection types, and that lets us
  * abstract away their differences, such as the lack of Scala semantics
  * in JsVector, or the need for ClassTag when mapping a scala.Array.
  *
  * The purpose of this class is to allow users to provide arbitrary
  * collection types to Laminar methods like `children <--`, including
  * types that have better performance than native Scala collections,
  * such as JsVector, or even a mutable js.Array.
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
class Seq[+A] private (
  seq: collection.Seq[A], // nullable
  scalaArray: scala.Array[A], // nullable
  jsArray: JsArray[A], // nullable
  val isMutable: Boolean // depends on the backing collection
) {

  def isEmpty: Boolean =
    if (seq ne null) {
      seq.isEmpty
    } else if (jsArray ne null) {
      jsArray.length == 0
    } else {
      scalaArray.isEmpty
    }

  def nonEmpty: Boolean = !isEmpty

  def head: A =
    if (seq ne null) {
      seq.head
    } else if (jsArray ne null) {
      (jsArray(0): js.UndefOr[A]).getOrElse(throw new NoSuchElementException("head of empty JsArray"))
    } else {
      scalaArray.head
    }

  def last: A =
    if (seq ne null) {
      seq.last
    } else if (jsArray ne null) {
      (jsArray(jsArray.length - 1): js.UndefOr[A]).getOrElse(throw new NoSuchElementException("last of empty JsArray"))
    } else {
      scalaArray.last
    }

  def map[B](project: A => B): Seq[B] = {
    // #TODO[Performance] May want to check this, if we don't get rid of this `map` method first.
    // I'm pretty sure that native JS arrays are faster,
    // so we convert to JsArray instead of creating a new
    // Scala collection instance.
    // Also, mapping over an array requires a ClassTag,
    // and we don't want to require that.
    // #Note: the resulting JsArray is freshly allocated and owned by the new Seq, so
    //  it is not externally mutable – hence hasMutableBacking = false.
    if (seq ne null) {
      val jsArr = JsArray[B]()
      seq.foreach(v => jsArr.push(project(v)))
      new Seq(null, null, jsArr, isMutable = false)
    } else if (jsArray ne null) {
      new Seq(null, null, jsArray.map(project), isMutable = false)
    } else {
      val jsArr = JsArray[B]()
      scalaArray.foreach(v => jsArr.push(project(v)))
      new Seq(null, null, jsArr, isMutable = false)
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

  /** Take an immutable snapshot of this Seq.
    *
    * If [[isMutable]] is true, this creates a new [[JsArray]] and a new [[laminar.Seq]].
    */
  private[laminar] def immutableSnapshot: Seq[A] = {
    if (isMutable) {
      // #TODO[Perf] review if there are any common cases when this copying would degrade performance
      //  - e.g. we normally use JsArray for performance – but it's mutable...
      //  - if this is a problem, consider implementing a custom class wrapping JsArray that copies
      //    the original array only if it's later edited (need to intercept calls to editing APIs)
      // The array copy is private and never mutated, so `isMutable = false` is safe here.
      val arrayCopy = JsArray[A]()
      foreach(arrayCopy.push(_))
      new Seq(null, null, arrayCopy, isMutable = false)
    } else {
      this
    }
  }
}

object Seq {

  private val _empty: Seq[Nothing] = from(Nil)

  @inline def empty[A]: Seq[A] = _empty

  def from[A](seq: collection.Seq[A]): Seq[A] = {
    new Seq(seq, null, null, isMutable = !seq.isInstanceOf[immutable.Seq[?]])
  }

  def from[A](array: scala.Array[A]): Seq[A] = {
    new Seq(null, array, null, isMutable = true)
  }

  def from[A](jsArray: JsArray[A]): Seq[A] = {
    new Seq(null, null, jsArray, isMutable = true)
  }

  def from[A](sjsArray: js.Array[A]): Seq[A] = {
    new Seq(null, null, sjsArray.ew, isMutable = true)
  }

  def from[A](jsVector: JsVector[A]): Seq[A] = {
    new Seq(null, null, jsVector.unsafeAsScalaJs.ew, isMutable = false)
  }
}
