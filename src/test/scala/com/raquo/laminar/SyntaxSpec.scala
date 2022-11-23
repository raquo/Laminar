package com.raquo.laminar

import com.raquo.airstream.custom.{CustomSource, CustomStreamSource}
import com.raquo.airstream.web.AjaxEventStream
import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.TestableOwner
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.collection.{immutable, mutable}
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.concurrent.Future
import scala.scalajs.js

class SyntaxSpec extends UnitSpec {

  def noop[A](v: A): Unit = ()

  it("seqToModifier, seqToNode implicit conversion works for both strings and nodes") {

    val strings = List("a", "b")
    val stringsBuffer = mutable.Buffer("aa", "bb")
    val stringsArray = Array("aaa", "bbb")
    val stringsJsArray = js.Array("aaaa", "bbbb")
    val nodes = Vector(span("ya"), article("yo")) // seq of elements.
    val nodesBuffer = mutable.Buffer(span("boo")) // mutable.Buffer[Span] is covariant as collection.Seq
    val nodesArray = Array(span("foo"), span("bar")) // Scala Arrays are not Seq-s. They are used in Scala 3 enums.
    val nodesJsArray = js.Array(span("js")) // JS arrays are invariant
    val mixed: Seq[Mod[HtmlElement]] = Vector("c", input())

    // @Note we make sure that none of the above go through expensive `nodesSeqToInserter` by never mounting the actualNode element
    //  - inserters require mounting to be processed.

    val actualNode = div(
      strings, stringsBuffer, stringsArray, stringsJsArray,
      nodes, nodesBuffer, nodesArray, nodesJsArray, mixed
    )

    expectNode(actualNode.ref, div.of(
      "a", "b",
      "aa", "bb",
      "aaa", "bbb",
      "aaaa", "bbbb",
      span of "ya", article of "yo",
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

    // @TODO[API] Can we have type inference for this [Int]?
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

    val customSource = CustomStreamSource[String]((_, _, _, _) => CustomSource.Config(() => (), () => ()))

    val periodicInt = EventStream.periodic(0)

    val ajaxStream = AjaxEventStream.get("")
    val xhrBus = new EventBus[dom.XMLHttpRequest]
    val ajaxObs = Observer.empty[dom.XMLHttpRequest]




    // @TODO[Test] this should be `"..." shouldNot typeCheck` but https://github.com/scalatest/scalatest/issues/1947
    assertDoesNotCompile("focus <-- boolSignal")

    //implicit def xxxx[A](obs: Observable[_]#Self[A]): Source[A] = obs: Observable[A]

    div(
      cls.toggle("cls1") <-- boolSignal,
      cls.toggle("cls1") <-- boolStream,
      cls.toggle("cls1") <-- boolBus,
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

}
