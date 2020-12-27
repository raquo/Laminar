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
      test(makeObservable = identity)
    }

    withClue("Signal:") {
      test(
        makeObservable = _.toSignal(span(text0)),
        expectedInitialChild = span of text0
      )
    }

    def test(
      makeObservable: EventStream[ChildNode[dom.Element]] => Observable[ChildNode[dom.Element]],
      expectedInitialChild: ExpectedNode = ExpectedNode.comment
    ): Unit = {

      val childBus = new EventBus[ChildNode[dom.Element]]
      val $child = makeObservable(childBus.events)

      mount(div("Hello, ", child <-- $child))
      expectNode(div.of("Hello, ", expectedInitialChild))

      withClue("First event:") {
        childBus.writer.onNext(span(text1))
        expectNode(div.of("Hello, ", span of text1))
      }

      withClue("Second event, changing node type (span->div):") {
        childBus.writer.onNext(div(text2))
        expectNode(div.of("Hello, ", div of text2))
      }

      withClue("Third event:") {
        childBus.writer.onNext(div(text3))
        expectNode(div.of("Hello, ", div of text3))
      }

      unmount()
    }
  }

  it("updates two children") {
    withClue("Stream:") {
      test(
        makeFooObservable = identity,
        makeBarObservable = identity
      )
    }

    withClue("Signal:") {
      test(
        makeFooObservable = _.toSignal(span(text0)),
        makeBarObservable = _.toSignal(span(text00)),
        initialFooChild = span of text0,
        initialBarChild = span of text00
      )
    }

    def test(
      makeFooObservable: EventStream[ChildNode[dom.Element]] => Observable[ChildNode[dom.Element]],
      makeBarObservable: EventStream[ChildNode[dom.Element]] => Observable[ChildNode[dom.Element]],
      initialFooChild: ExpectedNode = ExpectedNode.comment,
      initialBarChild: ExpectedNode = ExpectedNode.comment
    ): Unit = {
      val fooChildBus = new EventBus[ChildNode[dom.Element]]
      val barChildBus = new EventBus[ChildNode[dom.Element]]

      mount(div(child <-- makeFooObservable(fooChildBus.events), child <-- makeBarObservable(barChildBus.events)))
      expectNode(div.of(initialFooChild, initialBarChild))

      withClue("1. foo event:") {
        fooChildBus.writer.onNext(span(text1))
        expectNode(div.of(span of text1, initialBarChild))
      }

      withClue("2. bar event:") {
        barChildBus.writer.onNext(span(text4))
        expectNode(div.of(span of text1, span of text4))
      }

      withClue("3. another bar event:") {
        barChildBus.writer.onNext(span(text5))
        expectNode(div.of(span of text1, span of text5))
      }

      withClue("4. foo switch to div:") {
        fooChildBus.writer.onNext(div(text2))
        expectNode(div.of(div of text2, span of text5))
      }

      withClue("5. another foo event:") {
        fooChildBus.writer.onNext(div(text3))
        expectNode(div.of(div of text3, span of text5))
      }

      withClue("6. another bar event:") {
        barChildBus.writer.onNext(span(text6))
        expectNode(div.of(div of text3, span of text6))
      }

      withClue("7. yet another bar event:") {
        barChildBus.writer.onNext(span(text7))
        expectNode(div.of(div of text3, span of text7))
      }

      unmount()
    }
  }

//  ignore("updates two children with the same stream") {
//
//  }
//
//  ignore("works when nested") {
//
//  }
//
//  ignore("works with an attr receiver on the same node") {
//
//  }
//
//  ignore("works with an attr receiver with nesting") {
//
//  }
//
//  ignore("works with an attr receiver on this node") {
//
//  }

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
      div.of(L.a.of(href is "http://blog1.com/", "a blog"))
    )

    numCreateLinkCalls shouldBe 1
    numCreateLinkCalls = 0

    // --

    numVar.writer.onNext(Some(2))

    expectNode(
      div.of(L.a.of(href is "http://blog2.com/", "a blog"))
    )

    numCreateLinkCalls shouldBe 0

    // --

    unmount("first unmount")

    numVar.writer.onNext(Some(3))

    expectNode(
      el.ref,
      div.of(L.a.of(href is "http://blog2.com/", "a blog"))
    )

    numCreateLinkCalls shouldBe 0

    // --

    mount(el)

    expectNode(
      div.of(L.a.of(href is "http://blog2.com/", "a blog"))
    )

    numCreateLinkCalls shouldBe 0

    // --

    numVar.writer.onNext(Some(4))

    expectNode(
      div.of(L.a.of(href is "http://blog4.com/", "a blog"))
    )

    // Previous unmounting stopped the split stream, which cleared the memory of memoized elements
    numCreateLinkCalls shouldBe 1
    numCreateLinkCalls = 0

    // --

    numVar.writer.onNext(None)

    expectNode(
      div.of(i.of("no blog"))
    )

    // --

    numVar.writer.onNext(None)

    expectNode(
      div.of(i.of("no blog"))
    )

    numCreateLinkCalls shouldBe 0

    // --

    numVar.writer.onNext(Some(5))

    expectNode(
      div.of(L.a.of(href is "http://blog5.com/", "a blog"))
    )

    numCreateLinkCalls shouldBe 1
  }
}
