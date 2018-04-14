package com.raquo.laminar

import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class AttrReceiverSpec extends UnitSpec {

  it("updates attr") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val titleBus = new EventBus[String]

    mount(span(title <-- titleBus.events, "Hello"))
    expectNode(span like (title isEmpty, "Hello"))

    titleBus.writer.onNext(title1)
    expectNode(span like (title is title1, "Hello"))

    titleBus.writer.onNext(title2)
    expectNode(span like (title is title2, "Hello"))

    unmount()
    mount(span(className := "unrelated"))
    expectNode(span like (className is "unrelated"))

    titleBus.writer.onNext(title3)
    expectNode(span like (className is "unrelated"))
  }

  it("updates attr with memory stream") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")

    val titleBus = new EventBus[String]

    mount(div(title <-- titleBus.events.toSignal(title1), "Hello"))
    expectNode(div like (title is title1, "Hello"))

    titleBus.writer.onNext(title2)
    expectNode(div like (title is title2, "Hello"))

    titleBus.writer.onNext(title3)
    expectNode(div like (title is title3, "Hello"))

    unmount()
    mount(div(className := "unrelated"))
    expectNode(div like (className is "unrelated"))

    titleBus.writer.onNext(title4)
    expectNode(div like (className is "unrelated"))
  }

  it("supports multiple attr receivers on same node") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")
    val title5 = randomString("title5_")
    val title6 = randomString("title6_")

    val titleBus = new EventBus[String]

    val rel1 = randomString("rel1_")
    val rel2 = randomString("rel2_")
    val rel3 = randomString("rel3_")
    val rel4 = randomString("rel4_")
    val rel5 = randomString("rel5_")

    val relBus = new EventBus[String]

    mount(div(title <-- titleBus.events, rel <-- relBus.events, "Hello"))
    expectNode(div like "Hello")

    titleBus.writer.onNext(title1)
    expectNode(div like (title is title1, "Hello"))

    relBus.writer.onNext(rel1)
    expectNode(div like (title is title1, rel is rel1, "Hello"))

    titleBus.writer.onNext(title2)
    expectNode(div like (title is title2, rel is rel1, "Hello"))

    titleBus.writer.onNext(title3)
    expectNode(div like (title is title3, rel is rel1, "Hello"))

    relBus.writer.onNext(rel2)
    expectNode(div like (title is title3, rel is rel2, "Hello"))

    titleBus.writer.onNext(title4)
    expectNode(div like (title is title4, rel is rel2, "Hello"))

    relBus.writer.onNext(rel3)
    expectNode(div like (title is title4, rel is rel3, "Hello"))

    relBus.writer.onNext(rel4)
    expectNode(div like (title is title4, rel is rel4, "Hello"))

    titleBus.writer.onNext(title5)
    expectNode(div like (title is title5, rel is rel4, "Hello"))

    unmount()
    mount(div(className := "unrelated"))
    expectNode(div like (className is "unrelated"))

    titleBus.writer.onNext(title6)
    expectNode(div like (className is "unrelated"))

    relBus.writer.onNext(rel5)
    expectNode(div like (className is "unrelated"))
  }

  it("supports multiple memory streams") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")
    val title5 = randomString("title5_")
    val title6 = randomString("title6_")

    val titleBus = new EventBus[String]

    val rel1 = randomString("rel1_")
    val rel2 = randomString("rel2_")
    val rel3 = randomString("rel3_")
    val rel4 = randomString("rel4_")
    val rel5 = randomString("rel5_")

    val relBus = new EventBus[String]

    mount(div(title <-- titleBus.events.toSignal(title1), rel <-- relBus.events.toSignal(rel1), "Hello"))
    expectNode(div like (title is title1, rel is rel1, "Hello"))

    titleBus.writer.onNext(title2)
    expectNode(div like (title is title2, rel is rel1, "Hello"))

    titleBus.writer.onNext(title3)
    expectNode(div like (title is title3, rel is rel1, "Hello"))

    relBus.writer.onNext(rel2)
    expectNode(div like (title is title3, rel is rel2, "Hello"))

    titleBus.writer.onNext(title4)
    expectNode(div like (title is title4, rel is rel2, "Hello"))

    relBus.writer.onNext(rel3)
    expectNode(div like (title is title4, rel is rel3, "Hello"))

    relBus.writer.onNext(rel4)
    expectNode(div like (title is title4, rel is rel4, "Hello"))

    titleBus.writer.onNext(title5)
    expectNode(div like (title is title5, rel is rel4, "Hello"))

    unmount()
    mount(div(className := "unrelated"))
    expectNode(div like (className is "unrelated"))

    titleBus.writer.onNext(title6)
    expectNode(div like (className is "unrelated"))

    relBus.writer.onNext(rel5)
    expectNode(div like (className is "unrelated"))
  }

  it("updates two attrs subscribed to the same stream") {
    val value1 = randomString("title1_")
    val value2 = randomString("title2_")
    val value3 = randomString("title3_")

    val valueBus = new EventBus[String]

    mount(div(title <-- valueBus.events, rel <-- valueBus.events, "Hello"))
    expectNode(div like (title isEmpty, rel isEmpty, "Hello"))

    valueBus.writer.onNext(value1)
    expectNode(div like (title is value1, rel is value1, "Hello"))

    valueBus.writer.onNext(value2)
    expectNode(div like (title is value2, rel is value2, "Hello"))

    unmount()
    mount(div(className := "unrelated"))
    expectNode(div like (className is "unrelated"))

    valueBus.writer.onNext(value3)
    expectNode(div like (className is "unrelated"))
  }

  it("works with child receiver on same node") {

  }
}
