package com.raquo.laminar.tests

// Bring in the Laminar DSL, but hide the `label` *tag* so `label` unambiguously
// refers to `AuthoredLabel`'s `label` *attribute* below.
import com.raquo.laminar.api.L.{label => _, _}
import com.raquo.laminar.fixtures.AuthoredLabel
import com.raquo.laminar.fixtures.AuthoredLabel.label
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.tags.CustomHtmlTag
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.scalajs.js

/** Authoring a web component (custom element) whose shadow DOM is rendered and
  * managed by Laminar. See [[AuthoredLabel]] for the component itself: it builds
  * its subtree once with `renderDetached`, and activates / deactivates Laminar's
  * subscriptions in the standard `connectedCallback` / `disconnectedCallback`
  * reactions – so the browser drives the lifecycle, not the test.
  */
class WebComponentSpec extends UnitSpec {

  it("web component lifecycle: subscriptions follow connect/disconnect, no leaks") {

    // A custom element name can only be defined once per JS runtime.
    if (!AuthoredLabel.isRegistered) {
      dom.window.customElements.define(AuthoredLabel.tagName, js.constructorOf[AuthoredLabel])
      AuthoredLabel.isRegistered = true
    }

    // Create the custom element via a Laminar tag, and drive its `label`
    // attribute reactively – exactly as an end user would. `document.createElement`
    // (which the tag uses under the hood) upgrades the registered custom element,
    // so `element.ref` is an actual `AuthoredLabel` instance that we can mount /
    // unmount like any other Laminar element.
    val labelSource = Var("")
    val tag = new CustomHtmlTag[AuthoredLabel](AuthoredLabel.tagName)
    val element = tag(label <-- labelSource.signal)
    val el = element.ref

    // The shadow subtree, rendered and managed by Laminar inside the component.
    def content: Div = el.root.node

    def isMounted: Boolean = ReactiveElement.isActive(content)
    def labelObserverCount: Int = el.labelObserverCount

    // -- Before mounting: subscriptions are not running.

    assertEquals(isMounted, false)
    assertEquals(labelObserverCount, 0)

    // -- Mount: inserting into the document fires connectedCallback, which
    //    activates the Laminar subtree. `labelSource` replays its current value.

    mount(element)

    assertEquals(isMounted, true)
    assertEquals(labelObserverCount, 1)
    expectNode(content.ref, div of (sentinel, ""))

    // -- The `label` attribute flows into the shadow DOM via Laminar.

    labelSource.set("Hello")
    expectNode(content.ref, div of "Hello")

    // -- Unmount: removing from the document fires disconnectedCallback, which
    //    tears the subscriptions down (no leak).

    unmount()

    assertEquals(isMounted, false)
    assertEquals(labelObserverCount, 0)

    // -- While unmounted the `label <-- ...` binding is inactive, so pushing a
    //    new value does not reach the (deactivated) shadow DOM.

    labelSource.set("Ignored while unmounted")
    expectNode(content.ref, div of "Hello")

    // -- Remount: exactly one observer again (no double-subscription leak), and
    //    the shadow DOM catches up to the latest value as the signal replays.

    mount(element)

    assertEquals(isMounted, true)
    assertEquals(labelObserverCount, 1)
    expectNode(content.ref, div of "Ignored while unmounted")

    unmount()

    assertEquals(labelObserverCount, 0)
  }
}
