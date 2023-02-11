package com.raquo.laminar

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ChildNode
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.scalajs.js

class ShadowDomSpec extends UnitSpec {

  // @TODO This would be nicer if someone contributed Shadow DOM support to scala-js-dom
  //  - https://github.com/scala-js/scala-js-dom/issues/191

  it("Laminar can render into shadow root") {

    val modes = List("open", "closed")

    modes.foreach { mode =>
      withClue(s"MODE=${mode}") {
        val parentDiv = div().ref

        val parentDivDyn = parentDiv.asInstanceOf[js.Dynamic]
        val shadowRoot = parentDivDyn.attachShadow(js.Dynamic.literal("mode" -> mode)).asInstanceOf[dom.Node]

        val childDiv = div().ref
        shadowRoot.appendChild(childDiv)

        assert(shadowRoot.parentNode == null)
        assert(shadowRoot.asInstanceOf[js.Dynamic].host.asInstanceOf[Any] == parentDiv)

        assert(ChildNode.isDescendantOf(childDiv, parentDiv))
        assert(ChildNode.isDescendantOf(childDiv, shadowRoot))
        assert(!ChildNode.isDescendantOf(childDiv, dom.document))

        // --

        dom.document.body.appendChild(parentDiv)

        assert(ChildNode.isDescendantOf(childDiv, dom.document))

        // --

        val bus = new EventBus[String]

        val app = div(
          "Hello, ",
          span(child.text <-- bus.events)
        )

        val laminarRoot = render(container = childDiv, app)

        assert(app.ref.parentNode == childDiv)
        assert(ChildNode.isDescendantOf(app.ref, parentDiv))
        assert(ChildNode.isDescendantOf(app.ref, shadowRoot))
        assert(ChildNode.isDescendantOf(app.ref, dom.document))

        expectNode(app.ref, div.of(
          "Hello, ",
          span.of(sentinel))
        )

        // --

        bus.writer.onNext("world")

        expectNode(app.ref, div.of(
          "Hello, ",
          span.of("world")
        ))

        // --

        bus.writer.onNext("you")

        expectNode(app.ref, div.of(
          "Hello, ",
          span.of("you")
        ))

        // --

        dom.document.body.removeChild(parentDiv)

        assert(ChildNode.isDescendantOf(app.ref, parentDiv))
        assert(ChildNode.isDescendantOf(app.ref, shadowRoot))
        assert(!ChildNode.isDescendantOf(app.ref, dom.document))

        // --

        laminarRoot.unmount()

        assert(app.ref.parentNode == null)
        assert(!ChildNode.isDescendantOf(app.ref, parentDiv))
        assert(!ChildNode.isDescendantOf(app.ref, shadowRoot))
        assert(!ChildNode.isDescendantOf(app.ref, dom.document))
      }
    }
  }
}
