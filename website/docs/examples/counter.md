---
title: Counter 
---

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L._
import org.scalajs.dom

def Counter(label: String, initialStep: Int): HtmlElement = {

  val allowedSteps = List(1, 2, 3, 5, 10)

  val stepVar = Var(initialStep)

  val diffBus = new EventBus[Int]

  val $count: Signal[Int] = diffBus.events.foldLeft(initial = 0)(_ + _)

  div(
    p(
      "Step: ",
      select(
        value <-- stepVar.signal.map(_.toString),
        inContext { thisNode =>
          onChange.mapTo(thisNode.ref.value.toDouble.toInt) --> stepVar
        },
        allowedSteps.map { step =>
          option(value := step.toString, step)
        }
      )
    ),
    p(
      label + ": ",
      b(child.int <-- $count),
      " ",
      // Two different ways to get stepVar's value:
      button(
        "–",
        onClick.mapTo(-1 * stepVar.now()) --> diffBus
      ),
      button(
        "+",
        inContext(_.events(onClick).sample(stepVar.signal) --> diffBus)
      )
    )
  )
}

val app = div(
  h1("Let's count!"),
  Counter("Sheep", initialStep = 3)
)

// In most other examples, containerNode will be set to this behind the scenes
val containerNode = dom.document.querySelector("#mdoc-html-run0")

render(containerNode, app)
```

</div>

