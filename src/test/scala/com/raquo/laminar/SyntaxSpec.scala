package com.raquo.laminar

import com.raquo.airstream.custom.{CustomSource, CustomStreamSource}
import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.TestableOwner
import com.raquo.laminar.keys.DerivedStyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.collection.{immutable, mutable}
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

class SyntaxSpec extends UnitSpec {

  def noop[A](v: A): Unit = ()

  it("bundle sanity check") {

    // Simple types

    assert(div.name == "div")
    assert(onClick.name == "click")
    assert(value.name == "value")
    assert(idAttr.name == "id")
    assert(charset.name == "charset")
    assert(display.name == "display")

    // Event props

    assert(onClick.name == "click") // elements - global
    assert(onCopy.name == "copy") // elements - clipboard

    documentEvents { x => // document - global
      assert(x.onClick.name == "click")
      x.onClick
    }
    documentEvents { x => // document - clipboard
      assert(x.onCopy.name == "copy")
      x.onCopy
    }
    documentEvents { x => // document
      assert(x.onDomContentLoaded.name == "DOMContentLoaded")
      x.onDomContentLoaded
    }

    windowEvents { x => // window - global
      assert(x.onClick.name == "click")
      x.onClick
    }
    windowEvents { x => // window - clipboard
      assert(x.onCopy.name == "copy")
      x.onCopy
    }
    windowEvents { x => // window
      assert(x.onOffline.name == "offline")
      x.onOffline
    }

    // SVG namespaces

    assert(svg.xlinkHref.namespacePrefix.contains("xlink"))
    assert(svg.xlinkHref.name == "xlink:href")
    assert(svg.xlinkHref.namespaceUri.get == "http://www.w3.org/1999/xlink")

    // Aria attributes

    assert(aria.label.name == "aria-label")

    (div(aria.label := "hello").ref.getAttribute("aria-label") == "hello")
    (svg.circle(aria.label := "hello").ref.getAttribute("aria-label") == "hello")

    // Aliases

    assert(typ == `type`)
    assert(typ.name == "type")

    assert(svg.typ == svg.`type`)
    assert(svg.typ.name == "type")

    // Complex keys

    assert(cls.name == "class")
    // assert((cls := List("class1", "class2")).value == "class1 class2")

    // CSS keywords

    val s1: StyleSetter = display.none
    val v1: String = display.none.value
    assert(display.none.value == "none")

    // Base CSS keywords
    val s2: StyleSetter = padding.inherit
    val v2: String = padding.inherit.value
    assert(display.inherit.value == "inherit")

    // Derived CSS props (units)

    val p1: StyleProp[String] = padding
    val p2: DerivedStyleProp[Int] = padding.px
    assert((padding.px := 12).value == "12px")

    maxHeight.calc := "12px + 20em" // Length inherits Calc

    background.url: DerivedStyleProp[String]
    (background.url := "https://laminar.dev").value == """url("https://laminar.dev")"""

    assert(style.percent(55) == "55%")
    assert(style.calc("12px + 20em") == "calc(12px + 20em)")

    // Multi-parameter derived CSS props (units)

    val p3: StyleProp[String] = color
    val s3: StyleSetter = color.rgb(200, 100, 0)
    assert(color.rgb(200, 100, 0).value == "rgb(200, 100, 0)")

    assert(style.rgb(200, 100, 0) == "rgb(200, 100, 0)")
  }

  it("seqToModifier, seqToNode implicit conversion works for both strings and nodes") {

    val strings = List("a", "b")
    val stringsBuffer = mutable.Buffer("aa", "bb")
    val stringsArray = Array("aaa", "bbb")
    val stringsJsArray = js.Array("aaaa", "bbbb")
    val nodes = Vector(span("ya"), articleTag("yo")) // seq of elements.
    val nodesBuffer = mutable.Buffer(span("boo")) // mutable.Buffer[Span] is covariant as collection.Seq
    val nodesArray = Array(span("foo"), span("bar")) // Scala Arrays are not Seq-s. They are used in Scala 3 enums.
    val nodesJsArray = js.Array(span("js")) // JS arrays are invariant
    val mixed: Seq[Mod[HtmlElement]] = Vector("c", input())

    // @Note we make sure that none of the above go through expensive `nodesSeqToInserter` by never mounting the actualNode element
    //  - inserters require mounting to be processed.

    val actualNode = div(
      strings,
      stringsBuffer,
      stringsArray,
      stringsJsArray,
      nodes,
      nodesBuffer,
      nodesArray,
      nodesJsArray,
      mixed
    )

    expectNode(actualNode.ref, div.of(
      "a", "b",
      "aa", "bb",
      "aaa", "bbb",
      "aaaa", "bbbb",
      span of "ya", articleTag of "yo",
      span of "boo",
      span of "foo",
      span of "bar",
      span of "js",
      "c", input
    ))
  }

  it("inContext modifier infers precise type") {

    val testOwner = new TestableOwner

    val checkedBus = new EventBus[Boolean]

    val events = mutable.Buffer[Boolean]()
    checkedBus.events.foreach(events += _)(testOwner)

    val checkbox = input(
      typ := "checkbox",
      checked := true,
      inContext(thisNode => onClick.mapTo(thisNode.ref.checked) --> checkedBus)
    )

    mount(checkbox)

    events shouldBe mutable.Buffer()

    // --

    checkbox.ref.click()

    events shouldBe mutable.Buffer(false)
    events.clear()

    // --

    checkbox.ref.click()

    events shouldBe mutable.Buffer(true)
    events.clear()

    // --

    checkbox.ref.click()

    events shouldBe mutable.Buffer(false)
    events.clear()
  }

  it("onMountUnmountCallback infers precise type") {

    val el = div("Hello world", onMountUnmountCallback(
      mount = c => {
        val proof: MountContext[Div] = c
        ()
      },
      unmount = n => {
        val proof: Div = n
        ()
      }
    ))

    el.amend(
      onMountUnmountCallback[Div]( // @Note "Div" type param is required only in scala 2.12
        mount = c => {
          val proof: MountContext[Div] = c
          ()
        },
        unmount = n => {
          val proof: Div = n
          ()
        }
      )
    )

    mount(el)
  }

  it("onMountInsert with implicit renderable syntax") {
    val el = div(
      onMountInsert { _ =>
        "hello"
      },
      onMountInsert { _ =>
        123
      },
      "world"
    )

    mount(el)

    expectNode(div of ("hello", "123", "world"))
  }

  it("onMountBind with implicit setters syntax") {

    val el = div("Hello world")

    val bus = new EventBus[Int]
    val state = Var(5)
    val signal = state.signal
    val stream = signal.changes
    val observable: Observable[Int] = stream

    el.amend(
      onMountBind(_ => observable --> Observer[Int](num => num * 5)),
      onMountBind(_ => signal --> Observer[Int](num => num * 5)),
      onMountBind(_ => stream --> Observer[Int](num => num * 5))
    )

    el.amend(
      onMountInsert[Div](_ => div()) // @Note "Div" type param is required only in scala 2.12
    )

    el.amend(
      onMountBind(_ => observable --> (num => num * 5)),
      onMountBind(_ => signal --> (num => num * 5)),
      onMountBind(_ => stream --> (num => num * 5))
    )

    el.amend(
      onMountBind(_ => onClick --> Observer[dom.MouseEvent](ev => ())),
      onMountBind(_ => onClick.mapTo(1) --> Observer[Int](num => num * 5))
    )

    el.amend(
      onMountBind(_ => observable --> ((num: Int) => noop(num * 5))),
      onMountBind(_ => signal --> ((num: Int) => noop(num * 5))),
      onMountBind(_ => stream --> ((num: Int) => noop(num * 5)))
    )

    el.amend(
      onMountBind[Div](_.thisNode.events(onClick).map(_ => 1) --> (num => noop(num * 5))) // @Note "Div" type param is required only in scala 2.12
    )

    el.amend(
      onMountBind[Div](_ => stream --> bus.writer) // @Note "Div" type param is required only in scala 2.12
    )

    mount(el)
  }

  it("amend with implicit setters syntax") {

    val el = div("Hello world")

    val bus = new EventBus[Int]
    val state = Var(5)
    val signal = state.signal
    val stream = signal.changes
    val observable: Observable[Int] = stream

    // @TODO[API] Can we have type inference for this [Int]?
    el.amend(observable --> Observer[Int](num => num * 5))
    el.amend(signal --> Observer[Int](num => num * 5))
    el.amend(stream --> Observer[Int](num => num * 5))

    el.amend(observable --> (num => num * 5))
    el.amend(signal --> (num => num * 5))
    el.amend(stream --> (num => num * 5))

    el.amend(onClick --> Observer[dom.MouseEvent](ev => ()))
    el.amend(onClick.mapTo(1) --> Observer[Int](num => num * 5))

    el.amend(observable --> ((num: Int) => noop(num * 5)))
    el.amend(signal --> ((num: Int) => noop(num * 5)))
    el.amend(stream --> ((num: Int) => noop(num * 5)))

    el.amendThis(_ => stream --> bus.writer)

    mount(el)
  }

  it("amend and amendThis on inlined element") {
    // https://github.com/raquo/Laminar/issues/54

    div().amend(
      cls := "foo"
    )

    div().amend(
      cls := "foo",
      cls := "bar"
    )

    div().amendThis { thisNode =>
      val node = thisNode: Div // assert
      cls := "foo"
    }
  }

  it("Sinks and Sources and assorted type inference") {

    val boolStream = EventStream.fromValue(false)
    val boolSignal = Signal.fromValue(false) // Used in does-not-compile check!
    val boolBus = new EventBus[Boolean]
    val boolFn = (a: Boolean) => ()
    val boolObs = Observer.empty[Boolean]

    val doubleBus = new EventBus[Double]

    val textStream = EventStream.fromValue("")
    val textSignal: Signal[String] = Signal.fromValue("")
    val textVal: Val[String] = Signal.fromValue("")
    val textBus = new EventBus[String]
    val textObs = Observer.empty[String]
    val textObservable = textStream: Observable[String]


    // #Note using defs to avoid element reuse

    def divObservable: Observable[Div] = EventStream.fromValue(div())
    def divStream: EventStream[Div] = EventStream.fromValue(div())
    def divBus = new EventBus[Div]
    def divFuture = Future.successful(div("hello"))
    def divPromise = js.Promise.resolve[Div](div("hello"))

    def childrenStream: EventStream[List[Div]] = EventStream.fromValue(div() :: Nil)
    def childrenSignal: Signal[List[Div]] = childrenStream.toSignal(Nil)
    def childrenObs: Observable[immutable.Seq[Div]] = childrenStream

    val customSource = new CustomStreamSource[String]((_, _, _, _) => CustomSource.Config(() => (), () => ()))

    val periodicInt = EventStream.periodic(0)

    val ajaxStream = AjaxStream.get("")
    val xhrBus = new EventBus[dom.XMLHttpRequest]
    val ajaxObs = Observer.empty[dom.XMLHttpRequest]




    // @TODO[Test] this should be `"..." shouldNot typeCheck` but https://github.com/scalatest/scalatest/issues/1947
    assertDoesNotCompile("focus <-- boolSignal")

    //implicit def xxxx[A](obs: Observable[_]#Self[A]): Source[A] = obs: Observable[A]

    div(
      cls("cls1") <-- boolSignal,
      cls("cls1") <-- boolStream,
      cls("cls1") <-- boolBus,
      focus <-- boolStream,
      focus <-- boolBus,
      child <-- divObservable,
      child <-- divObservable.map(d => d),
      child <-- divObservable.map(d => d).map(d => d),
      child <-- divStream,
      child <-- divBus,
      child <-- divStream.map(d => d), // Tests Observable#Self type inference (note: IntelliJ might report non-existent error)
      child <-- divStream.map(d => d).map(d => d),
      child <-- EventStream.fromFuture(divFuture),
      child <-- EventStream.fromJsPromise(divPromise),
      child.maybe <-- divStream.map(Some(_)),
      child.maybe <-- divStream.toWeakSignal,
      child.maybe <-- divObservable.map(Option(_)),
      child.maybe <-- divObservable.map(Some(_)),
      child.maybe <-- Signal.fromFuture(divFuture),
      child.maybe <-- Signal.fromJsPromise(divPromise),
      //child.int <-- periodicInt,
      child.text <-- periodicInt,
      child.text <-- boolBus,
      child.text <-- doubleBus.events,
      text <-- periodicInt,
      text <-- boolBus,
      text <-- doubleBus.events,
      children <-- childrenObs,
      children <-- childrenObs.map(c => c),
      children <-- childrenStream,
      children <-- childrenStream.map(c => c),
      children <-- childrenSignal,
      children <-- childrenSignal.map(c => c),
      idAttr <-- textObservable,
      idAttr <-- textObservable.map(t => t),
      idAttr <-- textStream,
      idAttr <-- textStream.map(t => t),
      idAttr <-- customSource,
      idAttr <-- textSignal,
      idAttr <-- textSignal.map(t => t),
      idAttr <-- textVal,
      idAttr <-- textVal.map(t => t)
    )

    div(
      onClick --> (_ => println("yo")),
      onClick.mapTo(0) --> (_ => println("yo")),
      //onClick --> Observer(_ => println("yo")), // This didn't work pre-0.12.0 either :(
    )

    div(
      boolSignal --> boolFn,
      boolSignal --> boolObs,
      textBus --> textObs,
      textBus --> (_ => println("")),
      textSignal --> textBus,
      textSignal --> textObs,
      textSignal --> (_ => println("")),
      textStream --> textBus,
      textStream --> textObs,
      textStream --> (_ => println("")),
      ajaxStream --> ajaxObs,
      ajaxStream --> xhrBus,
      xhrBus --> xhrBus // lol
    )
  }

  it("apply methods of various Modifier aliases compile") {

    // No type param needed when in the element context
    div(
      Modifier { el => noop(el) },
      Setter { el => noop(el) },
      Binder { el => null.asInstanceOf[DynamicSubscription] }
    )

    // These require that the `Modifier` and other aliases defined in Laminar.scala
    // are val-s, they can't be def-s, at least in Scala 2

    Modifier[ReactiveElement.Base](el => println(el))
    Setter[ReactiveElement.Base](el => println(el))
    Binder[ReactiveElement.Base](el => ().asInstanceOf[DynamicSubscription])

    Modifier[ReactiveElement.Base] { el => println(el) }
    Setter[ReactiveElement.Base] { el => println(el) }
    Binder[ReactiveElement.Base] { el => null.asInstanceOf[DynamicSubscription] }

    Modifier { (el: ReactiveElement.Base) =>
      println(el)
    }
    Setter { (el: ReactiveElement.Base) =>
      println(el)
    }
    Binder { (el: ReactiveElement.Base) =>
      null.asInstanceOf[DynamicSubscription]
    }
  }

  it("Mod alias lets you get the base type") {
    val mod: Mod.Base = emptyMod
  }

  it("unit-based binding syntax") {
    var i = 0
    val intVar = Var(0)

    def eventObserver = Observer[dom.Event](_ => ())
    def boolObserver = Observer[Boolean](_ => ())

    val bus = new EventBus[Boolean]

    def effectReturningInt(): Int = {
      i += 1
      i
    }

    def effectReturningUnit(): Unit = {
      i += 1
    }

    def conditionalEventObserver = if (i > 0) eventObserver

    def conditionalBoolObserver = if (i > 0) boolObserver

    val el = div(
      onClick --> eventObserver,
      bus.events --> boolObserver,
      onClick.flatMapStream(_ => bus.events) --> boolObserver,
    )

    // We don't want unused (non-Unit) values to be silently swallowed by Laminar
    assertTypeError("div(onClick --> 5)")
    assertTypeError("div(onClick --> { 5 })")
    assertTypeError("div(onClick --> number())")
    assertTypeError("div(onClick --> effectReturningInt())")
    assertTypeError("div(onClick --> i)")

    assertTypeError("div(bus.events --> 5)")
    assertTypeError("div(bus.events --> number())")
    assertTypeError("div(bus.events --> effectReturningInt())")
    assertTypeError("div(bus.events --> i)")

    assertTypeError("div(onClick.flatMap(_ => bus.events) --> 5)")
    assertTypeError("div(onClick.flatMap(_ => bus.events) --> number())")
    assertTypeError("div(onClick.flatMap(_ => bus.events) --> effectReturningInt())")
    assertTypeError("div(onClick.flatMap(_ => bus.events) --> i)")

    // Same goes for sinks that are of the wrong type
    assertTypeError("div(onClick --> intVar)")
    assertTypeError("div(bus.events --> intVar)")
    assertTypeError("div(onClick.flatMap(_ => bus.events) --> intVar)")

    // --

    // Make sure the unitArrow API does not work without an explicit import.

    assertTypeError("div(onClick --> effectReturningUnit())")
    assertTypeError("div(onClick --> { effectReturningInt(); () })")
    assertTypeError("div(onClick --> intVar.update(_ + 5))")

    assertTypeError("div(bus.events --> effectReturningUnit())")
    assertTypeError("div(bus.events --> { effectReturningInt(); () })")
    assertTypeError("div(bus.events --> intVar.update(_ + 5))")

    assertTypeError("div(onClick.flatMapStream(_ => bus.events) --> effectReturningUnit())")
    assertTypeError("div(onClick.flatMapStream(_ => bus.events) --> { effectReturningInt(); () })")
    assertTypeError("div(onClick.flatMapStream(_ => bus.events) --> intVar.update(_ + 5))")

    {
      import com.raquo.laminar.api.features.unitArrows

      el.amend(
        onClick --> effectReturningUnit(),
        onClick --> { effectReturningInt(); () },
        onClick --> intVar.update(_ + 5),
        bus.events --> effectReturningUnit(),
        bus.events --> { effectReturningInt(); () },
        bus.events --> intVar.update(_ + 5),
        onClick.flatMapStream(_ => bus.events) --> effectReturningUnit(),
        onClick.flatMapStream(_ => bus.events) --> { effectReturningInt(); () },
        onClick.flatMapStream(_ => bus.events) --> intVar.update(_ + 5)
      )

      // The below will (undesirably) compile on Scala 3 but will (desirably) fail to compile on Scala 2.
      // Don't really feel like asserting disappointment.

      // el.amend(
      //   bus.events --> { if (i > 0) eventObserver },
      //   bus.events --> conditionalBoolObserver,
      //   onClick.flatMap(_ => bus.events) --> { if (i > 0) boolObserver },
      //   onClick.flatMap(_ => bus.events) --> conditionalBoolObserver,
      //   onClick.flatMap(_ => bus.events) --> { if (i > 0) eventObserver },
      //   onClick.flatMap(_ => bus.events) --> conditionalEventObserver
      // )
    }

    // --

    mount(el)

    assert(i == 0)
    assert(intVar.now() == 0)

    // --

    el.ref.click()

    assert(i == 2)
    assert(intVar.now() == 5)

    // --

    bus.emit(true)

    assert(i == 6)
    assert(intVar.now() == 15)
  }

  it("compose syntax") {
    val eventObs = Observer.empty[dom.Event]
    div(
      onClick.compose(_.delay(100)) --> eventObs,
      onClick(_.delay(100)) --> eventObs,
      //
      onClick.preventDefault.compose(_.delay(100)) --> eventObs,
      onClick.preventDefault(_.delay(100)) --> eventObs,
    )
  }
}
