package com.raquo.laminar

import com.raquo.airstream.core.Source
import com.raquo.airstream.custom.{CustomSource, CustomStreamSource}
import com.raquo.airstream.web.AjaxEventStream
import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.TestableOwner
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.collection.{immutable, mutable}
import scala.concurrent.Future
import scala.scalajs.js

class SyntaxSpec extends UnitSpec {

  it("ModifierSeq implicit conversion works for both strings and nodes") {

    val strings = List("a", "b")
    val nodes = Vector(span("ya"), article("yo")) // seq of elements.
    val nodesBuffer = mutable.Buffer(span("boo")) // mutable.Buffer[Span] is covariant as collection.Seq
    val jsNodes = js.Array(span("js")) // JS arrays are invariant
    val mixed: Seq[Mod[HtmlElement]] = Vector("c", input())

    // @Note we make sure that none of the above go through expensive `nodesSeqToInserter` by absence of a sentinel comment node

    mount(div(strings, nodes, nodesBuffer, jsNodes, mixed))

    expectNode(div.of(
      "a", "b",
      span of "ya", article of "yo",
      span of "boo",
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

    events shouldEqual mutable.Buffer()

    // --

    checkbox.ref.click()

    events shouldEqual mutable.Buffer(false)
    events.clear()

    // --

    checkbox.ref.click()

    events shouldEqual mutable.Buffer(true)
    events.clear()

    // --

    checkbox.ref.click()

    events shouldEqual mutable.Buffer(false)
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
      onMountBind(_ => observable --> ((num: Int) => num * 5)),
      onMountBind(_ => signal --> ((num: Int) => num * 5)),
      onMountBind(_ => stream --> ((num: Int) => num * 5))
    )

    el.amend(
      onMountBind[Div](_.thisNode.events(onClick).map(_ => 1) --> (num => num * 5)) // @Note "Div" type param is required only in scala 2.12
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

    el.amend(observable --> ((num: Int) => num * 5))
    el.amend(signal --> ((num: Int) => num * 5))
    el.amend(stream --> ((num: Int) => num * 5))

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




    "focus <-- boolSignal" shouldNot typeCheck

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
      child <-- divFuture,
      child <-- divPromise,
      child.maybe <-- divStream.map(Some(_)),
      child.maybe <-- divStream.toWeakSignal,
      child.maybe <-- divObservable.map(Option(_)),
      child.maybe <-- divObservable.map(Some(_)),
      child.maybe <-- divFuture,
      child.maybe <-- divPromise,
      child.int <-- periodicInt,
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

}
