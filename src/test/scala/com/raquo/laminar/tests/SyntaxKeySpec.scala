package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.modifiers.SimpleKeySetter
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec

class SyntaxKeySpec extends UnitSpec {

  // type F[V, El <: ReactiveElement.Base] = SimpleKeySetter.Of[V, ?, El]

  // def multiSetF[El <: ReactiveElement.Base](fs: F[_, El]*): Unit = {
  //   fs.foreach(println)
  // }

  def setS[El <: ReactiveElement.Base](setter: SimpleKeySetter[_, _, El]): Unit = {
    println(setter)
  }

  def multiSetS[El <: ReactiveElement.Base](setters: SimpleKeySetter[_, _, El]*): Unit = {
    setters.foreach(println)
  }

  it("xxx") {

    // multiSetF(
    //   href("foo"),
    //   checked(true),
    //   background("red"),
    //   display.none
    // )

    multiSetS(
      href("foo"),
      checked(true),
      background("red"),
      display.none
    )
  }
}
