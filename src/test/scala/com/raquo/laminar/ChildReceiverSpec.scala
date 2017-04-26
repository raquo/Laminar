package com.raquo.laminar

import com.raquo.laminar.tags.{div, span}
import com.raquo.laminar.utils.UnitSpec
import com.raquo.dombuilder.utils.testing.matching.ExpectedNode
import com.raquo.laminar
import com.raquo.laminar.nodes.ReactiveNode
import com.raquo.xstream.{ShamefulStream, XStream}

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
    withClue("Regular stream:") {
      test($child = XStream.create[ChildNode]())
    }

    withClue("Memory stream:") {
      test(
        $child = XStream.create().startWith(span(text0)),
        initialChild = span like text0
      )
    }

    def test(
      $child: XStream[ChildNode],
      initialChild: ExpectedNode[ReactiveNode] = laminar.commentBuilder likeWhatever
    ): Unit = {
      val $varChild = new ShamefulStream($child)

      mount(div("Hello, ", child <-- $child))
      expectNode(div like("Hello, ", initialChild))

      withClue("First event:") {
        $varChild.shamefullySendNext(span(text1))
        expectNode(div like("Hello, ", span like text1))
      }

      withClue("Second event, changing node type (span->div):") {
        $varChild.shamefullySendNext(div(text2))
        expectNode(div like("Hello, ", div like text2))
      }

      withClue("Third event:") {
        $varChild.shamefullySendNext(div(text3))
        expectNode(div like("Hello, ", div like text3))
      }

      unmount()
    }
  }

  it("updates two children") {
    withClue("Regular stream:") {
      test(
        $fooChild = XStream.create[ChildNode](),
        $barChild = XStream.create[ChildNode]()
      )
    }

    withClue("Memory stream:") {
      test(
        $fooChild = XStream.create().startWith(span(text0)),
        $barChild = XStream.create().startWith(span(text00)),
        initialFooChild = span like text0,
        initialBarChild = span like text00
      )
    }

    def test(
      $fooChild: XStream[ChildNode],
      $barChild: XStream[ChildNode],
      initialFooChild: ExpectedNode[ReactiveNode] = laminar.commentBuilder likeWhatever,
      initialBarChild: ExpectedNode[ReactiveNode] = laminar.commentBuilder likeWhatever
    ): Unit = {
      val $varFooChild = new ShamefulStream($fooChild)
      val $varBarChild = new ShamefulStream($barChild)

      mount(div(child <-- $fooChild, child <-- $barChild))
      expectNode(div like(initialFooChild, initialBarChild))

      withClue("1. foo event:") {
        $varFooChild.shamefullySendNext(span(text1))
        expectNode(div like(span like text1, initialBarChild))
      }

      withClue("2. bar event:") {
        $varBarChild.shamefullySendNext(span(text4))
        expectNode(div like(span like text1, span like text4))
      }

      withClue("3. another bar event:") {
        $varBarChild.shamefullySendNext(span(text5))
        expectNode(div like(span like text1, span like text5))
      }

      withClue("4. foo switch to div:") {
        $varFooChild.shamefullySendNext(div(text2))
        expectNode(div like(div like text2, span like text5))
      }

      withClue("5. another foo event:") {
        $varFooChild.shamefullySendNext(div(text3))
        expectNode(div like(div like text3, span like text5))
      }

      withClue("6. another bar event:") {
        $varBarChild.shamefullySendNext(span(text6))
        expectNode(div like(div like text3, span like text6))
      }

      withClue("7. yet another bar event:") {
        $varBarChild.shamefullySendNext(span(text7))
        expectNode(div like(div like text3, span like text7))
      }

      unmount()
    }
  }

  ignore("updates two children with the same stream") {

  }

  ignore("works when nested") {

  }

  ignore("works with an attr receiver on the same node") {

  }

  ignore("works with an attr receiver with nesting") {

  }

  ignore("works with an attr receiver on this node") {

  }
}
