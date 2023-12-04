package com.raquo.laminar.basic

import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import com.raquo.laminar.inserters.InserterHooks
import com.raquo.laminar.nodes.{ChildNode, ParentNode}
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom

import scala.scalajs.js

class TreeSpec extends UnitSpec {

  val noHooks: js.UndefOr[InserterHooks] = js.undefined

  it("DomApi.isDescendantOf") {

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

    DomApi.isDescendantOf(node = el0.ref, ancestor = rootNode.ref) shouldBe true
    DomApi.isDescendantOf(node = el0.ref, ancestor = el10.ref) shouldBe false
    DomApi.isDescendantOf(node = el10.ref, ancestor = rootNode.ref) shouldBe false
    DomApi.isDescendantOf(node = el11.ref, ancestor = el0.ref) shouldBe false
    DomApi.isDescendantOf(node = el11.ref, ancestor = el11.ref) shouldBe false
    DomApi.isDescendantOf(node = el0.ref, ancestor = otherRootNode.ref) shouldBe false
    DomApi.isDescendantOf(node = el0.ref, ancestor = otherEl.ref) shouldBe false

    ParentNode.appendChild(parent = el0, child = el10, noHooks)
    ParentNode.appendChild(parent = el0, child = el11, noHooks)

    DomApi.isDescendantOf(node = el10.ref, ancestor = rootNode.ref) shouldBe true
    DomApi.isDescendantOf(node = el11.ref, ancestor = rootNode.ref) shouldBe true
    DomApi.isDescendantOf(node = el10.ref, ancestor = el0.ref) shouldBe true
    DomApi.isDescendantOf(node = el11.ref, ancestor = el0.ref) shouldBe true
    DomApi.isDescendantOf(node = el11.ref, ancestor = el10.ref) shouldBe false
    DomApi.isDescendantOf(node = el11.ref, ancestor = el2.ref) shouldBe false
    DomApi.isDescendantOf(node = el11.ref, ancestor = otherRootNode.ref) shouldBe false
    DomApi.isDescendantOf(node = el11.ref, ancestor = otherEl.ref) shouldBe false

    ParentNode.appendChild(parent = el10, child = el2, noHooks)

    DomApi.isDescendantOf(node = el2.ref, rootNode.ref) shouldBe true
    DomApi.isDescendantOf(node = el2.ref, el0.ref) shouldBe true
    DomApi.isDescendantOf(node = el2.ref, el10.ref) shouldBe true
    DomApi.isDescendantOf(node = el2.ref, el11.ref) shouldBe false
    DomApi.isDescendantOf(node = el2.ref, el2.ref) shouldBe false
    DomApi.isDescendantOf(node = el2.ref, otherRootNode.ref) shouldBe false
    DomApi.isDescendantOf(node = el2.ref, otherEl.ref) shouldBe false

    ParentNode.appendChild(parent = el2, child = el3, noHooks)

    DomApi.isDescendantOf(node = el3.ref, ancestor = rootNode.ref) shouldBe true
    DomApi.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe true
    DomApi.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe true
    DomApi.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = otherRootNode.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false

    ParentNode.insertChildAtIndex(parent = elx, child = el3, index = 0, noHooks)

    DomApi.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe true
    DomApi.isDescendantOf(node = el3.ref, ancestor = rootNode.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = otherRootNode.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false

    ParentNode.insertChildAtIndex(parent = el10, child = el3, index = 0, noHooks)

    DomApi.isDescendantOf(node = el3.ref, ancestor = rootNode.ref) shouldBe true
    DomApi.isDescendantOf(node = el3.ref, ancestor = el0.ref) shouldBe true
    DomApi.isDescendantOf(node = el3.ref, ancestor = el10.ref) shouldBe true
    DomApi.isDescendantOf(node = el3.ref, ancestor = el11.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = el2.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = elx.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = otherRootNode.ref) shouldBe false
    DomApi.isDescendantOf(node = el3.ref, ancestor = otherEl.ref) shouldBe false
  }

  it("DomApi.isDescendantOf document") {

    val el0 = span("el0")
    val el10 = span("el10")
    val el2 = articleTag("el2")

    DomApi.isDescendantOf(node = el0.ref, dom.document) shouldBe false
    DomApi.isDescendantOf(node = el10.ref, dom.document) shouldBe false
    DomApi.isDescendantOf(node = el2.ref, dom.document) shouldBe false

    ParentNode.appendChild(parent = el0, child = el10, noHooks)

    DomApi.isDescendantOf(node = el10.ref, dom.document) shouldBe false

    mount(el0)

    DomApi.isDescendantOf(node = el0.ref, dom.document) shouldBe true
    DomApi.isDescendantOf(node = el10.ref, dom.document) shouldBe true
    DomApi.isDescendantOf(node = el2.ref, dom.document) shouldBe false

    unmount()
    mount(el2)

    DomApi.isDescendantOf(node = el0.ref, dom.document) shouldBe false
    DomApi.isDescendantOf(node = el10.ref, dom.document) shouldBe false
    DomApi.isDescendantOf(node = el2.ref, dom.document) shouldBe true

    ParentNode.appendChild(parent = el2, child = el0, noHooks)

    DomApi.isDescendantOf(node = el0.ref, dom.document) shouldBe true
    DomApi.isDescendantOf(node = el10.ref, dom.document) shouldBe true
    DomApi.isDescendantOf(node = el2.ref, dom.document) shouldBe true
  }

}
