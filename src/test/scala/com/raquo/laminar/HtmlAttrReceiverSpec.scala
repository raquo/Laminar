package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class HtmlAttrReceiverSpec extends UnitSpec {

  it("updates attr") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val titleBus = new EventBus[String]

    mount(span(title <-- titleBus.events, "Hello"))
    expectNode(span like (title.isEmpty, "Hello"))

    titleBus.writer.onNext(title1)
    expectNode(span like (title is title1, "Hello"))

    titleBus.writer.onNext(title2)
    expectNode(span like (title is title2, "Hello"))

    unmount()
    mount(span(alt := "unrelated"))
    expectNode(span like (alt is "unrelated"))

    titleBus.writer.onNext(title3)
    expectNode(span like (alt is "unrelated"))
  }

  it("updates attr with Signal") {
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
    mount(div(alt := "unrelated"))
    expectNode(div like (alt is "unrelated"))

    titleBus.writer.onNext(title4)
    expectNode(div like (alt is "unrelated"))
  }

  it("supports multiple attr receivers on same node") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")
    val title5 = randomString("title5_")
    val title6 = randomString("title6_")

    val titleBus = new EventBus[String]

    val alt1 = randomString("alt1_")
    val alt2 = randomString("alt2_")
    val alt3 = randomString("alt3_")
    val alt4 = randomString("alt4_")
    val alt5 = randomString("alt5_")

    val altBus = new EventBus[String]

    mount(div(title <-- titleBus.events, alt <-- altBus.events, "Hello"))
    expectNode(div like "Hello")

    titleBus.writer.onNext(title1)
    expectNode(div like (title is title1, "Hello"))

    altBus.writer.onNext(alt1)
    expectNode(div like (title is title1, alt is alt1, "Hello"))

    titleBus.writer.onNext(title2)
    expectNode(div like (title is title2, alt is alt1, "Hello"))

    titleBus.writer.onNext(title3)
    expectNode(div like (title is title3, alt is alt1, "Hello"))

    altBus.writer.onNext(alt2)
    expectNode(div like (title is title3, alt is alt2, "Hello"))

    titleBus.writer.onNext(title4)
    expectNode(div like (title is title4, alt is alt2, "Hello"))

    altBus.writer.onNext(alt3)
    expectNode(div like (title is title4, alt is alt3, "Hello"))

    altBus.writer.onNext(alt4)
    expectNode(div like (title is title4, alt is alt4, "Hello"))

    titleBus.writer.onNext(title5)
    expectNode(div like (title is title5, alt is alt4, "Hello"))

    unmount()
    mount(div(id := "unrelated"))
    expectNode(div like (id is "unrelated"))

    titleBus.writer.onNext(title6)
    expectNode(div like (id is "unrelated"))

    altBus.writer.onNext(alt5)
    expectNode(div like (id is "unrelated"))
  }

  it("supports multiple signals") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")
    val title5 = randomString("title5_")
    val title6 = randomString("title6_")

    val titleBus = new EventBus[String]

    val alt1 = randomString("alt1_")
    val alt2 = randomString("alt2_")
    val alt3 = randomString("alt3_")
    val alt4 = randomString("alt4_")
    val alt5 = randomString("alt5_")

    val altBus = new EventBus[String]

    mount(div(title <-- titleBus.events.toSignal(title1), alt <-- altBus.events.toSignal(alt1), "Hello"))
    expectNode(div like (title is title1, alt is alt1, "Hello"))

    titleBus.writer.onNext(title2)
    expectNode(div like (title is title2, alt is alt1, "Hello"))

    titleBus.writer.onNext(title3)
    expectNode(div like (title is title3, alt is alt1, "Hello"))

    altBus.writer.onNext(alt2)
    expectNode(div like (title is title3, alt is alt2, "Hello"))

    titleBus.writer.onNext(title4)
    expectNode(div like (title is title4, alt is alt2, "Hello"))

    altBus.writer.onNext(alt3)
    expectNode(div like (title is title4, alt is alt3, "Hello"))

    altBus.writer.onNext(alt4)
    expectNode(div like (title is title4, alt is alt4, "Hello"))

    titleBus.writer.onNext(title5)
    expectNode(div like (title is title5, alt is alt4, "Hello"))

    unmount()
    mount(div(href := "unrelated"))
    expectNode(div like (href is "unrelated"))

    titleBus.writer.onNext(title6)
    expectNode(div like (href is "unrelated"))

    altBus.writer.onNext(alt5)
    expectNode(div like (href is "unrelated"))
  }

  it("updates two attrs subscribed to the same stream") {
    val value1 = randomString("title1_")
    val value2 = randomString("title2_")
    val value3 = randomString("title3_")

    val valueBus = new EventBus[String]

    mount(div(title <-- valueBus.events, alt <-- valueBus.events, "Hello"))
    expectNode(div like (title.isEmpty, alt.isEmpty, "Hello"))

    valueBus.writer.onNext(value1)
    expectNode(div like (title is value1, alt is value1, "Hello"))

    valueBus.writer.onNext(value2)
    expectNode(div like (title is value2, alt is value2, "Hello"))

    unmount()
    mount(div(href := "unrelated"))
    expectNode(div like (href is "unrelated"))

    valueBus.writer.onNext(value3)
    expectNode(div like (href is "unrelated"))
  }

  it("supports attr(value) syntax") {
    mount(div(title("Hello"), tabIndex(5)))
    expectNode(div like (title is "Hello", tabIndex is 5))
  }
}
