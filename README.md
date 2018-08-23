# Laminar

[![Build Status](https://travis-ci.org/raquo/Laminar.svg?branch=master)](https://travis-ci.org/raquo/Laminar)
[![Join the chat at https://gitter.im/Laminar_/Lobby](https://badges.gitter.im/Laminar_/Lobby.svg)](https://gitter.im/Laminar_/Lobby)
![Maven Central](https://img.shields.io/maven-central/v/com.raquo/laminar_sjs0.6_2.12.svg)

Laminar is a small Scala.js library that lets you to build UI components using glitch-free reactive Streams, Signals and State variables. It is simpler and more powerful than virtual DOM based solutions.

    "com.raquo" %%% "laminar" % "0.3"




## Why Laminar

Laminar offers a unique blend of simplicity, expressiveness and safety.



### Simplicity
* Extremely predictable behaviour – no magic involved, what you write is what you get
* Source code is very approachable – small, no macros, almost no implicits
* Minimalistic pragmatism. No hardcore FP, no typed effects, etc.
* Precise DOM updates – no [complicated virtual DOM diffing](https://github.com/raquo/Laminar/blob/master/docs/Virtual-DOM.md)
* Native Scala.js lib with only in-house dependencies, no JS impedance mismatch



### Expressiveness
* Plain Scala FP and/or OOP composition and abstraction techniques instead of ad-hoc library features ("props" / "context" / "components" / etc.)
* Everything from individual attribute keys, key-value pairs, to whole elements is easily composable and abstractable
* First class, interoperable Event Streams and State reactive variables



### Safety
* Automatic and _mandatory_ memory management for _all_ subscriptions, even user created ones – this is significantly safer than what other libraries mean by "automatic memory management"
* Glitch-free reactive system – consistent observations at no runtime cost
* Precise Scala and JS types for DOM elements, attributes, etc. – no explicit or implicit casting 

I understand that the importance of some of these points might not be immediately apparent. I will eventually write a more detailed blog post about these, but for now the documentation below will have to do. 




## Community & Support

* [Gitter](https://gitter.im/Laminar_/Lobby) for chat and random questions
* [Github issues](https://github.com/raquo/laminar/issues) for bugs, feature requests, and more in-depth discussions
* I also offer commercial support and consulting services for Laminar and Airstream. Ping me at [nikita@raquo.com](nikita@raquo.com).




## Table of Contents

* [Introduction](#introduction)
* [Documentation](#documentation)
  * [Imports](#imports)
  * [Tags & Elements](#tags--elements)
  * [Modifiers](#modifiers)
    * [Nesting and Children](#nesting-and-children)
    * [Manual Application](#manual-application)
    * [inContext](#incontext)
    * [Reusing Elements](#reusing-elements)
    * [Modifiers FAQ](#modifiers-faq)
  * [Reactive Data](#reactive-data)
    * [Attributes and Properties](#attributes-and-properties)
    * [Event Streams, Signals, and State](#event-streams-signals-and-state)
    * [Individual Children](#individual-children)
    * [Lists of Children](#lists-of-children)
    * [Other Receivers](#other-receivers)
    * [Alternative Syntax for Receivers](#alternative-syntax-for-receivers)
  * [Event System: Emitters, Transformations, Buses](#event-system-emitters-transformations-buses)
    * [Registering a DOM Event Listener](#registering-a-dom-event-listener)
    * [EventBus](#eventbus)
    * [Alternative Event Listener Registration Syntax](#alternative-event-listener-registration-syntax)
    * [Multiple Event Listeners](#multiple-event-listeners)
    * [Event Transformations](#event-transformations)
      * [preventDefault & stopPropagation](#preventdefault--stoppropagation)
      * [useCapture](#usecapture)
      * [Obtaining Typed Event Target](#obtaining-typed-event-target)
  * [Ownership](#ownership)
  * [Memory Management](#memory-management)
  * [Element Lifecycle Events](#element-lifecycle-events)
    * [Parent Change Events](#parent-change-events)
    * [Mount Events](#mount-events)
    * [Order of Lifecycle Events](#order-of-lifecycle-events)
    * [Mount Event Streams](#mount-event-streams)
    * [Lifecycle Events Performance](#lifecycle-events-performance)
  * [Special Cases](#special-cases)
* [Related Projects](#my-related-projects)




## Introduction



### The Problem

To build single page web applications you need a method to keep the user interfaces in sync with the underlying application state. This goes both ways – changes in state should effortlessly propagate to the DOM, and DOM events should trigger changes in application state.

Laminar is a reactive solution to this problem, both UI and state management. See above: [Why Laminar](#why-laminar).



### Quick Start

Laminar's basic building block are elements:

```scala
import com.raquo.laminar.api.L._
 
val streamOfNames: EventStream[String] = ???
val helloDiv: Div = div("Hello, ", child.text <-- streamOfNames)
```

`helloDiv` is a Laminar Div element that contains the text "Hello, `<Name>`", where `<Name>` is the latest value emitted by `streamOfNames`. As you see, `helloDiv` is **self-contained**. It depends on a stream, but is not a stream itself. It manages itself, abstracting away the reactive complexity of its innards from the rest of your program.

Laminar does not use virtual DOM, and a Laminar element is not a virtual DOM node, instead it is linked one-to-one to an actual JS DOM element (available as `.ref`). That means that if you want something about that element to be dynamic, you should define it inside the element like we did with `child <-- nameStream` above. This allows for precision DOM updates instead of [inefficient virtual DOM diffing](https://github.com/raquo/Laminar/blob/master/docs/Virtual-DOM.md).

With that out of the way, here is what a pretty simple Laminar "component" could look like:

```scala
def Hello(helloNameStream: EventStream[String], helloColorStream: EventStream[String]): Div = {
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
  if (name == "Sébastien") "red" else "auto" // make Sébastien feel special
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
    InputBox(node, inputNode)
  }
}
```

And this is how we would use it:

```scala
import com.raquo.laminar.api.L._
import org.scalajs.dom
 
val inputBox = InputBox("Please enter your name:")

val nameStream = inputBox.inputNode
  .events(onInput) // .events(eventProp) gets a stream of <eventProp> events (works on any Laminar element)
  .mapTo(inputBox.inputNode.ref.value) // gets the current value from the input text box
 
val colorStream = nameStream.map { name =>
  if (name == "Sébastien") "red" else "auto" // make Sébastien feel special
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

It's all the same behaviour, just different composition. In this pattern the `InputBox` component exposes two important nodes: `node` that should be included into the DOM tree, and `inputNode` that it knows the consuming code will want to listen for events. This is a simple yet powerful pattern for generic components.

As you learn more about Laminar you will see that there are even more ways to structure this same relationship.

Laminar has more exciting features to make building your programs a breeze. There's a lot of documentation below explaining all of the concepts and features in much greater detail.




## Documentation

Laminar is very simple under the hood. You can see how most of it works just by using "Go to definition" functionality of your IDE. Nevertheless, the documentation provided here will help you understand how everything ties together. Documentation sections progress from basic to advanced, so each next section usually assumes that you've read all previous sections.



### Imports

You have two import choices: `import com.raquo.laminar.api.L._` is the easiest, it brings everything you need from Laminar in scope. Unless indicated otherwise, this import is assumed for all code snippets in this documentation.

Usually you will not need any other imports. In this documentation you will see references to Laminar types and values that are not available with just this one import. Most of those are available as aliases in the `L` object. For example, `ReactiveHtmlElement[dom.html.Element]` is aliased as simply `HtmlElement`, and `Modifier[El]` as `Mod[El]`.

However, you might want to avoid bringing so many values into scope, so instead you can `import com.raquo.laminar.api._`, and access Laminar and Airstream values and types with `L` and `A` prefixes respectively, e.g. `A.EventStream`, `L.div`, etc.`

Do check out the available aliases in the `L` object. It's much more pleasant to write and read `Mod[Input]` than `Modifier[ReactiveHtmlElement[dom.html.Input]]`.

Another import you will want in some cases is `import org.scalajs.dom` – whenever you see `dom.X` in documentation, this import is assumed. This object contains [scala-js-dom](https://github.com/scala-js/scala-js-dom) native JS DOM types.



### Tags & Elements

Laminar uses [Scala DOM Types](https://github.com/raquo/scala-dom-types) listings of typed HTML & SVG tags, attributes, props, event props, etc. For example, this is how we know that `onClick` events produce `dom.MouseEvent` events and not `dom.KeyboardEvent`.

`div` is a `ReactiveHtmlTag[dom.html.Div]`. It's a factory of div elements. `ReactiveHtmlTag` extends `Tag` from _Scala DOM Types_ and contains basic information needed to create such an element, such as its tag name ("div"). 

`div()` is a `ReactiveHtmlElement[dom.html.Div]` (aliased as `Div`). The `apply` comes from the parent trait `TagSyntax`, which comes from [Scala DOM Builder](https://github.com/raquo/scala-dom-builder), a low level DOM manipulation library that lets you build trees of Scala objects (elements) that represent trees of actual JS DOM elements.

`div()` is a Laminar element that is linked to the real JS DOM element that it represents. If you need to, you can access the underlying JS DOM element via the `.ref` property. This is useful mostly for third party integrations. 

`div()` is **not** a virtual DOM element. When you create a Laminar element, Laminar immediately creates the underlying `dom.Element` as well. That reference is immutable, so these two instances will go together for the duration of their lifetimes. In contrast, in a virtual DOM library (which Laminar is not) you typically create new instances of virtual elements when they change, and these get loosely matched to a `dom.Element` which could actually be a different element over time depending on how the updates that you're requesting and the implementation of virtual DOM's diffing algorithm.  

Read on for how to use this element we've created.



### Modifiers

The `div()` call described above creates an empty div element – no children, no attributes, nothing. Here's how to specify desired attributes:

```scala
input(typ := "checkbox", defaultChecked := true)
```

This creates an `input` Laminar element and sets two attributes on it. It's mostly obvious what it does, but how?

As we've established before, `input()` call is actually `TagSyntax[El].apply(modifiers: Modifier[El]*)`. So, we can pass Modifier-s to it. A `Modifier[El]` (aliased as `Mod[El]`) is a simple trait with an `apply(element: El): Unit` method. Conceptually, it's a function that you can apply to an element `El` to modify it in some way. In our case, `typ := "checkbox"` is a `Modifier` that sets the `type` attribute on the element given to it to `"checkbox"`.

`typ` is a `ReactiveHtmlAttr`, and `:=` is simply a method on it that creates a setter – a `Modifier` that sets a specific key to a specific value. You can set DOM props and CSS style props the same way (e.g. `backgroundColor := "red"`)

`typ` is coming from _Scala DOM Types_ and represents the "type" attribute. You should consult _Scala DOM Types_ documentation for a list of [naming differences](https://github.com/raquo/scala-dom-types#naming-differences-compared-to-native-html--dom) relative to the native JS DOM API. It will also explain why the `checked` attribute is called `defaultChecked`, and why it accepts a boolean even though all HTML attributes only ever deal in strings in JS DOM ([Codecs](https://github.com/raquo/scala-dom-types#codecs)).

Note: the `apply` method on attributes and other keys is aliased to `:=` in case you prefer a slightly shorter syntax:

```scala
input(typ("checkbox"), defaultChecked(true)) // 100% equivalent to the previous snippet
```  


#### Nesting and Children

A Laminar Element like `input(typ := "checkbox")` is also a Modifier, one that **appends** itself as a child to the element to which it is being applied. Now you can understand how nesting works in Laminar without any magic:

```scala
val inputCaption = span("First & last name:")
val inputMods = Seq(typ := "text", defaultValue := "Me")  
 
div(
  h1("Hello world", color := "red"),
  inputCaption,
  input(inputMods, name := "fullName"),
  div(
    ">>",
    button("Submit"),
    "<<"
  )
)
```

Well, a tiny bit of magic – strings are not modifiers themselves, but are implicitly converted to `Text` (Laminar text nodes) which _are_ modifiers that append text nodes with a given text to the element in question. They are not implicitly wrapped into `span` elements.

For convenience, `Seq[Modifier[A]]` is also implicitly converted to a `Modifier[A]` that applies all the modifiers in the Seq: see `Implicits.seqToModifier`. This is especially useful when making a component that accepts modifiers as VarArgs. Such design is one way to let users configure the component's elements without the component needing to whitelist all possible configurations in its list of inputs.

`Implicits.optionToModifier` performs a similar conversion for `Option[Modifier[A]]`.  

Modifiers are applied in the order in which they're passed to the element, so what you see in your code is what you get in the resulting DOM.

Notice that the code snippet above does not require you to HTML-escape `"&"` or `"<"`. You're just writing Scala code, you're not writing HTML (or anything that will be parsed as HTML), so don't worry about HTML escaping when using Laminar API. However, this warranty is void when you manually invoke native Javascript APIs (e.g. anything under `.ref`, or any interfaces provided by [scala-js-dom](https://github.com/scala-js/scala-js-dom)). Then you need to know Javascript and the DOM API to know what's safe and what [isn't](https://security.stackexchange.com/questions/32347/dom-based-xss-attacks-what-is-the-most-dangerous-example).


#### Manual Application

Modifiers have a public `apply(element: El)` method that you can use to manually apply modifiers. Using it is a bit clunky, for example: `(color := "red")(element)`. This pattern is discouraged to the extent that it enables imperative coding patterns. You should generally use [Reactive Data](#reactive-data) instead.


#### `inContext`

Sometimes you may need to have a reference to the element to which you want to apply a modifier in order to decide which modifier to apply. For example, a common use case is needing to get the input element's `value` property in order to build a modifier that will send that property to an event bus.

```scala
input(inContext(thisNode => onChange.mapTo(thisNode.ref.value) --> inputStringBus))
```

The general syntax of this feature is `inContext(El => Modifier[El]): Modifier[El]`. `thisNode` refers to the element to which this modifier will be applied. Its type is properly inferred to be `Input`.

Without getting ahead of ourselves too much, `onChange.mapTo(thisNode.ref.value) --> inputStringBus)` is a Modifier that handles `onChange` events on an input element, but as you see it needs a reference to this input element. You could get that reference in a couple different ways, for example:

```scala
val myInput: Input = input(onChange.mapTo(myInput.ref.value) --> inputStringBus)
```

Or

```scala
val myInput = input()
myInput.events(onChange).mapTo(myInput.ref.value).addObserver(inputStringBus)(owner = myInput)
```

But these can be very cumbersome in a real world application. `inContext` provides an easy, inline solution.

Note: all the unfamiliar syntax used here will be explained in other sections of this documentation. 


#### Reusing Elements

Don't. You can't put the same element twice into the real JS DOM – instead, it will be removed from its old location and **moved** into its new location. That's just how the browser DOM works. However, Laminar does not protect you from doing this for performance reasons, so you need to avoid this on your own.

Except for rendering dynamic lists of children (we'll get to that later), you should not need to move a DOM element around. Open a github issue if you want to discuss a valid use case for this.

Also, currently, even if a Laminar element was properly removed from the DOM, it can not be put back in there again because its subscription logic will be dead. This is a limitation with a known fix that will be implemented in the next version.


#### Modifiers FAQ

1. Yes, you can create custom Modifiers by simply extending the Modifier trait. You could for example define attributes or CSS props that are missing from _Scala DOM Types_ by extending `ReactiveHtmlAttr` or `ReactiveStyle`, or for completely custom logic you could create a class extending the `Modifier` trait. That way you could for example make a modifier that applies multiple other modifiers that you often use together.

2. No, Modifiers are not guaranteed to be idempotent. Applying the same Modifier to the same element multiple times might have different results from applying it only once. Setters like `key := value` typically _are_ idempotent, but for example a modifier like `onClick --> observer` isn't. There should be no surprises as long as you know what the modifier is supposed to do in plain English.

4. Yes, Modifiers are generally reusable. The same modifier can usually be applied to multiple nodes. But again, you have to understand what the modifier does. Setting an attribute to a particular value – sure, reusable. Adding a particular element as a child – no, not reusable – see [Reusing Elements](#reusing-elements). If you're writing your own Modifier, don't store element-specific state outside of its `apply` method to make it reusable.



### Rendering

When you create a Laminar element, the underlying real JS DOM element (`.ref`) is created at the same time. However, it is initially detached from the DOM. That is, it does not appear in the document that the user sees. Such an element is _unmounted_.

For the user to see this element we need to _mount_ it into the DOM by either adding it as a Modifier to another element that is (or at some point will become) _mounted_, or if we're dealing with the top element in our application's hierarchy, we ask Laminar to _render_ it into some container `dom.Element` that already exists on the page. Said container must not be managed by Laminar. 

```scala
val appContainer: dom.Element = dom.document.querySelector("#appContainer")
val appElement: Div = div(
  h1("Hello"),
  "Current time is:",
  b("12:00") 
)
 
val root: Root = render(appContainer, appElement)
```

That's it. Laminar will find an element with id "appContainer" in the document, and append `appElement.ref` as its child. For sanity sake, the container should not have any other children, but that's not really a requirement.

To remove your whole app from the DOM, simply call `root.unmount()`. 


### Reactive Data

In the code snippet above we're mounting a completely static element – it will not change over time. This section is about creating elements that will one way or another change over time. All of the above were just the basic mechanics, here we're digging into the meat of Laminar.

In a virtual DOM library like React or Snabbdom, changing an element over time is achieved by creating a new virtual element that contains the information on what the element should look like, then running a diffing algorithm on this new virtual element and the previous virtual element representing the same real JS DOM element. Said diffing algorithm will come up with a list of operations that need to be performed on the underlying JS DOM element in order to bring it to the state prescribed by the new virtual element you've created.  

Laminar does not work like that. We use reactive streams to represent things changing over time. Let's see how this is much simpler than any virtual DOM.

Laminar uses [Airstream](https://github.com/raquo/Airstream) observables (event streams, signals, state). Airstream is an integral part of Laminar, and a huge part of what makes it unique.

**To use Laminar effectively, you need to understand the principles of conventional lazy streams, and have basic knowledge of Airstream. Please do read [Airstream documentation](https://github.com/raquo/Airstream), it's not scary.**


#### Attributes and Properties

Dynamic attributes / properties / css properties are the simplest use case for Laminar's reactive layer:

```scala
val prettyColorStream: EventStream[String] = ???
val myDiv: Div = div(color <-- prettyColorStream, "Hello")
```

This creates a div element with the word "Hello" in it, and a dynamic `color` CSS property. Any time the `prettyColorStream` emits an event, this element's `color` property will be updated to the emitted value. 

The method `<--` comes from `ReactiveStyle` (which is what `color` is). It creates a `Modifier` that subscribes to the given stream. This subscription will be automatically removed when the div element is discarded. More on that in the sections [Stream Memory Management](#stream-memory-management) and [Element Lifecycle Events](#element-lifecycle-events) way below. Don't worry about that for now.


#### Event Streams, Signals, and State

What happens before `prettyColorStream` emitted its first event? To understand that, we need a primer on Airstream reactive variables:

- **EventStream** is a lazy Observable without current value. It represents events that happen over time. Philosophically, there is no such thing as a "current event" or "initial event", so streams have no initial value.

- **Signal** is like EventStream, but with current value. Signal represents a value over time, it always has a current value, and therefore it must have an initial value.

- **State** is like Signal, but it's not lazy, it's strict. It is used to represent application state. Signals are lazy, so whether they execute or not depends on whether they have observers. State will execute reliably, which is often desired.

The `<--` method subscribes to the given observable and updates the DOM whenever the observable emits. `prettyColorStream` is an EventStream, so it will only emit if and when the next event comes in. So, until that time, laminar will not set the color property to any initial or default value.

On the other hand, if `prettyColorStream` was a Signal or a State, the subscription inside the `<--` method would have immediately set the color property to the observable's current value, and behave similar to stream subscription afterwards.

We said that EventStreams and Signals are lazy – that means they wouldn't run without an Observer. By creating a subscription, the `<--` method creates an Observer of the observable that it is given, so we know for sure that it will run.


#### Individual Children

One very important takeaway from the `val myDiv: Div = div(color <-- prettyColorStream, "Hello")` example is that **`myDiv` is an element, not a stream of elements, even though it depends on a stream**. Think about it: you are not creating new elements when `prettyColorStream` emits an event, you are merely setting a style property to a new value on an existing element. This is very efficient and is in stark contrast to the amount of work a virtual DOM needs to do in order to perform a simple change like this.

So, **Laminar elements manage themselves**. A `ReactiveElement` encapsulates all the reactive updates that are happening inside. This means that you can add an element as a child without knowing how it's implemented or whether it's even static or dynamic. So a simple component could look like this:

```scala
// Define your "Component"
def Caption(caption: String, colorStream: EventStream[String]): Span = {
  span(color <-- colorStream, caption)
}
 
// Now use it
val prettyColorStream: EventStream[String] = ???
val myDiv: Div = div(
  "Hello ",
  Caption("World", prettyColorStream)
)
```

This lets you to build loosely coupled applications very easily.

Having a stable reference to `myDiv` also simplifies your code significantly. If myDiv was a stream of divs instead, you'd need to engage in a potentially complex composition exercise to access the latest version of the element. Such useless complexity really adds up as your application grows, and avoiding needless complexity is one of Laminar's most important goals.

However, there _is_ a use case for streams of elements. Laminar elements are tied one-to-one to real JS DOM elements, so if you need a different _type_ of element displayed, you will need to re-create the Laminar element. For example:

```scala
def MaybeBlogUrl(urlSignal: Signal[Option[String]]): Signal[HtmlElement] = {
  urlSignal.map {
    case Some(url) => a(href := url, "a blog")
    case None => i("no blog")
  }
}

val blogUrlSignal: Signal[String] = ???
val app: Div = div(
  "Hello, I have ",
  child <-- MaybeBlogUrl(blogUrlSignal),
  ", isn't it great?"
)
```

It should be pretty obvious what this does: `MaybeBlogUrl` maps input Signal to a Signal of Laminar elements, and `child <-- BlogUrl(blogUrlSignal)` creates a subscription that puts these elements into the div.

The elements are put in the obvious order – what you see is what you get – so, between _"Hello, I have "_ and _", isn't it great?"_ text nodes. Each next emitted element replaces the previously emitted one in the DOM.

EventStreams and State observables work similarly, and there's also `child.maybe` and `child.text` receivers that have more specialized `<--` methods accepting `Observable[Option[Node]]` and `Observable[String]` respectively.

You have to be careful when using streams of elements, whether with `child <--` or not. Make sure to read about unused elements in [Memory Management](#memory-management).


##### Efficiency

You might notice that this is not as efficient as it could be. Any time `blogUrlSignal` emits a new value, a new element is created, even if we're moving from `Some(url1)` to `Some(url2)`. Those are real JS DOM elements too, so if this method is hit often we want to optimize for that. For example, we could do this:

```scala
def MaybeBlogUrl(maybeUrlSignal: Signal[Option[String]]): Signal[HtmlElement] = {
  val urlSignal: Signal[String] = maybeUrlSignal.collect { case Some(url) => url }
  val hasUrlSignal: Signal[Boolean] = maybeUrlSignal.map(_.nonEmpty)
  hasUrlSignal.map {
    case true => a(href <-- urlSignal, "a blog")
    case false => i("no blog")
  }
}
```

Unlike EventStreams, Airstream Signals and State variables only emit distinct values. So if `maybeUrlSignal` emits `Some(url1)` and then `Some(url2)`, `hasUrlSignal` will emit `true`, but only once, not twice. So with this new logic, we create new elements only when we switch from `None` to `Some(url)` or the other way.

However, the following **IS NOT CURRENTLY POSSIBLE**: 

```scala
// Compiles, but does not work yet!
def MaybeBlogUrl(maybeUrlSignal: Signal[Option[String]]): Signal[HtmlElement] = {
  val urlSignal: Signal[String] = maybeUrlSignal.collect { case Some(url) => url }
  val hasUrlSignal: Signal[Boolean] = maybeUrlSignal.map(_.nonEmpty)
  val link = a(href <-- urlSignal, "a blog")
  val noLink = i("no blog")
  hasUrlSignal.map {
    case true => link 
    case false => noLink
  }
}
```

In this case, you would be trying to re-insert `link` and `noLink` elements into the DOM after they were previously removed from it. While this _should_ work, it _doesn't_, because when Laminar removes an element from the DOM, it kills all of the subscriptions bound to it, with no way to resurrect them. This particular use case will work in a future version of Laminar.

You can, however, achieve a similar effect by rendering both the link and no-link elements at the same time inside a container, and hiding one of them with CSS (`display := "none"`):

```scala
// Compiles, but does not work yet!
def MaybeBlogUrl(maybeUrlSignal: Signal[Option[String]]): Span = {
  val urlSignal: Signal[String] = maybeUrlSignal.collect { case Some(url) => url }
  val hasUrlSignal: Signal[Boolean] = maybeUrlSignal.map(_.nonEmpty)
  val linkDisplaySignal: Signal[String] = hasUrlSignal.map {
    case true => "inline"
    case false => "none"
  }
  val nolinkDisplaySignal: Signal[String] = hasUrlSignal.map {
      case true => "none"
      case false => "inline"
    }
  span(
    a(display <-- linkDisplaySignal, href <-- urlSignal, "a blog"),
    i(display <-- nolinkDisplaySignal, "no blog")
  )
}
```

Looking at this, I see that Laminar needs some kind of `noneIf(boolean)` helper for the `display` prop to shorten the boilerplate. @TODO[Feature]

On the bright side, `MaybeBlogUrl` does not return a Signal anymore, just an element. This is good, it's simpler and generally indicates "The Laminar Way".

When engaging in such optimizations, you have to ask yourself – is it worth it? Re-creating a JS DOM element – what we were trying to avoid in these examples – is only a problem when you need to do it often (performance), or when that element needs to maintain some JS DOM state (e.g. `input` elements will lose focus if re-created). To put it in the context of our example – if `maybeUrlSignal` is expected to emit only a few times during a user session, just use the dumbest shortest version of `MaybeBlogUrl` instead of optimizing it for no reason. But if you're building some kind of BlogRoulette app that updates the blog URL ten times a second, then yeah, better optimize it.


#### Lists of Children

Rendering dynamic lists of children efficiently is one of the most challenging parts in UI libraries, and Laminar has a couple solutions for that.


##### ChildrenReceiver / ChildrenSetter

```scala
val childrenSignal: Signal[immutable.Seq[Node]] = ???
div("Hello, ", children <-- childrenSignal)
```

If you have an Observable of desired children elements, this is the most straightforward way to subscribe to it.

As with all Laminar elements, you must avoid reusing elements that were previously removed from the DOM as their subscriptions will not function anymore.

Internally, this `<--` method keeps track of prevChildren and nextChildren. It then "diffs" them. The general idea here is _vaguely_ similar to virtual DOM, but it's much lighter diffing. We only compare elements for equality using `==`, we do not look at any attributes / props / state / children and we do not re-render anything like React.

We make one pass through nextChildren, and detect any inconsistencies by checking that `nextChildren(i).ref == childrenInDom(i)` (simplified). If this is false, we look at prevChildren to see what happened – whether the child was created, moved, or deleted. The algorithm is short (<50LoC total) but efficient.

Laminar will perform absolutely minimal operations that make sense – for example if a new element needs to be inserted, that is the only thing that will happen, _and_ this new element is the only element for which Laminar will spend time figuring out what to do, because all others will match. Reorderings are probably the worst case performance-wise, but still no worse than virtual DOM. You can read the full algorithm in `ChildSetter.updateChildren`.


##### Children Commands

But where would you actually get an observable of a list of children from? For a canonical example, see `TaskListView.taskViewsStream` in [laminar-examples](https://github.com/raquo/laminar-examples). But sometimes this might be unnecessarily hard. It might be easier for you to tell Laminar when you want items added or removed to the list:

```scala
val commandBus = new EventBus[ChildCommand]
val commandStream = commandBus.events
div("Hello, ", children.command <-- commandStream)
```

EventBus is an Airstream data structure that contains both an Observer (`.writer`) and an EventStream (`.events`). It's basically an event stream that you send events to by invoking the Observer. A more complete example:

```scala
val commandBus = new EventBus[ChildCommand]
val commandStream = commandBus.events
var counter = 0
div(
  "Hello, ",
  children.command <-- commandStream,
  button("Add a child", onClick.map { _ =>
    counter += 1
    CollectionCommand.Append(span(s"Child # $counter"))
  } --> commandBus)
)
```

We'll explain the `-->` method and the rest of event handling in later sections. Here we're sending `CollectionCommand.Append(span(s"Child # $counter"))` to `commandBus` on every button click. 

The ChildCommand approach offers the best performance too. Since you are directly telling Laminar what to do, no logic stands between your code and Laminar executing your requests. There is no diffing involved.

The downside of this approach is that you don't have an observable of the list of children naturally available to you, so if you want to display e.g. the number of children somewhere, you will need to create an observable for that yourself. For example:

```scala
val commandBus = new EventBus[ChildCommand]
val commandStream = commandBus.events
val countSignal = commandStream.fold(initial = 0)(_ + 1)

div(
  "Hello, ",
  children.command <-- commandStream,
  button("Add a child", onClick.map( _ =>
    CollectionCommand.Append(span(s"Just another child"))
  ) --> commandBus),
  div(
    "Number of children: ",
    child.text <-- countSignal.map(_.toString)    
  )
)
```

This would need more work if we wanted to support other actions such as removals, and you need to be careful to not overcomplicate things here, especially if you're only doing this for performance reasons. Make sure you actually have a performance problem first.

---

You can mix both `children <-- *` and `children.command <-- *` and any other children or modifiers in any order inside one element, and they will happily coexist with no interference. Correct order of elements will be maintained even in edge cases such as showing zero elements. Laminar adds a sentinel `<comment>` node into the DOM for each `child* <-- *` receiver to make this possible. These nodes effectively "reserve the spot" for children provided by a particular receiver.


#### Other Receivers


##### Focus Receiver

```scala
val focusStream: EventStream[Boolean]
input(focus <-- focusStream)
```

You can dynamically focus or blur an element by emitting `true` or `false` respectively. Keep in mind that this is just compositional sugar over calling `myInput.ref.focus()` or `myInput.ref.blur()` with all the same limitations – emitting `true` once will not make Laminar enforce focused state on the input until you emit `false`, you'd need more logic for that.


#### Alternative Syntax for Receivers

Sometimes you want to separate defining the DOM structure from adding reactive parts to it (for example you might want the former to be contained in a component), or you might need to resolve what would otherwise be a dependency loop between an element and a reactive variable. So you can do this:

```scala
myElement <-- color <-- prettyColorStream
myElement <-- child <-- childSignal
myInput <-- focus <-- focusStream
// (and so on)
```

All of these are equivalent to modifier applications like `(color <-- prettyColorStream)(myElement)`. The various `myElement.<--` methods are defined on `ReactiveElement`.



### Event System: Emitters, Transformations, Buses 

#### Registering a DOM Event Listener

Laminar uses native JS DOM events. We do not have a synthetic event system like React. There is no event pooling, no magic going on. This means there is nothing Laminar-specific to learn about events themselves. There's one exception for certain `onClick` events on checkboxes, see [Special Cases](#special-cases).

To start listening for DOM events, you need to register a listener for a specific event type on a specific element. In Laminar, `EventPropEmitter` is a `Modifier` that performs this action.

This is how it's done in the simplest case:

```scala
val clickObserver = Observer[dom.MouseEvent](onNext = ev => dom.console.log(ev.screenX))
val element: Div = div(onClick --> clickObserver, "Click me")
```

`Observer` is an Airstream type that is typically used to perform side effects on incoming events. Usually it's used like this: `someStream.addObserver(someObserver)(owner)`. This would ensure that `someStream` runs (remember, streams are lazy), and would call `someObserver.onNext(ev)` for every new event that `someStream` emits.

This `-->` method simply registers an event listener on `element` which calls `clickObserver.onNext(ev)` for every click event that comes in, and then the observer prints this event's X coordinate to the console.


#### EventBus

The contrived example above does not feel like reactive programming at all. As shown, it's just a cumbersome way to define a simple callback. Instead, we usually want to get a stream of events from an element. To do that, we need an Observer that will expose a stream of events that it receives. In FRP terms this is usually called a "Subject" or a "PublishSubject".

In Laminar, we have an `EventBus`. Same general idea, but instead of _being_ both an Observer and an Observable, it _contains_ them as `.writer` and `.events` respectively. This design makes it possible to easily share write-only or read-only parts of an EventBus, with no upcasting loopholes. You should generally avoid passing the whole EventBus to child components unless you really do need the child component to both write events to the bus and read the entirety of events written to that bus (including events written by other components into that bus).

Now let's see how we can use EventBus for the same logic:

```scala
val clickBus = new EventBus[dom.MouseEvent]
val element: Div = div(onClick --> clickBus.writer, "Click me") // .writer is actually optional here, for convenience
 
val coordinateObserver = Observer[Int](onNext = x => dom.console.log(x))
val coordinateStream: EventStream[Int] = clickBus.events.map(ev => ev.screenX)
 
coordinateStream.addObserver(coordinateObserver)(owner = element)
```

The first two lines work exactly the same as the previous example, except that `clickBus.writer` is now the Observer that we send the events to. For convenience, the `-->` method will also accept the event bus itself: `div(onClick --> clickBus, "Click me")` will have exactly the same effect.

Next, we define an observer that will receive and print out integers, and a stream of X coordinates derived from the stream of clicks.

Finally, we subscribe the coordinate observer to the stream of coordinates. This completes our contrived pipeline. In real life this component might only be exposing the stream of coordinates, and consuming components would provide their own observers. Or this same component could be deriving some of its own state from the click stream, such as tracking and displaying the number of clicks.

You might have noticed that to add an Observer to a stream, we need to specify an owner. Ownership is a very important Airstream feature that allows for memory safety. We will explain this later. In plain English this means that the `coordinateObserver` will be automatically unsubscribed from `coordinateStream` when `element` will be removed from the DOM. We will get into the details of Ownership below.


##### Transforming Observers

You can `contramap` and `filter` Observers, for example:

```scala
val pieObserver: Observer[Pie] = Observer(onNext = bakePie)
val appleObserver: Observer[Apple] = pieObserver.contramap(apple => makeApplePie(apple))
 
def AppleComponent(observer: Observer[Apple]): Div = {
  val appleStream: EventStream[Apple] = ???
  val node = div("Apple Component", ???)
  appleStream.addObserver(observer)(owner = node)
  node
}
 
val app = AppleComponent(appleObserver)
```

In this pattern AppleComponent has no knowledge of pies. All it knows is how to produce apples and send them to an apple observer. Delicious culinary metaphors aside, replace pies with ajax requests and apples with todo items that need to be created, and you can see how this separation of concerns is invaluable.


##### Feeding Multiple Sources into EventBus

Note that Airstream Observers, including `eventBus.writer`, can subscribe to multiple observables. So you can for example render a dynamic list of children, each of which sends its own events to the same Observer, possibly with information included in the event needed to identify which child the event came from. You can see an example of this pattern used in `TaskListView.taskViewsStream` in [laminar-examples](https://github.com/raquo/laminar-examples).

This can be achieved either with `addSource` or `addObserver`. Basically, in terms of laziness, with `addSource` EventBus behaves like a stream that merges other streams – much like `EventStream.merge(stream1, stream2)`, but with the ability to add and remove source streams at any time. So, if you do `eventBus.writer.addSource(sourceStream)(childOwner)`, `sourceStream` will not be started until and unless `eventBus.stream` is started. Conversely, `sourceStream.addObserver(eventBus.writer)(childOwner)` will immediately start `sourceStream` even if `eventBus` does not have any observers.

The best part of this pattern is that you don't need to write custom logic to call `removeSource` or `removeObserver` when removing a child from the list of children. `childOwner` will take care of that. Typically that would be the child's Laminar element. Laminar elements kill the subscriptions that they own when they get removed from the DOM. 


#### Alternative Event Listener Registration Syntax

Imagine you're building `TextInput`, a component that wraps an `input` element into a `div` with some styles added on top:

```scala
class TextInput private (
  val wrapperNode: Div,
  val inputNode: Input
)
 
object TextInput {
  def apply(caption: String): TextInput = {
    val inputNode = input(typ := "text", color := "grey")
    val wrapperNode = div(caption, inputNode)
    new TextInput(wrapperNode, inputNode)
  }
}
```

This generic component does not necessarily know what events on `inputNode` the consumer will care about when using it – `onKeyUp`, `onKeyPress`, `onChange`, `onInput`, others? There's just too many possibilities. And of course you wouldn't want to clutter your `TextInput`'s API by e.g. exposing all possibly useful event streams as vals on the `TextInput` class.

That's what `ReactiveElement.events(eventProp)` method was made for:

```scala
val textInput = TextInput("Full name:")
val changeEventStream: EventStream[dom.Event] = textInput.inputNode.events(onChange)
```

Under the hood, `element.events(eventProp)` creates an EventBus, applies an `eventProp --> eventBus` modifier to `element`, and returns `eventBus.events`. That's all there is to it, no magic – just alternative syntax that makes it easier to compose your components without tight coupling.


#### Multiple Event Listeners
 
Just like in native JS DOM, nothing is stopping you from registering two or more event listeners for the same event type on the same element:
 
 ```scala
div(onClick --> clickBus1, onClick --> clickBus2, "Click me")
``` 

In this case, two event listeners will be registered on the DOM node, one of them sending events to `clickBus1`, the other sending the exact same events to `clickBus2`. Of course, this works the same regardless of what syntax you use to register event listeners (see previous section).

In most cases you could simply add another listener to `clickBus.events` to achieve whatever you needed multiple event listeners bound to the same element for – it's easier and more performant in extreme cases (thousands of nodes, I'd guess).

However, sometimes simpler composition is more important. For example, consider the `TextInput` component mentioned above. That component itself might have an internal need to listen to its own `onChange` events (e.g. for some built-in validation), and it will not interfere with any other `onChange` event listeners the end user might want to add to it.


#### Event Transformations

Often times you don't really need a stream of e.g. click events – you only care about the subset of the events or only parts of their data. With the Alternative Syntax described above you would just use Airstream operators to transform the stream of events, like this:

```scala
val incrementButton = button("more")
val decrementButton = button("less")
 
val diffStream: EventStream[Int] = EventStream.merge(
  incrementButton.events(onClick).mapTo(1), 
  decrementButton.events(onClick).mapTo(-1) 
) // this stream emits 1 or -1 depending on which button was clicked
```

However, when using the standard `onClick --> eventBus` syntax, there is no stream that you could operate on before the events hit `eventBus`. Instead, we provide a different way to transform events:

```scala
val diffBus = new EventBus[Int]
val incrementButton = button("more", onClick.mapTo(1) --> diffBus)
val decrementButton = button("less", onClick.mapTo(-1) --> diffBus)
val diffStream: EventStream[Int] = diffBus.events // emits 1 or -1
```

This `mapTo` method is defined on `EventPropTransformation`, which `onClick` (`ReactiveEventProp`) is implicitly converted to. Also available are `mapToValue`, `map`, `filter`, `collect`, `preventDefault`, and `stopPropagation`, and you can chain them in any order.

More examples:

```scala
import org.scalajs.dom.ext.KeyCode
 
div("Click me", onClick.map(getClickCoordinates) --> clickCoordinatesBus)
 
div(onScroll.filter(throttle) --> filteredScrollEventBus)
 
div(onClick.config(useCapture = true) --> captureModeClickBus)
 
input(onKeyUp.filter(_.keyCode == KeyCode.Enter).preventDefault --> enterPressBus)
 
div(onClick.collect { case ev if ev.clientX > 100 => "yes" } --> yesStringBus)
 
// @TODO[Docs] Come up with more relatable examples
```

Just like `ReactiveEventProp`-s, `EventPropTransformation` instances are immutable and contain no element-specific state, so you can reuse them freely across multiple elements. For example:

```scala
import org.scalajs.dom.ext.KeyCode
 
val onEnterKeyUp = onKeyUp.filter(_.keyCode == KeyCode.Enter)
// and then
input(onEnterKeyUp.preventDefault --> enterPressBus)
textArea(onEnterKeyUp.preventDefault --> enterPressBus)
```

This kind of casual flexibility is what Laminar is all about.


##### preventDefault & stopPropagation

These methods correspond to invocations of the corresponding native JS `dom.Event` methods. MDN docs: [preventDefault](https://developer.mozilla.org/en-US/docs/Web/API/Event/preventDefault), [stopPropagation](https://developer.mozilla.org/en-US/docs/Web/API/Event/stopPropagation)

Importantly, these are just ordinarily transformations, and happen in the order in which you have chained them. For example, in the code snippet above `ev.preventDefault` will only be called on events that pass `filter(_.keyCode == KeyCode.Enter)`. Internally all transformations have access to both the latest processed value, and the original event, so it's fine to call the `.preventDefault` transformation even after you've used `.map(_.keyCode)`.


##### useCapture

JS DOM has two event modes: capture, and bubbling. Typically and by default we use the latter, but capture mode is sometimes useful for event listener priority/ordering (not specific to Laminar, standard JS DOM rules/limitations apply).

You need to specify whether to use capture mode the moment you register an event listener on the element, so it's passed as a parameter to `onClick.config(useCapture = true)` instead of being a method on `EventPropTransformation`. `// @TODO[API] Reconsider`

See MDN [addEventListener](https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener) page for details (see "useCapture" section).


##### Obtaining Typed Event Target

Due to dynamic nature of Javascript, `dom.Event.target` is typed only as `dom.EventTarget` for most events, which is not useful when you want to get `ev.target.value` from a target that is a `dom.html.Input` (but doesn't know it). So, you can't do this:

```scala
// Does not work because .value is defined on dom.html.Input, but not on dom.EventTarget, which what `ev` is :(
input(typ := "text", onChange().map(ev => ev.target.value) --> inputStringBus)
```

Easiest hackiest solution would be to use `.map(_.target.asInstanceOf[dom.html.Input].value)` but you should reconsider using Scala if you aren't cringing just looking at this.

You could use our Alternative Syntax for registering events (see section above) for a somewhat safer solution:

```scala
val inputNode = input(typ := "text")
val inputStringStream = inputNode.events(onChange).mapTo(inputNode.ref.value)
```

However, this is often cumbersome, and introduces the risk of referencing the wrong input node of the same type. We have a better way to get a properly typed target node:

```scala
input(inContext(thisNode => onChange.mapTo(thisNode.ref.value) --> inputStringBus))
```

This feature is not specific to events at all. The general syntax is `inContext(El => Modifier[El]): Modifier[El]`. `thisNode` refers to the element to which this modifier will be applied. Its type is properly inferred to be `Input`, which is an alias for `ReactiveHtmlElement[dom.html.Input]`, thus its `.ref` property is precisely typed as `dom.html.Input`, which is a native JS type that has a `.value` property that holds the current text in the input.

Because `thisNode.ref` refers to the element on which the event listener is **registered**, in JS DOM terms, it is actually equivalent to `dom.Event.currentTarget`, not `dom.Event.target` – the latter refers to the node at which the event **originates**. When dealing with inputs these two targets are usually the same since inputs don't have any child elements, but you need to be aware of this conceptual difference for other events. MDN docs: [target](https://developer.mozilla.org/en-US/docs/Web/API/Event/target), [currentTarget](https://developer.mozilla.org/en-US/docs/Web/API/Event/currentTarget).

You might have noticed that some event props like `onClick` promise somewhat peculiar event types like `TypedTargetMouseEvent` instead of the expected `MouseEvent` – these refined types come from _Scala DOM Types_, and merely provide more specific types for `.target` (as much as is reasonably possible). These types are optional – if you don't care about `.target`, you can just treat such events as simple `MouseEvent`s because `TypedTargetMouseEvent` does in fact extend `MouseEvent`. `// @TODO [API] I don't think we need this, really`.



### Ownership

Ownership is a core Airstream concept. You **really** need to read about it in [Airstream docs](https://github.com/raquo/Airstream#ownership).

Basically, Airstream does not let you create a leaky resource without specifying an Owner which will eventually kill said resource. For example, adding an Observer to an EventStream is a potential memory leak. Once a subscription is established like that, the stream in question obtains a reference to that observer, and the stream's parent stream obtains a reference to this stream, etc. Depending on how long that chain is and how it's used, there is a good chance it will either never be garbage collected or will be garbage collected much later than optimal.

With other libraries you would need to manually call `removeObserver` after calling `addObserver` to avoid memory leaks like that. In Airstream however, you don't need to do that. Instead, when adding an observer you need to specify not just the observer, but also the Owner of this new subscription. In Laminar, any Laminar element can be an owner. In fact, when you use `<--` methods on elements or in their modifiers, the element in question becomes the owner of the subscription that the `<--` method creates.

Laminar elements kill the subscriptions that they own when they are _discarded_. This happens when Laminar removes the element from the DOM. This is your answer to the question of "who should be the owner of this subscription?": you have to find an element the lifespan of which will match the desired lifespan of the subscription.

You can also create custom owners by extending the Owner trait. This is useful for leaky resources found in application-wide functionality such as [centralized state management](https://github.com/raquo/Laminar/issues/18) or an ajax service that has internal subscriptions.

Importantly, State is also a leaky resource, just like subscription. This is because unlike streams, State is not lazy, it starts itself upon creation, so you need an owner to stop the State at some point even if it has no observers.

So, if you have a component that renders an element and also maintains some State, that State's owner should be that element.



### Memory Management

Once again I assume that you have read the [relevant section](https://github.com/raquo/Airstream#ownership--memory-management) of Airstream docs. The basic principles are as follows:

* Lazy observables (streams, signals) are not garbage collected as long as they have observers or dependent observables that are not eligible for garbage collection

* State observables are not garbage collected unless they are killed by their owner

* However, if your entire observable dependency graph – everything from the original event producer to the observables that depend on it to the observers that listen to those observables – if all that becomes unreachable then all of that will be garbage collected.

* So none of this negates general JS garbage collection rules. We don't use WeakMap or WeakRef. I'm just giving you guidelines so that you don't need to go into Airstream and Laminar source code trying to figure out what holds references to what.

With proper use of ownership, Laminar's memory management is pleasantly automatic. Nevertheless, programming is hard, so here are a few rules of thumb for preventing memory leaks in Laminar:

* State and subscriptions should generally be owned by a Laminar element that has a lifespan that matches the desired lifespan of said state or subscription. In plain English, **it should make sense** for an element to own the State. For example, if your component needs to maintain a State and also returns an element to be rendered, that element should probably own that state because when that element is discarded you don't want to maintain that State anymore (assuming that's the case). 

* State that is defined in a component must not escape that component. Every State observable contains `map`, which is essentially a factory that creates a new State Observable **with the same owner as the instance it was called on**.

  * So, if you pass a State from a parent component to a child component, **you are letting the child create new State that will be owned by the parent. Do not do this. It's like giving your actual child your credit card.** Instead, convert the State to a Signal before passing it to the child. The child will be able to convert the Signal back to State if it needs to, but that state will be owned by the child, and will be promptly killed when the child is discarded.

  * Also, remember that Laminar has no built-in concept of a component. You need to be careful when passing State to any function, including callbacks. You need to ask yourself – does the lifespan of this function's scope match the lifespan of the function which I consider my "component"? If the answer is no, then you should not be passing State to that function. For example, you should not pass State to a function that is called whenever a click event is emitted on your component, because any State created within that function using `myState.map` will exist until the whole component is discarded, which is much longer than you probably want for a State that is specific to such an ephemeral function call.
  
  * This might seem complicated but it's really not. You just need to make sure that every piece of State is owned by the right owner. You will face this decision with any UI library.
  
  * Corollary: in most cases, feel free to use Signals instead of State. This will mean err-ing on the side of things not running when they should, which is easier to debug than things running when they shouldn't.

Admittedly we do have a couple gotchas in memory management:

* Every Laminar element is an Owner. An element kills the resources that it owns when that element is discarded. An element is discarded when it is removed from the DOM. Now, here is an unfortunate loophole: what about a Laminar element that was created, owns some State or subscriptions, but that was never inserted into the DOM? Since it was never inserted, it will never be removed from it, so the resources that it owns will never be cleaned up. Unfortunately there is currently no way around this. This is a design bug that I will fix in a later version. So for now, **do not proactively create elements that will never be added to the DOM**. The most trivial workaround is to create a factory that creates an element when it's actually needed instead.  

* When a Laminar element is removed from the DOM, the resources that it owns are killed with no built-in way to resurrect them. Therefore, **do not remove elements from the DOM that you will want to add back to the DOM later**, as their subscriptions and any State owned by them will not be functional anymore. When appropriate, hide the element using CSS instead of temporarily removing it from the DOM, or create a new (similar) element instead of trying to re-insert a previously removed element.



### Element Lifecycle Events

Laminar elements (see `ReactiveElement`) expose streams of lifecycle events that can be useful for integrating third party DOM libraries and other tasks. These events are similar in spirit to React.js lifecycle hooks like `componentDidMount` or `componentWillUnmount`, but are implemented very differently.

All lifecycle events are fired synchronously, with no async delay.


#### Parent Change Events

**`parentChangeEvents: EventStream[ParentChangeEvent]`**

This stream fires any time when this element's direct parent changes. Actually, two `ParentChangeEvent` events are fired at that time: immediately before the change is applied to both the real DOM and Laminar's DOM tree, and immediately after that. In addition to the differentiating `alreadyChanged` flag, these events carry references to `maybePrevParent` and `maybeNextParent`, letting you know exactly what change the event represents.

This stream only fires when direct parent of this element is changed. It does not track changes in the parent's parent and higher up ancestors. This stream fires regardless of whether this element is mounted or not (see definition below).


#### Mount Events

An element is considered mounted if it is present in the DOM, i.e. if its `.ref` DOM node is a descendant of `org.scalajs.dom.document`. All elements that are not mounted are considered unmounted.

Laminar has three kinds of mount events:

**`NodeDidMount`** – fired when the element **becomes mounted ** (i.e. it was previously unmounted). Specifically, this happens immediately after the corresponding parent change event with `alreadyChanged=true` (see above) is fired. At this point, the element is **already** present in the DOM.

In JS DOM, some operations like getting the width of the element only become available once the element is mounted, however in Laminar the element's subscriptions become active immediately on creation, so you should be wary of said JS DOM calls returning nulls or zeros before the element is mounted. Instead of listening for `NodeDidMount` event, you can also ask an element if it's currently mounted using `element.isMounted`.

**`NodeWillUnmount`** – fired when the element **becomes unmounted** (i.e. it was previously mounted). Specifically, this happens immediately after the corresponding parent change event with `alreadyChanged=false` (see above) is fired. At this point, the element is **still** present in the DOM, but will be removed from the DOM immediately after this event.

When an element is unmounted, the resources owned by this element are killed:

* Emitters (`onClick --> ...`) stop listening for and relaying events
* Receivers (`href <-- ...`) unsubscribe from input streams and stop updating the element's node
* Manually created State observables and subscriptions owned by this element are killed as well

If you want to temporarily "remove" the element from the DOM, but still keep its subscriptions active, you should hide it using CSS `display: none` instead of unmounting it. 

**`NodeWillBeDiscarded`** – fired when the end user has indicated that they will not mount this element ever again. Currently, this is always fired right after the `NodeWillUnmount` event, so currently unmounting an element means that you can't re-mount it again (its subscriptions will not re-activate). Future versions of Laminar will include a way for end users to specify that they intend to re-mount a given element after unmounting it.


#### Order of Lifecycle Events

For extra clarity, lifecycle events triggered by the same underlying parent change event are fired in the following order:

1) `ParentChangeEvent(alreadyChanged=false, maybePrevParent, maybeNextParent)`,
2) `NodeWillUnmount` (if we're unmounting),
3) `NodeWillBeDiscarded` (if we're unmounting),
4) `ParentChangeEvent(alreadyChanged=true, maybePrevParent, maybeNextParent)`,
5) `NodeDidMount` (if mounting)


#### Mount Event Streams

So how do you actually listen for mount events? Laminar exposes the following streams on each `ReactiveElement`:

**`mountEvents`** – fires a full stream of mount events that affect this node. If the node was mounted or will be unmounted, directly or indirectly, this event will be here. This stream is a simple merge of the two mutually exclusive streams below: 

**`thisNodeMountEvents`** – fires mount events that were caused by this element changing its parent only. Does not include mount events triggered by changes higher in the hierarchy (grandparent and up).

**`ancestorMountEvents`** – fires mount events that were caused by this element's parent changing its parent, or any such changes up the chain. Does not include mount events triggered by this element changing its parent.


#### Lifecycle Events Performance

Maintaining multiple lifecycle event streams for every single element in the DOM would be needlessly costly, especially given that `mountEvents` is recursive – a child's `mountEvents` stream is in part derived from all of its ancestors' `mountEvents` streams. Laminar has a few optimizations to make this efficient. Here is how it works:

1) All streams are defined as `lazy val`-s, and are not even initialized until you access them. Then when you do, only the required dependencies of those streams are initialized. So when you ask for a `mountEvents` stream of one element, only then will Laminar initialize `mountEvents` streams of this child's parents, if they weren't initialized already. 

2) Until the streams are initialized, the underlying event buses don't even receive events. `parentChangeEvents` gets its events from the event bus stored inside `maybeParentChangeBus`, but that `Option` remains `None` until `parentChangeEvents` is accessed. Similarly with `thisNodeMountEvents` and `maybeThisNodeMountEventBus`. All other lifecycle streams are derived from these two streams using simple transformations.

3) We avoid redundant computations as much as possible. All Airstream observables have shared execution, which works well for this case because elements high up in the DOM hierarchy will typically have many listeners on their `mountEvents` streams. Also, some of the most expensive calculations like determining whether an element is mounted or not are performed only once per original `ParentChangeEvent` – the same event instance is reused downstream with no additional allocations or DOM access required.

Importantly, you don't need to be accessing `mountEvents` directly in order to trigger initialization of all the lazy streams. Every element that has any subscriptions (either listens to streams or emits events) will be listening to its `mountEvents` already.

There's more to this system, but for now this will have to do as an MVP summary. All in all, this system should work quite well, and if you run into performance problems on huge DOM trees with many subscriptions, this should give you some understanding of which bottleneck you could be hitting, and how to work around it. 

One way to simplify your code that is Laminar-specific is to use `element.isMounted` method instead of subscribing to `mountEvents`. It is provided mostly as an escape hatch for third party integrations that do not map well to FRP design patterns. 



### Special Cases

Laminar is conceptually a simple layer adding a reactive API to Scala DOM Builder. In general there is no magic to it, what goes in goes out, transformed in some obvious way. However, in a few cases we do some ugly things under the hood so that you don't need to pull your hair and still do said ugly things in your own code.

Please let me know via github issues if any of this magic caused you grief. It's supposed to be almost universally helpful.


##### checkbox.onClick + event.preventDefault() = async event stream

All event streams in Laminar emit events synchronously – as soon as they happen – **except** if the stream in question is a stream of `onClick` events on an `input(typ := "checkbox")` element, and you generated this stream using the `.preventDefault` method in Laminar's API.

Such streams fire events after a `setTimeout(0)` instead of firing immediately after the browser triggers the event. Without this hack/magic, you would have been unable to update the `checked` property of this checkbox from a stream that is (synchronously) derived from the given stream of `onClick` events, and this is a common practice when building controlled components.

The underlying issue is described in [this StackOverflow answer](https://stackoverflow.com/a/32710212/2601788).

**Watch out:** If you are reading the `checked` property of the checkbox in an affected stream, it will contain the original, unchanged value. This behaviour could be surprising if you don't know about this stream being async, but do know about the native DOM behaviour of temporarily updating this value and then resetting it back. This is deemed a smaller problem than the original issue because it's easier to debug, and better matches the commonly-expected semantics of `preventDefault`.

**Escape hatch:** instead of using Laminar's `preventDefault` option/method, call `ev.preventDefault()` manually _after_ the event was passed to the event bus. Then event handling will work as it does in native JS DOM.




## My Related Projects

- [Airstream](https://github.com/raquo/Airstream) – State propagation and event streaming library that we use in Laminar
- [Scala DOM Types](https://github.com/raquo/scala-dom-types) – Type definitions that we use for all the HTML tags, attributes, properties, and styles
- [Scala DOM Builder](https://github.com/raquo/scala-dom-builder) – Low-level Scala & Scala.js library for building and manipulating DOM trees
- [Scala DOM TestUtils](https://github.com/raquo/scala-dom-testutils) – Test that your Javascript DOM nodes match your expectations




## Author

Nikita Gazarov – [raquo.com](http://raquo.com)




## License

Laminar is provided under the [MIT license](https://github.com/raquo/laminar/blob/master/LICENSE.md).

