package com.raquo.laminar

import com.raquo.ew.{ewArray, JsArray, JsVector}

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
  jsArray: JsArray[A] // nullable
) {

  def map[B](project: A => B): Seq[B] = {
    // #TODO[Performance] May want to check this, if we don't get rid of this `map` method first.
    // I'm pretty sure that native JS arrays are faster,
    // so we convert to JsArray instead of creating a new
    // Scala collection instance.
    // Also, mapping over an array requires a ClassTag,
    // and we don't want to require that.
    if (seq ne null) {
      val jsArr = JsArray[B]()
      seq.foreach(v => jsArr.push(project(v)))
      Seq.from(jsArr)
    } else if (jsArray ne null) {
      Seq.from(jsArray.map(project))
    } else {
      val jsArr = JsArray[B]()
      scalaArray.foreach(v => jsArr.push(project(v)))
      Seq.from(jsArr)
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

object Seq {

  private val _empty: Seq[Nothing] = from(Nil)

  @inline def empty[A]: Seq[A] = _empty

  def from[A](seq: collection.Seq[A]): Seq[A] = {
    new Seq(seq, null, null)
  }

  def from[A](array: scala.Array[A]): Seq[A] = {
    new Seq(null, array, null)
  }

  def from[A](jsArray: JsArray[A]): Seq[A] = {
    new Seq(null, null, jsArray)
  }

  def from[A](sjsArray: js.Array[A]): Seq[A] = {
    new Seq(null, null, sjsArray.ew)
  }

  def from[A](jsVector: JsVector[A]): Seq[A] = {
    new Seq(null, null, jsVector.unsafeAsScalaJs.ew)
  }
}
