# Laminar

_Laminar_ is a small reactive UI library for Scala.js, allowing you to interact with the DOM using reactive streams. I'm building _Laminar_ because I believe that UI logic is best expressed with functional reactive programming patterns in a type-safe environment.

    "com.raquo" %%% "laminar" % "0.1"

We have no concept of components because we don't need the conceptual overhead. Write your own functions or classes or anything that somehow provides a reference to a `ReactiveNode`, and you're good.

_Laminar_ uses [Scala DOM Builder](https://github.com/raquo/scala-dom-builder) under the hood, and is very efficient. Instead of using a virtual DOM, it makes precision updates to the real DOM. For example, if you say you need to update an attribute of a node, that's exactly what will happen. There is nothing else happening, no diffing of virtual DOM trees, no redundant re-evaluation of your component. _Laminar_'s API lets you express what exactly needs to happen when without the inefficiency and under-the-hood complexity of virtual DOM.

## Example Laminar "Component"

Here's `Counter.apply`, a contrived example function that produces a Counter "component" that exposes a `node` that should be provided to _Laminar_, and `$count`, a stream of counts generated from user clicks that you do whatever you want with.

```scala
class Counter private (
  val $count: XStream[Int],
  val node: RNode
)
 
object Counter {
  def apply(label: String): Counter = {
    val $incClick = XStream.create[MouseEvent]()
    val $decClick = XStream.create[MouseEvent]()
    
    val $count = XStream
      .merge($incClick.mapTo(1), $decClick.mapTo(-1))
      .fold((a: Int, b: Int) => a + b, seed = 0)
 
    val node = div(
      className := "Counter" // this does not need to be in a separate .apply() call, just for aesthetics
    )(
      button(onClick --> $decClick, "–"),
      child <-- $count.map(count => span(s" :: $count ($label) :: ")),
      button(onClick --> $incClick, "+")
    )
 
    new Counter($count, node)
  }
}
```

Cosmetically, this looks similar to [Outwatch](https://github.com/OutWatch/outwatch), however _Laminar_ is implemented very differently – instead of a virtual DOM it uses [Scala DOM Builder](https://github.com/raquo/scala-dom-builder), which is a simpler foundation for the kind of API that we provide.

There are more _Laminar_ examples in the [`example`](https://github.com/raquo/laminar/tree/master/src/main/scala/com/raquo/laminar/example) directory. If you clone this project, you can run the examples locally by running `fastOptJS::webpack` and opening the `index-fastopt.html` file in your browser.

I will eventually write up a detailed _"Laminar vs the World"_ post to compare it to other solutions and explain why _Laminar_ exists.

## Documentation

### Elements, Attributes, Props, etc.

Import those from `laminar.bundle._`. Naming generally follows native JS DOM naming, except it's camelCased. Some keys are named differently, those are documented in [Scala DOM Types](https://github.com/raquo/scala-dom-types#naming-differences-compared-to-native-html--dom). For example, the `value` *HTML attribute* is named `defaultValue` in Laminar, because [that's how it behaves](https://stackoverflow.com/a/6004028/2601788) and that's what the corresponding *DOM property* is called.

### Special Cases

Laminar is conceptually a simple layer adding a reactive streaming API to Scala DOM Builder. In general there is no magic to it, what goes in goes out, transformed in some obvious way. However, in a few cases we do some ugly things under the hood so that you don't need to pull your hair and still do said ugly things in your own code.

Please let me know via github issues if any of this magic caused you grief. It's supposed to be almost universally helpful.

##### 1. checkbox.onClick + event.preventDefault() = async event stream

All event streams in Laminar emit events synchronously, as soon as they happen, except if the stream is of `onClick` events on an `input(typ := "checkbox")` element, and you have used `preventDefault = true` option in Laminar's API.

In that case each event is sent after a `setTimeout(0)` after the browser triggers it. Without this, you would not have been able to update the `checked` property of this checkbox from a stream that is (synchronously) derived from the stream of `onClick` events, and this is a common practice when building controlled components.

The underlying issue is described in [this StackOverflow answer](https://stackoverflow.com/a/32710212/2601788).

**Escape hatch:** to bypass this magic, call `ev.preventDefault()` manually on the event object instead of using Laminar's `preventDefault` option.

**Watch out:** If you are reading the `checked` property of the checkbox in an affected stream, it might be the opposite of what you'd expect if you know about the native DOM behaviour of temporarily updating this value and then resetting it back. This is deemed a smaller problem than the original issue because it's easier to debug.


## My Related Projects

- [Scala DOM Types](https://github.com/raquo/scala-dom-types) – Type definitions that we use for all the HTML tags, attributes, properties, and styles
- [Scala DOM Builder](https://github.com/raquo/scala-dom-builder) – Low-level Scala & Scala.js library for building and manipulating DOM trees
- [Scala DOM TestUtils](https://github.com/raquo/scala-dom-testutils) – Test that your Javascript DOM nodes match your expectations
- [Snabbdom.scala](https://github.com/raquo/Snabbdom.scala) – Scala.js interface to a popular JS virtual DOM library
- [XStream.scala](https://github.com/raquo/XStream.scala) – Scala.js interface to a simple JS reactive streams library
- [Cycle.scala](https://github.com/raquo/Cycle.scala) – Scala.js interface to a popular JS functional reactive library


## Author

Nikita Gazarov – [raquo.com](http://raquo.com)

## License

_Laminar_ is provided under the [MIT license](https://github.com/raquo/laminar/blob/master/LICENSE.md).
