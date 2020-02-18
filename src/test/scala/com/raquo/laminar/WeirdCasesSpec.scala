package com.raquo.laminar

import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.TestableOwner
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec

/** These tests verify Laminar's behaviour in weird cases.
  *
  * DO NOT look into these test for inspiration for how to do things in Laminar.
  *
  * Every test will have a quick note on how to do achieve the same result better.
  */
class WeirdCasesSpec extends UnitSpec {

  // - Similar problem was described in https://github.com/raquo/Laminar/issues/47
  // - This used to be a problem because pre-v0.8 the mount event system used EventBus-es for mount event propagation

  /** See https://github.com/raquo/Laminar/issues/11 for a better approach
    * (basically, use child.text instead of nested observables)
    */
  it("nested, synchronously dependent observables work as expected for some reason") {

    val bus = new EventBus[Int]

    mount(
      div(
        "hello",
        child <-- bus.events.map(num =>
          span(
            s"num: $num",
            child <-- bus.events.map(innerNum => article(s"innerNum: $innerNum"))
          )
        )
      )
    )

    // @TODO[Test] we should not need to specify this sentinel comment node here. Gotta find a way to ignore those.
    expectNode(div like ("hello", ExpectedNode.comment()))

    bus.writer.onNext(1)

    expectNode(
      div like(
        "hello",
        span like(
          "num: 1",
          article like "innerNum: 1"
        )
      )
    )

    bus.writer.onNext(2)

    expectNode(
      div like(
        "hello",
        span like(
          "num: 2",
          article like "innerNum: 2"
        )
      )
    )
  }

  it("nested, synchronously dependent signals work as expected") {

    val bus = new EventBus[Int]
    val signal = bus.events.startWith(0)

    mount(
      div(
        "hello",
        child <-- signal.map(num =>
          span(
            s"num: $num",
            child <-- signal.map(innerNum => article(s"innerNum: $innerNum"))
          )
        )
      )
    )

    expectNode(div like ("hello", span like(
      "num: 0",
      article like "innerNum: 0"
    )))

    // --

    bus.writer.onNext(1)

    expectNode(
      div like(
        "hello",
        span like(
          "num: 1",
          article like "innerNum: 1"
        )
      )
    )

    // --

    bus.writer.onNext(2)

    expectNode(
      div like(
        "hello",
        span like(
          "num: 2",
          article like "innerNum: 2"
        )
      )
    )
  }

  it("nested, synchronously dependent observables work as expected (outer stream, inner signal)") {

    val bus = new EventBus[Int]
    val signal = bus.events.startWith(0)

    mount(
      div(
        "hello",
        child <-- bus.events.map(num =>
          span(
            s"num: $num",
            child <-- signal.map(innerNum => article(s"innerNum: $innerNum"))
          )
        )
      )
    )

    expectNode(div like ("hello", ExpectedNode.comment()))

    // --

    bus.writer.onNext(1)

    expectNode(
      div like(
        "hello",
        span like(
          "num: 1",
          article like "innerNum: 1"
        )
      )
    )

    // --

    bus.writer.onNext(2)

    expectNode(
      div like(
        "hello",
        span like(
          "num: 2",
          article like "innerNum: 2"
        )
      )
    )
  }

  it("TransferableSubscription.setOwner does not break when changing owner to the same owner") {

    // This sequence tests a very specific bug in TransferableSubscription.setOwner:
    // in `re-mount:`, delta element would end up inactive because its subscription
    // would have been killed during `setOwner`, and it never obtained a new one.
    // Honestly I'm not sure why, but it must have something to do with the timing.
    // During activation and deactivation the code that executes on activation and
    // deactivation can see temporary inconsistencies between isActive status of
    // owner and its subscriptions.
    //
    // If you remove "if (maybeCurrentOwner.contains(nextOwner)) {" check in Airstream,
    // this test should fail (unless we have since added a similar check on laminar side).

    val nikita = span("Nikita")

    val nameVar = Var[List[Span]](nikita :: Nil)

    val signal = nameVar.signal

    val el = div(
      "Hello ",
      children <-- signal,
      " world"
    )

    withClue("before first mount:") {
      expectNode(el.ref, div like(
        "Hello ",
        ExpectedNode.comment(),
        " world"
      ))
    }

    assert(!ReactiveElement.isActive(el))
    assert(!ReactiveElement.isActive(nikita))

    withClue("first mount:") {
      mount(el)

      expectNode(div like(
        "Hello ",
        ExpectedNode.comment(),
        span like ("Nikita"),
        " world"
      ))

      assert(ReactiveElement.isActive(el))
      assert(ReactiveElement.isActive(nikita))
    }

    val alpha = span("Alpha")
    val bravo = span("Bravo")
    val charlie = span("Charlie")
    val delta = span("Delta")
    val eagle = span("Eagle")

    withClue("first mounted event:") {
      nameVar.writer.onNext(alpha :: bravo :: charlie :: delta :: eagle :: Nil)

      expectNode(div like(
        "Hello ",
        ExpectedNode.comment(),
        span like ("Alpha"),
        span like ("Bravo"),
        span like ("Charlie"),
        span like ("Delta"),
        span like ("Eagle"),
        " world")
      )

      assert(ReactiveElement.isActive(el))
      assert(ReactiveElement.isActive(alpha))
      assert(ReactiveElement.isActive(bravo))
      assert(ReactiveElement.isActive(charlie))
      assert(ReactiveElement.isActive(delta))
      assert(ReactiveElement.isActive(eagle))

      assert(!ReactiveElement.isActive(nikita))
    }

    val john = span("John")
    val tor = span("Tor")
    unmount()

    withClue("unmounted event with other obs:") {

      val owner = new TestableOwner
      signal.foreach(_ => ())(owner)
      nameVar.writer.onNext(john :: alpha :: delta :: bravo :: tor :: Nil)
      owner.killSubscriptions()

      expectNode(el.ref, div like(
        "Hello ",
        ExpectedNode.comment(),
        span like ("Alpha"),
        span like ("Bravo"),
        span like ("Charlie"),
        span like ("Delta"),
        span like ("Eagle"),
        " world")
      )

      assert(!ReactiveElement.isActive(el))
      assert(!ReactiveElement.isActive(alpha))
      assert(!ReactiveElement.isActive(bravo))
      assert(!ReactiveElement.isActive(charlie))
      assert(!ReactiveElement.isActive(delta))
      assert(!ReactiveElement.isActive(eagle))

      assert(!ReactiveElement.isActive(john))
      assert(!ReactiveElement.isActive(tor))
    }

    withClue("re-mount:") {

      mount(el, "re-mount...")

      expectNode(div like(
        "Hello ",
        ExpectedNode.comment(),
        span like ("John"),
        span like ("Alpha"),
        span like ("Delta"),
        span like ("Bravo"),
        span like ("Tor"),
        " world")
      )

      assert(ReactiveElement.isActive(john))
      assert(ReactiveElement.isActive(alpha))
      assert(ReactiveElement.isActive(delta))
      assert(ReactiveElement.isActive(bravo))
      assert(ReactiveElement.isActive(tor))

      assert(!ReactiveElement.isActive(nikita))
      assert(!ReactiveElement.isActive(charlie))
      assert(!ReactiveElement.isActive(eagle))
    }

    val elan = span("Elan")

    withClue("first event after re-mounting:") {

      nameVar.writer.onNext(tor :: delta :: bravo :: alpha :: elan :: john :: Nil)

      expectNode(div like(
        "Hello ",
        ExpectedNode.comment(),
        span like ("Tor"),
        span like ("Delta"),
        span like ("Bravo"),
        span like ("Alpha"),
        span like ("Elan"),
        span like ("John"),
        " world")
      )
    }

    assert(ReactiveElement.isActive(tor))
    assert(ReactiveElement.isActive(delta))
    assert(ReactiveElement.isActive(bravo))
    assert(ReactiveElement.isActive(alpha))
    assert(ReactiveElement.isActive(elan))
    assert(ReactiveElement.isActive(john))

    assert(!ReactiveElement.isActive(nikita))
    assert(!ReactiveElement.isActive(charlie))
    assert(!ReactiveElement.isActive(eagle))
  }

  it("TransferableSubscription.setOwner but more elaborate") {

    // This is like the other TransferableSubscription.setOwner test, but with more confounding stuff
    // - Ultimately it tests for the same problem, just more painfully

    // @Note these tests detected more activation / deactivation bugs,
    //   so DO NOT REMOVE THESE TESTS even if the immediate subscription issue becomes irrelevant.

    // @Note if this test fails with cryptic mount / unmount related errors, add isActive assertions like in

    val nikita = span("Nikita")

    val nameVar = Var[List[Span]](nikita :: Nil)

    val signal = nameVar.signal

    // make a "copy" of each element, we can't put the same element twice into the dom
    val stream = signal.changes.map(_.map(el => span(el.ref.textContent)))

    val el = div(
      "Hello ",
      onMountInsert { _ =>
        children <-- signal
      },
      onMountInsert { _ => children <-- stream},
      " world"
    )

    withClue("before first mount:") {
      expectNode(el.ref, div like(
        "Hello ",
        ExpectedNode.comment(),
        ExpectedNode.comment(),
        " world"
      ))
    }

    assert(!ReactiveElement.isActive(el))
    assert(!ReactiveElement.isActive(nikita))

    withClue("first mount:") {
      mount(el)

      expectNode(div like(
        "Hello ",
        ExpectedNode.comment(),
        span like ("Nikita"),
        ExpectedNode.comment(),
        " world"
      ))

      assert(ReactiveElement.isActive(el))
      assert(ReactiveElement.isActive(nikita))
    }

    val alpha = span("Alpha")
    val bravo = span("Bravo")
    val charlie = span("Charlie")
    val delta = span("Delta")
    val eagle = span("Eagle")

    withClue("first mounted event:") {
      nameVar.writer.onNext(alpha :: bravo :: charlie :: delta :: eagle :: Nil)

      expectNode(div like(
        "Hello ",
        ExpectedNode.comment(),
        span like ("Alpha"),
        span like ("Bravo"),
        span like ("Charlie"),
        span like ("Delta"),
        span like ("Eagle"),
        ExpectedNode.comment(),
        span like ("Alpha"),
        span like ("Bravo"),
        span like ("Charlie"),
        span like ("Delta"),
        span like ("Eagle"),
        " world")
      )

      // @TODO[Test] we don't have references to stream elements handy so we don't test those yet

      assert(ReactiveElement.isActive(el))
      assert(ReactiveElement.isActive(alpha))
      assert(ReactiveElement.isActive(bravo))
      assert(ReactiveElement.isActive(charlie))
      assert(ReactiveElement.isActive(delta))
      assert(ReactiveElement.isActive(eagle))

      assert(!ReactiveElement.isActive(nikita))
    }

    val axel = span("Axel")
    val dick = span("Dick")
    val eric = span("Eric")

    withClue("first unmounted event") {

      unmount()

      // Var updates, but element won't be populated until re-mounted
      nameVar.writer.onNext(axel :: alpha :: delta :: dick :: bravo :: eric :: Nil)

      expectNode(el.ref, div like(
        "Hello ",
        ExpectedNode.comment(),
        span like ("Alpha"),
        span like ("Bravo"),
        span like ("Charlie"),
        span like ("Delta"),
        span like ("Eagle"),
        ExpectedNode.comment(),
        span like ("Alpha"),
        span like ("Bravo"),
        span like ("Charlie"),
        span like ("Delta"),
        span like ("Eagle"),
        " world")
      )

      assert(!ReactiveElement.isActive(el))
      assert(!ReactiveElement.isActive(alpha))
      assert(!ReactiveElement.isActive(bravo))
      assert(!ReactiveElement.isActive(charlie))
      assert(!ReactiveElement.isActive(delta))
      assert(!ReactiveElement.isActive(eagle))
    }

    val john = span("John")
    val tor = span("Tor")

    withClue("unmounted event with other obs:") {

      // Important, we check that updateChildren logic can handle this scenario - observable getting ahead of the DOM
      val owner = new TestableOwner
      signal.foreach(_ => ())(owner)
      stream.foreach(_ => ())(owner)
      nameVar.writer.onNext(john :: alpha :: delta :: bravo :: tor :: Nil)
      owner.killSubscriptions()

      expectNode(el.ref, div like(
        "Hello ",
        ExpectedNode.comment(),
        span like ("Alpha"),
        span like ("Bravo"),
        span like ("Charlie"),
        span like ("Delta"),
        span like ("Eagle"),
        ExpectedNode.comment(),
        span like ("Alpha"),
        span like ("Bravo"),
        span like ("Charlie"),
        span like ("Delta"),
        span like ("Eagle"),
        " world")
      )

      assert(!ReactiveElement.isActive(el))
      assert(!ReactiveElement.isActive(alpha))
      assert(!ReactiveElement.isActive(bravo))
      assert(!ReactiveElement.isActive(charlie))
      assert(!ReactiveElement.isActive(delta))
      assert(!ReactiveElement.isActive(eagle))

      assert(!ReactiveElement.isActive(john))
      assert(!ReactiveElement.isActive(tor))
    }

    withClue("re-mount:") {

      mount(el, "re-mount...")

      expectNode(div like(
        "Hello ",
        ExpectedNode.comment(),
        span like ("John"),
        span like ("Alpha"),
        span like ("Delta"),
        span like ("Bravo"),
        span like ("Tor"),
        ExpectedNode.comment(),
        " world")
      )

      assert(ReactiveElement.isActive(john))
      assert(ReactiveElement.isActive(alpha))
      assert(ReactiveElement.isActive(delta))
      assert(ReactiveElement.isActive(bravo))
      assert(ReactiveElement.isActive(tor))

      assert(!ReactiveElement.isActive(nikita))
      assert(!ReactiveElement.isActive(charlie))
      assert(!ReactiveElement.isActive(eagle))
      assert(!ReactiveElement.isActive(axel))
      assert(!ReactiveElement.isActive(dick))
      assert(!ReactiveElement.isActive(eric))
    }

    val elan = span("Elan")

    withClue("first event after re-mounting:") {

      nameVar.writer.onNext(tor :: delta :: bravo :: alpha :: elan :: john :: Nil)

      expectNode(div like(
        "Hello ",
        ExpectedNode.comment(),
        span like ("Tor"),
        span like ("Delta"),
        span like ("Bravo"),
        span like ("Alpha"),
        span like ("Elan"),
        span like ("John"),
        ExpectedNode.comment(),
        span like ("Tor"),
        span like ("Delta"),
        span like ("Bravo"),
        span like ("Alpha"),
        span like ("Elan"),
        span like ("John"),
        " world")
      )
    }

    assert(ReactiveElement.isActive(tor))
    assert(ReactiveElement.isActive(delta))
    assert(ReactiveElement.isActive(bravo))
    assert(ReactiveElement.isActive(alpha))
    assert(ReactiveElement.isActive(elan))
    assert(ReactiveElement.isActive(john))

    assert(!ReactiveElement.isActive(nikita))
    assert(!ReactiveElement.isActive(charlie))
    assert(!ReactiveElement.isActive(eagle))
    assert(!ReactiveElement.isActive(axel))
    assert(!ReactiveElement.isActive(dick))
    assert(!ReactiveElement.isActive(eric))
  }
}
