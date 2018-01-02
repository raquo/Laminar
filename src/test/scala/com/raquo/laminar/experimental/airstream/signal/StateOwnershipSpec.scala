package com.raquo.laminar.experimental.airstream.signal

import com.raquo.laminar.experimental.airstream.fixtures.{Calculation, Effect, TestableOwner, TestableSignal}
import com.raquo.laminar.experimental.airstream.state.Var
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable

class StateOwnershipSpec extends FunSpec with Matchers {

//  it("Var that was killed does not respond to updates") {
//
//    val effects = mutable.Buffer[Effect[String]]()
//
//    val calculations = mutable.Buffer[Calculation[_]]()
//
//    def makeCalculation[I, O](name: String, project: I => O)(value: I): O = {
//      val newValue = project(value)
//      calculations += Calculation(name, newValue)
//      newValue
//    }
//
//    def makeObserver(name: String)(value: String): Unit = {
//      effects += Effect(name, value)
//    }
//
//    implicit val owner1: TestableOwner = new TestableOwner
//    val owner2 = new TestableOwner
//
//    val var1 = new Var("a") with TestableSignal[String] // implicit owner1
//    val map1 = var1.map(makeCalculation("map1", _ + "-x")) // implicit owner1
//    val map2 = var1.map(makeCalculation("map2", _ + "-y"))(mapSignalOwner = owner2)
//
//    var1.foreach(makeObserver("var1-1"))
//    var1.foreach(makeObserver("var1-2"))
//    map1.foreach(makeObserver("map1"))
//    map2.foreach(makeObserver("map2"))
//
//    // Initial values:
//
//    effects shouldEqual mutable.Buffer(
//      Effect("var1-1", "a"),
//      Effect("var1-2", "a"),
//      Effect("map1", "a-x"),
//      Effect("map2", "a-y")
//    )
//    effects.clear()
//
//    calculations shouldEqual mutable.Buffer(
//      Calculation("map1", "a-x"),
//      Calculation("map2", "a-y")
//    )
//    calculations.clear()
//
//    // Killing should
//    // - not trigger any calculations even after updating the value of Var
//    // - clear list of children
//    // - clear list of observers
//    owner1.killPossessions()
//
//    var1._testObservers.toSeq shouldEqual Nil
//    var1._testChildren shouldEqual Nil
//
//    var1.set("b")
//
//    var1.now() shouldEqual "a" // Stop beating the dead horse. It's not going anywhere!
//    effects shouldEqual mutable.Buffer()
//    calculations shouldEqual mutable.Buffer()
//  }

  // @TODO[Test] Verify other kinds of signals as well

//  it("MapSignal that was killed does not respond to updates") {
//
//  }
//
//  it("CombineSignal that was killed does not respond to updates") {
//
//  }

}
