package com.raquo.laminar

import com.raquo.laminar
import com.raquo.laminar.allTags.{comment, div, span}
import com.raquo.laminar.subscriptions.DynamicNodeList
import com.raquo.laminar.subscriptions.DynamicNodeList.{Append, Insert, Prepend, Remove, Replace, ReplaceAll}
import com.raquo.laminar.utils.UnitSpec
import com.raquo.snabbdom.utils.testing.matching.Rule
import com.raquo.xstream.{ShamefulStream, XStream}

import scala.scalajs.js

class ChildrenReceiverSpec extends UnitSpec {

  private val text0 = randomString("text0_")
  private val text00 = randomString("text00_")
  private val text1 = randomString("text1_")
  private val text2 = randomString("text2_")
  private val text3 = randomString("text3_")
  private val text4 = randomString("text4_")
  private val text5 = randomString("text5_")
//  private val text6 = randomString("text6_")
//  private val text7 = randomString("text7_")

  it("updates a list of children") {
    val $diff = XStream.create[DynamicNodeList.Diff]()
    val $varDiff = new ShamefulStream($diff)

    mount(div("Hello", children("foo") <-- DynamicNodeList($diff), div("World")))
    expectChildren("none")

    $varDiff.shamefullySendNext(Append(span(laminar.key := "0", text0)))
    expectChildren("append #1:", span like text0)

    $varDiff.shamefullySendNext(Append(span(laminar.key := "1", text1)))
    expectChildren("append #2:", span like text0, span like text1)

    $varDiff.shamefullySendNext(Prepend(div(laminar.key := "2", text2)))
    expectChildren("prepend:", div like text2, span like text0, span like text1)

    $varDiff.shamefullySendNext(Remove(key = "0"))
    expectChildren("remove:", div like text2, span like text1)

    $varDiff.shamefullySendNext(Replace(key = "1", div(laminar.key := "3", text3)))
    expectChildren("replace:", div like text2, div like text3)

    def reverse(nodes: js.Array[RNode]): js.Array[RNode] = {
      val newNodes = nodes.jsSlice()
      newNodes.reverseInPlace()
      newNodes
    }

    $varDiff.shamefullySendNext(ReplaceAll(reverse))
    expectChildren("replaceAll:", div like text3, div like text2)

    $varDiff.shamefullySendNext(Append(span(laminar.key := "4", text4)))
    expectChildren("append #3:", div like text3, div like text2, span like text4)

    $varDiff.shamefullySendNext(Insert(span(laminar.key := "5", text5), atIndex = 2))
    expectChildren("insert:", div like text3, div like text2, span like text5, span like text4)

    def expectChildren(clue: String, childRules: Rule[RNode, RNodeData]*) = {
      withClue(clue) {
        val first: Rule[RNode, RNodeData] = "Hello"
        val openingBound: Rule[RNode, RNodeData] = comment likeEmpty
        val closingBound: Rule[RNode, RNodeData] = comment likeEmpty
        val last: Rule[RNode, RNodeData] = div like "World"
        val rules: Seq[Rule[RNode, RNodeData]] = first +: openingBound +: childRules :+ closingBound :+ last

        expectNode(div like(rules: _*))
      }
    }
  }
}
