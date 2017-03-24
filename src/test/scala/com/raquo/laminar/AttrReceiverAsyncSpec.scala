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

  it("unsubscribes from the stream after node was destroyed") {

    var titleCounter = 0
    val $title = XStream.create[String]()
    val $titleWithDebugger = $title.map(title => {
      titleCounter += 1
      title
    })
    mount(div(title <-- $titleWithDebugger, "Hello"))

    expectElement(div like (title isEmpty, "Hello"))

    val $writeableTitle = new ShamefulStream($title)

    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")

    titleCounter shouldBe 0

    $writeableTitle.shamefullySendNext(title1)
    expectElement(div like (title is title1, "Hello"))
    titleCounter shouldBe 1

    $writeableTitle.shamefullySendNext(title2)
    expectElement(div like (title is title2, "Hello"))
    titleCounter shouldBe 2

    patchMounted(div(rel := "unmounted"))

    expectElement(div like (title isEmpty, rel is "unmounted"))

    val promise = Promise[Assertion]()

    js.timers.setTimeout(1) {
      promise.complete(Try{
        $writeableTitle.shamefullySendNext(title3)
        expectElement(div like (title isEmpty, rel is "unmounted"))
        titleCounter shouldBe 2
      })
    }

    promise.future
  }
}
