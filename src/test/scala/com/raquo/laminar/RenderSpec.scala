package com.raquo.laminar

import com.raquo.snabbdom.tags.div
import com.raquo.laminar
import com.raquo.snabbdom.VNode
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalatest.{BeforeAndAfterEach, Suite}

trait RenderSpec extends BeforeAndAfterEach { this: Suite =>

  var container: Element = null

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetDocument()
  }

  override def afterEach(): Unit = {
    super.afterEach()
    clearDocument()
  }

  def clearDocument(): Unit = {
    removeAllChildren(dom.document.body)
  }

  def resetDocument(): Unit = {
    clearDocument()
    container = createContainer()
    dom.document.body.appendChild(container)
  }

  def mountedElement(): Element = {
    val numChildren = container.childNodes.length
    assert(
      numChildren == 1,
      s"mountedElement assert fail: container must have exactly 1 child, $numChildren found"
    )
    assert(
      container.firstChild.isInstanceOf[Element],
      s"mountedElement assert fail: mounted node is not an Element"
    )
    container.firstChild.asInstanceOf[Element]
  }

  def mount(vNode: VNode): Unit = {
    assert(
      container != null && container.parentNode == dom.document.body,
      "MOUNT ASSERT FAIL: Container is null or not mounted to <body> (what did you do!?)"
    )
    assert(
      container.firstChild == null, "MOUNT ASSERT FAIL: Unexpected children in container. Call unmount() before mounting again."
    )

    val entry = dom.document.createElement("div")
    entry.setAttribute("id", "laminar-entry")

    container.appendChild(entry)

    laminar.render(entry, vNode)
  }

  def unmount(): Unit = {
    // @TODO Are we properly unmounting snabbdom?
    mount(div())
    removeAllChildren(container)
  }

  def simulateClick(target: Element): Unit = {
    // @TODO Make a simple simulator package
    // @TODO This should be more compatible and more configurable
    // @see if we can use https://github.com/Rich-Harris/simulant
    val evt = dom.document.createEvent("HTMLEvents")
    evt.initEvent("click", canBubbleArg = true, cancelableArg = true)
    target.dispatchEvent(evt)
  }

  private def createContainer(): Element = {
    val container = dom.document.createElement("div")
    container.setAttribute("id", "laminar-container")
    container
  }

  private def removeAllChildren(node: Element): Unit = {
    while (node.firstChild != null) {
      node.removeChild(node.firstChild)
    }
  }
}
