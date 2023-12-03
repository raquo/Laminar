package com.raquo.laminar

import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.TestableOwner
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.utils.UnitSpec

class MountHooksSpec extends UnitSpec {

  it ("onMountCallback infers precise type and provides implicit owner") {

    var value = 0

    val bus = new EventBus[Int]
    val observer = Observer[Int](ev => value = ev)

    val el = div("Hello", onMountCallback(ctx => {
      import ctx.owner
      val x: Div = ctx.thisNode
      bus.events.addObserver(observer) // using owner implicitly
    }))

    // --

    bus.writer.onNext(1)

    assert(value == 0) // not subscribed yet

    mount(el)

    bus.writer.onNext(2)

    assert(value == 2)
  }

  it ("onMountBind works and infers precise type") {

    var value = 0
    var numMountCalls = 0

    val bus = new EventBus[Int]
    val observer = Observer[Int](ev => value = ev)

    val el = div("Hello", onMountBind(ctx => {
      val x: Div = ctx.thisNode
      numMountCalls += 1
      bus.events --> observer
    }))

    // --

    bus.writer.onNext(1)

    assert(value == 0) // not subscribed yet
    assert(numMountCalls == 0)

    // --

    mount(el)

    assert(value == 0)
    assert(numMountCalls == 1)

    numMountCalls = 0

    // --

    bus.writer.onNext(2)

    assert(value == 2)
    assert(numMountCalls == 0)

    // --

    unmount()

    assert(value == 2)
    assert(numMountCalls == 0)

    // --

    bus.writer.onNext(3) // unmounted

    assert(value == 2)
    assert(numMountCalls == 0)

    // --

    mount(el)

    assert(value == 2)
    assert(numMountCalls == 1)

    numMountCalls = 0

    // --

    bus.writer.onNext(4)

    assert(value == 4)

    assert(numMountCalls == 0)
  }

  it ("onMountCallback and onUnmountCallback work for repeated mounting") {

    var numMounts = 0
    var numUnmounts = 0

    val el = div(
      "Hello",
      onMountCallback(_ => numMounts += 1),
      onUnmountCallback(_ => numUnmounts += 1)
    )

    assert(numMounts == 0)
    assert(numUnmounts == 0)

    // --

    mount(el)

    assert(numMounts == 1)
    assert(numUnmounts == 0)

    numMounts = 0

    // --

    unmount()

    assert(numMounts == 0)
    assert(numUnmounts == 1)

    numUnmounts = 0

    // --

    mount(el)

    assert(numMounts == 1)
    assert(numUnmounts == 0)

    numMounts = 0

    // --

    unmount()

    assert(numMounts == 0)
    assert(numUnmounts == 1)

    numUnmounts = 0
  }

  it("onMountInsert child") {

    val nameVar = Var[String]("Nikita")
    var numMountCalls = 0

    val signal = nameVar.signal
    val stream = signal.changes

    val el = div(
      "Hello ",
      onMountInsert { c =>
        numMountCalls += 1
        child <-- signal.map(name => span(name))
      },
      onMountInsert { c =>
        child <-- stream.map(name => div(name))
      },
      " world"
    )

    withClue("before first mount:") {
      expectNode(el.ref, div.of("Hello ", sentinel, sentinel, " world"))

      assert(numMountCalls == 0) // important part of API. We reserve the spot but don't call the hook yet.
    }

    withClue("first mount:") {

      mount(el)

      expectNode(div.of("Hello ", sentinel, span.of("Nikita"), sentinel, " world"))
      assert(numMountCalls == 1)

      numMountCalls = 0
    }

    withClue("first mounted event:") {
      nameVar.writer.onNext("Yan")

      expectNode(div.of("Hello ", sentinel, span.of("Yan"), sentinel, div.of("Yan"), " world"))
      assert(numMountCalls == 0)
    }

    withClue("first unmounted event:") {

      unmount()

      nameVar.writer.onNext("Igor") // Var updates, but element won't be populated until re-mounted

      expectNode(el.ref, div.of("Hello ", sentinel, span.of("Yan"), sentinel, div.of("Yan"), " world"), "unmounted")
      assert(numMountCalls == 0)
    }


    withClue("unmounted event with other obs:") {

      val owner = new TestableOwner
      signal.foreach(_ => ())(owner)
      stream.foreach(_ => ())(owner)
      nameVar.writer.onNext("Igor2") // this value will be resurrected when remounting, and DOM nodes will be fine
      owner.killSubscriptions()

      expectNode(el.ref, div.of("Hello ", sentinel, span.of("Yan"), sentinel, div.of("Yan"), " world"), "unmounted")
      assert(numMountCalls == 0)
      assert(signal.now() == "Igor2")
    }

    withClue("re-mount:") {

      mount(el)

      expectNode(div.of("Hello ", sentinel, span.of("Igor2"), sentinel, div.of("Yan"), " world"))
      assert(numMountCalls == 1)

      numMountCalls = 0
    }

    withClue("first event after re-mounting:") {
      nameVar.writer.onNext("Elan")

      expectNode(div.of("Hello ", sentinel, span.of("Elan"), sentinel, div.of("Elan"), " world"))
      assert(numMountCalls == 0)
    }
  }

  it("onMountBind cancels previous dynamic subscriptions on unmount") {

    val num = Var(1)

    var numModCalls = 0
    var numBindCalls = 0
    var numSignalCalls = 0

    val signal = num.signal.map(n => {
      numSignalCalls += 1
      "x" + n
    })

    val el = div(
      "Hello ",
      Modifier { _ => numModCalls += 1 },
      onMountBind { _ =>
        numBindCalls += 1
        title <-- signal
      },
      " world"
    )

    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)

    assert(numModCalls == 1)
    assert(numBindCalls == 10)
    assert(numSignalCalls == 1)

    // 2 subs:
    // - onMountBind,
    // - title <-- signal
    // Note that el's pilot sub is owned by el's parent, not el.
    ReactiveElement.numDynamicSubscriptions(el) shouldBe 2
  }

  it("onMountInsert child cancels previous dynamic subscriptions on unmount") {

    val num = Var(1)

    var numModCalls = 0
    var numBindCalls = 0
    var numSignalCalls = 0

    val signal = num.signal.map(n => {
      numSignalCalls += 1
      span("x" + n)
    })

    val el = div(
      "Hello ",
      Modifier { _ => numModCalls += 1 },
      onMountInsert { _ =>
        numBindCalls += 1
        child <-- signal
      },
      " world"
    )

    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)

    assert(numModCalls == 1)
    assert(numBindCalls == 10)
    assert(numSignalCalls == 1)

    assert(el.ref.childNodes.length == 4) // Checks that onMountInsert properly discards of old items

    // 3 subs:
    // - onMountBind,
    // - child <-- signal
    // - child's pilot subscription
    // Note that el's pilot sub is owned by el's parent, not el.
    ReactiveElement.numDynamicSubscriptions(el) shouldBe 3
  }

  it("onMountInsert children cancels previous dynamic subscriptions on unmount") {

    val num = Var(1)

    var numModCalls = 0
    var numBindCalls = 0
    var numSignalCalls = 0

    val signal = num.signal.map(n => {
      numSignalCalls += 1
      span("a" + n) :: span("b" + n) :: span("c" + n) :: Nil
    })

    val el = div(
      "Hello ",
      Modifier { _ => numModCalls += 1 },
      onMountInsert { _ =>
        numBindCalls += 1
        children <-- signal
      },
      " world"
    )

    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)
    unmount()
    mount(el)

    assert(numModCalls == 1)
    assert(numBindCalls == 10)
    assert(numSignalCalls == 1)

    // 5 subs:
    // - onMountBind,
    // - children <-- signal
    // - 3x subs, for each child's pilot subscription
    // Note that el's pilot sub is owned by el's parent, not el.
    ReactiveElement.numDynamicSubscriptions(el) shouldBe 5
  }

  it("onMountInsert switches between different types of inserters (signals)") {

    var numModCalls = 0
    var numBindCalls = 0
    var numChildrenSignalCalls = 0

    // --

    val xChildIx = Var(1)

    val xChildSignal = xChildIx.signal.map { n =>
      numChildrenSignalCalls += 1
      span("x" + n)
    }

    // --

    val yChildrenIx = Var(1)

    val yChildrenSignal = yChildrenIx.signal.map { n =>
      Range.inclusive(1, n).map(i => div(s"y$i/$n"))
    }

    // --

    val zChildrenIx = Var(1)

    val zChildrenSignal = zChildrenIx.signal.map { n =>
      Range.inclusive(1, n).map(i => p(s"z$i/$n"))
    }

    // --

    val xChildInserter = child <-- xChildSignal

    val yChildrenInserter = children <-- yChildrenSignal

    var dynamicInserter: Inserter = xChildInserter

    // --

    val el = div(
      "Hello ",
      Modifier { _ => numModCalls += 1 },
      onMountInsert { _ =>
        numBindCalls += 1
        dynamicInserter
      },
      children <-- zChildrenSignal,
      " world"
    )

    mount("onMountInsert child <--:", el)

    // 5 subs:
    // - child element pilot sub (from dynamicInserter)
    // - child element pilot sub (from children <-- zChildrenSignal)
    // - onMountInsert itself
    // - dynamicInserter
    // - children <-- zChildrenSignal
    // (Note: el's own pilotSubscription is owned by el's parent, not el itself)
    ReactiveElement.numDynamicSubscriptions(el) shouldBe 5

    expectNode(
      div of (
        "Hello ",
        sentinel,
        span of "x1",
        sentinel,
        p of "z1/1",
        " world"
      )
    )

    // --

    xChildIx.set(2)

    zChildrenIx.set(2)

    // Added one sub:
    // - Child element pilot sub (now we have 2 children instead of 1 from children <-- zChildrenSignal)
    ReactiveElement.numDynamicSubscriptions(el) shouldBe 6

    expectNode(
      div of(
        "Hello ",
        sentinel,
        span of "x2",
        sentinel,
        p of "z1/2",
        p of "z2/2",
        " world"
      )
    )

    // --

    unmount()

    xChildIx.set(3)

    zChildrenIx.set(3)

    mount("onMountInsert child <-- AGAIN:", el)

    // Added one sub:
    // - Child element pilot sub (now we have 3 children instead of 1 from children <-- zChildrenSignal)
    ReactiveElement.numDynamicSubscriptions(el) shouldBe 7

    expectNode(
      div of(
        "Hello ",
        sentinel,
        span of "x3",
        sentinel,
        p of "z1/3",
        p of "z2/3",
        p of "z3/3",
        " world"
      )
    )

    // --

    unmount()

    dynamicInserter = yChildrenInserter

    mount("onMountInsert SWITCH to children <--:", el)

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 7

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/1",
        sentinel,
        p of "z1/3",
        p of "z2/3",
        p of "z3/3",
        " world"
      )
    )

    // --

    yChildrenIx.set(2)

    // Added one sub:
    // - Child element pilot sub (now we have 3 children instead of 1 from dynamicInserter (child <-- yChildrenSignal))
    ReactiveElement.numDynamicSubscriptions(el) shouldBe 8

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/2",
        div of "y2/2",
        sentinel,
        p of "z1/3",
        p of "z2/3",
        p of "z3/3",
        " world"
      )
    )

    // --

    zChildrenIx.set(2)

    // Removed one sub:
    // - Child element pilot sub (now we have 2 children instead of 3 from children <-- zChildrenSignal)
    ReactiveElement.numDynamicSubscriptions(el) shouldBe 7

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/2",
        div of "y2/2",
        sentinel,
        p of "z1/2",
        p of "z2/2",
        " world"
      )
    )

    // --

    unmount()

    dynamicInserter = xChildInserter

    mount("onMountInsert SWITCH BACK to child <--:", el)

    // Removed one sub:
    // - Child element pilot sub (now we have 1 child instead of 2 from dynamicInserter)
    ReactiveElement.numDynamicSubscriptions(el) shouldBe 6

    expectNode(
      div of(
        "Hello ",
        sentinel,
        span of "x3",
        sentinel,
        p of "z1/2",
        p of "z2/2",
        " world"
      )
    )


    assert(numModCalls == 1)
    assert(numBindCalls == 4)
    assert(numChildrenSignalCalls == 3)
  }

  it("onMountInsert switches between different types of inserters (streams)") {
    var numModCalls = 0
    var numBindCalls = 0
    var numChildrenStreamCalls = 0

    // --

    val xChildIx = new EventBus[Int]()

    val xChildStream = xChildIx.events.map { n =>
      numChildrenStreamCalls += 1
      span("x" + n)
    }

    // --

    val yChildrenIx = new EventBus[Int]

    val yChildrenStream = yChildrenIx.events.map { n =>
      Range.inclusive(1, n).map(i => div(s"y$i/$n"))
    }

    // --

    val xChildInserter = child <-- xChildStream

    val yChildrenInserter = children <-- yChildrenStream

    var dynamicInserter: Inserter = xChildInserter

    // --

    val el = div(
      "Hello ",
      Modifier { _ => numModCalls += 1 },
      onMountInsert { _ =>
        numBindCalls += 1
        dynamicInserter
      },
      " world"
    )

    mount(el)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        " world"
      )
    )

    // 2 subs:
    // - onMountInsert itself
    // - dynamicInserter
    // (Note: el's own pilotSubscription is owned by el's parent, not el itself)
    ReactiveElement.numDynamicSubscriptions(el) shouldBe 2

    // --

    xChildIx.emit(1)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        span of "x1",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 3

    // --

    unmount()

    dynamicInserter = yChildrenInserter

    mount(el)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        span of "x1",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 3


    // --

    yChildrenIx.emit(1)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/1",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 3

    // --

    yChildrenIx.emit(2)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/2",
        div of "y2/2",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 4

    // -- unmount and re-mount: children <-- inserter elements are retained.

    unmount()

    mount(el)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/2",
        div of "y2/2",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 4

    // -- switching from `children <-- stream` with 2 elements to `child <-- stream`

    unmount()

    dynamicInserter = xChildInserter

    mount(el)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/2",
        div of "y2/2",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 4

    // --

    xChildIx.emit(2)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        span of "x2",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 3

    // -- unmounting and re-mounting: child element is retained

    unmount()

    mount(el)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        span of "x2",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 3

    // -- switching back to children <-- stream

    unmount()

    dynamicInserter = yChildrenInserter

    mount(el)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        span of "x2",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 3

    // --

    yChildrenIx.emit(1)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/1",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 3

    // -- switch back to child <-- stream, but now children <-- inserter has only emitted a list of one child

    unmount()

    dynamicInserter = xChildInserter

    mount(el)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/1",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 3

    // --

    xChildIx.emit(3)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        span of "x3",
        " world"
      )
    )

    ReactiveElement.numDynamicSubscriptions(el) shouldBe 3


    assert(numModCalls == 1)
    assert(numBindCalls == 7)
    assert(numChildrenStreamCalls == 3) // due to new re-evaluation semantics of signals as of 0.15.0
  }

  it("onMountInsert switches between children and child.text (streams)") {

    val xChildIx = new EventBus[Int]()

    val xChildTextStream = xChildIx.events.map("x" + _)

    // --

    val yChildrenIx = new EventBus[Int]

    val yChildrenStream = yChildrenIx.events.map { n =>
      Range.inclusive(1, n).map(i => div(s"y$i/$n"))
    }

    // --

    val xChildInserter = child.text <-- xChildTextStream

    val yChildrenInserter = children <-- yChildrenStream

    var dynamicInserter: Inserter = xChildInserter

    // --

    val el = div(
      "Hello ",
      onMountInsert { _ =>
        dynamicInserter
      },
      " world"
    )

    mount(el)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        " world"
      )
    )

    // --

    unmount()

    dynamicInserter = yChildrenInserter

    mount(el)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        " world"
      )
    )

    // --

    yChildrenIx.emit(1)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/1",
        " world"
      )
    )

    // --

    yChildrenIx.emit(2)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/2",
        div of "y2/2",
        " world"
      )
    )

    // --

    unmount()

    dynamicInserter = xChildInserter

    mount(el)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/2",
        div of "y2/2",
        " world"
      )
    )

    // --

    xChildIx.emit(1)

    expectNode(
      div of(
        "Hello ",
        // Note: no extra sentinel node! For performance.
        "x1",
        " world"
      )
    )

    // --

    xChildIx.emit(2)

    expectNode(
      div of(
        "Hello ",
        // Note: no extra sentinel node! For performance.
        "x2",
        " world"
      )
    )

    // --

    unmount()

    dynamicInserter = yChildrenInserter

    mount(el)

    expectNode(
      div of(
        "Hello ",
        sentinel, // sentinel node already re-inserted by the children inserter
        "x2",
        " world"
      )
    )

    // --

    yChildrenIx.emit(3)

    expectNode(
      div of(
        "Hello ",
        sentinel,
        div of "y1/3",
        div of "y2/3",
        div of "y3/3",
        " world"
      )
    )
  }

  it("onFocusMount") {

    val el = div(
      onMountFocus, // I'd love to avoid this line compiling, but I can't.
      input(defaultValue("Your name "), onMountFocus),
      textArea(defaultValue("Your name "), onMountFocus)
    )

    mount(el)
  }

  it ("Element lifecycle owners can not be used after unmount") {

    var cleanedCounter = 0

    var log = List[Int]()
    val bus = new EventBus[Int]
    val observer = Observer[Int](ev => log = log :+ ev)

    var internalOwner: Owner = null

    val el = sectionTag(
      idAttr := "fullSection",
      mainTag(
        div(
          "Hello",
          className := "divClass",
          span(
            idAttr := "someSpan",
            className := "spanClass",
            onMountCallback(ctx => {
              internalOwner = ctx.owner
              bus.events.addObserver(observer)(ctx.owner)
            })
          )
        )
      )
    )

    mount(el)

    log shouldBe Nil

    // --

    bus.writer.onNext(1)
    log shouldBe List(1)

    bus.writer.onNext(2)
    log shouldBe List(1, 2)

    log = Nil

    // --

    unmount("unmount el")

    bus.writer.onNext(3)

    log shouldBe Nil

    // --

    val caught = intercept[Exception] {
      bus.events.addObserver(observer)(internalOwner)
    }
    assert(caught.getMessage == "Attempting to use owner of unmounted element: section#fullSection > main > div.divClass > span#someSpan")

    bus.writer.onNext(4)

    log shouldBe Nil
  }
}
