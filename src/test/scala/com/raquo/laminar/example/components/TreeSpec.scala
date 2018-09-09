package com.raquo.laminar.example.components

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveChildNode
import com.raquo.laminar.utils.UnitSpec

class TreeSpec extends UnitSpec {

  it("ReactiveChildNode.isDescendantOf") {

    val container1 = div(alt := "foo").ref
    val otherContainer = div(alt := "foo2").ref

    val el0 = span("el0")
    val el10 = span("el10")
    val el11 = span("el11")
    val el2 = article("el2")
    val el3 = L.a("el3", href := "http://example.com")
    val root = render(container1, el0)

    val otherEl = div("otherEl")
    val otherRoot = render(otherContainer, otherEl)

    val elx = b("elx")

    ReactiveChildNode.isDescendantOf(node = el0.ref, ancestor = root.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el0.ref, ancestor = el10.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el10.ref, ancestor = root.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el11.ref, ancestor = el0.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el11.ref, ancestor = el11.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el0.ref, ancestor = otherRoot.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el0.ref, ancestor = otherEl.ref) shouldBe false

    el0.appendChild(el10)
    el0.appendChild(el11)

    ReactiveChildNode.isDescendantOf(node = el10.ref, ancestor = root.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el11.ref, ancestor = root.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el10.ref, ancestor = el0.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el11.ref, ancestor = el0.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el11.ref, ancestor = el10.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el11.ref, ancestor = el2.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el11.ref, ancestor = otherRoot.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el11.ref, ancestor = otherEl.ref) shouldBe false

    el10.appendChild(el2)

    ReactiveChildNode.isDescendantOf(node = el2.ref, root.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el2.ref, el0.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el2.ref, el10.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el2.ref, el11.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el2.ref, el2.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el2.ref, otherRoot.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el2.ref, otherEl.ref) shouldBe false

    el2.appendChild(el3)

    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = root.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = otherRoot.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false

    elx.insertChild(el3, atIndex = 0)

    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = root.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = otherRoot.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false

    el10.insertChild(el3, atIndex = 0)

    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = root.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = el10.ref) shouldBe true
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = otherRoot.ref) shouldBe false
    ReactiveChildNode.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false
  }

  it("ReactiveChildNode.isMounted") {
    val container1 = div(alt := "foo").ref
    val otherContainer = div(alt := "foo2").ref

    val el0 = span("el0")
    val el10 = span("el10")
    val el2 = article("el2")
    val unmountedRoot = render(container1, el0)

    ReactiveChildNode.isNodeMounted(node = el0.ref) shouldBe false
    ReactiveChildNode.isNodeMounted(node = el10.ref) shouldBe false
    ReactiveChildNode.isNodeMounted(node = el2.ref) shouldBe false

    el0.appendChild(el10)

    ReactiveChildNode.isNodeMounted(node = el10.ref) shouldBe false

    mount(el0)

    ReactiveChildNode.isNodeMounted(node = el0.ref) shouldBe true
    ReactiveChildNode.isNodeMounted(node = el10.ref) shouldBe true
    ReactiveChildNode.isNodeMounted(node = el2.ref) shouldBe false

    unmount()
    mount(el2)

    ReactiveChildNode.isNodeMounted(node = el0.ref) shouldBe false
    ReactiveChildNode.isNodeMounted(node = el10.ref) shouldBe false
    ReactiveChildNode.isNodeMounted(node = el2.ref) shouldBe true

    el2.appendChild(el0)

    ReactiveChildNode.isNodeMounted(node = el0.ref) shouldBe true
    ReactiveChildNode.isNodeMounted(node = el10.ref) shouldBe true
    ReactiveChildNode.isNodeMounted(node = el2.ref) shouldBe true
  }

}
