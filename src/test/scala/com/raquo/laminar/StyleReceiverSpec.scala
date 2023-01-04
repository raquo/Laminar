package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec

class StyleReceiverSpec extends UnitSpec {

  it("updates style") {
    val display1 = display.none.value
    val display2 = display.inlineFlex.value
    val display3 = display.table.value
    val displayBus = new EventBus[String]

    mount(span(display <-- displayBus.events, "Hello"))
    expectNode(span.of(display is "", "Hello"))

    displayBus.writer.onNext(display1)
    expectNode(span.of(display is display1, "Hello"))

    displayBus.writer.onNext(display2)
    expectNode(span.of(display is display2, "Hello"))

    unmount()
    mount(span(alt := "unrelated"))
    expectNode(span.of(alt is "unrelated"))

    displayBus.writer.onNext(display3)
    expectNode(span.of(alt is "unrelated"))

  }

  it("updates length style [string]") {
    val height100px = 100
    val height200px = 200
    val height300px = 300

    val heightBus = new EventBus[Int]

    mount(div(height <-- heightBus.events.map(style.px), "Yo"))
    expectNode(div.of(height is "", "Yo"))

    heightBus.emit(height100px)
    expectNode(div.of(height is "100px", "Yo"))

    heightBus.emit(height200px)
    expectNode(div.of(height is "200px", "Yo"))

    heightBus.emit(height300px)
    expectNode(div.of(height is style.px(height300px), "Yo"))
  }

  it("updates length style [int]") {
    val height100px = 100
    val height200px = 200
    val height300px = 300

    val heightBus = new EventBus[Int]

    mount(div(height.px <-- heightBus.events, "Yo"))
    expectNode(div.of(height is "", "Yo"))

    heightBus.emit(height100px)
    expectNode(div.of(height is "100px", "Yo"))

    heightBus.emit(height200px)
    expectNode(div.of(height is "200px", "Yo"))

    heightBus.emit(height300px)
    expectNode(div.of(height is style.px(height300px), "Yo"))

  }

  it("updates length style [double]") {
    val height100px = 100.0
    val height200px = 200.0
    val height300px = 300.0

    val heightBus = new EventBus[Double]

    mount(div(height.px <-- heightBus.events, "Yo"))
    expectNode(div.of(height is "", "Yo"))

    heightBus.emit(height100px)
    expectNode(div.of(height is "100px", "Yo"))

    heightBus.emit(height200px)
    expectNode(div.of(height is "200px", "Yo"))

    heightBus.emit(height300px)
    expectNode(div.of(height is style.px(height300px), "Yo"))
  }

  it("updates calc style") {
    val height100 = "100px"
    val height200 = style.em(200)
    val height300 = style.percent(300)

    val heightBus = new EventBus[String]

    mount(div(height.calc <-- heightBus.events, "Yo"))
    expectNode(div.of(height is "", "Yo"))

    heightBus.emit(height100)
    expectNode(div.of(height is "calc(100px)", "Yo"))

    heightBus.emit(height200)
    expectNode(div.of(height is "calc(200em)", "Yo"))

    heightBus.emit(height300)
    expectNode(div.of(height is style.calc(height300), "Yo"))
  }

  it("updates url style") {
    val url1 = "/example.png"
    val url2 = "https://example.com"
    val url3 = "foo.jpg"

    val urlBus = new EventBus[String]

    // #TODO[Test] jsdom seems to behave differently from browsers when
    //  it comes to URLs (especially those with special characters that
    //  need escaping), so I can't test this fully here

    mount(div(backgroundImage.url <-- urlBus.events, "Yo"))
    expectNode(div.of(backgroundImage is "", "Yo"))

    urlBus.emit(url1)
    expectNode(div.of(backgroundImage is "url(/example.png)", "Yo"))

    urlBus.emit(url2)
    expectNode(div.of(backgroundImage is "url(https://example.com)", "Yo"))

    urlBus.emit(url3)
    expectNode(div.of(backgroundImage is "url(foo.jpg)", "Yo"))
  }

}
