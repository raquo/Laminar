---
title: Ajax
---

<div class = "mdoc-example">

```scala mdoc:js
import com.raquo.airstream.web.AjaxStream
import com.raquo.airstream.web.AjaxStream.AjaxStreamError
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

// Example based on plain JS version: http://plnkr.co/edit/ycQbBr0vr7ceUP2p6PHy?preview

case class AjaxOption(name: String, url: String)

val options = List(
  AjaxOption("Valid Ajax request", "https://api.zippopotam.us/us/90210"),
  AjaxOption("Download 100MB file (gives you time to abort)", "https://cachefly.cachefly.net/100mb.test"),
  AjaxOption("URL that will fail due to invalid domain", "https://api.zippopotam.uxx/us/90210"),
  AjaxOption("URL that will fail due to CORS restriction", "https://unsplash.com/photos/KDYcgCEoFcY/download?force=true")
)
val selectedOptionVar = Var(options.head)
val pendingRequestVar = Var[Option[dom.XMLHttpRequest]](None)
val eventsVar = Var(List.empty[String])

val app: HtmlElement = div(
  h1("Ajax Tester"),
  options.map { option =>
    div(
      input(
        typ("radio"),
        idAttr(option.name),
        nameAttr("ajaxOption"),
        checked <-- selectedOptionVar.signal.map(_ == option),
        onChange.mapTo(option) --> selectedOptionVar
      ),
      label(forId(option.name), " " + option.name)
    )
  },
  br(),
  div(
    button(
      "Send",
      inContext { thisNode =>
        val clickStream = thisNode.events(onClick).sample(selectedOptionVar.signal)
        val responseStream = clickStream.flatMap { opt =>
          AjaxStream
            .get(
              url = opt.url,
              // These observers are optional, we're just using them for demo
              requestObserver = pendingRequestVar.someWriter,
              progressObserver = eventsVar.updater { (evs, p) =>
                val ev = p._2
                evs :+ s"Progress: ${ev.loaded} / ${ev.total} (lengthComputable = ${ev.lengthComputable})"
              },
              readyStateChangeObserver = eventsVar.updater { (evs, req) =>
                evs :+ s"Ready state: ${req.readyState}"
              }
            )
            .map("Response: " + _.responseText)
            .recover { case err: AjaxStreamError => Some(err.getMessage) }
        }

        List(
          clickStream.map(opt => List(s"Starting: GET ${opt.url}")) --> eventsVar,
          responseStream --> eventsVar.updater[String](_ :+ _)
        )
      }
    ),
    " ",
    button(
      "Abort",
      onClick --> (_ => pendingRequestVar.now().foreach(_.abort()))
    )
  ),
  div(
    h2("Events:"),
    div(children <-- eventsVar.signal.map(_.map(div(_))))
  )
)

// In most other examples, containerNode will be set to this behind the scenes
val containerNode = dom.document.querySelector("#mdoc-html-run0")

render(containerNode, app)
```

</div>

