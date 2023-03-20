---
title: Time
---

<h2>Basic Interval Stream</h2>

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

val tickStream = EventStream.periodic(1000)

val app = div(
  div(
    "Tick #: ",
    child.text <-- tickStream.map(_.toString)
  ),
  div(
    "Random #: ",
    child.text <-- tickStream.mapTo(scala.util.Random.nextInt() % 100)
  )
)

render(containerNode, app)
```

</div>



<h2>Delay</h2>

Asynchrony works naturally with observables. In this example, on every click, we render a "Just clicked" message, and also schedule its removal 500ms later. When such a component is unmounted, its streams are stopped automatically, no need for manual cleanup or isMounted checks.

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

val clickBus = new EventBus[Unit]

val maybeAlertStream = EventStream.merge(
  clickBus.events.mapTo(Some(span("Just clicked!"))),
  clickBus.events.flatMap { _ =>
    EventStream.fromValue(None, emitOnce = true).delay(500)
  }
)

val app = div(
  button(onClick.mapTo(()) --> clickBus, "Click me"),
  child.maybe <-- maybeAlertStream
)

render(containerNode, app)
```

</div>



<h2>Debounce</h2>

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

def emailError(email: String): Option[String] =
  if (email.isEmpty)
    Some("Please fill out email")
  else if (!email.contains('@'))
    Some("Invalid email!")
  else
    None

val inputBus = new EventBus[String]

val debouncedErrorStream: EventStream[Option[String]] = 
  inputBus.events
    .debounce(1000)
    .map(emailError)

val app = div(
  span(
    label("Your email: "),
    input(
      value <-- inputBus.events,
      onInput.mapToValue --> inputBus
    )
  ),
  child <-- debouncedErrorStream.map {
    case Some(err) => span(cls("-error"), "Error: " + err)
    case None      => span(cls("-success"), "Email ok!")
  }
)

render(containerNode, app)
```

</div>
