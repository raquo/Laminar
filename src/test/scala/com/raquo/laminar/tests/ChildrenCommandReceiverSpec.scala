package com.raquo.laminar.tests

import com.raquo.domtestutils.matching.Rule
import com.raquo.laminar.api.L._
import com.raquo.laminar.domapi.DomError
import com.raquo.laminar.inserters.CollectionCommand.{Append, Insert, Prepend, Remove, Replace}
import com.raquo.laminar.utils.UnitSpec

import scala.collection.{immutable, mutable}

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
    val commandBus = new EventBus[CollectionCommand[Node]]

    val span0 = span(text0)
    val span1 = span(text1)
    val div2 = div(text2)
    val div3 = div(text3)
    val span4 = span(text4)
    val span5 = span(text5)

    val el = div(
      "Hello",
      children.command <-- commandBus.events,
      div("World")
    )

    mount(el)
    expectChildren("none")

    commandBus.writer.onNext(Append(span0))
    expectChildren("append #1:", span of text0)

    commandBus.writer.onNext(Append(span1))
    expectChildren("append #2:", span of text0, span of text1)

    commandBus.writer.onNext(Prepend(div2))
    expectChildren("prepend:", div of text2, span of text0, span of text1)

    commandBus.writer.onNext(Remove(span0))
    expectChildren("remove:", div of text2, span of text1)

    commandBus.writer.onNext(Replace(span1, div3))
    expectChildren("replace:", div of text2, div of text3)

    commandBus.writer.onNext(Append(span4))
    expectChildren("append #3:", div of text2, div of text3, span of text4)

    commandBus.writer.onNext(Insert(span5, atIndex = 2))
    expectChildren("insert:", div of text2, div of text3, span of text5, span of text4)

    def expectChildren(clue: String, childRules: Rule*): Unit = {
      withClue(clue) {
        val first: Rule = "Hello"
        val last: Rule = div of "World"
        val rules: immutable.Seq[Rule] = first +: (sentinel: Rule) +: childRules :+ last

        expectNode(div.of(rules: _*))
      }
    }
  }

  // https://github.com/raquo/Laminar/issues/195
  it("does not drift extraNodeCount when Append fails") {
    val errors = mutable.Buffer[Throwable]()
    val collectingCallback: Throwable => Unit = errors += _

    try {
      // Swap rethrow callback for collecting callback so errors don't fail the test immediately
      AirstreamError.unregisterUnhandledErrorCallback(AirstreamError.unsafeRethrowErrorCallback)
      AirstreamError.registerUnhandledErrorCallback(collectingCallback)

      val commandBus = new EventBus[CollectionCommand[Node]]

      val spanA = span(text0)
      val spanB = span(text1)
      val spanC = div(text2)

      val el = div(
        "Hello",
        children.command <-- commandBus.events,
        div("World")
      )

      mount(el)
      expectChildren(clue = "initial:")()

      commandBus.writer.onNext(Append(spanA))
      expectChildren(clue = "after append A:")(
        span of text0
      )

      commandBus.writer.onNext(Append(spanB))
      expectChildren(clue = "after append B:")(
        span of text0,
        span of text1
      )

      // Appending el to itself throws a HierarchyRequestError DOMException,
      // so insertChildAtIndex returns false and nothing is inserted.
      commandBus.writer.onNext(Append(el))
      expectChildren(clue = "after failed self-append:")(
        span of text0,
        span of text1
      )

      // With the bug, extraNodeCount was incorrectly incremented by the failed
      // append, so this next append lands after div("World") instead of before it.
      commandBus.writer.onNext(Append(spanC))
      expectChildren(clue = "after append C:")(
        span of text0,
        span of text1,
        div of text2
      )

      assertEquals(errors.size, 1)
      assert(errors.head.isInstanceOf[DomError])

      errors.clear()

      def expectChildren(clue: String)(childRules: Rule*): Unit = {
        withClue(clue) {
          val first: Rule = "Hello"
          val last: Rule = div of "World"
          val rules: immutable.Seq[Rule] = first +: (sentinel: Rule) +: childRules :+ last
          expectNode(div.of(rules: _*))
        }
      }
    } finally {
      AirstreamError.unregisterUnhandledErrorCallback(collectingCallback)
      AirstreamError.registerUnhandledErrorCallback(AirstreamError.unsafeRethrowErrorCallback)
    }
  }
}
