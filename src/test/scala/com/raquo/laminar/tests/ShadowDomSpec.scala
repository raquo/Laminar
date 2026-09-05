package com.raquo.laminar.tests

import com.raquo.laminar.api.L._
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom
import org.scalajs.dom.{ShadowRootInit, ShadowRootMode}

import scala.scalajs.js

class ShadowDomSpec extends UnitSpec {

  // @TODO This would be nicer if someone contributed full Shadow DOM support (e.g. .host field) to scala-js-dom
  //  - https://github.com/scala-js/scala-js-dom/issues/191

  it("Laminar can render into shadow root") {

    val modes = List(
      dom.ShadowRootMode.open,
      dom.ShadowRootMode.closed,
    )

    modes.foreach { _mode =>
      withClue(s"MODE=${_mode}") {
        val parentDiv = div().ref

        val shadowRoot = parentDiv.attachShadow(new ShadowRootInit {
          var mode: ShadowRootMode = _mode
        })

        val childDiv = div().ref
        shadowRoot.appendChild(childDiv)

        assert(shadowRoot.parentNode == null)
        assert(shadowRoot.asInstanceOf[js.Dynamic].host.asInstanceOf[Any] == parentDiv)

        assert(DomApi.raw.isDescendantOf(childDiv, parentDiv))
        assert(DomApi.raw.isDescendantOf(childDiv, shadowRoot))
        assert(!DomApi.raw.isDescendantOf(childDiv, dom.document))

        // --

        dom.document.body.appendChild(parentDiv)

        assert(DomApi.raw.isDescendantOf(childDiv, dom.document))

        // --

        val bus = new EventBus[String]

        val app = div(
          "Hello, ",
          span(text <-- bus.events)
        )

        val laminarRoot = render(container = childDiv, app)

        assert(app.ref.parentNode == childDiv)
        assert(DomApi.raw.isDescendantOf(app.ref, parentDiv))
        assert(DomApi.raw.isDescendantOf(app.ref, shadowRoot))
        assert(DomApi.raw.isDescendantOf(app.ref, dom.document))

        expectNode(
          app.ref,
          div.of(
            "Hello, ",
            span.of(sentinel)
          )
        )

        // --

        bus.writer.onNext("world")

        expectNode(
          app.ref,
          div.of(
            "Hello, ",
            span.of("world")
          )
        )

        // --

        bus.writer.onNext("you")

        expectNode(
          app.ref,
          div.of(
            "Hello, ",
            span.of("you")
          )
        )

        // --

        dom.document.body.removeChild(parentDiv)

        assert(DomApi.raw.isDescendantOf(app.ref, parentDiv))
        assert(DomApi.raw.isDescendantOf(app.ref, shadowRoot))
        assert(!DomApi.raw.isDescendantOf(app.ref, dom.document))

        // --

        laminarRoot.unmount()

        assert(app.ref.parentNode == null)
        assert(!DomApi.raw.isDescendantOf(app.ref, parentDiv))
        assert(!DomApi.raw.isDescendantOf(app.ref, shadowRoot))
        assert(!DomApi.raw.isDescendantOf(app.ref, dom.document))
      }
    }
  }

  it("Laminar can render directly into a shadow root (no wrapper element)") {

    val modes = List(
      dom.ShadowRootMode.open,
      dom.ShadowRootMode.closed
    )

    modes.foreach { shadowMode =>
      withClue(s"MODE=${shadowMode}") {
        val host = div()

        val shadowRoot: dom.ShadowRoot = host.ref.attachShadow(
          new dom.ShadowRootInit { var mode: dom.ShadowRootMode = shadowMode }
        )

        dom.document.body.appendChild(host.ref)

        // The shadow root itself is a `dom.ShadowRoot` (a `DocumentFragment`,
        // not a `dom.Element`), and `render` accepts it directly.
        val bus = new EventBus[String]
        val app = div("Hello, ", span(text <-- bus.events))

        val laminarRoot = render(container = shadowRoot, app)

        assert(app.ref.parentNode == shadowRoot)
        assert(laminarRoot.ref eq shadowRoot)
        assert(DomApi.raw.isDescendantOf(app.ref, shadowRoot))
        assert(DomApi.raw.isDescendantOf(app.ref, host.ref))
        assert(DomApi.raw.isDescendantOf(app.ref, dom.document))

        expectNode(
          app.ref,
          div.of("Hello, ", span.of(sentinel))
        )

        // --

        bus.writer.onNext("world")

        expectNode(
          app.ref,
          div.of("Hello, ", span.of("world"))
        )

        // --

        laminarRoot.unmount()

        assert(app.ref.parentNode == null)
        assert(!DomApi.raw.isDescendantOf(app.ref, shadowRoot))

        dom.document.body.removeChild(host.ref)
      }
    }
  }
}
