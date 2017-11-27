package com.raquo.laminar

import com.raquo.laminar.bundle._
import com.raquo.laminar.lifecycle.ParentChangeEvent
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec
import com.raquo.xstream.Listener
import org.scalajs.dom

class ParentChangeEventSpec extends UnitSpec {

  case class TestCase(
    action: ReactiveElement[dom.html.Element] => Unit,
    changeEvents: Seq[ParentChangeEvent]
  )

  it("Changing parent on a node fires appropriate ParentChangeEvent-s") {

    val grandChild = b("yolo")
    val child = span(grandChild)
    val otherChild = i()

    val parent1 = div()
    val parent2 = p("hello", "world")

    var parentEvents = Seq[ParentChangeEvent]()

    val testCases = Seq(
      TestCase(
        parent1.appendChild,
        Seq(
          ParentChangeEvent(
            alreadyChanged = false,
            maybePrevParent = None,
            maybeNextParent = Some(parent1)
          ),
          ParentChangeEvent(
            alreadyChanged = true,
            maybePrevParent = None,
            maybeNextParent = Some(parent1)
          )
        )
      ),
      TestCase(
        parent1.appendChild,
        Seq()
      ),
      TestCase(
        child => parent2.insertChild(child, atIndex = 1),
        Seq(
          ParentChangeEvent(
            alreadyChanged = false,
            maybePrevParent = Some(parent1),
            maybeNextParent = Some(parent2)
          ),
          ParentChangeEvent(
            alreadyChanged = true,
            maybePrevParent = Some(parent1),
            maybeNextParent = Some(parent2)
          )
        )
      ),
      TestCase(
        parent2.removeChild,
        Seq(
          ParentChangeEvent(
            alreadyChanged = false,
            maybePrevParent = Some(parent2),
            maybeNextParent = None
          ),
          ParentChangeEvent(
            alreadyChanged = true,
            maybePrevParent = Some(parent2),
            maybeNextParent = None
          )
        )
      ),
      TestCase(
        parent2.appendChild,
        Seq(
          ParentChangeEvent(
            alreadyChanged = false,
            maybePrevParent = None,
            maybeNextParent = Some(parent2)
          ),
          ParentChangeEvent(
            alreadyChanged = true,
            maybePrevParent = None,
            maybeNextParent = Some(parent2)
          )
        )
      ),
      TestCase(
        parent2.replaceChild(_, otherChild),
        Seq(
          ParentChangeEvent(
            alreadyChanged = false,
            maybePrevParent = Some(parent2),
            maybeNextParent = None
          ),
          ParentChangeEvent(
            alreadyChanged = true,
            maybePrevParent = Some(parent2),
            maybeNextParent = None
          )
        )
      )
    )

    // @TODO[Test] do we care about garbage-collecting these subscriptions?

    child.$parentChange.addListener(Listener(onNext = ev => {
      parentEvents = parentEvents :+ ev
    }))

    grandChild.$parentChange.addListener(Listener(onNext = parentChangeEvent => {
      fail("grandChild received a ParentChangeEvent. This is not supposed to happen, as such events should not be inherited by child nodes.")
    }))

    testCases.zipWithIndex.foreach { case (testCase, index) =>
      withClue(s"Case index=$index:") {
        testCase.action(child)
        parentEvents shouldEqual testCase.changeEvents
        parentEvents = Seq()
      }
    }
  }

  it("$parentChange lazy val is not initialized if nothing asks for it even if the node has children") {

    val parent1 = div()
    val parent2 = p("Yolo")

    val child = span(b("orly"), "yarly")

    child.maybeParentChangeBus.isDefined shouldBe false

    parent1(child)

    child.maybeParentChangeBus.isDefined shouldBe false

    parent2(child)

    child.maybeParentChangeBus.isDefined shouldBe false

    child.$parentChange // Touching this should be enough to initialize the stream

    child.maybeParentChangeBus.isDefined shouldBe true
  }
}
