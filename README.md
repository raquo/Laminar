# Laminar

[![Build Status](https://circleci.com/gh/raquo/Laminar.svg?style=svg)](https://circleci.com/gh/raquo/Laminar)
[![Join the chat at https://gitter.im/Laminar_/Lobby](https://badges.gitter.im/Laminar_/Lobby.svg)](https://gitter.im/Laminar_/Lobby)
![Maven Central](https://img.shields.io/maven-central/v/com.raquo/laminar_sjs1_2.13.svg)

Laminar is a small Scala.js library that lets you build UI components using glitch-free Streams and Signals (reactive state variables). It is simpler and more powerful than virtual DOM based solutions.

    "com.raquo" %%% "laminar" % "0.8.0"   // Scala.js 1.x only




## Why Laminar

Laminar offers a unique blend of simplicity, expressiveness and safety.



### Simplicity
* Extremely predictable behaviour – no magic involved, what you write is what you get
* Source code is very approachable – small, no macros, almost no implicits
* Minimalistic pragmatism – no hardcore FP, no typed effects, no backpressure, etc.
* Precise DOM updates – no [complicated virtual DOM diffing](https://github.com/raquo/Laminar/blob/master/docs/Virtual-DOM.md)
* Native Scala.js lib with no third party or JS dependencies – no JS impedance mismatch



### Expressiveness
* Plain Scala FP and/or OOP composition and abstraction techniques instead of ad-hoc library features ("props" / "context" / "components" / etc.)
* First class, interoperable Event Streams and State reactive variables
* Everything from individual attribute keys, key-value pairs, to whole elements is easily composable and abstractable



### Safety
* Automatic and _mandatory_ memory management for _all_ subscriptions, even user created ones – this is significantly safer than what other libraries mean by "automatic memory management"
* Glitch-free reactive system – consistent observations at no runtime cost
* Precise Scala and JS types for DOM elements, attributes, etc. – no unsafe casting 

I understand that the importance of some of these points might not be immediately apparent. I will eventually write a more detailed blog post about these, but for now the documentation below will have to do. 



## Community & Support

* [Gitter](https://gitter.im/Laminar_/Lobby) for chat and random questions
* [Github issues](https://github.com/raquo/laminar/issues) for bugs, feature requests, and more in-depth discussions
* I also offer commercial support and consulting services for Laminar and Airstream. Ping me at [nikita@raquo.com](nikita@raquo.com).
* **Laminar examples**
  * [Laminar ScalaFiddle](https://scalafiddle.io/sf/3SDr2ii/1) to try things on the fly
  * [Laminar TodoMVC](https://github.com/raquo/laminar-examples) – example implementation of a standard todo list app
  * [sherpal/full-scala-scala-heroku](https://github.com/sherpal/full-scala-scala-heroku) – Laminar setup with Play & Heroku
  * [lolgab/scala-fullstack](https://github.com/lolgab/scala-fullstack) – Laminar setup with Akka HTTP, [Mill](https://github.com/lihaoyi/mill), [Sloth](https://github.com/cornerman/sloth)



## Laminar Addons

* [Waypoint](https://github.com/raquo/Waypoint) – Efficient router for Laminar



## Documentation

Laminar and Airstream are well documented: 

| Laminar | Airstream |
| :--- | :--- |
| **[master](https://github.com/raquo/Laminar/blob/master/docs/Documentation.md)** | **[master](https://github.com/raquo/Airstream/blob/master/README.md)** |
| **[v0.8.0](https://github.com/raquo/Laminar/blob/v0.8.0/docs/Documentation.md)** | **[v0.8.0](https://github.com/raquo/Airstream/blob/v0.8.0/README.md)** |
| **[v0.7.2](https://github.com/raquo/Laminar/blob/v0.7.2/docs/Documentation.md)** | **[v0.7.2](https://github.com/raquo/Airstream/blob/v0.7.2/README.md)** |
| **[v0.6](https://github.com/raquo/Laminar/blob/v0.6/docs/Documentation.md)** | **[v0.5.1](https://github.com/raquo/Airstream/blob/v0.5.1/README.md)** |
| **[v0.5.1](https://github.com/raquo/Laminar/blob/v0.5.1/docs/Documentation.md)** | **[v0.4.1](https://github.com/raquo/Airstream/blob/v0.4.1/README.md)** |
| **[v0.4](https://github.com/raquo/Laminar/blob/v0.4/docs/Documentation.md)** | **[v0.3](https://github.com/raquo/Airstream/blob/v0.3/README.md)** |
| **[v0.3](https://github.com/raquo/Laminar/blob/v0.3/README.md)** | **[v0.2](https://github.com/raquo/Airstream/blob/v0.2/README.md)** |
| **[v0.2](https://github.com/raquo/Laminar/blob/v0.2/README.md)** | **[v0.1](https://github.com/raquo/Airstream/blob/v0.1/README.md)** |

The latest version of Laminar always uses the latest version of Airstream.




## The Problem

To build single page web applications you need a method to keep the user interfaces in sync with the underlying application state. This goes both ways – changes in state should effortlessly propagate to the DOM, and DOM events should trigger changes in application state.

Laminar is a reactive solution to this problem, both UI and state management. See above: [Why Laminar](#why-laminar).



## Quick Start

Laminar's basic building block are elements:

```scala
import com.raquo.laminar.api.L._
 
val streamOfNames: EventStream[String] = ???
val helloDiv: Div = div("Hello, ", child.text <-- streamOfNames)
```

`helloDiv` is a Laminar Div element that contains the text "Hello, `<Name>`", where `<Name>` is the latest value emitted by `streamOfNames`. As you see, `helloDiv` is **self-contained**. It depends on a stream, but is not a stream itself. It manages itself, abstracting away the reactive complexity of its innards from the rest of your program.

Laminar does not use virtual DOM, and a Laminar element is not a virtual DOM node, instead it is linked one-to-one to an actual JS DOM element (available as `.ref`). That means that if you want something about that element to be dynamic, you should define it **inside** the element like we did with `child <-- nameStream` above. This allows for precision DOM updates instead of [inefficient virtual DOM diffing](https://github.com/raquo/Laminar/blob/master/docs/Virtual-DOM.md).

With that out of the way, here is what a pretty simple Laminar "component" could look like:

```scala
def Hello(
  helloNameStream: EventStream[String],
  helloColorStream: EventStream[String]
): Div = {
  div(
    fontSize := "20px", // static CSS property
    color <-- helloColorStream, // dynamic CSS property
    strong("Hello, "), // static child element with a grandchild text node
    child.text <-- helloNameStream // dynamic child (text node in this case)
  )
}
```

Almost the same as what we had before, but now with dynamic color and a bit of styling, and more importantly – abstracted away inside a function. Here's how you use it in your app:

```scala
import com.raquo.laminar.api.L._
import org.scalajs.dom
 
val nameStream: EventStream[String] = ???
val colorStream: EventStream[String] = ???
 
val appDiv: Div = div(
  h1("User Welcomer 9000"),
  div(
    "Please accept our greeting: ",
    Hello(nameStream, colorStream) // Inserts the Laminar div element here
  )
)

// Mount the application into a pre-existing container
render(dom.document.querySelector("#appContainer"), appDiv)
```

Easy, eh? But wait a minute, the streams are coming out of thin air! Fair enough, let's add an input text box for users to type their name into, and get the name from there:

```scala
import com.raquo.laminar.api.L._
import org.scalajs.dom
 
val nameBus = new EventBus[String]
val colorStream: EventStream[String] = nameBus.events.map { name =>
  if (name == "Sébastien") "red" else "unset" // make Sébastien feel special
}
 
val appDiv: Div = div(
  h1("User Welcomer 9000"),
  div(
    "Please enter your name:",
    input(
      typ := "text",
      inContext(thisNode => onInput.mapTo(thisNode.ref.value) --> nameBus) // extract text entered into this input node whenever the user types in it
    )
  ),
  div(
    "Please accept our greeting: ",
    Hello(nameBus.events, colorStream)
  )
)

render(dom.document.querySelector("#appContainer"), appDiv)
```

That's a lot to take in, so let's explain some new features we're using:

Inside the `input` node we're registering an event listener for the `onInput` event, and apply some transformations to grab the input's current text value. Then we pass those text values into `nameBus` using `-->`. `nameBus` is an `EventBus`, a special Subject-like object (in FRP terms) that can grab events from a source like this and re-emit them as a stream of `events`. All this flow is explained in detail in the documentation.

`colorStream` is now derived entirely out of the event stream provided by `nameBus`.
 
For extra clarity: `nameBus.events` is a stream of all values passed to `nameBus`. In this case we wired it to contain a stream of values from the input text box. Whenever the user types into the text box, this stream emits an updated name.

`mapTo` here might seem like magic, but all it does is grab the current value of a mutable DOM reference using a [by-name parameter](https://docs.scala-lang.org/tour/by-name-parameters.html).

We could abstract away the input box to simplify our `appDiv` code. Here's one way to do it:

```scala
def InputBox(caption: String, textBus: WriteBus[String]): Div = {
  div(
    caption,
    input(
      typ := "text",
      inContext(thisNode => onInput.mapTo(thisNode.ref.value) --> textBus)
    )
  )
}
```

Then you just call `InputBox("Please enter your name:", nameBus)` instead of `div("Please enter your name:", input(...))` in `appDiv`.

But this is not the only way! Being a generic component, `InputBox` should probably not assume what events the consumer is interested in (`onInput`, `onKeyUp`, `onChange`?), so instead we could write a component that simply exports the elements that it creates, letting the consumer subscribe to whatever events it cares about on those elements: 

```scala
class InputBox private ( // create instances of InputBox using InputBox.apply only
  val node: Div, // consumers should add this element into the tree
  val inputNode: Input // consumers can subscribe to events coming from this element
)
 
object InputBox {
  def apply(caption: String): InputBox = {
    val inputNode = input(typ := "text")
    val node = div(caption, inputNode)
    new InputBox(node, inputNode)
  }
}
```

And this is how we would use it:

```scala
import com.raquo.laminar.api.L._
import org.scalajs.dom
 
val inputBox = InputBox("Please enter your name:")

val nameStream = inputBox.inputNode
  .events(onInput) // gets the stream of onInput events (works on any Laminar element)
  .mapTo(inputBox.inputNode.ref.value) // gets the current value from the input text box (note: parameter passed by name)
 
val colorStream = nameStream.map { name =>
  if (name == "Sébastien") "red" else "unset" // make Sébastien feel special
}

val appDiv: Div = div(
  h1("User Welcomer 9000"),
  div(
    "Please enter your name:",
    inputBox.node,
  ),
  div(
    "Please accept our greeting: ",
    Hello(nameStream, colorStream) // Inserts the div element here
  )
)

render(dom.document.querySelector("#appContainer"), appDiv)
```

It's all the same behaviour, just different composition. In this pattern the `InputBox` component exposes two important nodes: `node` that should be included into the DOM tree, and `inputNode` that the consuming code might want to listen for events. This is a simple yet powerful pattern for generic components.

As you learn more about Laminar you will see that there are even more ways to structure this same relationship.

Laminar has more exciting features to make building your programs a breeze. There's a lot of documentation explaining all of the concepts and features in much greater detail.

Read the [docs](https://github.com/raquo/Laminar/blob/master/docs/Documentation.md), check out some [examples](https://github.com/raquo/laminar-examples), and join us in [gitter](https://gitter.im/Laminar_/Lobby)!




## My Related Projects

- [Waypoint](https://github.com/raquo/Waypoint) – Efficient router for Laminar
- [Airstream](https://github.com/raquo/Airstream) – State propagation and event streaming library used in Laminar
- [Scala DOM Types](https://github.com/raquo/scala-dom-types) – Type definitions that we use for all the HTML tags, attributes, properties, and styles
- [Scala DOM Builder](https://github.com/raquo/scala-dom-builder) – Low-level Scala & Scala.js library for building and manipulating DOM trees
- [Scala DOM TestUtils](https://github.com/raquo/scala-dom-testutils) – Test that your Javascript DOM nodes match your expectations




## Author

Nikita Gazarov – [@raquo](https://twitter.com/raquo)




## License

Laminar is provided under the [MIT license](https://github.com/raquo/laminar/blob/master/LICENSE.md).

