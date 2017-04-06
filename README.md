# Laminar

Laminar is a small reactive UI library for Scala.js, allowing you to interact with the DOM using reactive streams.

Laminar has no concept of components because it has no need for them. Write your own functions that accept any kind of inputs and one way or another provide you with a reference to a Node.

Laminar is very efficient. It makes minimal required changes to the DOM. For example, if you say you need to update an attribute of a node, that's exactly what will happen. No other code of yours will be re-evaluated because of that (unlike for example React.js where the render() methods on components will be called recursively on every little change in props).

Currently Laminar depends on a virtual DOM library called [Snabbdom](https://github.com/raquo/Snabbdom.scala), however I am working on removing that dependency. Laminar's API allows us to be even more efficient than Snabbdom by making direct DOM updates. Snabbdom's patching model also doesn't map all that well to our precise-DOM-updates use case.

I created Laminar because I believe that UI is best built with functional reactive programming patterns in a type-safe environment. I will publish a TodoMVC example app to showcase how nice Laminar is to work with as soon as I get a bit of time.

## Example Laminar "component"

Here's `Counter.apply`, a contrived example function that produces a Counter "component" that exposes a node that should be provided to Laminar, and a stream of counts generated from user clicks that you do whatever you want with.

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

Cosmetically, this looks similar to [Outwatch](https://github.com/OutWatch/outwatch), however Laminar is implemented rather differently, and supports more flexible use cases. For example Laminar is more accommodating when you need to render a dynamically updated list of children (yes, I know, not shown in the example above). Laminar also uses my much more type-safe interface to Snabbdom.

I will eventually write up a detailed _"Laminar vs the World"_ post to compare it to other solutions and explain why Laminar exists.

## Status

This is an early preview of an upcoming library. It already works, a few example components are included in the repo.

Next step is to get rid of the Snabbdom dependency. Once that is done, building reusable components with Laminar will become even more pleasant.

Another thing I would like to do is to create a couple wrappers around [XStream](https://github.com/raquo/XStream.scala), the stream library used by Laminar, to provide better types for streams.
  
Once both of these are done I will write proper documentation and publish the library to Maven Central. For now you would need to use `sbt publishLocal` to try it out.

## Author

Nikita Gazarov – [raquo.com](http://raquo.com)

License – MIT
