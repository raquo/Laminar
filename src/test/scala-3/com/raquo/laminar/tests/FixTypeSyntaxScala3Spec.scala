package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

import scala.collection.mutable

class FixTypeSyntaxScala3Spec extends UnitSpec {

  // The whole point of `.fixType` is to let IntelliJ infer the element types of a
  // tuple-unpacking lambda `{ (a, b) => ... }` on the right hand side of `-->`.
  // That untupling syntax is Scala 3 only, so this test lives in the scala-3 dir.

  it("fixType lets a multi-arg lambda unpack a tuple source") {

    val received = mutable.Buffer[String]()

    val stream2: EventStream[(Int, String)] = EventStream.fromValue((1, "a"))
    val signal3: Signal[(Int, String, Boolean)] = Signal.fromValue((2, "b", true))

    val el = div(
      stream2.fixType --> { (n, s) =>
        val nInt: Int = n
        val sString: String = s
        received += s"stream2:$nInt:$sString"
      },
      signal3.fixType --> { (n, s, b) =>
        val nInt: Int = n
        val sString: String = s
        val bBool: Boolean = b
        received += s"signal3:$nInt:$sString:$bBool"
      }
    )

    mount(el)

    received.toList shouldBe List("signal3:2:b:true", "stream2:1:a")
  }

  it("fixType lets a multi-arg lambda unpack a tuple event processor") {

    val received = mutable.Buffer[String]()

    val el = div(
      onClick.mapTo((1, "a")).fixType --> { (n, s) =>
        val nInt: Int = n
        val sString: String = s
        received += s"click2:$nInt:$sString"
      },
      onClick.mapTo((2, "b", true)).fixType --> { (n, s, b) =>
        val nInt: Int = n
        val sString: String = s
        val bBool: Boolean = b
        received += s"click3:$nInt:$sString:$bBool"
      }
    )

    mount(el)

    el.ref.click()

    received.toList shouldBe List("click2:1:a", "click3:2:b:true")
  }
}
