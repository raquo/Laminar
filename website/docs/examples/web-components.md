---
title: Using Web Components
---

In addition to the code presented, for these examples we use Scala.js / Laminar interfaces for Material UI linked below, and also we load the font files required by those web components by means of `siteConfig.js` for this website. And of course we add the material UI packages as npm dependencies in `build.sbt`.

<h2>Material UI Button</h2>

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L._
import website.webcomponents.material.Button
import org.scalajs.dom

val actionVar = Var("Do the thing")
val allowedIcons = List("🎉", "🚀", "🍉")
val iconVar = Var(initial = allowedIcons.head)

val app = div(
  p(
    label("Button label: "),
    input(
      value <-- actionVar.signal,
      inContext { thisNode =>
        onInput.mapTo(thisNode.ref.value) --> actionVar.writer
      }
    )
  ),
  p(
    label("Button icon: "),
    select(
      inContext { thisNode =>
        onChange.mapTo(thisNode.ref.value) --> iconVar.writer
      },
      value <-- iconVar.signal,
      allowedIcons.map(icon => option(value(icon), icon))
    )
  ),
  p(
    Button(
      _.id := "myButton",
      _.label <-- actionVar.signal,
      _.raised := true,
      _.styles.mdcThemePrimary := "#6200ed",
      _ => onClick --> (_ => dom.window.alert("Click")), // standard event
      _.onMouseOver --> (_ => println("MouseOver")), // "custom" event
      _.slots.icon(span(child.text <-- iconVar.signal)),
      //_ => onMountCallback(ctx => ctx.thisNode.ref.doThing()) // doThing is not implemented, just for reference
    )
  ),
)

render(containerNode, app)
```

The button above is a **[@material/mwc-button](https://github.com/material-components/material-components-web-components/tree/master/packages/button)** web component, used via **[Button.scala](https://github.com/raquo/Laminar/blob/master/websiteJS/src/main/scala/website/webcomponents/material/Button.scala)** interface.

</div>


<div class = "mdoc-example">

<h2>Material UI Slider & ProgressBar</h2>

```scala mdoc:js
import com.raquo.laminar.api.L._
import website.webcomponents.material.{LinearProgressBar, Slider}
import org.scalajs.dom
import scala.scalajs.js

val progressVar = Var(0.0)

val app = div(
  "Select fraction: ",
  p(
    Slider(
      _.pin := true,
      _.min := 0,
      _.max := 20,
       _ => onMountCallback(ctx => {
         js.timers.setTimeout(1) {
           // This component initializes its mdcFoundation asynchronously,
           // so we need a short delay before accessing .layout() on it.
           // To clarify, thisNode.ref already exists on mount, but the web component's
           // implementation of layout() depends on thisNode.ref.mdcFoundation, which is
           // populated asynchronously for some reason so it's not available on mount.
           dom.console.log(ctx.thisNode.ref.layout())
         }
       }),
      slider => inContext { thisNode =>
        slider.onInput.mapTo(thisNode.ref.value / 20) --> progressVar
      }
    )
  ),
  p("You've selected:"),
  div(
    LinearProgressBar(
      _.progress <-- progressVar.signal
    )
  )
)

render(containerNode, app)
```

The slider control above is a **[@material/mwc-slider](https://github.com/material-components/material-components-web-components/tree/master/packages/slider)** web component, used via **[Slider.scala](https://github.com/raquo/Laminar/blob/master/websiteJS/src/main/scala/website/webcomponents/material/Slider.scala)** interface.

The progress bar is a **[@material/mwc-linear-progress](https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress)** web component, used via **[LinearProgressBar.scala](https://github.com/raquo/Laminar/blob/master/websiteJS/src/main/scala/website/webcomponents/material/Slider.scala)** interface.

</div>
