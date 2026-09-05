package com.raquo.laminar.fixtures

import com.raquo.airstream.core.BaseObservable.numAllObservers
import com.raquo.laminar.codecs.Codec
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportStatic
import com.raquo.laminar.api.L._

object AuthoredLabel {

  // Note: @JSExportStatic vals must be defined before any other val/var.
  @JSExportStatic
  val observedAttributes: js.Array[String] = js.Array("label")

  val tagName = "authored-label"

  /** The component's public `label` attribute, exposed as a Laminar key so it
    * can be set reactively: `tag(label <-- someSource)`.
    */
  val label: HtmlAttr[String] = htmlAttr("label", Codec.stringAsIs)

  var isRegistered = false
}

/** A minimal custom element whose shadow DOM is rendered and managed by Laminar.
  *
  * This is a Scala.js-defined JS class extending the native `dom.HTMLElement`,
  * so it can be registered via `customElements.define`.
  */
class AuthoredLabel extends dom.HTMLElement {

  private val labelVar = Var("")

  // Build the Laminar subtree once, up front. It is activated / deactivated
  // in the connected / disconnected reactions below.
  val root: DetachedRoot[Div] = renderDetached(
    div(text <-- labelVar),
    activateNow = false
  )

  // The shadow root is created once, in the constructor. Rendering happens
  // directly into it – no wrapper element needed.
  private val shadow: dom.ShadowRoot = this.attachShadow(
    new dom.ShadowRootInit { var mode: dom.ShadowRootMode = dom.ShadowRootMode.open }
  )

  shadow.appendChild(root.ref)

  def connectedCallback(): Unit = {
    root.activate()
  }

  def disconnectedCallback(): Unit = {
    root.deactivate()
  }

  def attributeChangedCallback(name: String, oldValue: String, newValue: String): Unit = {
    if (name == "label") {
      labelVar.set(if (newValue == null) "" else newValue)
    }
  }

  /** Test-only: number of observers on the internal label signal, used to
    * assert that subscriptions are torn down (no leaks) on disconnect.
    */
  def labelObserverCount: Int = numAllObservers(labelVar.signal)
}
