package com.raquo.laminar

import com.raquo.domtestutils.matching.{ExpectedNode, Rule}
import com.raquo.laminar.api.L._
import com.raquo.laminar.CollectionCommand.{Append, Insert, Prepend, Remove, Replace, ReplaceAll}
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
    expectChildren("append #1:", span of text0)

    commandBus.writer.onNext(Append(span1))
    expectChildren("append #2:", span of text0, span of text1)

    commandBus.writer.onNext(Prepend(div(text2)))
    expectChildren("prepend:", div of text2, span of text0, span of text1)

    commandBus.writer.onNext(Remove(span0))
    expectChildren("remove:", div of text2, span of text1)

    commandBus.writer.onNext(Replace(span1, div(text3)))
    expectChildren("replace:", div of text2, div of text3)

    commandBus.writer.onNext(ReplaceAll(mutable.Buffer(span1, span0)))
    expectChildren("replaceAll:", span of text1, span of text0)

    commandBus.writer.onNext(Append(span(text4)))
    expectChildren("append #3:", span of text1, span of text0, span of text4)

    commandBus.writer.onNext(Insert(span(text5), atIndex = 2))
    expectChildren("insert:", span of text1, span of text0, span of text5, span of text4)

    def expectChildren(clue: String, childRules: Rule*): Unit = {
      withClue(clue) {
        val first: Rule = "Hello"
        val last: Rule = div of "World"
        val rules: Seq[Rule] = first +: (sentinel: Rule) +: childRules :+ last

        expectNode(div.of(rules: _*))
      }
    }
  }
}
