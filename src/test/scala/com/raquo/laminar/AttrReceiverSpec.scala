package com.raquo.laminar

import com.raquo.laminar.utils.UnitSpec
import com.raquo.laminar.attrs._
import com.raquo.laminar.tags._
import com.raquo.xstream.{MemoryStream, ShamefulStream, XStream}
import org.scalajs.dom

import scala.collection.mutable
import scala.concurrent.Promise
import scala.scalajs.js
import scala.scalajs.js.|

class AttrReceiverSpec extends UnitSpec {

  it("updates attr") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")
    val $title = XStream.create[String]()
    val $writeableTitle = new ShamefulStream($title)

    mount(div(title <-- $title, "Hello"))
    expectElement(div like (title isEmpty, "Hello"))

    $writeableTitle.shamefullySendNext(title1)
    expectElement(div like (title is title1, "Hello"))

    $writeableTitle.shamefullySendNext(title2)
    expectElement(div like (title is title2, "Hello"))

    patchMounted(div(cls := "unrelated"))
    expectElement(div like (cls is "unrelated"))

    $writeableTitle.shamefullySendNext(title3)
    expectElement(div like (cls is "unrelated"))

    $writeableTitle.shamefullySendNext(title4)
    expectElement(div like (cls is "unrelated"))
  }

  it("updates attr with memory stream") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")
    val title5 = randomString("title5_")
    val $title: MemoryStream[String, Nothing] = XStream.create[String]().startWith(title1)
    val $writeableTitle = new ShamefulStream($title)

    mount(div(title <-- $title, "Hello"))
    expectElement(div like (title is title1, "Hello"))

    $writeableTitle.shamefullySendNext(title2)
    expectElement(div like (title is title2, "Hello"))

    $writeableTitle.shamefullySendNext(title3)
    expectElement(div like (title is title3, "Hello"))

    patchMounted(div(cls := "unrelated"))
    expectElement(div like (cls is "unrelated"))

    $writeableTitle.shamefullySendNext(title4)
    expectElement(div like (cls is "unrelated"))

    $writeableTitle.shamefullySendNext(title5)
    expectElement(div like (cls is "unrelated"))
  }

  it("works with another attr receiver on same node") {

  }

  it("works with child receiver on same node") {

  }

  it("works with changing vnode") {

  }
}
