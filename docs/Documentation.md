# Documentation

* [Introduction](#introduction)
* [Imports](#imports)
* [Tags & Elements](#tags--elements)
* [Modifiers](#modifiers)
  * [Nesting and Children](#nesting-and-children)
  * [Manual Application](#manual-application)
  * [inContext](#incontext)
  * [Reusing Elements](#reusing-elements)
  * [Missing Keys](#missing-keys)
  * [Modifiers FAQ](#modifiers-faq)
* [Reactive Data](#reactive-data)
  * [Attributes and Properties](#attributes-and-properties)
  * [Event Streams and Signals](#event-streams-and-signals)
  * [Individual Children](#individual-children)
  * [Lists of Children](#lists-of-children)
    * [`children <-- observableOfElements`](#children----observableofelements)
    * [Performant Children Rendering – `split`](#performant-children-rendering--split) 
    * [Performant Children Rendering – `children.command`](#performant-children-rendering--childrencommand)
  * [Binding Observables](#binding-observables)
  * [Other Binders](#other-binders)
* [ClassName and Other Special Keys](#classname-and-other-special-keys)
  * [cls](#cls)
  * [Other Composite Keys](#other-composite-keys)
  * [SVG](#svg)
* [Event System: Emitters, Transformations, Buses](#event-system-emitters-transformations-buses)
  * [Registering a DOM Event Listener](#registering-a-dom-event-listener)
  * [EventBus](#eventbus)
  * [Alternative Event Listener Registration Syntax](#alternative-event-listener-registration-syntax)
  * [Multiple Event Listeners](#multiple-event-listeners)
  * [Event Transformations](#event-transformations)
    * [preventDefault & stopPropagation](#preventdefault--stoppropagation)
    * [useCapture](#usecapture)
    * [Obtaining Typed Event Target](#obtaining-typed-event-target)
  * [Window & Document Events](#window--document-events)
* [Ownership](#ownership)
  * [Laminar's Use of Airstream Ownership](#laminars-use-of-airstream-ownership)
* [Memory Management](#memory-management)
* [Element Lifecycle Hooks](#element-lifecycle-hooks)
  * [What Is Mounting?](#what-is-mounting)
  * [`onMountCallback`](#onmountcallback)
  * [`onUnmountCallback` & `onMountUnmountCallback`](#onunmountcallback--onmountunmountcallback)
  * [`onMountSet`](#onmountset)
  * [`onMountBind`](#onmountbind)
  * [`onMountInsert`](#onmountinsert)
  * [Why Use Lifecycle Hooks?](#why-use-lifecycle-hooks)
  * [Lifecycle Event Timing](#lifecycle-event-timing)
  * [How Are Mount Events Propagated?](#how-are-mount-events-propagated)
* [URL Routing](#url-routing)
* [Special Cases](#special-cases)





## Introduction

#### First, see [Why Laminar](../README.md#why-laminar) and [Quick Start](../README.md#quick-start).

Make sure you're reading the docs for the right version:

#### Documentation

| Laminar | Airstream |
| :--- | :--- |
| **[master](https://github.com/raquo/Laminar/blob/master/docs/Documentation.md)** | **[master](https://github.com/raquo/Airstream/blob/master/README.md)** |
| **[v0.10.3](https://github.com/raquo/Laminar/blob/v0.10.3/docs/Documentation.md)** | **[v0.10.1](https://github.com/raquo/Airstream/blob/v0.10.1/README.md)** |
| **[v0.9.2](https://github.com/raquo/Laminar/blob/v0.9.2/docs/Documentation.md)** | **[v0.9.2](https://github.com/raquo/Airstream/blob/v0.9.2/README.md)** |
| **[v0.8.0](https://github.com/raquo/Laminar/blob/v0.8.0/docs/Documentation.md)** | **[v0.8.0](https://github.com/raquo/Airstream/blob/v0.8.0/README.md)** |
| **[v0.7.2](https://github.com/raquo/Laminar/blob/v0.7.2/docs/Documentation.md)** | **[v0.7.2](https://github.com/raquo/Airstream/blob/v0.7.2/README.md)** |

For documentation of older versions, see git tags.

Note: the latest version of Laminar always uses the latest version of Airstream.

[Laminar API doc](https://javadoc.io/doc/com.raquo/laminar_sjs1_2.13/latest/com/raquo/laminar/index.html) • [Airstream API doc](https://javadoc.io/doc/com.raquo/airstream_sjs1_2.13/latest/com/raquo/airstream/index.html)

Laminar is very simple under the hood. You can see how most of it works just by using "Go to definition" functionality of your IDE. Nevertheless, the documentation provided here will help you understand how everything ties together. Documentation sections progress from basic to advanced, so each next section usually assumes that you've read all previous sections.



## Imports

You have two import choices: `import com.raquo.laminar.api.L._` is the easiest, it brings everything you need from Laminar in scope. Unless indicated otherwise, this import is assumed for all code snippets in this documentation.

Usually you will not need any other imports. In this documentation you will see references to Laminar types and values that are not available with just this one import because we spell out the types for the sake of explanation. Most of those are available as aliases in the `L` object. For example, `ReactiveHtmlElement[dom.html.Element]` is aliased as simply `HtmlElement`, and `Modifier[El]` as `Mod[El]`.

However, you might want to avoid bringing so many values into scope, so instead you can `import com.raquo.laminar.api._`, and access Laminar and Airstream values and types with `L` and `A` prefixes respectively, e.g. `A.EventStream`, `L.div`, etc.`

There are special import considerations for working with [SVG elements](#svg).

Do check out the available aliases in the `L` object. It's much more pleasant to write and read `Mod[Input]` than `Modifier[ReactiveHtmlElement[dom.html.Input]]`.

Another import you will want in some cases is `import org.scalajs.dom` – whenever you see `dom.X` in documentation, this import is assumed. This object contains [scala-js-dom](https://github.com/scala-js/scala-js-dom) native JS DOM types. I highly recommend that you import this `dom` object and not `dom._` as the `dom.` prefix will help you distinguish native JS platform types from Laminar types.



## Tags & Elements

Laminar uses [Scala DOM Types](https://github.com/raquo/scala-dom-types) listings of typed HTML & SVG tags, attributes, props, event props, etc. For example, this is how we know that `onClick` events produce `dom.MouseEvent` events and not `dom.KeyboardEvent`.

`div` is an `HtmlTag[dom.html.Div]`. It's a factory of div elements. `HtmlTag` extends `Tag` from _Scala DOM Types_ and contains basic information needed to create such an element, such as its tag name ("div").

`div()` is a `ReactiveHtmlElement[dom.html.Div]` (aliased as `Div`). The `apply` method created a Laminar Div element that is linked to the real JS DOM element that it represents. If you need to, you can access the underlying JS DOM element via the `.ref` property. This is useful mostly for event handlers and third party integrations.

`div()` is **not** a virtual DOM element. When you create a Laminar element, Laminar immediately creates the underlying `dom.Element` as well. That reference is immutable, so these two instances will go together for the duration of their lifetimes. In contrast, in a virtual DOM library (which Laminar is not) you typically create new instances of virtual elements when they change, and these get loosely matched to a `dom.Element` which could actually be a different element over time depending on how the updates that you're requesting and the implementation of virtual DOM's diffing algorithm.  

Read on for how to use this element we've created.



## Modifiers

The `div()` call described above creates an empty div element – no children, no attributes, nothing. Here's how to specify desired attributes:

```scala
input(typ := "checkbox", defaultChecked := true)
```

This creates an `input` Laminar element and sets two attributes on it. It's mostly obvious what it does, but how?

As we've established before, `input()` call is actually `HtmlTag[El].apply(modifiers: Modifier[El]*)`. So, we can pass Modifiers to it. A `Modifier[El]` (aliased as `Mod[El]`) is a simple trait with an `apply(element: El): Unit` method. Conceptually, it's a function that you can apply to an element `El` to modify it in some way. In our case, `typ := "checkbox"` is a `Modifier` that sets the `type` attribute on an element to `"checkbox"`.

`typ` is a `ReactiveHtmlAttr`, and `:=` is simply a method on it that creates a `Setter` – a `Modifier` that sets a specific key to a specific value. You can set DOM props and CSS style props the same way (e.g. `backgroundColor := "red"`)

`typ` is coming from _Scala DOM Types_ and represents the "type" attribute. You should consult _Scala DOM Types_ documentation for a list of [naming differences](https://github.com/raquo/scala-dom-types#naming-differences-compared-to-native-html--dom) relative to the native JS DOM API. It will also explain why the `checked` attribute is called `defaultChecked`, and why it accepts a boolean even though all HTML attributes only ever deal in strings in JS DOM ([Codecs](https://github.com/raquo/scala-dom-types#codecs)).

Note: the `apply` method on attributes and other keys is aliased to `:=` in case you prefer a slightly shorter syntax:

```scala
input(typ("checkbox"), defaultChecked(true)) // 100% equivalent to the previous snippet
```  


### Nesting and Children

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

Well, a tiny bit of magic – strings are not modifiers themselves, but are implicitly converted to `TextNode` (Laminar text nodes) which _are_ modifiers that append plain JS text nodes with a given text to the element in question. They are not implicitly wrapped into `span` elements.

For convenience, `Seq[Modifier[A]]` is also implicitly converted to a `Modifier[A]` that applies all the modifiers in the Seq: see `Implicits.seqToModifier`. This is especially useful when making a component that accepts modifiers as VarArgs. Such design is one way to let users configure the component's elements without the component needing to whitelist all possible configurations in its list of inputs.

`Implicits.optionToModifier` performs a similar conversion for `Option[Modifier[A]]`. We also have `emptyMod`, a universal Modifier that does nothing, for when you don't want to wrap your stuff in Option-s.

Modifiers are applied in the order in which they're passed to the element, so in terms of children ordering what you see in your code is what you get in the resulting DOM.

Notice that the code snippet above does not require you to HTML-escape `"&"` or `"<"`. You're just writing Scala code, you're not writing HTML (or anything that will be parsed as HTML), so don't worry about HTML escaping when using Laminar API. **However**, this warranty is void when you manually invoke native Javascript APIs (e.g. anything under `.ref`, or any interfaces provided by [scala-js-dom](https://github.com/scala-js/scala-js-dom)). Then you need to know Javascript and the DOM API to know what's safe and what [isn't](https://security.stackexchange.com/questions/32347/dom-based-xss-attacks-what-is-the-most-dangerous-example).

---

Note: this section of documentation only explained how to render **static** data. You will learn to render dynamic data and lists of children in [Reactive Data](#reactive-data) section. 


### HTML Entities

Laminar does not attempt to parse your strings as HTML, so there is no need to use [HTML entities](https://developer.mozilla.org/en-US/docs/Glossary/Entity) like `"&nbsp;"` or `"&amp;"`. Instead, you just insert the actual character into your Scala strings.

Laminar actually provides a shorthand for non-breaking space – `nbsp`, and for any other special characters you can just use them verbatim, e.g. `"»"` instead of  or `"&raquo;"`.

```scala
div(s"${date}${nbsp}${time}")
div(b("Posts"), "»", categoryName) // `»` instead of `&raquo;`
div("Features — not bugs") // `—` instead of `&emdash;`
```


### Empty Nodes

Thanks to the `optionToModifier` implicit mentioned above, you can use `Option[ReactiveElement]` where a `Modifier` is expected, but in other cases you might not want to deal with Option's boxing.  

And so, an empty DOM node can be created with... `emptyNode`.

```scala
val node: Node = if (foo) element else emptyNode
```

`emptyNode` outputs an empty HTML comment node, which is a `CommentNode`, not a `ReactiveElement`. Their common type is `ChildNode[dom.Node]`, aliased as simply `Node`. This distinction comes from JS world where `dom.Comment` nodes and `dom.Text` nodes can't have children, and are not `dom.Element`-s.


### Manual Application

Typically you want to apply modifiers to an element when creating it: `div(color := "red", ...)`.

To add modifiers to an already existing element, call `amend` on it:

```scala
val el = div("Hello")
 
el.amend(
  color := "red",
  span("a new child!")
)
```

This is convenient not only to break apart your logic, but also because `el.amend()` returns `this`, it is especially useful to add arbitrary behaviour to abstracted away "components". The following example uses Reactive Data that we haven't covered yet, but hopefully the pattern is clear enough:

```scala
val nameVar = Var("")
def TextInput(): Input = input(typ := "text")
 
div(
  "Please enter your name: ",
  TextInput().amend(onInput --> nameVar.writer)
)
```

And of course, every Modifier has a public `apply(element: El)` method that you can use to manually apply it to any element, for example: `(color := "red")(element)`.

Using manual application is only encouraged when it's not enabling imperative coding patterns. You should learn to use [Reactive Data](#reactive-data) effectively before you sprinkle your code with manual applications.


### `inContext`

Sometimes you may need to have a reference to the element before you can decide which modifier to apply to it. For example, a common use case is needing to get the input element's `value` property in order to build a modifier that will send that property to an event bus.

```scala
input(inContext(thisNode => onChange.mapTo(thisNode.ref.value) --> inputStringBus))
```

The general syntax of this feature is `inContext(El => Modifier[El]): Modifier[El]`. `thisNode` refers to the element to which this modifier will be applied. In our example its type is properly inferred to be `Input`, which is why you can call `.ref.value` on it without `.asInstanceOf`.

Without getting ahead of ourselves too much, `onChange.mapTo(thisNode.ref.value) --> inputStringBus)` is a Modifier that handles `onChange` events on an input element, but as you see it needs a reference to this input element. You could get that reference in a couple _other_ ways, for example:

```scala
val myInput: Input = input(onChange.mapTo(myInput.ref.value) --> inputStringBus) // make sure to write out the type ascription
```

Or

```scala
val myInput = input()
myInput.amend(
  myInput.events(onChange).mapTo(myInput.ref.value) --> inputStringBus
)
```

But these are of course rather cumbersome. `inContext` provides an easy, inline solution that always works.

Note: all the unfamiliar syntax used here will be explained in other sections of this documentation. 


### Reusing Elements

**Do not.** An element can only have one parent element at a time, so you can't put the same element twice into the real JS DOM – instead, it will be removed from its old location and **moved** into its new location. That's just how the browser DOM works. Laminar does not protect you from doing this for performance reasons, so you need to avoid this on your own, or you will run into unexpected behaviour.

On a related note, it *is* safe to unmount a Laminar element and then re-mount it in any location, assuming that you use Laminar methods to achieve that, and that you don't compete with any other `child <--` or `children <--` bindings you've set up for the same set of elements.


### Missing Keys

Laminar gets the definition of HTML and SVG DOM tags, attributes, properties, events and CSS styles from [Scala DOM Types](https://github.com/raquo/scala-dom-types). These definitions provide hundreds of keys, but are not exhaustive. For example, we don't have touch events like `touchmove` defined, so those are not available to be put on the left hand side of `:=` methods.

To work around this, you can contribute definitions of missing keys to Scala DOM Types. It's easy – you don't even need to code any logic, just specify the names and types of the things you want to add. See the [Contribution Guide](https://github.com/raquo/scala-dom-types/blob/master/CONTRIBUTING.md) and an [example PR](https://github.com/raquo/scala-dom-types/pull/16/files).

Alternatively, you can define the keys locally in your project in the same manner as Laminar does it, for example:

```scala
val onTouchMove = new ReactiveEventProp[dom.TouchEvent]("touchmove")
div(onTouchMove --> eventBus)
```

To clarify, you don't have to do this for touch events specifically as [@Busti](https://github.com/busti) already added the superior [pointer-events](https://github.com/raquo/scala-dom-types/pull/49) to address this particular shortcoming. Unless you want touch events regardless of that, in which case, you're welcome to it.


### Modifiers FAQ

1. Yes, you can create custom Modifiers by simply extending the Modifier trait. You could for example define attributes or CSS props that are missing from _Scala DOM Types_ by extending `ReactiveHtmlAttr` or `ReactiveStyle`, or for completely custom logic you could create a class extending the base `Modifier` trait. That way you could for example make a modifier that applies multiple other modifiers that you often use together.

2. No, Modifiers are not guaranteed to be idempotent. Applying the same Modifier to the same element multiple times might have different results from applying it only once. `Setter`s like `key := value` _are_ idempotent, but for example a `Binder` like `onClick --> observer` isn't. We will talk more about those modifier types later.

4. Yes, Modifiers are generally reusable. The same modifier can usually be applied to multiple nodes without any conflict. But again, you have to understand what the modifier does. Setting an attribute to a particular value – sure, reusable. Adding a particular element as a child – no, not reusable – see [Reusing Elements](#reusing-elements). If you're writing your own Modifier and want to make it reusable, don't store element-specific state outside of its `apply` method.



## Rendering

When you create a Laminar element, the underlying real JS DOM element (`.ref`) is created at the same time. However, it is initially detached from the DOM. That is, it does not appear in the document that the user sees. Such an element is _unmounted_.

For the user to see this element we need to _mount_ it into the DOM by either adding it as a Modifier to another element that is (or at some point will become) _mounted_, or if we're dealing with the top element in our application's hierarchy, we ask Laminar to _render_ it into some container `dom.Element` that already exists on the page. Said container must not be managed by Laminar. 

```scala
val appContainer: dom.Element = dom.document.querySelector("#appContainer")
val appElement: Div = div(
  h1("Hello"),
  "Current time is:",
  b("12:00") 
)
 
val root: RootNode = render(appContainer, appElement)
```

That's it. Laminar will find an element with id "appContainer" in the document, and append `appElement.ref` as its child. For sanity sake, the container should not have any other children, but that's not really a requirement.

Note: if `appContainer` was not present in the DOM when you called `render()`, you will need to manually call `root.mount()` to mount `appElement`. More on this in [Element Lifecycle Hooks](#element-lifecycle-hooks).

To remove `appElement` from the DOM, simply call `root.unmount()`.



## Reactive Data

In the code snippet above we're mounting a completely static element – it will not change over time. This section is about creating elements that will one way or another change over time. All of the above were just the basic mechanics, here we're digging into the meat of Laminar, the fun parts.

In a virtual DOM library like React or Snabbdom, changing an element over time is achieved by creating a new virtual element that contains the information on what the element should look like, then running a diffing algorithm on this new virtual element and the previous virtual element representing the same real JS DOM element. Said diffing algorithm will come up with a list of operations that need to be performed on the underlying JS DOM element in order to bring it to the state prescribed by the new virtual element you've created.  

Laminar does **not** work like that. We use reactive streams to represent things changing over time. Let's see how this is much simpler than any virtual DOM.

Laminar uses [Airstream](https://github.com/raquo/Airstream) observables (event streams and signals). Airstream is an integral part of Laminar, and a huge part of what makes it unique.

**To use Laminar effectively, you need to understand the principles of conventional lazy streams, and have basic knowledge of Airstream. Please do read [Airstream documentation](https://github.com/raquo/Airstream), it's not scary.**


### Attributes and Properties

Dynamic attributes / properties / css properties are the simplest use case for Laminar's reactive layer:

```scala
val prettyColorStream: EventStream[String] = ???
val myDiv: Div = div(color <-- prettyColorStream, "Hello")
```

This creates a div element with the word "Hello" in it, and a dynamic `color` CSS property. Any time the `prettyColorStream` emits an event, this element's `color` property will be updated to the emitted value. 

The method `<--` comes from `ReactiveStyle` (which is what `color` is). It creates a `Binder` modifier that subscribes to the given stream. This subscription is automatically enabled when the div element is added into the DOM, and automatically disabled when the div is removed from the DOM. More on that in the sections [Ownership](#ownership) and [Element Lifecycle Hooks](#element-lifecycle-hooks) way below. Don't worry about that for now.


### Event Streams and Signals

What happens before `prettyColorStream` emitted its first event? To understand that, we need a primer on Airstream reactive variables:

- **EventStream** is a lazy Observable without current value. It represents events that happen at discrete points in time. Philosophically, there is no such thing as a "current event" or "initial event", so streams have no initial or "current" value.

- **Signal** are also lazy Observables, but unlike streams they do have a current value. Signal represents state – a value persisted over time. It always has a current value, and therefore it must have an initial value.

The `<--` method subscribes to the given observable and updates the DOM whenever the observable emits. `prettyColorStream` is an EventStream, so it will only emit if and when the next event comes in. So, until that time, laminar will not set the color property to any initial or default value.

On the other hand, if `prettyColorStream` was a Signal, the subscription inside the `<--` method would have set the color property to the observable's current value as soon as the element was added to the DOM, and would behave similar to stream subscription afterwards.

We said that EventStreams and Signals are lazy – that means they wouldn't run without an Observer. By creating a subscription, the `<--` method creates an Observer of the observable that it is given, so we know for sure that it will run (while the element is mounted into the DOM).


### Individual Children

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

Having a stable reference to `myDiv` also simplifies your code significantly. If myDiv was a stream of divs instead, you'd need to engage in a potentially complex composition exercise to access the latest version of the element. Such useless complexity really adds up as your application grows, and eliminating needless complexity is one of Laminar's most important goals.

However, there _is_ a use case for streams of elements. Laminar elements are tied one-to-one to real JS DOM elements, so if you need a different _type_ of element displayed, you will need to re-create the Laminar element. For example:

```scala
def MaybeBlogUrl(maybeUrlSignal: Signal[Option[String]]): Signal[HtmlElement] = {
  maybeUrlSignal.map {
    case Some(url) => a(href := url, "a blog")
    case None => i("no blog")
  }
}

val maybeBlogUrlSignal: Signal[Option[String]] = ???
val app: Div = div(
  "Hello, I have ",
  child <-- MaybeBlogUrl(maybeBlogUrlSignal),
  ", isn't it great?"
)
```

It should be pretty obvious what this does: `MaybeBlogUrl` maps input Signal to a Signal of Laminar elements, and `child <-- MaybeBlogUrl(maybeBlogUrlSignal)` creates a subscription that puts these elements into the div.

The elements are put in the obvious order – what you see is what you get – so, between _"Hello, I have "_ and _", isn't it great?"_ text nodes. Each next emitted element replaces the previously emitted one in the DOM.

This example uses Signals, but EventStreams work similarly, and there's also `child.maybe` and `child.text` receivers that have more specialized `<--` methods accepting `Observable[Option[Node]]` and `Observable[String]` respectively.


#### Efficiency

You might notice that this is not as efficient as it could be. Any time `maybeBlogUrlSignal` emits a new value, a new element is created, even if we're moving from `Some(url1)` to `Some(url2)`. And those are real JS DOM elements, so **if** this method is hit often we might want to optimize for that. For example, we could do this:

```scala
def renderBlogLink(urlSignal: Signal[String]): HtmlElement = {
  a(href <-- urlSignal, "a blog")
}
 
def MaybeBlogUrl(maybeUrlSignal: Signal[Option[String]]): Signal[HtmlElement] = {
  val noBlog = i("no blog")
  val maybeLinkSignal: Signal[Option[HtmlElement]] =
    maybeUrlSignal.split(_ => ())((_, _, urlSignal) => renderBlogLink(urlSignal))
  maybeLinkSignal.map(_.getOrElse(noBlog))
}
```

The main hero of this pattern is Airstream's `split` operator. It's designed specifically to avoid re-rendering of elements in this kind of situation. We will explain its operation in detail when talking about performant `children` rendering below, but in short, `renderBlogLink` is called only when maybeUrlSignal switches from `None` to `Some(url)`. When it goes from `Some(url1)` to `Some(url2)`, it's `href <-- urlSignal` that takes responsibility for updating the blog link.

Note that we only create one `noBlog` element, and reuse it every time maybeUrlSignal emits `None`.

---

You don't actually need to unmount elements in order to hide them. You can achieve a similar effect by rendering both the link and no-link elements at the same time inside a container, and hiding one of them with CSS (`display <-- ...`). That way both elements will remain mounted all the time, but one of them will be hidden.

When engaging in such optimizations, you have to ask yourself – is it worth it? Re-creating a JS DOM element – what we were trying to avoid in this example – is only a problem when you need to do it often (performance), or when that element needs to maintain some JS DOM state (e.g. `input` elements will lose focus if re-created). To put this in the context of our example – if `maybeUrlSignal` is expected to emit only a few times during a user session, just use the dumbest shortest version of `MaybeBlogUrl` instead of optimizing it for no reason. But if you're building some kind of BlogRoulette app that updates the blog URL a hundred times per second, or if the element being re-created is very complex (e.g. your entire app or a big section thereof) then yeah, better optimize it.


### Lists of Children

Rendering dynamic lists of children efficiently is one of the most challenging parts in UI libraries, and Laminar has a few solutions for that.


#### `children <-- observableOfElements`

```scala
val childrenSignal: Signal[immutable.Seq[Node]] = ??? // immutable only!
div("Hello, ", children <-- childrenSignal)
```

If you have an Observable of desired children elements, this is the most straightforward way to subscribe to it.

Internally, this `<--` method keeps track of prevChildren and nextChildren. It then "diffs" them. The general idea here is only cosmetically similar to virtual DOM. Laminar diffing is much lighter than typical virtual DOM machinery. We only compare elements for reference equality, we do not look at any attributes / props / state / children and we do not re-render anything like React.

We make a single pass through nextChildren, and detect any inconsistencies by checking that `nextChildren(i).ref == childrenInDom(i)` (simplified). If this is false, we look at prevChildren to see what happened – whether the child was created, moved, or deleted. The algorithm is short (<50LoC total) but efficient.

Laminar will perform absolutely minimal operations that make sense – for example if a new element needs to be inserted, that is the only thing that will happen, _and_ this new element is the only element for which Laminar will spend time figuring out what to do, because all others will match. Reorderings are probably the worst case performance-wise, but still no worse than virtual DOM. You can read the full algorithm in `ChildrenInserter.updateChildren`.

Note: a given DOM node can not exist in multiple locations in the DOM at the same time. Therefore, the Seq-s you provide to `children <--` must not contain several references to the same element. This is on you, as Laminar does not spend CPU cycles to verify this. Generally this isn't a problem unless you're deliberately caching elements for reuse in this manner. If you _are_ doing that, consider using the `split` operator explained below.


#### Performant Children Rendering – `split`

We've now established how `children <-- childrenObservable` works, but generating this `childrenObservable` easily and efficiently can be quite complicated depending on your dataflow. For maximum efficiency, you should use Airstream's `split` method. Before we dive into that, let's see what the problem is without it. 

Suppose you want to render a dynamic list of users. The set of users, their order, and the attributes of individual users can all change, except for user id, which uniquely identifies a user. You also know how to render an individual user element:

```scala
case class User(id: String, name: String, lastModified: String)
 
val usersStream: EventStream[List[User]] = ???
   
def renderUser(user: User): Div = {
  div(
    p("user id: ", user.id),
    p("name: ", user.name)
  )
}
```

Given this, you might be tempted to render users like this:

```scala
val userElementsStream: EventStream[List[Div]] =
  usersStream.map(users => users.map(renderUser))
 
div(
  children <-- userElementsStream
)
```

This will work, but it is inefficient. Every time `usersStream` emits a new list of users, Laminar re-creates DOM elements for every single user in the list, and then replaces the old list of elements with this new one.

This is the kind of situation that virtual DOM was designed to avoid. While this pattern is perfectly fine for small lists that don't update often (and assuming you don't care about losing DOM element state such as text in an input box), with larger lists and frequent updates there is a lot of needless work being done.

So does this mean that virtual DOM is more efficient than Laminar when rendering large lists of dynamic children? Yes, if you render children **as described above**. This might be a surprising admission, but this default is necessary to uphold Laminar's core value: simplicity. There is no magic in Laminar. Laminar does what we tell it to do, and in this case we very explicitly tell it to create a list of elements every time the stream emits a new list of users, and then replace the old elements with brand new ones.

So let's write smarter code. It's actually very easy.

All we need to do is adjust our `renderUser` function to take a different set of parameters:

```scala
def newRenderUser(userId: String, initialUser: User, userStream: EventStream[User]): Div = {
  div(
    p("user id: " + userId),
    p("name: ", child.text <-- userStream.map(_.name)), 
    p(
      "user was updated: ",
      child.text <-- userStream.map(_.lastModified != initialUser.lastModified)
    ) 
  )
}
```

And with this, efficient rendering is as simple as:

```scala
val userElementsStream: EventStream[List[Div]] =
  usersStream.split(_.id)(newRenderUser)
 
div(
  children <-- userElementsStream
)
```

Airstream's `split` operator was designed specifically for this use case. On a high level, you need three things to use it:

* `usersStream` – a stream (or signal) of `immutable.Seq[User]`
* `_.id` – a way to uniquely identify users
* `newRenderUser` – a way to render a user into a single element given its unique key (`id`), the initial instance of that user (`initialUser`), and a stream of updates to that same user (`userStream`).

To clarify, Airstream-provided `userStream` is a safe and performant equivalent to `usersStream.map(_.find(_.id == userId).get)`. It provides you with updates of one specific user.

---

Here is how `split` works in more detail:

* As soon as a `User` with id="123" first appears in `usersStream`, we call `newRenderUser` to render it. This gives us a `Div` element **that we will reuse** for all future versions of this user.
* We remember this `Div` element. Whenever `usersStream` emits a list that includes a new version of the id="123" User, the `userStream` in `newRenderUser` emits this new version.
* Similarly, when other users are updated in `usersStream` their updates are scoped to their own invocations of `newRenderUser`. The grouping happens by `key`, which in our case is the `id` of `User`.
* When the list emitted by `usersStream` no longer contains a User with id="123", we remove its Div from the output and forget that we ever made it.
* Thus `userElementsStream` contains a list of Div elements matching one-to-one to the users in the `usersStream`.

---

Lastly, aside from `split` there is also `splitIntoSignals`. Which one you want depends on whether you want `newRenderUser` to accept a stream or a signal. Remember that unlike streams, signals keep track of their current state, and only emit values that don't `==`-equal their current state. This might be desirable or not (or irrelevant) depending on your use case. 


#### Performant Children Rendering – `children.command`

In certain cases such as rendering live logs, it might be easier for you to tell Laminar when you want items added or removed to the list instead of giving it an entire updated list of children:

```scala
val commandBus = new EventBus[ChildrenCommand]
val commandStream = commandBus.events
div("Hello, ", children.command <-- commandStream)
```

EventBus is an Airstream data structure that contains both an Observer (`.writer`) and an EventStream (`.events`). It's basically an event stream that you send events to by invoking the Observer. A more complete example:

```scala
val commandBus = new EventBus[ChildrenCommand]
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

We'll explain the `-->` method and the rest of event handling in later sections. What's important is that in this example we're sending `CollectionCommand.Append(span(s"Child # $counter"))` events to `commandBus` on every button click. 

The ChildrenCommand approach offers the best performance on Laminar side. Since you are directly telling Laminar what to do, no logic stands between your code and Laminar executing your requests. There is no diffing involved.

That said, don't go crazy trying to adapt your logic to it. Airstream's `split` operator described in the previous section offers great performance too. Choose the method that feels more natural for your needs, and micro-optimize later, if needed.

One downside of ChildrenCommand is that you don't have an observable of the list of children naturally available to you, so if you want to display e.g. the number of children somewhere, you will need to create an observable for that yourself. For example:

```scala
val commandBus = new EventBus[ChildrenCommand]
val commandStream = commandBus.events
val countSignal = commandStream.foldLeft(initial = 0)(_ + 1)

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

You can mix both `children <-- *` and `children.command <-- *` and any other children or modifiers in any order inside one element, and they will happily coexist with no interference. Correct order of elements will be maintained even in edge cases such as showing zero elements. Laminar adds a sentinel HTML comment node into the DOM for each `child* <-- *` receiver to make this possible. These nodes effectively "reserve the spot" for children provided by a particular receiver.


### Binding Observables

By now you must have realized that `-->` and `<--` methods are the main syntax to connect / bind / subscribe observables to observers in Laminar.

To be more precise, such methods always return a `Modifier` that needs to be applied to an element. And when it is applied, a dynamic subscription is created such that it activates when the element is mounted, and deactivates when the element gets unmounted. The types of such modifiers are `Binder` for those that deal with props / attributes / events / etc., and `Inserter` for those that add children to the element.

Inserters are special because they reserve a spot in the element's children where the children provided by the Inserter will be inserted. How does that work? When an Inserter is applied to an element, it appends a sentinel node to the element – just an empty, invisible `CommentNode`. When it's time to insert some children, the inserter either replaces the sentinel node with the next child (in case of `child <-- childStream`), or inserts new children right after the sentinel node (in case of children <-- `childrenStream`). That way your dynamic children will always appear where you expect them to, and you can mix and match any number of child inserters in a single parent element.

So far we've talked about Binders and Inserters provided by Laminar. But what if you want to subscribe an arbitrary Observable to an arbitrary Observer? First of all, why would you even need Laminar's help in this? The answer is [Ownership](#ownership) – in order to create an Airstream Subscription, you need an `Owner`, and in 99.9% cases you don't want to create those manually. You want to use owners provided by Laminar. In fact, what you really want is to avoid the ownership boilerplate altogether. When you sent click events to an observer using `onClick --> observer`, you didn't need to think about owners. That was nice.

It should come naturally then that Laminar offers a similar way to bind any observable to any observer:

```scala
val eventStream: EventStream[A] = ???
val eventBus: EventBus[A] = ???
val observable: Observable[A] = ???
val observer: Observer[A] = ???
def doSomething(value: A): Unit = ???
 
div(observable --> observer)
div(observable --> doSomething)
div(eventStream --> eventBus.writer)
div(eventStream --> eventBus) // same as above
```

Now, what's your intuition for how long any of these subscriptions will live? Subscriptions can't just live forever, they are leaky resources and need to be shut down. Airstream offers a way to manage this automatically using `Owners`. But you don't see any `Owner`s here (there's nothing `implicit` either).

The answer was a few paragraphs ago. Subscriptions created by modifiers (and `observable --> observer` is a `Modifier`, a `Binder` to be exact) are activated when the element to which that modifier is applied is mounted, and deactivated when the element is unmounted. This was entirely seamless for `onClick --> observer` because users wouldn't be able to click on your element when it was unmounted anyway, but for arbitrary observables you need to be aware that their subscription is deactivated when the element is unmounted. That would mean that if `observable` doesn't have any other observers, it would stop (See Lazyness section in Airstream docs if you're not sure why).

The lifecycle of subscriptions is discussed in greater detail in the [Ownership](#ownership) section below.


### Other Binders


#### Focus

```scala
val focusStream: EventStream[Boolean]
input(focus <-- focusStream)
```

You can dynamically focus or blur an element by emitting `true` or `false` respectively. Keep in mind that this is just compositional sugar over calling `myInput.ref.focus()` or `myInput.ref.blur()` with all the same limitations – emitting `true` once will not make Laminar enforce focused state on the input until you emit `false`. If the user clicks away from the focused element, the focus will still be lost, and if you need it otherwise, you'll have to code that yourself.

Speaking of focus, Laminar provides a simple `onMountFocus` modifier that focuses the element when it's mounted into the DOM. It's like the HTML `autoFocus` attribute that actually works for dynamic content.



## ClassName and Other Special Keys

Almost all props and attributes behave exactly the same way regarding the `apply` / `:=` / `<--` methods, however a few keys are special in Laminar.


### cls

This [reflected attribute](https://github.com/raquo/scala-dom-types#reflected-attributes) (aliased as **`className`**) contains a string with space-separated CSS class names that a given HTML or SVG element belongs to. Laminar provides a specialized API for this attribute. Consider this use case:

```css
# CSS
.LabelComponent {
  font-size: 20pt;
  color: red;
}
.vip {
  text-transform: uppercase;
}
```

```scala
// Scala
val veryImportant: Modifier[HtmlElement] = cls := "vip"
def Label(caption: String): HtmlElement = div(
  veryImportant,
  cls := "LabelComponent",
  caption
)
 
val myLabel: HtmlElement = Label("Hi, I'm a caption.") 
```

Here we have a Label component that we can use to render a pretty caption, and a reusable `veryImportant` modifier that makes any element it is applied to look VERY IMPORTANT. This tasteless styling is provided by CSS class names `LabelComponent` and `vip`.

The code above makes it obvious that we expect `myLabel` element to contain two CSS classes: `LabelComponent` and `vip`. However, does it? From previous documentation sections we know that the `:=` method _sets_ the value of an attribute, overriding its previous value, so in this case it looks like `cls := "LabelComponent"` overrides `cls := "vip"`, leaving `myLabel` without the `vip` class.

Needless to say, if `cls` behaved like that it would be really inconvenient to work with. Just imagine how much more complicated / coupled / inflexible you'd need to make the code to support this simple example of ours.

And so `cls` does not behave like this. Instead of **setting** the list of CSS classes, `cls := "newClass"` **appends** `newClass` to the list of CSS classes already present on the element, so our example code works as intended.

Here are some more `cls` tricks. All of these produce a `Modifier[HtmlElement]`:

```scala
// ADD multiple class names
cls := ("class1", "class2")
cls := List("class1", "class2")
 
// Remember that apply method is alias for :=. Below is the same as above
cls("class1", "class2")
cls(List("class1", "class2"))
 
// ADD and REMOVE class names (true=add, false=remove)
cls := ("class1" -> true, "class2" -> false)
cls := Seq("class1" -> true, "class2" -> false)
cls := (Seq("class1" -> true, "class2" -> false), Seq("class3" -> true))
cls := Map("class1" -> true, "class2" -> false)
 
// As usual, all := methods are aliased as `apply`
cls(List("class1", "class2"))
 
// SET a class name instead of ADDING it
cls.set("class1")
// SET multiple class names
cls.set("class1 class2")
cls.set("class1", "class2")
// CLEAR all class names
cls.set("")

// REMOVE class names
cls.remove("class1")
cls.remove("class1 class2")
cls.remove("class1", "class2")

// TOGGLE class names (true = add, false = remove)
cls.toggle("class1") := true
cls.toggle("class1 class2") := false
cls.toggle("class1", "class2") := true

// Note: some of the := methods accept an implicit param, so you might
// need to wrap the modifier in a `locally` to help Scala parse an
// immediate application of such a modifier to an element:
locally(cls := ("class1", "class2"))(element)

// Using the `amend` method is nicer anyway:
element.amend(cls := ("class1", "class2"))
```

Of course, the reactive layer is similarly considerate in regard to `cls`. Consider this use case:

```scala
val classesStream: EventStream[Seq[String]] = ???
val boolStream: EventStream[Boolean] = ???
 
div(
  cls := "MyComponent",
  cls <-- classesStream,
  cls.toggle("class1", "class2") <-- boolStream 
)
``` 

Once again, we don't want the CSS class names coming in from `classesStream` to override (remove) `MyComponent` class name. We want that one to stay forever, and for the class names coming from `classStream` to **override the previous values emitted by that stream**. And this is exactly how Laminar behaves. The Modifier `cls <-- classesStream` keeps track of the class names that it last added to the element's `cls` attribute. When a new event comes in, we remove those previously added class names and add new ones that are not already on the element.

So for example, when `classesStream` emits `List("class1", "class2")`, we will _add_ those classes to the element. When it subsequently emits `List("class1", "class3")`, we will remove `class2` and add `class3` to the element's class list.

The **`<--`** method can be called with Observables of `String`, `Seq[String]`, `Seq[(String, Boolean)]`, `Map[String, Boolean]`, `Seq[Seq[String]]`, `Seq[Seq[(String, Boolean)]]`. The ones involving booleans let you issue events that instruct Laminar to remove certain classes (by setting their value to `false`). Note that unless you instruct Laminar to add those classes back, they will not be added back automatically. Only previous _additions_ of classes are undone in the manner described in the previous paragraph, not removals.

Each `cls <-- source` modifier should deal with a set of CSS classes that does not intersect with the sets used by other such modifiers. In other words, you should generally avoid adding classes to an element in one reactive modifier and then removing them in another reactive modifier. Basically, if you do this you have two competing sources of truth for whether an element should carry a class at any given time, and the latest source to emit will win. That works, but is a bad practice. Search for "interference" in `CompositeKeySpec` for a simple example.


### Other Composite Keys

**`rel`** is another space-separated attribute. Its API in Laminar is exactly identical to that of `cls` (see right above). For example `rel := ("noopener", "noreferrer")` is a Modifier that makes the `a` element it applies to [safer](https://mathiasbynens.github.io/rel-noopener/) without removing existing `rel` attribute value.

**`role`** attribute works similarly.


### SVG

Laminar lets you work with SVG elements (almost) just as well as HTML. Everything works pretty much the same, just need to understand the required imports:

* SVG elements and attributes are available in the `com.raquo.laminar.api.L.svg` object
* Some of the attributes of SVG and HTML have the same names, for example there are two instances of the `className` attribute, one is `L.className` and is an HTML attribute, the other is `L.svg.className` and is an SVG attribute. You can only apply SVG attributes to SVG elements (and not HTML elements), and similarly for HTML attributes.
* Therefore, you generally should **not** import both `com.raquo.laminar.api.L.svg._` and `com.raquo.laminar.api.L._` as that would cause name collisions.
* On the other hand, event prop keys such as `com.raquo.laminar.api.L.onClick` apply universally to all elements, both HTML and SVG.
* If you are working mostly with HTML elements in a given file, you can just import `com.raquo.laminar.api.L._` and refer to SVG elements and attributes with an `svg` prefix, like this:
  ```scala
  div(
    className := "someHtmlClassName",
    onClick --> ???,
    svg.svg(
      svg.height := "800",
      svg.width := "500",
      svg.polyline(
        svg.points := "20,20 40,25 60,40",
        svg.className := "someSvgClassName",
        onClick --> ???,
        children <-- ???
      )
    )
  )
  ```  
* On the other hand, if you are working mostly with SVG elements in a given file, you can use a different set of imports :
  ```scala
  import com.raquo.laminar.api.L.svg._ // get svg keys without the svg prefix
  import com.raquo.laminar.api._ // get `L` and standard implicits like `textToNode`
  // DO NOT IMPORT: com.raquo.laminar.api.L._
  
  L.div(
    L.className := "someHtmlClassName",
    L.onClick --> ???,
    svg(
      height := "800",
      width := "500",
      polyline(
        points := "20,20 40,25 60,40",
        className := "someSvgClassName",
        L.onClick --> ???,
        L.children <-- ???
      )
    )
  )
  ```



## Event System: Emitters, Transformations, Buses 


### Registering a DOM Event Listener

Laminar uses native JS DOM events. We do not have a synthetic event system like React.js. There is no event pooling, no magic going on. This means there is nothing Laminar-specific to learn about events themselves. There's one exception for certain `onClick` events on checkboxes, see [Special Cases](#special-cases).

To start listening for DOM events, you need to register a listener for a specific event type on a specific element. In Laminar, `EventPropBinder` is a `Modifier` that performs this action.

This is how it's done in the simplest case:

```scala
val clickObserver = Observer[dom.MouseEvent](onNext = ev => dom.console.log(ev.screenX))
val element: Div = div(
  onClick --> clickObserver,
  "Click me"
)
```

`Observer` is an Airstream type that is typically used to perform side effects on incoming events. Usually it's used like this: `someStream.addObserver(someObserver)(owner)`. This would ensure that `someStream` runs (remember, streams are lazy), and would call `someObserver.onNext(ev)` for every new event that `someStream` emits.

This `-->` method simply registers an event listener on `element` which calls `clickObserver.onNext(ev)` for every click event that comes in, and then the observer prints this event's X coordinate to the console.


### EventBus

The contrived example above does not feel like reactive programming at all. As shown, it's just a cumbersome way to define a simple callback. Instead, we usually want to get a stream of events from an element. To do that, we need an Observer that will expose a stream of events that it receives. In FRP terms this is usually called a "Subject" or a "PublishSubject".

In Laminar, we have an `EventBus`. Same general idea, but instead of _being_ both an Observer and an Observable, it _contains_ them as `.writer` and `.events` respectively. This design makes it possible to easily share write-only or read-only parts of an EventBus, with no upcasting loopholes. You should generally avoid passing the whole EventBus to child components unless you really do need the child component to both write events to the bus and read the entirety of events written to that bus (including events written by other components into that bus).

Now let's see how we can use EventBus for the same logic:

```scala
val clickBus = new EventBus[dom.MouseEvent]
val coordinateStream: EventStream[Int] = clickBus.events.map(ev => ev.screenX)
val coordinateObserver = Observer[Int](onNext = x => dom.console.log(x))
 
val element: Div = div(
  onClick --> clickBus.writer, // .writer is actually optional here, for convenience
  "Click me",
  coordinateStream --> coordinateObserver
)
```

`onClick --> clickBus.writer` works exactly the same as the previous example, except that `clickBus.writer` is now the Observer that we send the events to. For convenience, the `-->` method will also accept the event bus itself: `div(onClick --> clickBus, ...)` woudl have exactly the same effect.

What's new here is how we subscribe `coordinateObserver` to `onClick` events. As we established, we first send onClick events to `clickBus`. Then we have a `coordinateStream` that maps every click event from `clickBus` to its `screenX` coordinate.

Finally, we subscribe `coordinateObserver` to `coordinateStream` to print out those coordinates on every click. This completes our contrived pipeline. In real life this component might only be exposing the stream of coordinates, and consuming components would provide their own observers. Or this same component could be deriving some of its own state from the click stream, such as tracking and displaying the number of clicks.

It's not a coincidence that `coordinateStream --> coordinateObserver` syntax looks very similar to `onClick --> clickBus.writer`. Both of these `-->` methods create Modifiers that send events from the left hand side to the right hand side for as long as the element is mounted, conceptually speaking. Both methods are also provided implicitly as defined in `Implicits.scala`.


#### Transforming Observers

You can `contramap` and `filter` Observers, for example:

```scala
val pieObserver: Observer[Pie] = Observer(onNext = bakePie)
val appleObserver: Observer[Apple] = pieObserver.contramap(apple => makeApplePie(apple))
 
def AppleComponent(observer: Observer[Apple]): Div = {
  val appleStream: EventStream[Apple] = ???
  div(
    "Apple Component",
    appleStream --> observer,
    ???
  )
}
 
val app = AppleComponent(appleObserver)
```

In this pattern AppleComponent has no knowledge of pies. All it knows is how to produce apples and send them to an apple observer. Delicious culinary metaphors aside, replace pies with ajax requests and apples with todo items that need to be created, and you can see how this separation of concerns is useful.

If your observer is a `WriteBus` (e.g. `myEventBus.writer`), you have even more methods available to you: `filterWriter`, `contramapWriter` and (gasp) `contracomposeWriter`. 


#### Feeding Multiple Sources into EventBus

Note that Airstream Observers, including `eventBus.writer`, can subscribe to multiple observables. So you can for example render a dynamic list of children, each of which sends its own events to the same Observer, possibly with information included in the event needed to identify which child the event came from. You can see an example of this pattern used in `TaskListView.taskViewsStream` in [laminar-examples](https://github.com/raquo/laminar-examples).

This can be achieved either with `addSource` or `addObserver`. Basically, in terms of laziness, with `addSource` EventBus behaves like a stream that merges other streams – much like `EventStream.merge(stream1, stream2)`, but with the ability to add and remove source streams at any time. So, if you do `eventBus.writer.addSource(sourceStream)(childOwner)`, `sourceStream` will not be started until and unless `eventBus.stream` is started. Conversely, `sourceStream.addObserver(eventBus.writer)(childOwner)` will immediately start `sourceStream` even if `eventBus` does not have any observers.

The best part of this pattern is that you don't need to write custom logic to call `removeSource` or `subscription.kill` when removing a child from the list of children. `childOwner` will take care of that. Typically, that would be provided by the child Laminar element, via `onMountCallback`. Such an owner kills its subscriptions when the corresponding element gets removed from the DOM.


### Alternative Event Listener Registration Syntax

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

This generic component does not necessarily know what events on `inputNode` the consumer will care about when using it – `onKeyUp`, `onKeyPress`, `onChange`, `onInput`, others? There's just too many possibilities. And of course you wouldn't want to clutter your `TextInput`'s API by e.g. exposing all _potentially_ useful event streams as vals on the `TextInput` class.

That's what `ReactiveElement.events(eventProp)` method was made for:

```scala
val textInput = TextInput("Full name:")
val changeEventStream: EventStream[dom.Event] =
  textInput.inputNode.events(onChange)
val changeObserver: Observer[dom.Event] = ???
 
div("Hello", changeEventStream --> changeObserver)
```

Under the hood, `element.events(eventProp)` creates an EventBus, applies an `eventProp --> eventBus` modifier to `element`, and returns `eventBus.events`. That's all there is to it, no magic – just alternative syntax that makes it easier to compose your components without tight coupling.

The `events` method also accepts `EventPropTransformation`, not just ReactiveEventProp. This allows for very flexible composition:
 
 ```scala
// You can define this once for reusability
val onEnterPress: EventPropTransformation[dom.KeyboardEvent, dom.KeyboardEvent] =
  onKeyPress.filter(_keyCode == KeyCodes.Enter)
 
input(onEnterPress --> observer)
 
textArea(onEnterPress.preventDefault --> observer)
 
val changeEventStream = myInputNode.events(onEnterPress)
```



### Multiple Event Listeners
 
Just like in native JS DOM, nothing is stopping you from registering two or more event listeners for the same event type on the same element:
 
 ```scala
div(onClick --> clickBus1, onClick --> clickBus2, "Click me")
``` 

In this case, two event listeners will be registered on the DOM node, one of them sending events to `clickBus1`, the other sending the exact same events to `clickBus2`. Of course, this works the same regardless of what syntax you use to register event listeners (see previous section).

In most cases you could simply add another listener to `clickBus.events` to achieve whatever you needed multiple event listeners bound to the same element for – it's easier and more performant in extreme cases (thousands of nodes, I'd guess).

However, sometimes simpler composition is more important. For example, consider the `TextInput` component mentioned above. That component itself might have an internal need to listen to its own `onChange` events (e.g. for some built-in validation), and it will not interfere with any other `onChange` event listeners the end user might want to add to it.


### Event Transformations

Often times you don't really need a stream of e.g. click events – you only care about the subset of the events or only parts of their data. With the Alternative Syntax described above you would just use Airstream operators to transform the stream of events, like this:

```scala
val incrementButton = button("more")
val decrementButton = button("less")
 
val diffStream: EventStream[Int] = EventStream.merge(
  incrementButton.events(onClick).mapTo(1), 
  decrementButton.events(onClick).mapTo(-1) 
) // this stream emits 1 or -1 depending on which button was clicked
```

Or even:

```scala
val clickObserver: Observer[dom.Event] = ???
input(inContext(_.events(onClick).delay(100) --> clickObserver))
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
 
form(onSubmit.preventDefault.map(getFormData) --> formSubmitObserver)
 
div(onClick.useCapture --> captureModeClickObserver)
 
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


#### preventDefault & stopPropagation

```scala
div(onScroll.stopPropagation.filter(throttle) --> filteredScrollEventBus)
form(onSubmit.preventDefault.map(getFormData) --> formSubmitObserver)
```

These methods correspond to invocations of the corresponding native JS `dom.Event` methods. MDN docs: [preventDefault](https://developer.mozilla.org/en-US/docs/Web/API/Event/preventDefault), [stopPropagation](https://developer.mozilla.org/en-US/docs/Web/API/Event/stopPropagation)

Importantly, these are just ordinarily transformations, and happen in the order in which you have chained them. For example, in the code snippet above `ev.preventDefault` will only be called on events that pass `filter(_.keyCode == KeyCode.Enter)`. Internally all transformations have access to both the latest processed value, and the original event, so it's fine to call the `.preventDefault` transformation even after you've used `.map(_.keyCode)`.


#### useCapture

```scala
div(onClick.useCapture --> captureModeClickObserver)
```

JS DOM has two event modes: capture, and bubbling. Typically and by default everyone uses the latter, but capture mode is sometimes useful for event listener priority/ordering/efficiency (not specific to Laminar, standard JS DOM rules/limitations apply).

`.useBubbleMode` is the opposite of `.useCapture`. The former is the default, so you don't need to call it unless you're calling it on an EventPropTransformation that you've previously enabled capture mode for.

See MDN [addEventListener](https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener) page for details (see "useCapture" section).


#### Obtaining Typed Event Target

Due to dynamic nature of Javascript, `dom.Event.target` is typed only as `dom.EventTarget` for most events, which is not useful when you want to get `ev.target.value` from a target that is a `dom.html.Input` (but doesn't know it). So, you can't do this:

```scala
// Does not work because .value is defined on dom.html.Input, but not on dom.EventTarget, which is what `ev` is :(
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


### Window & Document Events

In Javascript, `window` and `document` support their own custom sets of events. You can obtain streams of those events like this: `documentEvents.onDomContentLoaded.map(ev => renderApp())`.

`windowEvents` provides similar access to `window` events.

These event streams specify [`useCapture = false`](#usecapture), which is probably what you want. If you need capture mode for window or document events, you can instantiate a `DomEventStream` manually:

```scala
new DomEventStream[TypedTargetEvent[dom.Node]](
  dom.document,
  onClick.name,
  useCapture = true
)
```

Depending on your desired logic, you might not have a Laminar element to act as an Owner for subscriptions of these event streams. For example, if you implement an Ajax service based on observables, you would probably want it making requests regardless of observers. For such app-wide subscriptions you can use `unsafeWindowOwner`. It will never kill its possessions, so needless to say – use with caution. Any subscriptions created with this owner will never be cleaned up by Laminar, so you will need to clean them up manually if needed.



## Ownership

Ownership is a core Airstream concept. You **really** need to read about it in [Airstream docs](https://github.com/raquo/Airstream#ownership) before proceeding with this section.

Basically, Airstream does not let you create a leaky resource without specifying an Owner which will eventually kill said resource. For example, adding an Observer to an EventStream is a potential memory leak. Once a subscription is established like that, the stream in question obtains a reference to that observer, and the stream's parent stream obtains a reference to this stream, etc. Depending on how long that chain is and how it's used, there is a good chance it will either never be garbage collected or will be garbage collected much later than optimal.

With other libraries you would need to eventually manually call `subscription.kill()` for every `addObserver` call you make to avoid memory leaks like that. In Airstream however, you do not need to do that. Instead, when adding an observer you need to specify not just the observer, but also the Owner of this new subscription.


### Laminar's Use of Airstream Ownership

In Laminar versions **prior to v0.8.0** every Laminar element was an `Owner`. In fact, when you used `<--` methods on elements or in their modifiers, the element in question was the owner of the subscription that the `<--` method created. Laminar elements killed their subscriptions when they were unmounted. Sounds simple and effective enough, yet this old design had two known flaws:

1. The subscriptions of an element that was created but never added to the DOM  would be created when the element was instantiated, but would never be killed since it was never unmounted from the DOM, so you needed to be careful to not create any elements that you weren't going to immediately put into the DOM.

2. Once you unmounted an element, its subscriptions would be killed with no way to resurrect them. So essentially you weren't able to reuse previously unmounted elements as their subscriptions wouldn't be re-activated when the element was put back into the DOM.

Laminar v0.8.0 introduced dynamic subscriptions with a new contract: an element is no longer an Owner, instead an element creates a new Owner every time it's mounted, and kills that owner and all of its subscriptions when it's unmounted. This means that the element's subscriptions are not activated until it's mounted, solving problem 1. We now also track your subscriptions in a way that allows Laminar to re-activate them after the element is re-mounted, solving problem 2.

Let's see how ownership in the new Laminar works under the hood by analyzing a simple snippet. You might want to follow along by reading the source code of the methods involved.

```scala
val urlBus: EventBus[String] = new EventBus
val urlStream: EventStream[String] = urlBus.events
val element = a(href <-- urlStream) // create the element
render(element) // mount the element
```

1. `href <-- urlStream` creates a `Binder` (see `ReactiveProp.<--`) 

2. That `Binder` being a `Modifier` is applied to the `a` element immediately after it's created by the `a(...)` call..

3. That Binder's apply method does this (see `ReactiveProp.<--` again):
  
    ```scala
    ReactiveElement.bindFn(element, $value) { value =>
      DomApi.setHtmlProperty(element, this, value)
    }
    ```
  
    Which after a few layers of abstraction resolves to:
  
    ```scala
    new DynamicSubscription(element.dynamicOwner, activate = owner => Some(
      val subscription: Subscription = urlStream.foreach { url =>
        DomApi.setHtmlProperty(element, href, url)
      } (owner)
      subscription
    ))
    ```
   
4. So when the `href <-- urlStream` Binder is applied to the `a()` element, it creates a `DynamicSubscription` which in turn has an activation function that creates a regular non-Dynamic `Subscription`.

5. Let's unravel this onion. When the element is mounted its `dynamicOwner` is activated, and in turn activates all of its `DynamicSubscription`-s including the one we see here.

    Note that `DynamicOwner` is not a subtype of `Owner`, and `DynamicSubscription` is not a subtype of `Subscription`. The `Dynamic*` counterparts are simply wrappers / helpers for a certain pattern.  

6. When the `DynamicSubscription` is activated, it calls its activation function `activate`. That function is provided with an `Owner` that the element's `DynamicOwner` created upon activation.

7. This `Owner` is essentially our proof that the element is currently mounted, so the activate function uses it to create a regular non-dynamic `Subscription`.

8. As long as this `Subscription` is alive, every time `urlStream` emits a url string, Airstream calls `DomApi.setHtmlProperty` to set the element's href property to this new url. 

8. When we eventually decide to unmount the `element`, the same process unravels in reverse:

9. The element's dynamicOwner is deactivated. It deactivates all of its `DynamicSubscription`s including this one we're looking at.

10. When this DynamicSubscription is deactivated, it permanently kills the `Subscription` that its activate function created (see step 6). This removes the observer from `urlStream` (and in this case stops the stream because it has no other subscriptions).

11. Now that the element is unmounted and the `subscription` is killed, sending events to `urlStream` will not change the `href` property anymore.

12. After the element has been unmounted for a while, we decide to mount it into the DOM again. This activates the element's `dynamicOwner`, which activates the same `dynamicSubscription`, which calls its `activate` function to create a new `subscription`, which binds `urlStream` to an observer that sets the `href` property to the new url.

13. So in other words, on this second mounting we repeat only steps 5 to 8. Steps 1 to 4 were only taken on initial creation of the element. 

This was a lot of words, but hopefully I spelled it out clearly enough. The actual code of `DynamicSubscription` is much shorter than my explanation, and `DynamicOwner` is not much worse, and even then it's mostly to handle scheduling edge cases that are mostly transparent to end users.



## Memory Management

Once again I assume that you have read the [relevant section](https://github.com/raquo/Airstream#ownership--memory-management) of Airstream docs. The basic principles are as follows:

* Observables (streams, signals) are not garbage collected as long as they have observers or dependent observables that are not eligible for garbage collection

* However, if your entire observable dependency graph – everything from the original event producer to the observables that depend on it and the observers that listen to those observables – if all that becomes unreachable then all of that will be garbage collected.

* So none of this negates general JS garbage collection rules. We don't use WeakMap or WeakRef. I'm just giving you guidelines so that you don't need to go into Airstream and Laminar source code trying to figure out what holds references to what.

With proper use of ownership, Laminar's memory management is pleasantly automatic. Nevertheless, programming is hard, so here are is the rule of thumb for preventing memory leaks in Laminar:

* Subscriptions should generally be owned by a Laminar element that has a lifespan that matches the desired lifespan of said subscription. In plain English, **it should make sense** for an element to own the subscription – the element should be the main user of that subscription. The question to ask yourself is – at which point do you want the subscription to die and release its resources? The element that makes use of the subscription and is unmounted at the same time should be its owner.

* Corollary: if you have a parent element which has dynamic children, for example by means of `children <-- childrenStream`, each child must NOT use the parent's `Owner` to create subscriptions, because those subscriptions would only terminate when the parent is unmounted, not the individual child. So if you cycle through a lot of children you will accumulate worse-than-useless subscriptions that should have been killed when previous children were unmounted.

  Of course, Laminar's API does not let you do this _easily_. Also of course, Laminar's API does let you do this if you directly ask for this by getting the parent's `Owner` from an `onMount` hook and passing that owner to children. With Laminar you can always shoot yourself in the foot, but you have to explicitly ask for it. So you should prefer less manual methods: a simple `foo <-- bar` instead of messing with owners manually.

Lastly, a warning about `textContent` and `innerHTML`:

* You must not set [textContent](https://developer.mozilla.org/en-US/docs/Web/API/Node/textContent) or [innerHTML](https://developer.mozilla.org/en-US/docs/Web/API/Element/innerHTML) properties on Laminar elements that have any other children given to them by Laminar. We deliberately do not provide an API to set these properties because of their messy side effects. If you need this functionality, it is up to you to use native JS functionality available on `element.ref` to set those properties, **and** ensure that you either manage the contents of a particular element with Laminar, or you do it manually with `innerHtml` / `textContent`, not both. The two approaches don't mix on the same element. Needless to say, when using native JS methods including `innerHtml` you need to understand [XSS](https://www.owasp.org/index.php/Cross-site_Scripting_(XSS)) and other risks as well.



## Element Lifecycle Hooks

Lifecycle hooks let you do stuff when the element is being mounted or unmounted, as well as obtain and use an `Owner` associated with the element when it element is mounted. 


### What Is Mounting?

**In JS DOM** an element is said to be mounted when one of its ancestors is the document being displayed: `dom.document`. In other words, a mounted element is present in the DOM, or "attached" to the DOM. On the contrary, elements that are not mounted are called "unmounted" or "detached". Those elements are not present in the DOM. They might have none, one or more ancestors, but none of them are `dom.document`, so the highest level ancestor of this element must have no parent.

**Laminar** does not get notified by the browser when elements change parents and become mounted / unmounted. Therefore technically we use a slightly different definition of mounted / unmounted elements. A Laminar element is considered mounted when one of its ancestors is a **mounted** Laminar `RootNode`. A `RootNode` is created with `val rootNode = render(containerEl, contentEl)`. A `RootNode` is mounted immediately if `containerEl` was present in the DOM when `render` is called. Other than that, you need to manually call `mount()` / `unmount()` methods on `rootNode` to toggle its mounted / unmounted state.

In practice this distinction is only useful for integrating with third party libraries, e.g. rendering a Laminar element inside a React.js component. If you're rendering your whole app in Laminar, you're likely rendering it into some `<div id="appContainer"></div>` element that's always present in the DOM so there is no practical difference between Laminar's definition of mounting and JS DOM definition of mounting.

Laminar elements that are mounted are also called active, because the subscriptions associated with them are also active while they're mounted. Every Laminar element starts out unmounted because it has no parent. An element can become mounted if its parent becomes mounted – either if parent is changed to a different one, or if the same parent becomes mounted. The latter is a recursion that terminates at the `RootNode`.

You can check if a node is mounted in JS DOM terms with `ChildNode.isNodeMounted`.

You can check if a Laminar element is mounted in Laminar terms using `ReactiveElement.isActive`.

Laminar offers a few helpers to react to element lifecycle events:


### `onMountCallback`

**Don't rush to use `onMountCallback` for everything, read the docs for all lifecycle hooks first.**

Simplest way to run some code when the element is mounted. For example:

```scala
input(onMountCallback(ctx => ctx.thisNode.ref.focus()))
```

This code focuses an input element every time it's mounted. This `onMountCallback(...)` modifier provides a `MountContext` to a callback, and runs that callback every time the element is mounted.

Aside from `thisNode` which refers to the element to which the modifier is being applied – our input node in this case – `MountContext` also provides you with an `owner` which was just created, and which will be killed when the element is next unmounted. So you can use this owner to bind any Airstream subscriptions, for example:

```scala
val observable: Observable[Int] = ???
val onNext: Int => () = ???
input(onMountCallback(ctx => observable.foreach(onNext)(ctx.owner)))
```

However, you don't need to do this. If you just need a reference to `thisNode`, you can just use `inContext(thisNode => modifier)`, or if you just need the `owner` to bind a subscription, you can just say `input(observable --> onNext)` to achieve the same effect, or even combine the two approaches.

The small but important difference is that `onMountCallback` will only execute the callback when the element is being mounted, so if you add the `onMountCallback` modifier to an already mounted element, the callback will not fire until the element is unmounted and then mounted again. On the other hand, the `observable --> onNext` modifier would bind the subscription even if you added it after the element was mounted, like any Binder would. So for example:

```scala
val inputEl = input(onMountCallback(_ => println("OG"))) // create element
val rootNode = render(inputEl) // mount it. This will print "OG"
val printBus: EventBus[String] = new EventBus 

inputEl.amend(
  printBus.events --> (str => println("Mod: " + str)),
  onMountCallback { _ =>
    println("New gang") // this will print nothing right now
  },
  onMountCallback { ctx =>
    printBus.events.foreach(str => "Mount: " + str)(ctx.owner)
  }
)

printBus.writer.onNext("hello") // this will print "Mod: hello"
 
rootNode.unmount() // unmount the element
rootNode.mount() // this will print both "OG" and "New gang"

printBus.writer.onNext("hi") // this will print "Mod: hi" and "Mount: hi"
```

The bottom line is, avoid using `onMountCallback` if all you need is a reference to `thisNode` or `Owner`. Better use `inContext` or `amend` if you need `thisNode`, and binders like `foo <-- bar` instead of messing with owners manually. Use `onMountCallback` when its purpose aligns with your big picture: running some code on mount. The same goes for all other helpers below.
 

### `onUnmountCallback` & `onMountUnmountCallback`

Works just like `onMountCallback`, same caveats.

```scala
div(
  onUnmountCallback(thisNode => println("unmounting")),
  onMountUnmountCallback(
    mount = ctx => println("mounting"),
    unmount = thisNode => println("unmounting")
  )
)
```

For easier integration with third party libraries we also have `onMountUnmountCallbackWithState` that can remember any value returned by the mounting callback:

```scala
div(
  onMountUnmountCallbackWithState[Div, State]( // need to provide type params explicitly
    mount = ctx => doStuffAndReturnState(),
    unmount = (thisNode, maybeState) => maybeState.foreach(cleanupState)
  )
)
```


### `onMountSet`

As you might remember, `Setter` modifiers are idempotent, which is to say applying the same modifier N times in a row to the same element has the same effect regardless of what N is, if N >= 1. We have a special helper to apply one or more Setters on mount:

```scala
val link = a(
  "website",
  onMountSet(ctx => {
    println("mount")
    if (ctx.thisNode.maybeParent.exists(_.ref.classList.contains("-legit"))) {
      href := "http://example.com"
    } else {
      href := "http://evil.com"
    }
  })
)
``` 

In this contrived example, every time this `link` element is mounted, the callback will execute, and the resulting setter will be applied to the element. So every time `link` is mounted, it will print "mount", check whether its new parent has a "-legit" CSS class name, and set the `href` attribute based on that.

Note that when the element is unmounted, any setters applied to it this way are not un-applied. So for example if you were to apply red color to evil links like this:

```scala
val link = a(
  "website",
  onMountSet(ctx => {
    println("mount")
    if (ctx.thisNode.maybeParent.exists(_.ref.classList.contains("-legit"))) {
      href := "http://example.com"
    } else {
      List(
        href := "http://evil.com",
        color := "red"
      )
    }
  })
)
```

Once `link` was mounted and deemed evil, it would become red and never cease being red even if it was re-mounted as a non-evil link, because there is no code to unset this red color or replace it with any other color. As you might have guessed, `Seq[Setter[El]]` implicitly converts to `Setter[El]`, so all you need to do is to change `href := "http://example.com"` to `List(href := "http://example.com", color := "black")` to make the color change with the url.


### `onMountBind`

This is a special helper for binders like `onClick --> observer`, `attr <-- stream`.

```scala
val observer: Observer[dom.MouseEvent] = ???
div(
  onMountBind(ctx => {
    doSomething(ctx) // let's assume we need this for some reason 
    onClick --> observer
  })
)
```

It looks similar to `onMountCallback`, so why do we need a special method for this? The short answer is that `onMountBind` will permanently kill the dynamic subscription created by `onClick --> observer` on unmount, ensuring that we don't accumulate N dynamic subscriptions after the element is mounted N times. 

---

It's not a coincidence that `onMountCallback` expects the callback to return `Unit` rather than `Modifier`. Let's say you wanted to circumvent this API design by applying the modifier manually inside the callback:

```scala
val observer: Observer[dom.MouseEvent] = ???
div(
  onMountCallback(ctx => {
    doSomething(ctx) // let's assume we need this for some reason
    // WRONG – DO NOT DO THIS!
    ctx.thisNode.amend(onClick --> observer)
  })
)
```

As we know, the callback in `onMountCallback` will be executed every time the element is mounted. The first time it's called all is fine – we add an `onClick --> observer` modifier to the div, and the dynamic subscription produced by this binder activates as the element mounts.

Next, you unmount the div element. This deactivates the binder's dynamic subscription

Finally, you mount the div element once again. This re-activates the binder's dynamic subscription. Of course you would expect that, because you expect to be able to unmount and re-mount Laminar elements, and for them to not lose any dynamic subscriptions you defined on them.

However, another thing that happens on the second mount is that the callback in `onMountCallback` executes again. And what does it do? It adds another `onClick --> observer` modifier to the same element. So, now we have two onClick event listeners sending events to the same `observer`, so the observer will be triggered twice for every click event. And if you re-mount the element N times, the observer will be triggered N times for every click event. 

In other words, while `Setter` modifiers are idempotent, `Binder` modifiers are not. They add a dynamic subscription, and if you call them N times, they add N dynamic subscriptions. Those subscriptions don't permanently go away on unmount because that would mean that your elements would become dysfunctional after being unmounted, and we don't want that.

`onMountBind` solves this problem by remembering the dynamic subscription returned by the binder and permanently killing it on unmount, instead of merely deactivating it. So when the element mounts again, onMountBind callback will run again, but there will be no pre-existing dynamic subscription from the previous mount left. 

So we know why `onMountCallback` fails us technically, but why is it failing us philosophically? Well, Laminar is just doing what we're telling it to do: "Every time this element mounts, create a new click listener and add it to this element". And the reason you're even able to tell this to Laminar is: _what if this was what you actually wanted to do_? What would be a simpler way to express that? I can't think of any without introducing completely arbitrary helpers that are dependent on fleeting fashions on frontend dev.

And so, `onMountBind` exists as the simplest way to do what it does, and `onMountCallback` as the simplest way to do the other thing.


### `onMountInsert`

Just like you use `onMountBind` when you need `MountContext` to bind a subscription, you use `onMountInsert` when you need `MountContext` to insert children (or if you truly need to insert children only on mount).

```scala
div(
  "Hello, ",
  onMountInsert { ctx =>
    val childStream: EventStream[Span] = makeStream(ctx.owner)    
    child <-- childStream
  },
  "!"
)
```

Essentially `onMountInsert` is to `Inserter` what `onMountBind` is to `Binder`, and what `onMountSet` is to `Setter`, and what `onMountCallback` is to `() => Unit`.

Thus the callback can return any `Inserter`, including those created by modifiers `children <-- ???`, `children.command <-- ???`, `child <-- ???`, `child.maybe <-- ???`, and `child.text <-- ???`.

We also have implicit conversions defined in `Implicits.LowPriorityImplicits` that let you return an individual element or a collection of elements from the onMountInsert callback – they would be converted into an Inserter.

So why does `onMountInsert` need to exist, and why doesn't Laminar allow us to put `child <-- childStream` into `onMountBind`?

`onMountInsert` offers the same deal as `onMountBind` in terms of cancelling the subscription on unmount to prevent a conflict between N `child <-- childStream` modifiers after mounting the element N times. Note that cancelling this subscription does **not** mean that the previously inserted child is removed. If you want that, emit `emptyNode` into childStream while it's still mounted.

But as you might recall Inserters like `child <-- ...` and `children <-- ...` reserve a stable spot in the parent element where they will insert new children. That spot is after the last child of the parent element _at the time of reservation_.

This would obviously not work inside `onMountInsert` – the callback is executed only on mount, so the `child <-- childStream` modifier is added only on mount, and at that point the element already has two child nodes in it: `div(TextNode("Hello, "), TextNode("!"))`. Therefore `child <-- childStream` would have reserved a spot **after** "!", which is not what we want, we obviously want it to reserve the spot where the `onMountInsert` modifier itself is visually located, i.e. between "Hello, " and "!".

The `onMountInsert` modifier exists to solve this – it reserves a spot when it's applied to the parent element – before any mount events are fired – and provides information about that reserved spot to the `Inserter` returned by the callback. And that reserved spot persists after mounting and unmounting, of course.


### Why Use Lifecycle Hooks?

* If you legitimately need to run arbitrary code on mount and/or on unmount, for example to integrate with a third party library.

* If you need your callback to run on mount **but also** to run immediately in case the element is already mounted, use `ReactiveElement.bindFn` / `bindObserver` / etc. methods. Generally you shouldn't need these but they might be useful for integration or extensions. 

* If you want an `Owner` to _simplify_ some code. For example, if you have a complex component where you don't want to fiddle with ownership everywhere, you can pass a parent's owner to it using `onMountInsert(ctx => makeComponent(ctx.owner)`, but you have to know what you're doing to avoid memory leaks. Read more about Ownership in Laminar and Airstream docs.

  * Note: if you want an `Owner` because you're upgrading from Laminar v0.7 where each element was an Owner, this might not be the best way to achieve what you want. We've redesigned the API to reduce the need mess around with owners manually. For example, you can have modifiers like `stream --> observer` now.

* If you just need a reference to `thisNode`, don't use lifecycle hooks, use `inContext` aka `forthis`.


### Lifecycle Event Timing

Mounting and unmounting are discrete events that happen at a certain time.

All mounting callbacks / effects are executed when the element has just mounted. At this point the element has its new parent already, if that changed. So pretty much equivalent to React's `componentDidMount` hook.

All unmounting callbacks / effects are executed when the element is about to become unmounted. At this point the element still has its old parent, if it's being moved or detached. So like React's `componentWillUnmount` hook.


### How Are Mount Events Propagated?

Every Laminar element starts its life unmounted: `div("Hello")`. This element has no parent specified, therefore it has no ancestors, and can't have an ancestor that is a mounted RootNode. This element still exists, but it's not present in the DOM, it's detached from teh DOM.

We could mount this element directly: `render(appContainer, div("Hello"))` – but almost all the elements in your application are mounted indirectly, by setting their parent to an already mounted element. How do these elements know when they're being mounted, if their direct parent does not change? 

Consider this example:

```scala
val container: dom.Element = dom.document.querySelector("#appContainer")
val nameStream: EventStream[String] = ???
val titleEl = h1("Welcomer ", b("9000")) 
val contentEl = div(
  titleEl,
  child <-- nameStream.map(name => span("Hello ", b(name)))
)
 
val rootNode: RootNode = render(container, contentEl)
``` 

Assuming container is present in the DOM, `render(container, contentEl)` will immediately mount the `contentEl` div. But if you refer to our definitions above, you'll see that the `h1("Welcomer 9000")` element must also be mounted - its parent is now contentEl, which is mounted. Let's see how this is achieved:

1. When `contentEl` element is created, we create a `DynamicOwner` for it. This `DynamicOwner` owns all `DynamicSubsciption`-s associated with this element, and will last the lifetime of this element. It will create non-dynamic `Owner` for this element every time this element is mounted, and will kill that `Owner` every time this element is unmounted. Laminar does this for every element.

2. `element.dynamicOwner` needs to know when the element is mounted and unmounted so that it can activate and deactivate respectively at the same time.

3. In our example the elements are mounted because the `RootNode` is being mounted. `rootNode.dynamicOwner` is controlled directly by its mount status, and it's triggered to become mounted when we called `render` as we established above. This activates `rootNode.dynamicOwner`.

4. Unlike `RootNode`, any other element's `DynamicOwner` is controlled by its parent element. Basically, when the element's parent element is mounted, or when the element's parent is changed from an unmounted one to a mounted one, its `DynamicOwner` is activated.

5. This is achieved by means of `pilotSubscription` that exists on each element and registers itself as a subscription on the parent element, activating the element's dynamicOwner when the parent's dynamicOwner is activated (and similarly for deactivation). This is a special `TransferableSubscription` that can change its owner from one parent to another if an element changes parents.

6. This proceeds to happen in a cascade. When `contentEl` element is mounted, its DynamicOwner is activated, which activates `titleEl.pilotSubscription`, which activates `titleEl.dynamicOwner`, which activates the `DynamicSubscription` of the `b("9000")` element which activates its `dynamicOwner`. That last element has no children so the chain stops here in this case.

7. But it's not just child elements that create subscriptions managed by the element's `DynamicOwner`. The `child <-- ...` modifier created a subscription on `contentEl.dynamicOwner` that was activated when `contentEl` was mounted.

8. Now that this subscription is active, if we emit "Nikita" into `nameStream` a `span("Hello ", b("Nikita"))` element will be created and inserted into `contentEl`. This will mount / activate this new `span` element, and then its child `b("Nikita")` by the same cascading logic we described above.

9. If we then emit "Ivan" into `nameStream`, the parent of `span("Hello ", b("Nikita"))` will be set to `None`, and that will trigger an unmount / deactivation cascade for these `span` and `b` elements. A new element will then be created and mounted / activated: `span("Hello ", b("Ivan"))`.  



## URL Routing

[Waypoint](https://github.com/raquo/Waypoint) – My URL router for Laminar.

[tulz-app/laminar-router](https://github.com/tulz-app/laminar-router) – Alternative router for Laminar with API inspired by Akka HTTP

These routers are designed for Laminar, but don't actually depend on it, only on Airstream, so you could potentially use them without Laminar.



## Special Cases

Laminar is conceptually a simple layer that brings a lightweight reactive API to native Javascript DOM. In general there is no magic to it, what goes in goes out, transformed in some obvious way. However, in a few cases we do some ugly things under the hood so that you don't need to pull your hair and still do said ugly things in your own code.

Please let me know via github issues if any of this magic caused you grief. It's supposed to be almost universally helpful.


#### checkbox.onClick + event.preventDefault() = async event stream

All event streams in Laminar emit events synchronously – as soon as they happen – **except** if the stream in question is a stream of `onClick` events on an `input(typ := "checkbox")` element, **and** you generated this stream using the `.preventDefault` method in Laminar's API.

Such streams fire events after a `setTimeout(0)` instead of firing immediately after the browser triggers the event. Without this hack/magic, you would have been unable to update the `checked` property of this checkbox from a stream that is (synchronously) derived from the given stream of `onClick` events, and this is common practice when building controlled components.

The underlying issue is described in [this StackOverflow answer](https://stackoverflow.com/a/32710212/2601788).

**Watch out:** If you are reading the `checked` property of the checkbox in an affected stream, it will contain the original, unchanged value. This behaviour could be surprising if you don't know about this stream being async, but do know about the native DOM behaviour of temporarily updating this value and then resetting it back. This is deemed a smaller problem than the original issue because it's easier to debug, and better matches the commonly-expected semantics of `preventDefault`.

**Escape hatch:** instead of using Laminar's `preventDefault` option/method, call `ev.preventDefault()` manually _after_ the event was passed to the observer. Then event handling will work the same way it does in native JS DOM.


#### `tbody`

You might be used to writing HTML markup like this:

```html
<table>
  <tr>
    <td>Row1 Cell1</td>
    <td>Row1 Cell2</td>
  </tr>
  <tr>
    <td>Row2 Cell1</td>
    <td>Row2 Cell2</td>
  </tr>
</table>
```

However, this is not actually valid HTML. There needs to be a [`tbody`](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/tbody) (or `thead` or `tfoot`) element wrapping `tr` elements – you can't nest them directly under `table`.

Nevertheless, web browsers render this invalid HTML just fine, by silently inserting a `tbody` element to wrap all `tr` elements. This is a problem because the web browser will not notify Laminar about this, so Laminar's DOM tree will become incorrect.

Therefore, if you're rendering a table you must make sure to wrap your `tr` elements in `tbody` (or `thead` or `tfoot`) elements.



## Fin

Hey, you've read all of this? Amazing. Join us in [gitter](https://gitter.im/Laminar_/Lobby).
