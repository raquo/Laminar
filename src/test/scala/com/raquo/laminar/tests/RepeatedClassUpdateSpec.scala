package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

class RepeatedClassUpdateSpec extends UnitSpec {

  it("creates an empty class attribute on the first empty emission") {
    val updates = EventBus[String]()
    val host = div(cls <-- updates.events)
    mount(host)
    assert(!host.ref.hasAttribute("class"))

    updates.emit("")
    assert(host.ref.hasAttribute("class"))
    assert(host.ref.getAttribute("class") == "")
  }

  it("skips unchanged class writes while preserving external classes and later updates") {
    val updates = EventBus[String]()
    val host = div(cls <-- updates.events)
    mount(host)
    updates.emit("alpha beta")
    host.ref.classList.add("external")
    val observer = dom.MutationObserver((_, _) => ())
    observer.observe(host.ref, dom.MutationObserverInit(attributes = true))

    try {
      withClue("Equivalent classes: ") {
        updates.emit(" beta  alpha ")
        updates.emit("alpha beta")
        expectNode(div.of(cls is "alpha beta external"))
        assert(observer.takeRecords().length == 0)
      }

      withClue("Changed classes: ") {
        updates.emit("gamma")
        expectNode(div.of(cls is "external gamma"))
        assert(observer.takeRecords().length == 1)
      }

      withClue("Remove managed classes: ") {
        updates.emit("")
        expectNode(div.of(cls is "external"))
        assert(observer.takeRecords().length == 1)
      }
    } finally {
      observer.disconnect()
    }
  }
}
