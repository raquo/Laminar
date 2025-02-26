package com.raquo.laminar.tests.basic

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.inserters.InserterHooks
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.scalajs.js

class TreeSpec extends UnitSpec {

  val noHooks: js.UndefOr[InserterHooks] = js.undefined

  it("DomApi.raw.isDescendantOf") {

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

    DomApi.raw.isDescendantOf(node = el0.ref, ancestor = rootNode.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el0.ref, ancestor = el10.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el10.ref, ancestor = rootNode.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el11.ref, ancestor = el0.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el11.ref, ancestor = el11.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el0.ref, ancestor = otherRootNode.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el0.ref, ancestor = otherEl.ref) shouldBe false

    DomApi.appendChild(parent = el0, child = el10, noHooks)
    DomApi.appendChild(parent = el0, child = el11, noHooks)

    DomApi.raw.isDescendantOf(node = el10.ref, ancestor = rootNode.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el11.ref, ancestor = rootNode.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el10.ref, ancestor = el0.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el11.ref, ancestor = el0.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el11.ref, ancestor = el10.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el11.ref, ancestor = el2.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el11.ref, ancestor = otherRootNode.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el11.ref, ancestor = otherEl.ref) shouldBe false

    DomApi.appendChild(parent = el10, child = el2, noHooks)

    DomApi.raw.isDescendantOf(node = el2.ref, rootNode.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el2.ref, el0.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el2.ref, el10.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el2.ref, el11.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el2.ref, el2.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el2.ref, otherRootNode.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el2.ref, otherEl.ref) shouldBe false

    DomApi.appendChild(parent = el2, child = el3, noHooks)

    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = rootNode.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = otherRootNode.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false

    DomApi.insertChildAtIndex(parent = elx, child = el3, index = 0, noHooks)

    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = rootNode.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = otherRootNode.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false

    DomApi.insertChildAtIndex(parent = el10, child = el3, index = 0, noHooks)

    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = rootNode.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = el10.ref) shouldBe true
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = otherRootNode.ref) shouldBe false
    DomApi.raw.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false
  }

  it("DomApi.raw.isDescendantOf document") {

    val el0 = span("el0")
    val el10 = span("el10")
    val el2 = articleTag("el2")

    DomApi.raw.isDescendantOf(node = el0.ref, dom.document) shouldBe false
    DomApi.raw.isDescendantOf(node = el10.ref, dom.document) shouldBe false
    DomApi.raw.isDescendantOf(node = el2.ref, dom.document) shouldBe false

    DomApi.appendChild(parent = el0, child = el10, noHooks)

    DomApi.raw.isDescendantOf(node = el10.ref, dom.document) shouldBe false

    mount(el0)

    DomApi.raw.isDescendantOf(node = el0.ref, dom.document) shouldBe true
    DomApi.raw.isDescendantOf(node = el10.ref, dom.document) shouldBe true
    DomApi.raw.isDescendantOf(node = el2.ref, dom.document) shouldBe false

    unmount()
    mount(el2)

    DomApi.raw.isDescendantOf(node = el0.ref, dom.document) shouldBe false
    DomApi.raw.isDescendantOf(node = el10.ref, dom.document) shouldBe false
    DomApi.raw.isDescendantOf(node = el2.ref, dom.document) shouldBe true

    DomApi.appendChild(parent = el2, child = el0, noHooks)

    DomApi.raw.isDescendantOf(node = el0.ref, dom.document) shouldBe true
    DomApi.raw.isDescendantOf(node = el10.ref, dom.document) shouldBe true
    DomApi.raw.isDescendantOf(node = el2.ref, dom.document) shouldBe true
  }

}
