package com.raquo.laminar

import com.raquo.domtestutils.matching.{ExpectedNode, Rule}
import com.raquo.laminar.api.L._
import com.raquo.laminar.utils.UnitSpec
import org.scalajs.dom
import org.scalatest.BeforeAndAfter

class ChildrenReceiverSpec extends UnitSpec with BeforeAndAfter {

  before {
    AirstreamError.unregisterUnhandledErrorCallback(AirstreamError.consoleErrorCallback)
    AirstreamError.registerUnhandledErrorCallback(AirstreamError.unsafeRethrowErrorCallback)
  }

  after {
    AirstreamError.registerUnhandledErrorCallback(AirstreamError.consoleErrorCallback)
    AirstreamError.unregisterUnhandledErrorCallback(AirstreamError.unsafeRethrowErrorCallback)
  }

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

    val childrenBus = new EventBus[Vector[Child]]
    val $children = childrenBus.events

    val span0 = span(text0)
    val span1 = span(text1)
    val div2 = div(text2)
    val div3 = div(text3)
    val span4 = span(text4)
    val span5 = span(text5)

    mount(main(children <-- $children))
    expectChildren("none")

    childrenBus.writer.onNext(Vector())

    childrenBus.writer.onNext(Vector(span0))
    expectChildren("append #1:", span like text0)

    childrenBus.writer.onNext(Vector(span0, span1))
    expectChildren("append #2:", span like text0, span like text1)

    childrenBus.writer.onNext(Vector(div2, span0, span1))
    expectChildren("prepend:", div like text2, span like text0, span like text1)

    childrenBus.writer.onNext(Vector(div2, span1))
    expectChildren("remove:", div like text2, span like text1)

    childrenBus.writer.onNext(Vector(div2, div3))
    expectChildren("replace:", div like text2, div like text3)

    childrenBus.writer.onNext(Vector(span1, span0))
    expectChildren("replaceAll:", span like text1, span like text0)

    childrenBus.writer.onNext(Vector(span0, span1))
    expectChildren("switch places:", span like text0, span like text1)

    childrenBus.writer.onNext(Vector(span1, span0, span4))
    expectChildren("switch places & append:", span like text1, span like text0, span like text4)

    childrenBus.writer.onNext(Vector(span1, span0, span5, span4))
    expectChildren("insert:", span like text1, span like text0, span like text5, span like text4)

    childrenBus.writer.onNext(Vector(span5, span4))
    expectChildren("remove #2:", span like text5, span like text4)

    childrenBus.writer.onNext(Vector(span1, span5, span4))
    expectChildren("prepend #2:", span like text1, span like text5, span like text4)

    childrenBus.writer.onNext(Vector(span1, span5, span4, span0, div2, div3))
    expectChildren("insert #2:", span like text1, span like text5, span like text4, span like text0, div like text2, div like text3)

    childrenBus.writer.onNext(Vector(span1, span5, span4, span0))
    expectChildren("remove #3:", span like text1, span like text5, span like text4, span like text0)

    childrenBus.writer.onNext(Vector(span0, span1, div2, div3, span4))
    expectChildren("mix:", span like text0, span like text1, div like text2, div like text3, span like text4)

    childrenBus.writer.onNext(Vector(div3, div2, span1, span4, span0))
    expectChildren("reorder:", div like text3, div like text2, span like text1, span like text4, span like text0)

    childrenBus.writer.onNext(Vector())
    expectChildren("clear:")

    def expectChildren(clue: String, childRules: Rule*): Unit = {
      withClue(clue) {
        val first: Rule = "Hello"
        val last: Rule = article like "World"
        val sentinelNode: Rule = ExpectedNode.comment()
        val rules: Seq[Rule] = sentinelNode +: childRules

        expectNode(main like(rules: _*))
      }
    }
  }
}
