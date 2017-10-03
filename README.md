# Laminar

_Laminar_ is a small reactive UI library for Scala.js, allowing you to interact with the DOM using reactive streams. I'm building _Laminar_ because I believe that UI logic is best expressed with functional reactive programming patterns in a type-safe environment.

    "com.raquo" %%% "laminar" % "0.1"

We have no concept of components because we don't need the conceptual overhead. Write your own functions or classes or anything that somehow provides a reference to a `ReactiveNode`, and you're good.

_Laminar_ uses [Scala DOM Builder](https://github.com/raquo/scala-dom-builder) under the hood, and is very efficient. Instead of using a virtual DOM, it makes precision updates to the real DOM. For example, if you say you need to update an attribute of a node, that's exactly what will happen. There is nothing else happening, no diffing of virtual DOM trees, no redundant re-evaluation of your component. Laminar's API lets you express what exactly needs to happen when without the inefficiency and under-the-hood complexity of virtual DOM.

## Example Laminar "Component"

Here's `Counter.apply`, a contrived example function that produces a Counter "component" that exposes a `node` that should be provided to Laminar, and `$count`, a stream of counts generated from user clicks that you do whatever you want with.

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
      cls := "Counter" // this does not need to be in a separate .apply() call, just for aesthetics
    )(
      button(onClick --> $decClick, "–"),
      child <-- $count.map(count => span(s" :: $count ($label) :: ")),
      button(onClick --> $incClick, "+")
    )
 
    new Counter($count, node)
  }
}
```

Cosmetically, this looks similar to [Outwatch](https://github.com/OutWatch/outwatch), however Laminar is implemented very differently – instead of a virtual DOM it uses [Scala DOM Builder](https://github.com/raquo/scala-dom-builder), which is a simpler foundation for the kind of API that we provide.

There are more _Laminar_ examples in the [`example`](https://github.com/raquo/laminar/tree/master/src/main/scala/com/raquo/laminar/example) directory. If you clone this project, you can run the examples locally by running `fastOptJS::webpack` and opening the `index-fastopt.html` file in your browser.

I will eventually write up a detailed _"Laminar vs the World"_ post to compare it to other solutions and explain why Laminar exists.

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
