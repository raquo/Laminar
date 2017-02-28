package com.raquo.laminar

import com.raquo.laminar.utils.UnitSpec
import com.raquo.snabbdom.{Child, Modifier, VNode, attrs}
import com.raquo.snabbdom.attrs._
import com.raquo.snabbdom.nodes.TextNode
import com.raquo.snabbdom.setters.{Attr, Key, Setter}
import com.raquo.snabbdom.tags._
import com.raquo.xstream.{ShamefulStream, XStream}
import org.scalajs.dom

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.|

class AttrReceiverSpec extends UnitSpec {

  it("updates attr") {
    val $title = XStream.create[String]()
    mount(div(title <-- $title, "Hello"))

    expectElement(div like (title isEmpty, "Hello"))

    val $writeableTitle = new ShamefulStream($title)

    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")

    $writeableTitle.shamefullySendNext(title1)
    expectElement(div like (title is title1, "Hello"))

    $writeableTitle.shamefullySendNext(title2)
    expectElement(div like (title is title2, "Hello"))

    mount(div())

    $writeableTitle.shamefullySendNext(title3)
  }

  it("updates attr with memory stream") {

  }

  it("works with another attr receiver on same node") {

  }

  it("works with child receiver on same node") {

  }

  it("works with changing vnode") {

  }

  it("unsubscribes from the stream after node was destroyed") {

    var titleCounter = 0
    val $title = XStream.create[String]()
    val $titleWithDebugger = $title.debugWithSpy(title => {
      titleCounter += 1
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

    mount(div())

    $writeableTitle.shamefullySendNext(title3)
    titleCounter shouldBe 2  // @TODO this does not work. Figure out why. Probably has to do with ShamefulStuff. See if we can use a producer instead
  }
}
