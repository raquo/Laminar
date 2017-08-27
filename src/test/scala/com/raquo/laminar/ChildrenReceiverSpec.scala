package com.raquo.laminar

import com.raquo.domtestutils.matching.Rule
import com.raquo.laminar
import com.raquo.laminar.tags.{div, span}
import com.raquo.laminar.receivers.ChildrenReceiver
import com.raquo.laminar.utils.UnitSpec
import com.raquo.laminar.nodes.ReactiveNode
import com.raquo.xstream.{ShamefulStream, XStream}

import scala.collection.mutable

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
    val $diff = XStream.create[ChildrenReceiver.Diff]()
    val $varDiff = new ShamefulStream($diff)

    val span0 = span(text0)
    val span1 = span(text1)

    mount(div("Hello", children <-- $diff, div("World")))
    expectChildren("none")

    $varDiff.shamefullySendNext(ChildrenReceiver.append(span0))
    expectChildren("append #1:", span like text0)

    $varDiff.shamefullySendNext(ChildrenReceiver.append(span1))
    expectChildren("append #2:", span like text0, span like text1)

    $varDiff.shamefullySendNext(ChildrenReceiver.prepend(div(text2)))
    expectChildren("prepend:", div like text2, span like text0, span like text1)

    $varDiff.shamefullySendNext(ChildrenReceiver.remove(span0))
    expectChildren("remove:", div like text2, span like text1)

    $varDiff.shamefullySendNext(ChildrenReceiver.replace(span1, div(text3)))
    expectChildren("replace:", div like text2, div like text3)

    $varDiff.shamefullySendNext(ChildrenReceiver.replaceAll(mutable.Buffer(span1, span0)))
    expectChildren("replaceAll:", span like text1, span like text0)

    $varDiff.shamefullySendNext(ChildrenReceiver.append(span(text4)))
    expectChildren("append #3:", span like text1, span like text0, span like text4)

    $varDiff.shamefullySendNext(ChildrenReceiver.insert(span(text5), atIndex = 2))
    expectChildren("insert:", span like text1, span like text0, span like text5, span like text4)

    def expectChildren(clue: String, childRules: Rule[ReactiveNode]*) = {
      withClue(clue) {
        val first: Rule[ReactiveNode] = "Hello"
        val last: Rule[ReactiveNode] = div like "World"
        val sentinelNode: Rule[ReactiveNode] = comment likeWhatever
        val rules: Seq[Rule[ReactiveNode]] = first +: sentinelNode +: childRules :+ last

        expectNode(div like(rules: _*))
      }
    }
  }
}
