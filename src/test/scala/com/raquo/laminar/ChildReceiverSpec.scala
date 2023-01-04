package com.raquo.laminar

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ChildNode
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

class ChildReceiverSpec extends UnitSpec {

  private val text0 = randomString("text0_")
  private val text00 = randomString("text00_")
  private val text1 = randomString("text1_")
  private val text2 = randomString("text2_")
  private val text3 = randomString("text3_")
  private val text4 = randomString("text4_")
  private val text5 = randomString("text5_")
  private val text6 = randomString("text6_")
  private val text7 = randomString("text7_")

  it("updates one child") {
    withClue("Stream:") {
      test(makeObservable = identity, expectedInitialChild = None)
    }

    withClue("Signal:") {
      test(
        makeObservable = _.toSignal(span(text0)),
        expectedInitialChild = Some(span of text0)
      )
    }

    def test(
      makeObservable: EventStream[ChildNode[dom.Element]] => Observable[ChildNode[dom.Element]],
      expectedInitialChild: Option[ExpectedNode]
    ): Unit = {

      val childBus = new EventBus[ChildNode[dom.Element]]
      val childSource = makeObservable(childBus.events)

      mount(div("Hello, ", child <-- childSource))
      expectNode(div.of("Hello, ", sentinel, expectedInitialChild))

      withClue("First event:") {
        childBus.writer.onNext(span(text1))
        expectNode(div.of("Hello, ", sentinel, span of text1))
      }

      withClue("Second event, changing node type (span->div):") {
        childBus.writer.onNext(div(text2))
        expectNode(div.of("Hello, ", sentinel, div of text2))
      }

      withClue("Third event:") {
        childBus.writer.onNext(div(text3))
        expectNode(div.of("Hello, ", sentinel, div of text3))
      }

      unmount()
    }
  }

  it("updates two children") {
    withClue("Stream:") {
      test(
        makeFooObservable = identity,
        makeBarObservable = identity,
        initialFooChild = None,
        initialBarChild = None
      )
    }

    withClue("Signal:") {
      test(
        makeFooObservable = _.toSignal(span(text0)),
        makeBarObservable = _.toSignal(span(text00)),
        initialFooChild = Some(span of text0),
        initialBarChild = Some(span of text00)
      )
    }

    def test(
      makeFooObservable: EventStream[ChildNode[dom.Element]] => Observable[ChildNode[dom.Element]],
      makeBarObservable: EventStream[ChildNode[dom.Element]] => Observable[ChildNode[dom.Element]],
      initialFooChild: Option[ExpectedNode],
      initialBarChild: Option[ExpectedNode]
    ): Unit = {
      val fooChildBus = new EventBus[ChildNode[dom.Element]]
      val barChildBus = new EventBus[ChildNode[dom.Element]]

      mount(
        div(
          child <-- makeFooObservable(fooChildBus.events),
          child <-- makeBarObservable(barChildBus.events)
        )
      )
      expectNode(div.of(sentinel, initialFooChild, sentinel, initialBarChild))

      withClue("1. foo event:") {
        fooChildBus.writer.onNext(span(text1))
        expectNode(div.of(sentinel, span of text1, sentinel, initialBarChild))
      }

      withClue("2. bar event:") {
        barChildBus.writer.onNext(span(text4))
        expectNode(div.of(sentinel, span of text1, sentinel, span of text4))
      }

      withClue("3. another bar event:") {
        barChildBus.writer.onNext(span(text5))
        expectNode(div.of(sentinel, span of text1, sentinel, span of text5))
      }

      withClue("4. foo switch to div:") {
        fooChildBus.writer.onNext(div(text2))
        expectNode(div.of(sentinel, div of text2, sentinel, span of text5))
      }

      withClue("5. another foo event:") {
        fooChildBus.writer.onNext(div(text3))
        expectNode(div.of(sentinel, div of text3, sentinel, span of text5))
      }

      withClue("6. another bar event:") {
        barChildBus.writer.onNext(span(text6))
        expectNode(div.of(sentinel, div of text3, sentinel, span of text6))
      }

      withClue("7. yet another bar event:") {
        barChildBus.writer.onNext(span(text7))
        expectNode(div.of(sentinel, div of text3, sentinel, span of text7))
      }

      unmount()
    }
  }

  it("split[Option] - caching elements to change tag type") {

    var numCreateLinkCalls = 0

    def renderBlogLink(urlSignal: Signal[String]): HtmlElement = {
      numCreateLinkCalls += 1
      L.a(href <-- urlSignal, "a blog")
    }

    def MaybeBlogUrl(maybeUrlSignal: Signal[Option[String]]): Signal[HtmlElement] = {
      val noBlog = i("no blog")
      maybeUrlSignal
        .split(_ => ())((_, _, urlSignal) => renderBlogLink(urlSignal))
        .map(_.getOrElse(noBlog))
    }

    val numVar = Var[Option[Int]](Some(1))

    val maybeUrlSignal = numVar.signal.map(_.map(num => s"http://blog${num}.com/"))

    val el = div(
      child <-- MaybeBlogUrl(maybeUrlSignal)
    )

    // --

    mount(el)

    expectNode(
      div.of(
        sentinel,
        L.a.of(href is "http://blog1.com/", "a blog")
      )
    )

    numCreateLinkCalls shouldBe 1
    numCreateLinkCalls = 0

    // --

    numVar.writer.onNext(Some(2))

    expectNode(
      div.of(
        sentinel,
        L.a.of(href is "http://blog2.com/", "a blog")
      )
    )

    numCreateLinkCalls shouldBe 0

    // --

    unmount("first unmount")

    numVar.writer.onNext(Some(3))

    expectNode(
      el.ref,
      div.of(
        sentinel,
        L.a.of(href is "http://blog2.com/", "a blog")
      )
    )

    numCreateLinkCalls shouldBe 0

    // --

    mount(el)

    expectNode(
      div.of(
        sentinel,
        L.a.of(href is "http://blog2.com/", "a blog")
      )
    )

    numCreateLinkCalls shouldBe 0

    // --

    numVar.writer.onNext(Some(4))

    expectNode(
      div.of(
        sentinel,
        L.a.of(href is "http://blog4.com/", "a blog")
      )
    )

    // We don't clear memoized state when split signal is stopped anymore
    numCreateLinkCalls shouldBe 0
    numCreateLinkCalls = 0

    // --

    numVar.writer.onNext(None)

    expectNode(
      div.of(
        sentinel,
        i.of("no blog")
      )
    )

    // --

    numVar.writer.onNext(None)

    expectNode(
      div.of(
        sentinel,
        i.of("no blog")
      )
    )

    numCreateLinkCalls shouldBe 0

    // --

    numVar.writer.onNext(Some(5))

    expectNode(
      div.of(
        sentinel,
        L.a.of(href is "http://blog5.com/", "a blog")
      )
    )

    numCreateLinkCalls shouldBe 1
  }

  it("can move child from one receiver to another") {

    val spanA = span("a")
    val spanB = span("b")
    val spanC = span("c")
    val spanD = span("d")
    val spanE = span("e")

    val bus1 = new EventBus[HtmlElement]
    val bus2 = new EventBus[HtmlElement]

    val el = div(
      child <-- bus1,
      child <-- bus2,
    )

    mount(el)

    // --

    expectNode(
      div of(
        sentinel,
        sentinel
      )
    )

    // --

    EventBus.emit(
      bus1 -> spanA,
      bus2 -> spanD
    )

    expectNode(
      div of(
        sentinel,
        span of "a",
        sentinel,
        span of "d",
      )
    )

    // -- Steal D from inserter #2 to inserter #1

    EventBus.emit(
      bus1 -> spanD
    )

    expectNode(
      div of(
        sentinel,
        span of "d",
        sentinel
      )
    )

    // -- Request invalid state (same element in both places)

    EventBus.emit(
      bus1 -> spanA,
      bus2 -> spanA
    )

    expectNode(
      div of(
        sentinel,
        sentinel,
        span of "a"
      )
    )

    // -- Recover from invalid state

    EventBus.emit(
      bus1 -> spanA,
      bus2 -> spanB
    )

    expectNode(
      div of(
        sentinel,
        span of "a",
        sentinel,
        span of "b",
      )
    )

    // -- Unmount and re-mount

    unmount()

    mount(el)

    expectNode(
      div of(
        sentinel,
        span of "a",
        sentinel,
        span of "b",
      )
    )

    // --

    EventBus.emit(
      bus1 -> spanC,
      bus2 -> spanD
    )

    expectNode(
      div of(
        sentinel,
        span of "c",
        sentinel,
        span of "d"
      )
    )

    // --

    EventBus.emit(
      bus1 -> spanD,
      bus2 -> spanE
    )

    expectNode(
      div of(
        sentinel,
        span of "d",
        sentinel,
        span of "e"
      )
    )

    // --

    bus1.emit(spanD)
    bus2.emit(spanC)

    expectNode(
      div of(
        sentinel,
        span of "d",
        sentinel,
        span of "c"
      )
    )

    // --

    bus2.emit(spanD)
    bus1.emit(spanC)

    expectNode(
      div of(
        sentinel,
        span of "c",
        sentinel,
        span of "d"
      )
    )
  }

}
