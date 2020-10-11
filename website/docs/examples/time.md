---
title: Time
---

<h2>Basic Interval Stream</h2>

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L._
import org.scalajs.dom

val $tick = EventStream.periodic(1000)

val app = div(
  div(
    "Tick #: ",
    child.text <-- $tick.map(_.toString)
  ),
  div(
    "Random #: ",
    child.text <-- $tick.mapTo((scala.util.Random.nextInt() % 100).toString)
  ) 
)

render(containerNode, app)
```

</div>



<h2>Delay</h2>

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L._
import org.scalajs.dom

val clickBus = new EventBus[Unit]

val $maybeAlert = EventStream.merge(
  clickBus.events.mapTo(Some(span("Just clicked!"))),
  clickBus.events.flatMap { _ =>
    EventStream.fromValue(None, emitOnce = true).delay(500)
  }
)

val app = div(
  button(onClick.mapTo(()) --> clickBus, "Click me"),
  child.maybe <-- $maybeAlert
)

render(containerNode, app)
```

</div>



<h2>Debounce</h2>

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L._
import org.scalajs.dom

def emailError(email: String): Option[String] =
  if (email.isEmpty)
    Some("Please fill out email")
  else if (!email.contains('@'))
    Some("Invalid email!")
  else
    None

val inputBus = new EventBus[String]

val $debouncedError: EventStream[Option[String]] = 
  inputBus.events
    .debounce(1000)
    .map(emailError)

val app = div(
  span(
    label("Your email: "),
    input(
      value <-- inputBus.events,
      inContext { thisNode => onInput.mapTo(thisNode.ref.value) --> inputBus }
    ),
  ),
  child <-- $debouncedError.map {
    case Some(err) => span(cls("-error"), "Error: " + err)
    case None      => span(cls("-success"), "Email ok!")
  }
)

render(containerNode, app)
```

</div>
