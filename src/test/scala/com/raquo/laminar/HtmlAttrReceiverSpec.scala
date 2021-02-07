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
    expectNode(span.of(title.isEmpty, "Hello"))

    titleBus.writer.onNext(title1)
    expectNode(span.of(title is title1, "Hello"))

    titleBus.writer.onNext(title2)
    expectNode(span.of(title is title2, "Hello"))

    unmount()
    mount(span(alt := "unrelated"))
    expectNode(span.of(alt is "unrelated"))

    titleBus.writer.onNext(title3)
    expectNode(span.of(alt is "unrelated"))
  }

  it("updates attr with Signal") {
    val title1 = randomString("title1_")
    val title2 = randomString("title2_")
    val title3 = randomString("title3_")
    val title4 = randomString("title4_")

    val titleBus = new EventBus[String]

    mount(div(title <-- titleBus.events.toSignal(title1), "Hello"))
    expectNode(div.of(title is title1, "Hello"))

    titleBus.writer.onNext(title2)
    expectNode(div.of(title is title2, "Hello"))

    titleBus.writer.onNext(title3)
    expectNode(div.of(title is title3, "Hello"))

    unmount()
    mount(div(alt := "unrelated"))
    expectNode(div.of(alt is "unrelated"))

    titleBus.writer.onNext(title4)
    expectNode(div.of(alt is "unrelated"))
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
    expectNode(div of "Hello")

    titleBus.writer.onNext(title1)
    expectNode(div.of(title is title1, "Hello"))

    altBus.writer.onNext(alt1)
    expectNode(div.of(title is title1, alt is alt1, "Hello"))

    titleBus.writer.onNext(title2)
    expectNode(div.of(title is title2, alt is alt1, "Hello"))

    titleBus.writer.onNext(title3)
    expectNode(div.of(title is title3, alt is alt1, "Hello"))

    altBus.writer.onNext(alt2)
    expectNode(div.of(title is title3, alt is alt2, "Hello"))

    titleBus.writer.onNext(title4)
    expectNode(div.of(title is title4, alt is alt2, "Hello"))

    altBus.writer.onNext(alt3)
    expectNode(div.of(title is title4, alt is alt3, "Hello"))

    altBus.writer.onNext(alt4)
    expectNode(div.of(title is title4, alt is alt4, "Hello"))

    titleBus.writer.onNext(title5)
    expectNode(div.of(title is title5, alt is alt4, "Hello"))

    unmount()
    mount(div(idAttr := "unrelated"))
    expectNode(div.of(idAttr is "unrelated"))

    titleBus.writer.onNext(title6)
    expectNode(div.of(idAttr is "unrelated"))

    altBus.writer.onNext(alt5)
    expectNode(div.of(idAttr is "unrelated"))
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
    expectNode(div.of(title is title1, alt is alt1, "Hello"))

    titleBus.writer.onNext(title2)
    expectNode(div.of(title is title2, alt is alt1, "Hello"))

    titleBus.writer.onNext(title3)
    expectNode(div.of(title is title3, alt is alt1, "Hello"))

    altBus.writer.onNext(alt2)
    expectNode(div.of(title is title3, alt is alt2, "Hello"))

    titleBus.writer.onNext(title4)
    expectNode(div.of(title is title4, alt is alt2, "Hello"))

    altBus.writer.onNext(alt3)
    expectNode(div.of(title is title4, alt is alt3, "Hello"))

    altBus.writer.onNext(alt4)
    expectNode(div.of(title is title4, alt is alt4, "Hello"))

    titleBus.writer.onNext(title5)
    expectNode(div.of(title is title5, alt is alt4, "Hello"))

    unmount()
    mount(div(href := "unrelated"))
    expectNode(div.of(href is "unrelated"))

    titleBus.writer.onNext(title6)
    expectNode(div.of(href is "unrelated"))

    altBus.writer.onNext(alt5)
    expectNode(div.of(href is "unrelated"))
  }

  it("updates two attrs subscribed to the same stream") {
    val value1 = randomString("title1_")
    val value2 = randomString("title2_")
    val value3 = randomString("title3_")

    val valueBus = new EventBus[String]

    mount(div(title <-- valueBus.events, alt <-- valueBus.events, "Hello"))
    expectNode(div.of(title.isEmpty, alt.isEmpty, "Hello"))

    valueBus.writer.onNext(value1)
    expectNode(div.of(title is value1, alt is value1, "Hello"))

    valueBus.writer.onNext(value2)
    expectNode(div.of(title is value2, alt is value2, "Hello"))

    unmount()
    mount(div(href := "unrelated"))
    expectNode(div.of(href is "unrelated"))

    valueBus.writer.onNext(value3)
    expectNode(div.of(href is "unrelated"))
  }

  it("supports attr(value) syntax") {
    mount(div(title("Hello"), tabIndex(5)))
    expectNode(div.of(title is "Hello", tabIndex is 5))
  }
}
