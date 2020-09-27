---
title: Working With Timers
---

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.laminar.api.L._
import org.scalajs.dom

val diffBus = new EventBus[Int]
dom.window.setInterval(
  () =>  diffBus.writer.onNext(scala.util.Random.nextInt),
  1000
)

val app = div(
  "Random number every second: ",
  child.text <-- diffBus.events.map(_.toString)
)

render(node, app)
```

</div>
