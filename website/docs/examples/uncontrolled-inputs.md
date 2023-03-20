---
title: Uncontrolled Inputs
---

<h2>Listening to User Input</h2>

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

val inputTextVar = Var("")

val checkedVar = Var(false)

val app = div(
  p(
    label("Name: "),
    input(
      onInput.mapToValue --> inputTextVar
    )
  ),
  p(
    "You typed: ",
    child.text <-- inputTextVar
  ),
  p(
    label("I like to check boxes: "),
    input(
      typ("checkbox"),
      onInput.mapToChecked --> checkedVar
    )
  ),
  p(
    "You checked the box: ",
    child.text <-- checkedVar
  )
)

render(containerNode, app)
```

</div>



<h2>Transforming User Input</h2>

In other UI libraries if you want to **transform** user input you need to use controlled components. We have that too, but for relatively simple cases you can use `setAsValue` and `setAsChecked` event processor operators instead.

The way it works is simple: when the event processor reaches the `setAsValue` operator, it writes the string provided to it into `event.target.value`. For this reason you  shouldn't use the `filter` event processor operator before `setAsValue`: `setAsValue` will not be called if the predicate doesn't match. Note that we use the `map` operator here, not `filter`. The `filter` we use is actually a method we call on String to transform it.

If you want to **filter** user input, for example if you want to **prevent** any input containing non-digits (as opposed to stripping out non-digits from such input), you should use Laminar [controlled inputs](https://laminar.dev/examples/controlled-inputs) instead.

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

val zipVar = Var("")

val app = div(
  p(
    label("Zip code: "),
    input(
      placeholder("12345"),
      maxLength(5), // HTML can help block some undesired input
      onInput
        .mapToValue
        .map(_.filter(Character.isDigit))
        .setAsValue --> zipVar
    )
  ),
  p(
    "Your zip code: ",
    child.text <-- zipVar
  ),
  button(
    onClick.mapTo(zipVar.now()) --> (zip => dom.window.alert(zip)),
    "Submit"
  )
)

render(containerNode, app)
```

</div>




<h2>Forms Without Vars</h2>

You don't need to keep track of state in Vars. It is often useful, and more complex code tends to need that for auxiliary reasons, but you can fetch the state from the DOM instead:

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

val inputEl = input(
  placeholder("12345"),
  maxLength(5), // HTML can help block some undesired input
  onInput
    .mapToValue
    .map(_.filter(Character.isDigit))
    .setAsValue --> Observer.empty
)

val app = div(
  form(
    onSubmit
      .preventDefault
      .mapTo(inputEl.ref.value) --> (zip => dom.window.alert(zip)),
    p(
      label("Zip code: "),
      inputEl
    ),
    p(
      button(typ("submit"), "Submit")
    )
  )
)

render(containerNode, app)
```

</div>
