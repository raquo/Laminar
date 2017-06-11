package com.raquo.laminar

import com.raquo.laminar._
import com.raquo.laminar.utils.AsyncUnitSpec
import com.raquo.laminar.attrs.{title, rel}
import com.raquo.laminar.tags.{div, span}
import com.raquo.xstream.{ShamefulStream, XStream}
import org.scalatest.{Assertion, AsyncFunSpec}

import scala.concurrent.Promise
import scala.scalajs.concurrent.JSExecutionContext
import scala.scalajs.js
import scala.util.Try

class AttrReceiverAsyncSpec extends AsyncUnitSpec {

  implicit override def executionContext = JSExecutionContext.queue

//  it("async works") {
//    val promise = Promise[Assertion]()
//
//    js.timers.setTimeout(10) {
//      promise.success {
////        $writeableTitle.shamefullySendNext(title3)
//        //        titleCounter shouldBe 2
//        println("FOOOO")
//        assert(true)
//      }
//    }
//
//    promise.future
//  }

  val title1 = randomString("title1_")
  val title2 = randomString("title2_")
  val title3 = randomString("title3_")
  val title4 = randomString("title4_")

  it("unsubscribes from the stream after unmounting") {
    var titleCounter = 0
    val $title = XStream.create[String]()
    val $titleWithDebugger = $title.map(title => {
      titleCounter += 1
      title
    })

    val element = div(title <-- $titleWithDebugger, "Hello")
    mount(element)

    expectNode(div like (title isEmpty, "Hello"))

    val $writeableTitle = new ShamefulStream($title)

    titleCounter shouldBe 0

    $writeableTitle.shamefullySendNext(title1)
    expectNode(div like (title is title1, "Hello"))
    titleCounter shouldBe 1

    $writeableTitle.shamefullySendNext(title2)
    expectNode(div like (title is title2, "Hello"))
    titleCounter shouldBe 2

    unmount()
    mount(div(rel := "unmounted"))

    expectNode(div like (title isEmpty, rel is "unmounted"))

    val promise = Promise[Assertion]()
    js.timers.setTimeout(1) {
      promise.complete(Try{
        $writeableTitle.shamefullySendNext(title3)
        expectNode(div like (title isEmpty, rel is "unmounted"))
        titleCounter shouldBe 2
      })
    }
    promise.future
  }

  it("unsubscribes from the stream after node is removed using replaceChild") {
    var titleCounter = 0
    val $title = XStream.create[String]()
    val $titleWithDebugger = $title.map(title => {
      titleCounter += 1
      title
    })

    val subscribedElement = span(title <-- $titleWithDebugger)

    val parentElement = div(subscribedElement, "Hello")
    mount(parentElement)

    expectNode(div like (span like (title isEmpty), "Hello"))

    val $writeableTitle = new ShamefulStream($title)

    titleCounter shouldBe 0

    $writeableTitle.shamefullySendNext(title1)
    expectNode(div like (span like (title is title1), "Hello"))
    titleCounter shouldBe 1

    $writeableTitle.shamefullySendNext(title2)
    expectNode(div like (span like (title is title2), "Hello"))
    titleCounter shouldBe 2

    // This should unsubscribe `subscribedElement` from all its subscriptions
    parentElement.replaceChild(subscribedElement, span(rel := "newChild"))

    expectNode(div like (span like (rel is "newChild"), "Hello"))

    val promise = Promise[Assertion]()
    js.timers.setTimeout(1) {
      promise.complete(Try{
        $writeableTitle.shamefullySendNext(title3)
        expectNode(div like (span like (title isEmpty, rel is "newChild"), "Hello"))
        titleCounter shouldBe 2
      })
    }
    promise.future
  }
}
