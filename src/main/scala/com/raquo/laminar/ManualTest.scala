package com.raquo.laminar

import com.raquo.laminar.attrs.{rel, title}
import com.raquo.laminar.tags.div
import com.raquo.snabbdom.utils.testing.UtilSpec
import com.raquo.snabbdom.utils.testing.matching.{MountOps, RuleImplicits}
import com.raquo.xstream.{ShamefulStream, XStream}
import org.scalajs.dom
import org.scalatest.Assertion

import scala.concurrent.Promise
import scala.scalajs.js
import scala.scalajs.js.JavaScriptException
import scala.util.Try

object ManualTest extends MountOps[RNode] with RuleImplicits[RNode] with ReactiveBuilders with UtilSpec {

  override def doAssert(condition: Boolean, message: String): Unit = {
    if (!condition) {
      doFail(message)
    }
  }

  override def doFail(message: String): Nothing = {
    dom.console.log(message)
//    js.debugger()
    throw new JavaScriptException(message)
  }

  def apply(): Unit = {
    resetDocument()


    var titleCounter = 0
    val $title = XStream.create[String]()
    val $titleWithDebugger = $title.map(title => {
      titleCounter += 1
      title
    })
    mount(div(title <-- $titleWithDebugger, "Hello"))

    expectElement(div like(title isEmpty, "Hello"))

    val $writeableTitle = new ShamefulStream($title)

    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")

    doAssert(titleCounter == 0, "titleCounter should be 0")

//    $writeableTitle.shamefullySendNext(title1)
//    expectElement(div like(title is title1, "Hello"))
//    doAssert(titleCounter == 1, "titleCounter should be 1")
//
//    $writeableTitle.shamefullySendNext(title2)
//    expectElement(div like(title is title2, "Hello"))
//    doAssert(titleCounter == 2, "titleCounter should be 2")

    patchMounted(div(rel := "unmounted", "UNMOUNTED"))

    expectElement(div like(title isEmpty, rel is "unmounted", "UNMOUNTED"))

    // @TODO so that patchMounted thing doesn't work!!!!!!!!

    // @TODO ^^^ Is it possible that we're patching a stale node?
    // @TODO Maybe we should be patching the container instead... or adding a hook or something...

    dom.console.log(">>> UNMOUNTED <<<")

    js.timers.setTimeout(10) {
      $writeableTitle.shamefullySendNext(title3)
      expectElement(div like(title isEmpty, rel is "unmounted", "UNMOUNTED"))
      doAssert(titleCounter == 0, "titleCounter should be 0")
    }


  }
}
