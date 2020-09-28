---
title: Basic Interactivity 
---

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L._
import org.scalajs.dom

val nameBus = new EventBus[String]
val colorStream: EventStream[String] = nameBus.events.map { name =>
  if (name == "Sébastien") "red" else "unset" // make Sébastien feel special
}

def Hello(
  helloNameStream: EventStream[String],
  helloColorStream: EventStream[String]
): Div = {
  div(
    fontSize := "20px", // static CSS property
    color <-- helloColorStream, // dynamic CSS property
    strong("Hello, "), // static child element with a grandchild text node
    child.text <-- helloNameStream // dynamic child (text node in this case)
  )
}

val appDiv: Div = div(
  strong("User Welcomer 9000"),
  div(
    "Please enter your name:",
    input(
      typ := "text",
      // extract text entered into this input node whenever the user types in it
      inContext(thisNode => onInput.mapTo(thisNode.ref.value) --> nameBus)
    )
  ),
  div(
    "Please accept our greeting: ",
    Hello(nameBus.events, colorStream)
  )
)

render(node, appDiv)
```

</div>

