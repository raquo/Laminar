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
  }

//  it("updates attr with multiple streams on same attr") {
//    // @TODO This isn't really a published feature, should we really test for it?
//  }

  it("updates attr with memory stream") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")
    val $title = XStream.create[String]().startWith(title1)
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
  }

  it("supports multiple attr receivers on same node") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")
    val title5 = randomString("title5_")
    val title6 = randomString("title6_")
    val $title = XStream.create[String]()
    val $writeableTitle = new ShamefulStream($title)

    val rel1 = randomString("rel1_")
    val rel2 = randomString("rel2_")
    val rel3 = randomString("rel3_")
    val rel4 = randomString("rel4_")
    val rel5 = randomString("rel5_")
    val $rel = XStream.create[String]()
    val $writeableRel = new ShamefulStream($rel)

    mount(div(title <-- $title, rel <-- $rel, "Hello"))
    expectElement(div like "Hello")

    $writeableTitle.shamefullySendNext(title1)
    expectElement(div like (title is title1, "Hello"))

    $writeableRel.shamefullySendNext(rel1)
    expectElement(div like (title is title1, rel is rel1, "Hello"))

    $writeableTitle.shamefullySendNext(title2)
    expectElement(div like (title is title2, rel is rel1, "Hello"))

    $writeableTitle.shamefullySendNext(title3)
    expectElement(div like (title is title3, rel is rel1, "Hello"))

    $writeableRel.shamefullySendNext(rel2)
    expectElement(div like (title is title3, rel is rel2, "Hello"))

    $writeableTitle.shamefullySendNext(title4)
    expectElement(div like (title is title4, rel is rel2, "Hello"))

    $writeableRel.shamefullySendNext(rel3)
    expectElement(div like (title is title4, rel is rel3, "Hello"))

    $writeableRel.shamefullySendNext(rel4)
    expectElement(div like (title is title4, rel is rel4, "Hello"))

    $writeableTitle.shamefullySendNext(title5)
    expectElement(div like (title is title5, rel is rel4, "Hello"))

    patchMounted(div(cls := "unrelated"))
    expectElement(div like (cls is "unrelated"))

    $writeableTitle.shamefullySendNext(title6)
    expectElement(div like (cls is "unrelated"))

    $writeableRel.shamefullySendNext(rel5)
    expectElement(div like (cls is "unrelated"))
  }

  it("supports multiple memory streams") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")
    val title5 = randomString("title5_")
    val title6 = randomString("title6_")
    val $title = XStream.create[String]().startWith(title1)
    val $writeableTitle = new ShamefulStream($title)

    val rel1 = randomString("rel1_")
    val rel2 = randomString("rel2_")
    val rel3 = randomString("rel3_")
    val rel4 = randomString("rel4_")
    val rel5 = randomString("rel5_")
    val $rel = XStream.create[String]().startWith(rel1)
    val $writeableRel = new ShamefulStream($rel)

    mount(div(title <-- $title, rel <-- $rel, "Hello"))
    expectElement(div like (title is title1, rel is rel1, "Hello"))

    $writeableTitle.shamefullySendNext(title2)
    expectElement(div like (title is title2, rel is rel1, "Hello"))

    $writeableTitle.shamefullySendNext(title3)
    expectElement(div like (title is title3, rel is rel1, "Hello"))

    $writeableRel.shamefullySendNext(rel2)
    expectElement(div like (title is title3, rel is rel2, "Hello"))

    $writeableTitle.shamefullySendNext(title4)
    expectElement(div like (title is title4, rel is rel2, "Hello"))

    $writeableRel.shamefullySendNext(rel3)
    expectElement(div like (title is title4, rel is rel3, "Hello"))

    $writeableRel.shamefullySendNext(rel4)
    expectElement(div like (title is title4, rel is rel4, "Hello"))

    $writeableTitle.shamefullySendNext(title5)
    expectElement(div like (title is title5, rel is rel4, "Hello"))

    patchMounted(div(cls := "unrelated"))
    expectElement(div like (cls is "unrelated"))

    $writeableTitle.shamefullySendNext(title6)
    expectElement(div like (cls is "unrelated"))

    $writeableRel.shamefullySendNext(rel5)
    expectElement(div like (cls is "unrelated"))
  }

  it("works with child receiver on same node") {

  }

  it("works with changing vnode") {

  }
}
