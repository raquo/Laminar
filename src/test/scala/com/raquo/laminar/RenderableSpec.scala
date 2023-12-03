package com.raquo.laminar

import com.raquo.ew
import com.raquo.laminar.api.L._
import com.raquo.laminar.modifiers.{RenderableNode, RenderableText}
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec

import scala.collection.mutable
import scala.scalajs.js

class RenderableSpec extends UnitSpec {

  object TextNodeImplicits extends BaseTrait {
    implicit val intRenderableX: RenderableText[Int] = RenderableText("%04d".format(_))
  }

  trait BaseTrait {
    implicit val boolRenderable: RenderableText[Boolean] = RenderableText(_.toString.toUpperCase())
  }

  it("Custom RenderableText implicits") {

    import TextNodeImplicits._

    val el = div(
      1,
      true,
      2.0,
      child.text <-- Val(3),
      child.text <-- Val(4.0),
      child.text <-- Val(false)
    )

    mount(el)

    expectNode(
      div of(
        "0001", // Int – using custom implicit val
        "TRUE", // Boolean – using custom implicit val
        "2", // Double – using built-in implicit val
        "0003", // Int – using custom implicit val
        "4", // Double – using built-in implicit val
        "FALSE" // Boolean – using custom implicit val
      )
    )
  }

  trait Component {
    val node: ReactiveElement.Base
  }

  trait InputComponent extends Component {
    override val node: Input
  }

  class TextInputComponent(mods: Modifier[Input]*) extends InputComponent {
    override val node: Input = input(
      cls("Input"),
      typ("text"),
      mods
    )
  }

  implicit val componentRenderable: RenderableNode[Component] =
    RenderableNode(_.node, _.map(_.node), _.map(_.node), _.map(_.node))


  it("Component rendering") {

    def component(id: String) = new TextInputComponent(idAttr(id))

    def componentOption(idSuffix: String = "") = Option(component("Option" + idSuffix))

    def componentsList(idSuffix: String = "") = List(component("List" + idSuffix))

    def componentsVector(idSuffix: String = "") = Vector(component("Vector" + idSuffix))

    def componentsBuffer(idSuffix: String = "") = mutable.Buffer(component("Buffer" + idSuffix))

    def componentsScalaArray(idSuffix: String = "") = scala.Array(component("ScalaArray" + idSuffix))

    def componentsJsArray(idSuffix: String = "") = ew.JsArray(component("JsArray" + idSuffix))

    def componentsSjsArray(idSuffix: String = "") = js.Array(component("SjsArray" + idSuffix))

    object forElement {

      val componentSignal = Val(component("Solo.child"))

      val componentOptionSignal = Val(componentOption(".child.maybe"))

      val componentListSignal = Val(componentsList(".children"))

      val componentVectorSignal = Val(componentsVector(".children"))
    }

    object forInsert {

      val componentSignal = Val(component("Solo.child.onMountInsert"))

      val componentOptionSignal = Val(componentOption(".child.maybe.onMountInsert"))

      val componentListSignal = Val(componentsList(".children.onMountInsert"))

      val componentVectorSignal = Val(componentsVector(".children.onMountInsert"))
    }

    // --

    val el1 = div(
      component("Solo"),
      componentOption(),
      componentsList(),
      componentsVector(),
      componentsBuffer(),
      componentsScalaArray(),
      componentsJsArray(),
      componentsSjsArray()
    )

    mount(el1, "Static values")

    expectNode(
      div of(
        input of (idAttr is "Solo"),
        input of (idAttr is "Option"),
        input of (idAttr is "List"),
        input of (idAttr is "Vector"),
        input of (idAttr is "Buffer"),
        input of (idAttr is "ScalaArray"),
        input of (idAttr is "JsArray"),
        input of (idAttr is "SjsArray")
      )
    )

    // Ensure that we don't accidentally use dynamic inserters
    // We expect 8 subscriptions - one pilot sub for each of child elements
    assertEquals(ReactiveElement.numDynamicSubscriptions(el1), 8)

    unmount()

    // --

    val el2 = div(
      child <-- forElement.componentSignal,
      child.maybe <-- forElement.componentOptionSignal,
      children <-- forElement.componentListSignal,
      children <-- forElement.componentVectorSignal
    )

    mount(el2, "Dynamic children")

    expectNode(
      div of(
        sentinel,
        input of (idAttr is "Solo.child"),
        sentinel,
        input of (idAttr is "Option.child.maybe"),
        sentinel,
        input of (idAttr is "List.children"),
        sentinel,
        input of (idAttr is "Vector.children")
      )
    )

    // Expect two subscriptions per child – one pilot sub and one for the inserter
    assertEquals(ReactiveElement.numDynamicSubscriptions(el2), 8)

    unmount()

    // --

    val el3 = div(
      onMountInsert(_ => component("Solo.onMountInsert")),
      onMountInsert(_ => child <-- forInsert.componentSignal),
      onMountInsert(_ => child.maybe <-- forInsert.componentOptionSignal),
      onMountInsert(_ => children <-- forInsert.componentListSignal),
      onMountInsert(_ => children <-- forInsert.componentVectorSignal)
    )

    mount(el3)

    expectNode(
      div of(
        sentinel,
        input of (idAttr is "Solo.onMountInsert"),
        sentinel,
        input of (idAttr is "Solo.child.onMountInsert"),
        sentinel,
        input of (idAttr is "Option.child.maybe.onMountInsert"),
        sentinel,
        input of (idAttr is "List.children.onMountInsert"),
        sentinel,
        input of (idAttr is "Vector.children.onMountInsert")
      )
    )

    // Expect three subscriptions per child:
    // - 1 for onMountInsert
    // - 1 for inserter inside onMountInsert
    // - 1 pilot subscription for child element
    assertEquals(ReactiveElement.numDynamicSubscriptions(el3), 14)
  }

  it("Regular node rendering") {

    def node(id: String) = input(idAttr(id))

    def nodeOption(idSuffix: String = "") = Option(node("Option" + idSuffix))

    def nodeList(idSuffix: String = "") = List(node("List" + idSuffix))

    def nodeVector(idSuffix: String = "") = Vector(node("Vector" + idSuffix))

    def nodeBuffer(idSuffix: String = "") = mutable.Buffer(node("Buffer" + idSuffix))

    def nodeScalaArray(idSuffix: String = "") = scala.Array(node("ScalaArray" + idSuffix))

    def nodeJsArray(idSuffix: String = "") = ew.JsArray(node("JsArray" + idSuffix))

    def nodeSjsArray(idSuffix: String = "") = js.Array(node("SjsArray" + idSuffix))

    object forElement {

      val nodeSignal = Val(node("Solo.child"))

      val nodeOptionSignal = Val(nodeOption(".child.maybe"))

      val nodeListSignal = Val(nodeList(".children"))

      val nodeVectorSignal = Val(nodeVector(".children"))
    }

    object forInsert {

      val nodeSignal = Val(node("Solo.child.onMountInsert"))

      val nodeOptionSignal = Val(nodeOption(".child.maybe.onMountInsert"))

      val nodeListSignal = Val(nodeList(".children.onMountInsert"))

      val nodeVectorSignal = Val(nodeVector(".children.onMountInsert"))
    }

    // --

    val el1 = div(
      node("Solo"),
      nodeOption(),
      nodeList(),
      nodeVector(),
      nodeBuffer(),
      nodeScalaArray(),
      nodeJsArray(),
      nodeSjsArray()
    )

    mount(el1, "Static values")

    expectNode(
      div of(
        input of (idAttr is "Solo"),
        input of (idAttr is "Option"),
        input of (idAttr is "List"),
        input of (idAttr is "Vector"),
        input of (idAttr is "Buffer"),
        input of (idAttr is "ScalaArray"),
        input of (idAttr is "JsArray"),
        input of (idAttr is "SjsArray")
      )
    )

    // Ensure that we don't accidentally use dynamic inserters
    // We expect 8 subscriptions - one pilot sub for each of child elements
    assertEquals(ReactiveElement.numDynamicSubscriptions(el1), 8)

    unmount()

    // --

    val el2 = div(
      child <-- forElement.nodeSignal,
      child.maybe <-- forElement.nodeOptionSignal,
      children <-- forElement.nodeListSignal,
      children <-- forElement.nodeVectorSignal
    )

    mount(el2, "Dynamic children")

    expectNode(
      div of (
        sentinel,
        input of (idAttr is "Solo.child"),
        sentinel,
        input of (idAttr is "Option.child.maybe"),
        sentinel,
        input of (idAttr is "List.children"),
        sentinel,
        input of (idAttr is "Vector.children")
      )
    )

    // Expect two subscriptions per child – one pilot sub and one for the inserter
    assertEquals(ReactiveElement.numDynamicSubscriptions(el2), 8)

    unmount()

    // --

    val el3 = div(
      onMountInsert(_ => node("Solo.onMountInsert")),
      onMountInsert(_ => child <-- forInsert.nodeSignal),
      onMountInsert(_ => child.maybe <-- forInsert.nodeOptionSignal),
      onMountInsert(_ => children <-- forInsert.nodeListSignal),
      onMountInsert(_ => children <-- forInsert.nodeVectorSignal)
    )

    mount(el3)

    expectNode(
      div of (
        sentinel,
        input of (idAttr is "Solo.onMountInsert"),
        sentinel,
        input of (idAttr is "Solo.child.onMountInsert"),
        sentinel,
        input of (idAttr is "Option.child.maybe.onMountInsert"),
        sentinel,
        input of (idAttr is "List.children.onMountInsert"),
        sentinel,
        input of (idAttr is "Vector.children.onMountInsert")
      )
    )

    // Expect three subscriptions per child:
    // - 1 for onMountInsert
    // - 1 for inserter inside onMountInsert
    // - 1 pilot subscription for child element
    assertEquals(ReactiveElement.numDynamicSubscriptions(el3), 14)
  }

  it("RenderableText with child and child.text") {

    val bus = new EventBus[Int]

    val el = div(
      child <-- bus.events.map {
        case 0 => "empty-0"
        case _ => div("div-0")
      },
      child <-- bus.events.map {
        case 0 => 1
        case _ => div("div-1")
      },
      child <-- bus.events.map {
        case 0 => 2
        case _ => "text-2"
      },
      child <-- bus.events.map {
        case 0 => new TextInputComponent()
        case _ => "text-3"
      },
      child <-- bus.events.mapTo(new TextNode("4")),
      child.text <-- bus.events.mapTo(new TextNode("5")),
      child <-- bus.events.map {
        case 0 => 6
        case _ => "text-6"
      },
      // #TODO[Scala] I should not need `TextNode` type param here,
      //  but I can't figure out how to make implicits work.
      child.text <-- bus.events.map[TextNode] {
        case 0 => 7
        case _ => "text-7"
      }
    )

    mount(el)

    bus.emit(0)

    expectNode(
      div of (
        sentinel,
        "empty-0",
        sentinel,
        "1",
        sentinel,
        "2",
        sentinel,
        input,
        sentinel,
        "4",
        sentinel,
        "5",
        sentinel,
        "6",
        sentinel,
        "7"
      )
    )

    bus.emit(1)

    expectNode(
      div of(
        sentinel,
        div of ("div-0"),
        sentinel,
        div of ("div-1"),
        sentinel,
        "text-2",
        sentinel,
        "text-3",
        sentinel,
        "4",
        sentinel,
        "5",
        sentinel,
        "text-6",
        sentinel,
        "text-7"
      )
    )
  }

  it("RenderableText with child.maybe") {

    val bus = new EventBus[Int]

    val el = div(
      child.maybe <-- bus.events.map {
        case 0 => None
        case _ => Some(div("div-0"))
      },
      child.maybe <-- bus.events.map {
        case 0 => None
        case _ => Some(1)
      },
      child.maybe <-- bus.events.map {
        case 0 => Some(2)
        case _ => Some("text-2")
      },
      child.maybe <-- bus.events.map {
        case 0 => Some(new TextInputComponent())
        case _ => Some("text-3")
      },
      child.maybe <-- bus.events.mapTo(Some(new TextNode("4")))
    )

    mount(el)

    bus.emit(0)

    expectNode(
      div of(
        sentinel,
        sentinel,
        sentinel,
        sentinel,
        sentinel,
        "2",
        sentinel,
        input,
        sentinel,
        "4"
      )
    )

    bus.emit(1)

    expectNode(
      div of(
        sentinel,
        div of ("div-0"),
        sentinel,
        "1",
        sentinel,
        "text-2",
        sentinel,
        "text-3",
        sentinel,
        "4"
      )
    )
  }

  it("RenderableText with static children") {

    val el = div(
      false,
      "string-0",
      1,
      2.0,
      if (true) "string-3" else 3,
      if (true) 4 else "string-4",
    )

    mount(el)

    expectNode(
      div of (
        "false",
        "string-0",
        "1",
        "2",
        "string-3",
        "4"
      )
    )
  }
}
