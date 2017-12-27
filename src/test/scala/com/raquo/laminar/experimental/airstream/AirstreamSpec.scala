package com.raquo.laminar.experimental.airstream

import com.raquo.laminar.experimental.airstream
import org.scalajs.dom
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable
import scala.scalajs.js

class AirstreamSpec extends FunSpec with Matchers {

  ignore("simple var-map chain with two forks - propagates values") {

    implicit val context: Context = new Context

    val $v1 = new Var(1)
    val $m2 = $v1.map(_ + 1)
    val $m3 = $m2.map(_ + 10)
    val $m4 = $m3.map(_ + 100)

    val $m5 = $m3.map(_ + 1000)
    val $m6 = $m5.map(_ + 10000)

    val $m7 = $v1.map(_ + 100000)
    val $m8 = $m7.map(_ + 1000000)

    $v1.now() shouldBe 1
    $m2.now() shouldBe 2
    $m3.now() shouldBe 12
    $m4.now() shouldBe 112

    $m5.now() shouldBe 1012
    $m6.now() shouldBe 11012

    $m7.now() shouldBe 100001
    $m8.now() shouldBe 1100001

    $v1.set(2)

    $v1.now() shouldBe 2
    $m2.now() shouldBe 3
    $m3.now() shouldBe 13
    $m4.now() shouldBe 113

    $m5.now() shouldBe 1013
    $m6.now() shouldBe 11013

    $m7.now() shouldBe 100002
    $m8.now() shouldBe 1100002

    $v1.set(3)

    $v1.now() shouldBe 3
    $m2.now() shouldBe 4
    $m3.now() shouldBe 14
    $m4.now() shouldBe 114

    $m5.now() shouldBe 1014
    $m6.now() shouldBe 11014

    $m7.now() shouldBe 100003
    $m8.now() shouldBe 1100003
  }

  ignore("simple var-map chain with two forks - triggers observers") {

    implicit val context: Context = new Context

    case class Effect(name: String, value: Int)

    val effects = mutable.Buffer[Effect]()

    def makeObserver(name: String)(value: Int) = {
      effects += Effect(name, value)
    }

    // We define a simple chain of mapped signals with observers defined on some of them

    val $v1 = new Var(1)
    $v1.foreach(makeObserver("v1"))
    val $m2 = $v1.map(_ + 1)
    $m2.foreach(makeObserver("m2"))
    val $m3 = $m2.map(_ + 10)
    $m3.foreach(makeObserver("m3"), skipInitial = true)
    val $m4 = $m3.map(_ + 100)
    $m4.foreach(makeObserver("m4"))

    val $m5 = $m3.map(_ + 1000)
    $m5.foreach(makeObserver("m5-1"), skipInitial = true)
    $m5.foreach(makeObserver("m5-2"))
    $m5.foreach(makeObserver("m5-3"))
    val $m6 = $m5.map(_ + 10000)

    val $m7 = $v1.map(_ + 100000)
    val $m8 = $m7.map(_ + 1000000)
    $m8.foreach(makeObserver("m8"))
    $m6.foreach(makeObserver("m6"))

    // For initial values:
    // - Only observers with skipInitial=false (the default) should be triggered
    // - The order of observers matches the order in which they were defined, since there is no propagation going on
    //   - Notice that m8 is expected to fire before m6 this time
    // - Each observer is executed only once, and receives the signal's initial value
    effects shouldEqual mutable.Buffer(
      Effect("v1", 1),
      Effect("m2", 2),
      Effect("m4", 112),
      Effect("m5-2", 1012),
      Effect("m5-3", 1012),
      Effect("m8", 1100001),
      Effect("m6", 11012)
    )
    effects.clear()

    // For subsequent values:
    // - All observers now fire
    // - Order is topological (happens to be depth-first)
    $v1.set(2)

    effects shouldEqual mutable.Buffer(
      Effect("v1", 2),
      Effect("m2", 3),
      Effect("m3", 13),
      Effect("m4", 113),
      Effect("m5-1", 1013),
      Effect("m5-2", 1013),
      Effect("m5-3", 1013),
      Effect("m6", 11013),
      Effect("m8", 1100002)
    )
    effects.clear()

    // Adding another observer after the original observers were defined should work as expected
    $m7.foreach(makeObserver("m7"))

    effects shouldEqual mutable.Buffer(
      Effect("m7", 100002)
    )
    effects.clear()

    // Subsequent update should include the m7 observer effect that we added later on
    $v1.set(3)

    effects shouldEqual mutable.Buffer(
      Effect("v1", 3),
      Effect("m2", 4),
      Effect("m3", 14),
      Effect("m4", 114),
      Effect("m5-1", 1014),
      Effect("m5-2", 1014),
      Effect("m5-3", 1014),
      Effect("m6", 11014),
      Effect("m7", 100003),
      Effect("m8", 1100003)
    )
    effects.clear()
  }

  it("combine after vars - triggers observers") {

    implicit val context: Context = new Context

    case class Effect[V](name: String, value: V)

    val effects = mutable.Buffer[Effect[_]]()

    def makeObserver[V](name: String)(value: V) = {
      effects += Effect(name, value)
    }

    // We define two vars that combine into one signal

    val $v1 = new Var(1)
    val $v2 = new Var(10)
    val $m3 = $v2.map(_ + 10)
    val $c4 = Signal.combine($v1, $m3)
    val $m5 = $c4.map2(_ + _ + 1000)
    $m5.foreach(makeObserver[Int]("m5"))
    $c4.foreach(makeObserver[(Int, Int)]("c4"))
    $m3.foreach(makeObserver[Int]("m3"))
    $v2.foreach(makeObserver[Int]("v2"))
    $v1.foreach(makeObserver[Int]("v1"))

    effects shouldEqual mutable.Buffer(
      Effect("m5", 1021),
      Effect("c4", (1, 20)),
      Effect("m3", 20),
      Effect("v2", 10),
      Effect("v1", 1)
    )
    effects.clear()

    // Updating v1 and v2 should only update their respective parts of the chain

    $v1.set(2)

    effects shouldEqual mutable.Buffer(
      Effect("v1", 2),
      Effect("c4", (2, 20)),
      Effect("m5", 1022)
    )
    effects.clear()

    $v2.set(20)

    effects shouldEqual mutable.Buffer(
      Effect("v2", 20),
      Effect("m3", 30),
      Effect("c4", (2, 30)),
      Effect("m5", 1032)
    )
    effects.clear()

    airstream.batchUpdate(
      Assignment($v1, 3),
      Assignment($v2, 30)
    )

    // Batch update should be treated like a single propagation (e.g. c4 updated only once)
    // @TODO[Test] Actually, all of the signals are also guaranteed to have executed exactly once, we should test for that too.
    // @TODO[Test] Can we maybe write some kind of test helper to verify all this?

    effects shouldEqual mutable.Buffer(
      Effect("v1", 3),
      Effect("v2", 30),
      Effect("m3", 40),
      Effect("c4", (3, 40)),
      Effect("m5", 1043)
    )
    effects.clear()
  }

  // TODO[Test] Review if we need this test
  it("diamond case") {

    implicit val context: Context = new Context

    val $int1 = new Var(1)
    val $int2 = new Var(100)

    val $int11 = $int1.map(_ + 1)
    val $int22 = $int2.map(_ + 50)
    val $int222 = $int22.map(_ + 50)

    val $int3 = Signal.combine($int11, $int222)
    val $int33 = $int3.map2(_ + _)

    $int1.now() shouldBe 1
    $int2.now() shouldBe 100

    $int11.now() shouldBe 2
    $int22.now() shouldBe 150
    $int222.now() shouldBe 200

    $int3.now() shouldEqual (2, 200)
    $int33.now() shouldBe 202

    $int1.set(2)

    $int1.now() shouldBe 2
    $int2.now() shouldBe 100

    $int11.now() shouldBe 3
    $int22.now() shouldBe 150
    $int222.now() shouldBe 200

    $int3.now() shouldEqual (3, 200)
    $int33.now() shouldBe 203

  }

}
