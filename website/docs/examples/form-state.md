---
title: Form State & Validation
---

Here is one of many ways you could model form state and validation in Laminar.

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

case class FormState(
  zip: String = "",
  description: String = "",
  showErrors: Boolean = false
) {

  def hasErrors: Boolean = zipError.nonEmpty || descriptionError.nonEmpty

  def zipError: Option[String] = {
    if (zip.forall(Character.isDigit) && zip.length == 5) {
      None
    } else {
      Some("Zip code must consist of 5 digits")
    }
  }

  def descriptionError: Option[String] = {
    if (description.nonEmpty) {
      None
    } else {
      Some("Description must not be empty.")
    }
  }

  def displayError(error: FormState => Option[String]): Option[String] = {
    error(this).filter(_ => showErrors)
  }
}

val stateVar = Var(FormState())

val zipWriter = stateVar.updater[String]((state, zip) => state.copy(zip = zip))

val descriptionWriter = stateVar.updater[String]((state, desc) => state.copy(description = desc))

val submitter = Observer[FormState] { state =>
  if (state.hasErrors) {
    stateVar.update(_.copy(showErrors = true))
  } else {
    dom.window.alert(s"Zip: ${state.zip}; Description: ${state.description}")
  }
}

def renderInputRow(error: FormState => Option[String])(mods: Modifier[HtmlElement]*): HtmlElement = {
  val errorSignal = stateVar.signal.map(_.displayError(error))
  div(
    cls("-inputRow"),
    cls.toggle("x-hasError") <-- errorSignal.map(_.nonEmpty),
    p(mods),
    child.maybe <-- errorSignal.map(_.map(err => div(cls("-error"), err)))
  )
}

val app = div(
  form(
    onSubmit
      .preventDefault
      .mapTo(stateVar.now()) --> submitter,

    renderInputRow(_.zipError)(
      label("Zip code: "),
      input(
        placeholder("12345"),
        controlled(
          value <-- stateVar.signal.map(_.zip),
          onInput.mapToValue.filter(_.forall(Character.isDigit)) --> zipWriter
        )
      ),
      button(
        typ("button"), // "submit" is the default in HTML
        "Set SF zip code",
        onClick.mapTo("94110") --> zipWriter
      )
    ),

    renderInputRow(_.descriptionError)(
      label("Description: "),
      input(
        controlled(
          value <-- stateVar.signal.map(_.description),
          onInput.mapToValue --> descriptionWriter
        )
      ),
      button(
        typ("button"), // "submit" is the default in HTML
        "Clear",
        onClick.mapTo("") --> descriptionWriter
      )
    ),

    p(
      button(typ("submit"), "Submit")
    )
  )
)

render(containerNode, app)
```

</div>
