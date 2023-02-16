package com.raquo.laminar

import com.raquo.domtestutils.matching.Rule
import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.AirstreamFixtures.Effect
import com.raquo.laminar.nodes.ChildNode
import com.raquo.laminar.utils.UnitSpec
import org.scalatest.BeforeAndAfter

import scala.collection.mutable

class ChildrenReceiverSpec extends UnitSpec with BeforeAndAfter {

  case class Foo(id: String, version: Int)

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

    val childrenBus = new EventBus[Vector[ChildNode.Base]]
    val childrenStream = childrenBus.events

    val span0 = span(text0)
    val span1 = span(text1)
    val div2 = div(text2)
    val div3 = div(text3)
    val span4 = span(text4)
    val span5 = span(text5)

    mount(mainTag(children <-- childrenStream))
    expectChildren("none")

    childrenBus.writer.onNext(Vector())

    childrenBus.writer.onNext(Vector(span0))
    expectChildren("append #1:", span of text0)

    childrenBus.writer.onNext(Vector(span0, span1))
    expectChildren("append #2:", span of text0, span of text1)

    childrenBus.writer.onNext(Vector(div2, span0, span1))
    expectChildren("prepend:", div of text2, span of text0, span of text1)

    childrenBus.writer.onNext(Vector(div2, span1))
    expectChildren("remove:", div of text2, span of text1)

    childrenBus.writer.onNext(Vector(div2, div3))
    expectChildren("replace:", div of text2, div of text3)

    childrenBus.writer.onNext(Vector(span1, span0))
    expectChildren("replaceAll:", span of text1, span of text0)

    childrenBus.writer.onNext(Vector(span0, span1))
    expectChildren("switch places:", span of text0, span of text1)

    childrenBus.writer.onNext(Vector(span1, span0, span4))
    expectChildren("switch places & append:", span of text1, span of text0, span of text4)

    childrenBus.writer.onNext(Vector(span1, span0, span5, span4))
    expectChildren("insert:", span of text1, span of text0, span of text5, span of text4)

    childrenBus.writer.onNext(Vector(span5, span4))
    expectChildren("remove #2:", span of text5, span of text4)

    childrenBus.writer.onNext(Vector(span1, span5, span4))
    expectChildren("prepend #2:", span of text1, span of text5, span of text4)

    childrenBus.writer.onNext(Vector(span1, span5, span4, span0, div2, div3))
    expectChildren("insert #2:", span of text1, span of text5, span of text4, span of text0, div of text2, div of text3)

    childrenBus.writer.onNext(Vector(span1, span5, span4, span0))
    expectChildren("remove #3:", span of text1, span of text5, span of text4, span of text0)

    childrenBus.writer.onNext(Vector(span0, span1, div2, div3, span4))
    expectChildren("mix:", span of text0, span of text1, div of text2, div of text3, span of text4)

    childrenBus.writer.onNext(Vector(div3, div2, span1, span4, span0))
    expectChildren("reorder:", div of text3, div of text2, span of text1, span of text4, span of text0)

    childrenBus.writer.onNext(Vector())
    expectChildren("clear:")

    def expectChildren(clue: String, childRules: Rule*): Unit = {
      withClue(clue) {
        val first: Rule = "Hello"
        val last: Rule = articleTag of "World"
        val rules: Seq[Rule] = (sentinel: Rule) +: childRules

        expectNode(mainTag.of(rules: _*))
      }
    }
  }

  it("raw split stream timing") {

    val effects = mutable.Buffer[Effect[String]]()

    val bus = new EventBus[List[Foo]]

    var ix = 0

    // #Note: `identity` instead of the `_.distinct` default
    val splitSignal = bus.events.split(_.id, distinctCompose = identity)((id, initialFoo, fooSignal) => {
      ix += 1
      val thisIx = ix
      effects += Effect(s"render-$id-$thisIx", initialFoo.toString)
      div(
        "ID: " + id,
        span(
          child.text <-- (
            fooSignal
              .debugSpyEvents(foo => effects += Effect(s"fooSignal-child1-$id-$thisIx", foo.toString))
              .map(_.id)
          )
        ),
        child.text <-- (
          fooSignal
            .debugSpyEvents(foo => effects += Effect(s"fooSignal-child2-$id-$thisIx", foo.toString))
            .map(_.version)
        )
      )
    }).debugSpyEvents(els => effects += Effect("splitSignal", els.map(_.ref.outerHTML).toString))

    // --

    val el = div(
      "Hello",
      //onMountUnmountCallback(_ => println(s"[] MOUNTED EL"), _ => println(s"[] UNMOUNTED EL")),
      children <-- splitSignal
    )

    mount("mount-1", el)

    expectNode(div like (
      "Hello",
      sentinel
    ))

    effects shouldBe mutable.Buffer(
      Effect("splitSignal","List()")
    )

    effects.clear()

    // --

    bus.emit(List(Foo("a", 1), Foo("b", 10)))

    expectNode(div like (
      "Hello",
      sentinel,
      div like ("ID: a", span like "a", "1"),
      div like ("ID: b", span like "b", "10")
    ))

    effects shouldBe mutable.Buffer(
      Effect("render-a-1", "Foo(a,1)"),
      Effect("render-b-2", "Foo(b,10)"),
      Effect("splitSignal", "List(<div>ID: a<span><!----></span><!----></div>, <div>ID: b<span><!----></span><!----></div>)"),
      Effect("fooSignal-child1-a-1", "Foo(a,1)"),
      Effect("fooSignal-child2-a-1", "Foo(a,1)"),
      Effect("fooSignal-child1-b-2", "Foo(b,10)"),
      Effect("fooSignal-child2-b-2", "Foo(b,10)")
    )

    effects.clear()

    // --

    bus.emit(List(Foo("a", 1), Foo("b", 10), Foo("c", 100)))

    expectNode(div like (
      "Hello",
      sentinel,
      div like ("ID: a", span like "a", "1"),
      div like ("ID: b", span like "b", "10"),
      div like ("ID: c", span like "c", "100")
    ))

    effects shouldBe mutable.Buffer(
      Effect("render-c-3", "Foo(c,100)"),
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span><!----></span><!----></div>)"),
      Effect("fooSignal-child1-c-3", "Foo(c,100)"),
      Effect("fooSignal-child2-c-3", "Foo(c,100)"),
      Effect("fooSignal-child1-a-1", "Foo(a,1)"),
      Effect("fooSignal-child2-a-1", "Foo(a,1)"),
      Effect("fooSignal-child1-b-2", "Foo(b,10)"),
      Effect("fooSignal-child2-b-2", "Foo(b,10)")
    )

    effects.clear()

    // --

    unmount("unmount-1")

    effects shouldBe mutable.Buffer()

    // --

    mount("mount-2", el)

    effects shouldBe mutable.Buffer(
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span>c</span>100</div>)"),
      Effect("fooSignal-child1-a-1", "Foo(a,1)"),
      Effect("fooSignal-child2-a-1", "Foo(a,1)"),
      Effect("fooSignal-child1-b-2", "Foo(b,10)"),
      Effect("fooSignal-child2-b-2", "Foo(b,10)"),
      Effect("fooSignal-child1-c-3", "Foo(c,100)"),
      Effect("fooSignal-child2-c-3", "Foo(c,100)")
    )

    effects.clear()

    // --

    unmount("unmount-2")

    mount("mount-3", el)

    unmount("unmount-4")

    mount("mount-5", el)

    effects shouldBe mutable.Buffer(
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span>c</span>100</div>)"),
      Effect("fooSignal-child1-a-1", "Foo(a,1)"),
      Effect("fooSignal-child2-a-1", "Foo(a,1)"),
      Effect("fooSignal-child1-b-2", "Foo(b,10)"),
      Effect("fooSignal-child2-b-2", "Foo(b,10)"),
      Effect("fooSignal-child1-c-3", "Foo(c,100)"),
      Effect("fooSignal-child2-c-3", "Foo(c,100)"),
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span>c</span>100</div>)"),
      Effect("fooSignal-child1-a-1", "Foo(a,1)"),
      Effect("fooSignal-child2-a-1", "Foo(a,1)"),
      Effect("fooSignal-child1-b-2", "Foo(b,10)"),
      Effect("fooSignal-child2-b-2", "Foo(b,10)"),
      Effect("fooSignal-child1-c-3", "Foo(c,100)"),
      Effect("fooSignal-child2-c-3", "Foo(c,100)")
    )

    effects.clear()

    // --

    bus.emit(List(Foo("a", 1), Foo("c", 101)))

    effects shouldBe mutable.Buffer(
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: c<span>c</span>100</div>)"),
      Effect("fooSignal-child1-a-1", "Foo(a,1)"),
      Effect("fooSignal-child2-a-1", "Foo(a,1)"),
      Effect("fooSignal-child1-c-3", "Foo(c,101)"),
      Effect("fooSignal-child2-c-3", "Foo(c,101)")
    )

    effects.clear()

    // --

    bus.emit(List(Foo("b", 2), Foo("c", 102)))

    effects shouldBe mutable.Buffer(
      Effect("render-b-4", "Foo(b,2)"),
      Effect("splitSignal", "List(<div>ID: b<span><!----></span><!----></div>, <div>ID: c<span>c</span>101</div>)"),
      Effect("fooSignal-child1-b-4", "Foo(b,2)"),
      Effect("fooSignal-child2-b-4", "Foo(b,2)"),
      Effect("fooSignal-child1-c-3", "Foo(c,102)"),
      Effect("fooSignal-child2-c-3", "Foo(c,102)")
    )

    effects.clear()

    // --

  }

  it("raw split signal timing") {

    val effects = mutable.Buffer[Effect[String]]()

    val modelsVar = Var(List(Foo("initial", 1)))

    var ix = 0

    // #Note: `identity` instead of the `_.distinct` default
    val splitSignal = modelsVar.signal.split(_.id, distinctCompose = identity)((id, initialFoo, fooSignal) => {
      ix += 1
      val thisIx = ix
      effects += Effect(s"render-$id-$thisIx", initialFoo.toString)
      div(
        "ID: " + id,
        //onMountUnmountCallback(_ => println(s"[] mounted ${id}-${thisI}"), _ => println(s"[] unmounted ${id}-${thisI}")),
        span(
          child.text <-- (
            fooSignal
              .debugSpyEvents(foo => effects += Effect(s"fooSignal-child1-$id-$thisIx", foo.toString))
              .map(_.id)
            )
        ),
        child.text <-- (
          fooSignal
            .debugSpyEvents(foo => effects += Effect(s"fooSignal-child2-$id-$thisIx", foo.toString))
            .map(_.version)
          )
      )
    }).debugSpyEvents(els => effects += Effect("splitSignal", els.map(_.ref.outerHTML).toString))

    // --

    val el = div(
      "Hello",
      //onMountUnmountCallback(_ => println(s"[] MOUNTED EL"), _ => println(s"[] UNMOUNTED EL")),
      children <-- splitSignal
    )

    mount("mount-1", el)

    expectNode(div like (
      "Hello",
      sentinel,
      div like ("ID: initial", span like "initial", "1"),
    ))

    effects shouldBe mutable.Buffer(
      Effect("render-initial-1", "Foo(initial,1)"),
      Effect("fooSignal-child1-initial-1", "Foo(initial,1)"),
      Effect("fooSignal-child2-initial-1", "Foo(initial,1)"),
      Effect("splitSignal","List(<div>ID: initial<span>initial</span>1</div>)"),
    )

    effects.clear()

    // --

    modelsVar.set(List(Foo("a", 1), Foo("b", 10)))

    expectNode(div like (
      "Hello",
      sentinel,
      div like ("ID: a", span like "a", "1"),
      div like ("ID: b", span like "b", "10")
    ))

    effects shouldBe mutable.Buffer(
      Effect("render-a-2", "Foo(a,1)"),
      Effect("render-b-3", "Foo(b,10)"),
      Effect("splitSignal", "List(<div>ID: a<span><!----></span><!----></div>, <div>ID: b<span><!----></span><!----></div>)"),
      Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      Effect("fooSignal-child1-b-3", "Foo(b,10)"),
      Effect("fooSignal-child2-b-3", "Foo(b,10)")
    )

    effects.clear()

    // --

    modelsVar.set(List(Foo("a", 1), Foo("b", 10), Foo("c", 100)))

    expectNode(div like (
      "Hello",
      sentinel,
      div like ("ID: a", span like "a", "1"),
      div like ("ID: b", span like "b", "10"),
      div like ("ID: c", span like "c", "100")
    ))

    effects shouldBe mutable.Buffer(
      Effect("render-c-4", "Foo(c,100)"),
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span><!----></span><!----></div>)"),
      Effect("fooSignal-child1-c-4", "Foo(c,100)"),
      Effect("fooSignal-child2-c-4", "Foo(c,100)"),
      Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      Effect("fooSignal-child1-b-3", "Foo(b,10)"),
      Effect("fooSignal-child2-b-3", "Foo(b,10)")
    )

    effects.clear()

    // --

    unmount("unmount-1")

    effects shouldBe mutable.Buffer()

    // --

    mount("mount-2", el)

    effects shouldBe mutable.Buffer(
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span>c</span>100</div>)"),
      Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      Effect("fooSignal-child1-b-3", "Foo(b,10)"),
      Effect("fooSignal-child2-b-3", "Foo(b,10)"),
      Effect("fooSignal-child1-c-4", "Foo(c,100)"),
      Effect("fooSignal-child2-c-4", "Foo(c,100)")
    )

    effects.clear()

    // --

    unmount("unmount-2")

    mount("mount-3", el)

    unmount("unmount-4")

    mount("mount-5", el)

    effects shouldBe mutable.Buffer(
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span>c</span>100</div>)"),
      Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      Effect("fooSignal-child1-b-3", "Foo(b,10)"),
      Effect("fooSignal-child2-b-3", "Foo(b,10)"),
      Effect("fooSignal-child1-c-4", "Foo(c,100)"),
      Effect("fooSignal-child2-c-4", "Foo(c,100)"),
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span>c</span>100</div>)"),
      Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      Effect("fooSignal-child1-b-3", "Foo(b,10)"),
      Effect("fooSignal-child2-b-3", "Foo(b,10)"),
      Effect("fooSignal-child1-c-4", "Foo(c,100)"),
      Effect("fooSignal-child2-c-4", "Foo(c,100)")
    )

    effects.clear()

    // --

    modelsVar.set(List(Foo("a", 1), Foo("c", 101)))

    effects shouldBe mutable.Buffer(
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: c<span>c</span>100</div>)"),
      Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      Effect("fooSignal-child1-c-4", "Foo(c,101)"),
      Effect("fooSignal-child2-c-4", "Foo(c,101)")
    )

    effects.clear()

    // --

    modelsVar.set(List(Foo("b", 2), Foo("c", 102)))

    effects shouldBe mutable.Buffer(
      Effect("render-b-5", "Foo(b,2)"),
      Effect("splitSignal", "List(<div>ID: b<span><!----></span><!----></div>, <div>ID: c<span>c</span>101</div>)"),
      Effect("fooSignal-child1-b-5", "Foo(b,2)"),
      Effect("fooSignal-child2-b-5", "Foo(b,2)"),
      Effect("fooSignal-child1-c-4", "Foo(c,102)"),
      Effect("fooSignal-child2-c-4", "Foo(c,102)")
    )

    effects.clear()

    // --

  }

  it("split signal timing - with distinct") {

    val effects = mutable.Buffer[Effect[String]]()

    val modelsVar = Var(List(Foo("initial", 1)))

    var ix = 0

    // #Note: Using `distinct` default now
    val splitSignal = modelsVar.signal.split(_.id)((id, initialFoo, fooSignal) => {
      ix += 1
      val thisIx = ix
      effects += Effect(s"render-$id-$thisIx", initialFoo.toString)
      div(
        "ID: " + id,
        span(
          child.text <-- (
            fooSignal
              .debugSpyEvents(foo => effects += Effect(s"fooSignal-child1-$id-$thisIx", foo.toString))
              .map(_.id)
            )
        ),
        child.text <-- (
          fooSignal
            .debugSpyEvents(foo => effects += Effect(s"fooSignal-child2-$id-$thisIx", foo.toString))
            .map(_.version)
          )
      )
    }).debugSpyEvents(els => effects += Effect("splitSignal", els.map(_.ref.outerHTML).toString))

    // --

    val el = div(
      "Hello",
      children <-- splitSignal
    )

    mount("mount-1", el)

    expectNode(div like (
      "Hello",
      sentinel,
      div like ("ID: initial", span like "initial", "1"),
    ))

    effects shouldBe mutable.Buffer(
      Effect("render-initial-1", "Foo(initial,1)"),
      Effect("fooSignal-child1-initial-1", "Foo(initial,1)"),
      Effect("fooSignal-child2-initial-1", "Foo(initial,1)"),
      Effect("splitSignal","List(<div>ID: initial<span>initial</span>1</div>)"),
    )

    effects.clear()

    // --

    modelsVar.set(List(Foo("a", 1), Foo("b", 10)))

    expectNode(div like (
      "Hello",
      sentinel,
      div like ("ID: a", span like "a", "1"),
      div like ("ID: b", span like "b", "10")
    ))

    effects shouldBe mutable.Buffer(
      Effect("render-a-2", "Foo(a,1)"),
      Effect("render-b-3", "Foo(b,10)"),
      Effect("splitSignal", "List(<div>ID: a<span><!----></span><!----></div>, <div>ID: b<span><!----></span><!----></div>)"),
      Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      Effect("fooSignal-child1-b-3", "Foo(b,10)"),
      Effect("fooSignal-child2-b-3", "Foo(b,10)")
    )

    effects.clear()

    // --

    modelsVar.set(List(Foo("a", 1), Foo("b", 10), Foo("c", 100)))

    expectNode(div like (
      "Hello",
      sentinel,
      div like ("ID: a", span like "a", "1"),
      div like ("ID: b", span like "b", "10"),
      div like ("ID: c", span like "c", "100")
    ))

    effects shouldBe mutable.Buffer(
      Effect("render-c-4", "Foo(c,100)"),
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span><!----></span><!----></div>)"),
      Effect("fooSignal-child1-c-4", "Foo(c,100)"),
      Effect("fooSignal-child2-c-4", "Foo(c,100)"),
      //Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      //Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      //Effect("fooSignal-child1-b-3", "Foo(b,10)"),
      //Effect("fooSignal-child2-b-3", "Foo(b,10)")
    )

    effects.clear()

    // --

    unmount("unmount-1")

    effects shouldBe mutable.Buffer()

    // --

    mount("mount-2", el)

    effects shouldBe mutable.Buffer(
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span>c</span>100</div>)"),
      Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      Effect("fooSignal-child1-b-3", "Foo(b,10)"),
      Effect("fooSignal-child2-b-3", "Foo(b,10)"),
      Effect("fooSignal-child1-c-4", "Foo(c,100)"),
      Effect("fooSignal-child2-c-4", "Foo(c,100)")
    )

    effects.clear()

    // --

    unmount("unmount-2")

    mount("mount-3", el)

    unmount("unmount-4")

    mount("mount-5", el)

    effects shouldBe mutable.Buffer(
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span>c</span>100</div>)"),
      Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      Effect("fooSignal-child1-b-3", "Foo(b,10)"),
      Effect("fooSignal-child2-b-3", "Foo(b,10)"),
      Effect("fooSignal-child1-c-4", "Foo(c,100)"),
      Effect("fooSignal-child2-c-4", "Foo(c,100)"),
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: b<span>b</span>10</div>, <div>ID: c<span>c</span>100</div>)"),
      Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      Effect("fooSignal-child1-b-3", "Foo(b,10)"),
      Effect("fooSignal-child2-b-3", "Foo(b,10)"),
      Effect("fooSignal-child1-c-4", "Foo(c,100)"),
      Effect("fooSignal-child2-c-4", "Foo(c,100)")
    )

    effects.clear()

    // --

    modelsVar.set(List(Foo("a", 1), Foo("c", 101)))

    effects shouldBe mutable.Buffer(
      Effect("splitSignal", "List(<div>ID: a<span>a</span>1</div>, <div>ID: c<span>c</span>100</div>)"),
      //Effect("fooSignal-child1-a-2", "Foo(a,1)"),
      //Effect("fooSignal-child2-a-2", "Foo(a,1)"),
      Effect("fooSignal-child1-c-4", "Foo(c,101)"),
      Effect("fooSignal-child2-c-4", "Foo(c,101)")
    )

    effects.clear()

    // --

    modelsVar.set(List(Foo("b", 2), Foo("c", 102)))

    effects shouldBe mutable.Buffer(
      Effect("render-b-5", "Foo(b,2)"),
      Effect("splitSignal", "List(<div>ID: b<span><!----></span><!----></div>, <div>ID: c<span>c</span>101</div>)"),
      Effect("fooSignal-child1-b-5", "Foo(b,2)"),
      Effect("fooSignal-child2-b-5", "Foo(b,2)"),
      Effect("fooSignal-child1-c-4", "Foo(c,102)"),
      Effect("fooSignal-child2-c-4", "Foo(c,102)")
    )

    effects.clear()

    // --

  }

  it("can move children from one dynamic list to another") {

    val spanA = span("a")
    val spanB = span("b")
    val spanC = span("c")
    val spanD = span("d")
    val spanE = span("e")
    val spanF = span("f")

    val bus1 = new EventBus[List[HtmlElement]]
    val bus2 = new EventBus[List[HtmlElement]]

    val el = div(
      children <-- bus1,
      span("--"),
      children <-- bus2,
    )

    mount(el)

    // --

    expectNode(
      div of(
        sentinel,
        span of "--",
        sentinel
      )
    )

    // --

    EventBus.emit(
      bus1 -> List(spanA, spanB, spanC),
      bus2 -> List(spanD, spanE, spanF)
    )

    expectNode(
      div of(
        sentinel,
        span of "a",
        span of "b",
        span of "c",
        span of "--",
        sentinel,
        span of "d",
        span of "e",
        span of "f"
      )
    )

    EventBus.emit(
      bus1 -> List(spanA),
      bus2 -> List(spanD)
    )

    expectNode(
      div of(
        sentinel,
        span of "a",
        span of "--",
        sentinel,
        span of "d",
      )
    )

    // --

    EventBus.emit(
      bus1 -> List(spanA, spanD),
      bus2 -> List(spanE)
    )

    expectNode(
      div of(
        sentinel,
        span of "a",
        span of "d",
        span of "--",
        sentinel,
        span of "e",
      )
    )

    // --

    EventBus.emit(
      bus1 -> List(spanA),
      bus2 -> List(spanE, spanD)
    )

    expectNode(
      div of(
        sentinel,
        span of "a",
        span of "--",
        sentinel,
        span of "e",
        span of "d",
      )
    )

    // --

    EventBus.emit(
      bus1 -> List(spanD, spanA),
      bus2 -> List(spanE)
    )

    expectNode(
      div of(
        sentinel,
        span of "d",
        span of "a",
        span of "--",
        sentinel,
        span of "e",

      )
    )

    // --

    EventBus.emit(
      bus1 -> List(spanF, spanC),
      bus2 -> List(spanE, spanA, spanD)
    )

    expectNode(
      div of(
        sentinel,
        span of "f",
        span of "c",
        span of "--",
        sentinel,
        span of "e",
        span of "a",
        span of "d",

      )
    )

    // --

    EventBus.emit(
      bus1 -> List(spanF, spanA, spanC, spanD),
      bus2 -> List(spanE)
    )

    expectNode(
      div of(
        sentinel,
        span of "f",
        span of "a",
        span of "c",
        span of "d",
        span of "--",
        sentinel,
        span of "e",
      )
    )

    // --

    EventBus.emit(
      bus1 -> List(spanE),
      bus2 -> List(spanF, spanA, spanC, spanD)
    )

    expectNode(
      div of(
        sentinel,
        span of "e",
        span of "--",
        sentinel,
        span of "f",
        span of "a",
        span of "c",
        span of "d"
      )
    )

    // #TODO[Test]: also test for externally removing an element?
  }

  it("unmount stream") {

    val bus = new EventBus[List[String]]

    val el = div(
      children <-- bus.events.map(texts => texts.map(span(_)))
    )

    // --

    mount(el)

    expectNode(
      div of (
        sentinel
      )
    )

    // --

    bus.emit(List("a", "b"))

    expectNode(
      div of (
        sentinel,
        span of "a",
        span of "b"
      )
    )

    // --

    unmount()

    expectNode(
      el.ref,
      div of(
        sentinel,
        span of "a",
        span of "b"
      )
    )

    mount(el)

    expectNode(
      div of(
        sentinel,
        span of "a",
        span of "b"
      )
    )
  }
}
