package com.raquo.laminar

import com.raquo.domtestutils.matching.{ExpectedNode, Rule}
import com.raquo.laminar.api.L._
import com.raquo.laminar.collection.CollectionCommand.{Append, Insert, Prepend, Remove, Replace, ReplaceAll}
import com.raquo.laminar.experimental.airstream.eventbus.EventBus
import com.raquo.laminar.setters.ChildrenCommandSetter.ChildrenCommand
import com.raquo.laminar.utils.UnitSpec

import scala.collection.mutable

class ChildrenCommandReceiverSpec extends UnitSpec {

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
    val commandBus = new EventBus[ChildrenCommand]

    val span0 = span(text0)
    val span1 = span(text1)

    mount(div("Hello", children.command <-- commandBus.events, div("World")))
    expectChildren("none")

    commandBus.writer.onNext(Append(span0))
    expectChildren("append #1:", span like text0)

    commandBus.writer.onNext(Append(span1))
    expectChildren("append #2:", span like text0, span like text1)

    commandBus.writer.onNext(Prepend(div(text2)))
    expectChildren("prepend:", div like text2, span like text0, span like text1)

    commandBus.writer.onNext(Remove(span0))
    expectChildren("remove:", div like text2, span like text1)

    commandBus.writer.onNext(Replace(span1, div(text3)))
    expectChildren("replace:", div like text2, div like text3)

    commandBus.writer.onNext(ReplaceAll(mutable.Buffer(span1, span0)))
    expectChildren("replaceAll:", span like text1, span like text0)

    commandBus.writer.onNext(Append(span(text4)))
    expectChildren("append #3:", span like text1, span like text0, span like text4)

    commandBus.writer.onNext(Insert(span(text5), atIndex = 2))
    expectChildren("insert:", span like text1, span like text0, span like text5, span like text4)

    def expectChildren(clue: String, childRules: Rule*): Unit = {
      withClue(clue) {
        val first: Rule = "Hello"
        val last: Rule = div like "World"
        val sentinelNode: Rule = ExpectedNode.comment()
        val rules: Seq[Rule] = first +: sentinelNode +: childRules :+ last

        expectNode(div like(rules: _*))
      }
    }
  }
}
