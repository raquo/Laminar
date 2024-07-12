package com.raquo.laminar.tests

import com.raquo.ew.JsArray
import com.raquo.laminar.api.L._
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.collection.mutable

class SplitVarSpec extends UnitSpec {

  // This tests Airstream Var splitting.
  // Ideally those tests belong in Airstream, but mocking Laminar
  // lifecycle behaviour there is needlessly complicated.

  case class Foo(id: Int, name: String, version: Int)

  it("standard child Var behaviour") {

    val effects: mutable.Buffer[String] = mutable.Buffer()

    val fooVars: mutable.Map[Int, Var[Foo]] = mutable.Map()

    val foosVar = Var[List[Foo]](Nil)

    def clickOnItem(id: Int): Unit = {
      dom.document.getElementById(s"item-${id}").asInstanceOf[dom.html.Element].click()
    }

    mount(div(
      children <-- foosVar.split(_.id)((id, initial, fooVar) => {
        fooVar.setDisplayName(s"fooVar-${id}")
        effects += s"create-${initial.toString}"
        fooVars.update(id, fooVar)
        div(
          idAttr("item-" + id.toString),
          fooVar.signal --> { f => effects += s"update-${f.toString}" },
          text <-- fooVar.signal.map(f => f.name + "." + f.version),
          onClick --> fooVar.update(f => f.copy(version = f.version + 1))
        )
      })
    ))

    expectNode(div of (
      sentinel
    ))

    assertEquals(effects.toList, Nil)

    // --

    foosVar.update(_ :+ Foo(1, "One", 1))

    expectNode(div of (
      sentinel,
      div of (
        idAttr is s"item-1",
        "One.1"
      )
    ))

    assertEquals(effects.toList, List(
      "create-Foo(1,One,1)",
      "update-Foo(1,One,1)",
    ))
    effects.clear()

    // --

    clickOnItem(1)

    expectNode(div of (
      sentinel,
      div of (
        idAttr is s"item-1",
        "One.2"
      )
    ))

    assertEquals(effects.toList, List(
      "update-Foo(1,One,2)",
    ))
    effects.clear()

    // --

    foosVar.update(_.appended(Foo(2, "Two", 1)).appended(Foo(3, "Three", 1)))

    expectNode(div of (
      sentinel,
      div of (
        idAttr is s"item-1",
        "One.2"
      ),
      div of (
        idAttr is s"item-2",
        "Two.1"
      ),
      div of (
        idAttr is s"item-3",
        "Three.1"
      )
    ))

    assertEquals(effects.toList, List(
      "create-Foo(2,Two,1)",
      "create-Foo(3,Three,1)",
      "update-Foo(2,Two,1)",
      "update-Foo(3,Three,1)",
    ))
    effects.clear()

    assertEquals(fooVars(1).now(), Foo(1, "One", 2))
    assertEquals(fooVars(2).now(), Foo(2, "Two", 1))
    assertEquals(fooVars(3).now(), Foo(3, "Three", 1))

    assertEquals(effects.toList, Nil)

    // --

    clickOnItem(2)
    clickOnItem(2)
    clickOnItem(3)
    clickOnItem(2)
    clickOnItem(3)

    expectNode(div of (
      sentinel,
      div of (
        idAttr is s"item-1",
        "One.2"
      ),
      div of (
        idAttr is s"item-2",
        "Two.4"
      ),
      div of (
        idAttr is s"item-3",
        "Three.3"
      )
    ))

    assertEquals(effects.toList, List(
      "update-Foo(2,Two,2)",
      "update-Foo(2,Two,3)",
      "update-Foo(3,Three,2)",
      "update-Foo(2,Two,4)",
      "update-Foo(3,Three,3)",
    ))
    effects.clear()

    // --

    foosVar.update(_.sortBy(_.version))

    expectNode(div of (
      sentinel,
      div of (
        idAttr is s"item-1",
        "One.2"
      ),
      div of (
        idAttr is s"item-3",
        "Three.3"
      ),
      div of (
        idAttr is s"item-2",
        "Two.4"
      )
    ))

    assertEquals(effects.toList, Nil)

    // --

    foosVar.update(_.reverse)

    expectNode(div of (
      sentinel,
      div of (
        idAttr is s"item-2",
        "Two.4"
      ),
      div of (
        idAttr is s"item-3",
        "Three.3"
      ),
      div of (
        idAttr is s"item-1",
        "One.2"
      ),
    ))

    assertEquals(effects.toList, Nil)

    assertEquals(fooVars(1).now(), Foo(1, "One", 2))
    assertEquals(fooVars(2).now(), Foo(2, "Two", 4))
    assertEquals(fooVars(3).now(), Foo(3, "Three", 3))

    assertEquals(effects.toList, Nil)

    // --

    foosVar.set(foosVar.now().reverse.updated(0, Foo(1, "One", 1)))

    expectNode(div of (
      sentinel,
      div of (
        idAttr is s"item-1",
        "One.1"
      ),
      div of (
        idAttr is s"item-3",
        "Three.3"
      ),
      div of (
        idAttr is s"item-2",
        "Two.4"
      ),
    ))

    assertEquals(effects.toList, List(
      "update-Foo(1,One,1)"
    ))
    effects.clear()

  }

  it("unused child var behaviour") {

    // Confirm expected var behaviour in case when the callback
    // does not create subscribers on child var's signal

    val effects: mutable.Buffer[String] = mutable.Buffer()

    val fooVars: mutable.Map[Int, Var[Foo]] = mutable.Map()

    val foosVar = Var[List[Foo]](Nil).setDisplayName("foosVar")

    mount(div(
      children <-- foosVar.split(_.id)((id, initial, fooVar) => {
        fooVar.setDisplayName(s"fooVar-${id}")
        effects += s"create-${initial.toString}"
        fooVars.update(id, fooVar)
        div(
          idAttr("item-" + id.toString),
        )
      })
    ))

    expectNode(div of (
      sentinel
    ))

    assertEquals(effects.toList, Nil)

    // --

    foosVar.set(Foo(1, "One", 1) :: Foo(2, "Two", 1) :: Foo(3, "Three", 1) :: Nil)

    expectNode(div of (
      sentinel,
      div of (idAttr is s"item-1"),
      div of (idAttr is s"item-2"),
      div of (idAttr is s"item-3"),
    ))

    assertEquals(effects.toList, List(
      "create-Foo(1,One,1)",
      "create-Foo(2,Two,1)",
      "create-Foo(3,Three,1)",
    ))
    effects.clear()

    // --

    fooVars(1).update(f => f.copy(version = f.version + 1))

    assertEquals(effects.toList, Nil)

    assertEquals(fooVars(1).now(), Foo(1, "One", 2))

    // --

    fooVars(1).set(Foo(1, "Onne", 3))

    assertEquals(effects.toList, Nil)

    assertEquals(fooVars(1).now(), Foo(1, "Onne", 3))
    assertEquals(fooVars(2).now(), Foo(2, "Two", 1))
    assertEquals(fooVars(3).now(), Foo(3, "Three", 1))

  }

  it("standard mutable collection Var") {

    val effects: mutable.Buffer[String] = mutable.Buffer()

    val fooVars: mutable.Map[Int, Var[Foo]] = mutable.Map()

    val foosVar = Var[JsArray[Foo]](JsArray())

    def clickOnItem(id: Int): Unit = {
      dom.document.getElementById(s"item-${id}").asInstanceOf[dom.html.Element].click()
    }

    mount(div(
      children <-- foosVar.split(_.id)((id, initial, fooVar) => {
        fooVar.setDisplayName(s"fooVar-${id}")
        effects += s"create-${initial.toString}"
        fooVars.update(id, fooVar)
        div(
          idAttr("item-" + id.toString),
          fooVar.signal --> { f => effects += s"update-${f.toString}" },
          text <-- fooVar.signal.map(f => f.name + "." + f.version),
          onClick --> fooVar.update(f => f.copy(version = f.version + 1))
        )
      })
    ))

    expectNode(div of (
      sentinel
      ))

    assertEquals(effects.toList, Nil)

    // --

    foosVar.set(JsArray(Foo(1, "One", 1), Foo(2, "Two", 1)))

    expectNode(div of (
      sentinel,
      div of (
        idAttr is s"item-1",
        "One.1"
      ),
      div of (
        idAttr is s"item-2",
        "Two.1"
      )
    ))

    assertEquals(effects.toList, List(
      "create-Foo(1,One,1)",
      "create-Foo(2,Two,1)",
      "update-Foo(1,One,1)",
      "update-Foo(2,Two,1)",
    ))
    effects.clear()

    // --

    val arrRef = foosVar.now()

    clickOnItem(1)

    expectNode(div of (
      sentinel,
      div of (
        idAttr is s"item-1",
        "One.2"
      ),
      div of (
        idAttr is s"item-2",
        "Two.1"
      )
    ))

    assertEquals(effects.toList, List(
      "update-Foo(1,One,2)",
    ))
    effects.clear()

    // Assert that new array was created, instead of being mutated in-place
    assert(arrRef ne foosVar.now())
  }

  it("splitMutate: in-place updates of mutable collection Var") {

    val effects: mutable.Buffer[String] = mutable.Buffer()

    val fooVars: mutable.Map[Int, Var[Foo]] = mutable.Map()

    val foosVar = Var[JsArray[Foo]](JsArray())

    def clickOnItem(id: Int): Unit = {
      dom.document.getElementById(s"item-${id}").asInstanceOf[dom.html.Element].click()
    }

    mount(div(
      children <-- foosVar.splitMutate(_.id)((id, initial, fooVar) => {
        fooVar.setDisplayName(s"fooVar-${id}")
        effects += s"create-${initial.toString}"
        fooVars.update(id, fooVar)
        div(
          idAttr("item-" + id.toString),
          fooVar.signal --> { f => effects += s"update-${f.toString}" },
          text <-- fooVar.signal.map(f => f.name + "." + f.version),
          onClick --> fooVar.update(f => f.copy(version = f.version + 1))
        )
      }).tapEach(_ => effects += "result-event")
    ))

    expectNode(div of (
      sentinel
    ))

    assertEquals(effects.toList, List(
      "result-event"
    ))
    effects.clear()

    // --

    foosVar.set(JsArray(Foo(1, "One", 1), Foo(2, "Two", 1)))

    expectNode(div of (
      sentinel,
      div of (
        idAttr is s"item-1",
        "One.1"
      ),
      div of (
        idAttr is s"item-2",
        "Two.1"
      )
    ))

    assertEquals(effects.toList, List(
      "create-Foo(1,One,1)",
      "create-Foo(2,Two,1)",
      "result-event",
      "update-Foo(1,One,1)",
      "update-Foo(2,Two,1)",
    ))
    effects.clear()

    // --

    val arrRef = foosVar.now()

    clickOnItem(1)

    expectNode(div of (
      sentinel,
      div of (
        idAttr is s"item-1",
        "One.2"
      ),
      div of (
        idAttr is s"item-2",
        "Two.1"
      )
    ))

    assertEquals(effects.toList, List(
      "result-event",
      "update-Foo(1,One,2)",
    ))
    effects.clear()

    // Assert that the array was mutated in-place, instead of a copy being created
    assert(arrRef eq foosVar.now())
  }

}
