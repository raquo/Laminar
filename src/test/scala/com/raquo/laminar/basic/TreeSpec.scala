package com.raquo.laminar.basic

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.{ChildNode, ParentNode}
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

class TreeSpec extends UnitSpec {

  it("ChildNode.isDescendantOf") {

    val container1 = div(alt := "foo").ref
    val otherContainer = div(alt := "foo2").ref

    // Root needs to be attached to a mounted element
    dom.document.body.appendChild(container1)
    dom.document.body.appendChild(otherContainer)

    val el0 = span("el0")
    val el10 = span("el10")
    val el11 = span("el11")
    val el2 = articleTag("el2")
    val el3 = L.a("el3", href := "http://example.com")
    val otherEl = div("otherEl")

    val rootNode = render(container1, el0)
    val otherRootNode = render(otherContainer, otherEl)

    val elx = b("elx")

    ChildNode.isDescendantOf(node = el0.ref, ancestor = rootNode.ref) shouldBe true
    ChildNode.isDescendantOf(node = el0.ref, ancestor = el10.ref) shouldBe false
    ChildNode.isDescendantOf(node = el10.ref, ancestor = rootNode.ref) shouldBe false
    ChildNode.isDescendantOf(node = el11.ref, ancestor = el0.ref) shouldBe false
    ChildNode.isDescendantOf(node = el11.ref, ancestor = el11.ref) shouldBe false
    ChildNode.isDescendantOf(node = el0.ref, ancestor = otherRootNode.ref) shouldBe false
    ChildNode.isDescendantOf(node = el0.ref, ancestor = otherEl.ref) shouldBe false

    ParentNode.appendChild(parent = el0, child = el10)
    ParentNode.appendChild(parent = el0, child = el11)

    ChildNode.isDescendantOf(node = el10.ref, ancestor = rootNode.ref) shouldBe true
    ChildNode.isDescendantOf(node = el11.ref, ancestor = rootNode.ref) shouldBe true
    ChildNode.isDescendantOf(node = el10.ref, ancestor = el0.ref) shouldBe true
    ChildNode.isDescendantOf(node = el11.ref, ancestor = el0.ref) shouldBe true
    ChildNode.isDescendantOf(node = el11.ref, ancestor = el10.ref) shouldBe false
    ChildNode.isDescendantOf(node = el11.ref, ancestor = el2.ref) shouldBe false
    ChildNode.isDescendantOf(node = el11.ref, ancestor = otherRootNode.ref) shouldBe false
    ChildNode.isDescendantOf(node = el11.ref, ancestor = otherEl.ref) shouldBe false

    ParentNode.appendChild(parent = el10, child = el2)

    ChildNode.isDescendantOf(node = el2.ref, rootNode.ref) shouldBe true
    ChildNode.isDescendantOf(node = el2.ref, el0.ref) shouldBe true
    ChildNode.isDescendantOf(node = el2.ref, el10.ref) shouldBe true
    ChildNode.isDescendantOf(node = el2.ref, el11.ref) shouldBe false
    ChildNode.isDescendantOf(node = el2.ref, el2.ref) shouldBe false
    ChildNode.isDescendantOf(node = el2.ref, otherRootNode.ref) shouldBe false
    ChildNode.isDescendantOf(node = el2.ref, otherEl.ref) shouldBe false

    ParentNode.appendChild(parent = el2, child = el3)

    ChildNode.isDescendantOf(node = el3.ref, ancestor = rootNode.ref) shouldBe true
    ChildNode.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe true
    ChildNode.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe true
    ChildNode.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = otherRootNode.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false

    ParentNode.insertChild(parent = elx, child = el3, atIndex = 0)

    ChildNode.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe true
    ChildNode.isDescendantOf(node = el3.ref, ancestor = rootNode.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = otherRootNode.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false

    ParentNode.insertChild(parent = el10, child = el3, atIndex = 0)

    ChildNode.isDescendantOf(node = el3.ref, ancestor = rootNode.ref) shouldBe true
    ChildNode.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe true
    ChildNode.isDescendantOf(node = el3.ref, ancestor = el10.ref) shouldBe true
    ChildNode.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = otherRootNode.ref) shouldBe false
    ChildNode.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false
  }

  it("ChildNode.isNodeMounted") {

    val el0 = span("el0")
    val el10 = span("el10")
    val el2 = articleTag("el2")

    ChildNode.isNodeMounted(node = el0.ref) shouldBe false
    ChildNode.isNodeMounted(node = el10.ref) shouldBe false
    ChildNode.isNodeMounted(node = el2.ref) shouldBe false

    ParentNode.appendChild(parent = el0, child = el10)

    ChildNode.isNodeMounted(node = el10.ref) shouldBe false

    mount(el0)

    ChildNode.isNodeMounted(node = el0.ref) shouldBe true
    ChildNode.isNodeMounted(node = el10.ref) shouldBe true
    ChildNode.isNodeMounted(node = el2.ref) shouldBe false

    unmount()
    mount(el2)

    ChildNode.isNodeMounted(node = el0.ref) shouldBe false
    ChildNode.isNodeMounted(node = el10.ref) shouldBe false
    ChildNode.isNodeMounted(node = el2.ref) shouldBe true

    ParentNode.appendChild(parent = el2, child = el0)

    ChildNode.isNodeMounted(node = el0.ref) shouldBe true
    ChildNode.isNodeMounted(node = el10.ref) shouldBe true
    ChildNode.isNodeMounted(node = el2.ref) shouldBe true
  }

}
