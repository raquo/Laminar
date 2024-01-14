---
title: Documentation
---

* [Introduction](#introduction)
* [Dependencies](#dependencies)
* [Imports](#imports)
* [Rendering](#rendering)
  * [Waiting for the DOM to load](#waiting-for-the-dom-to-load)
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
  * [Text and Text-like values](#text-and-text-like-values)
  * [Rendering Custom Components](#rendering-custom-components)
  * [Lists of Children](#lists-of-children)
    * [children <-- observableOfElements](#children----observableofelements)
    * [Performant Children Rendering – split](#performant-children-rendering--split)
    * [Splitting Without a Key](#splitting-without-a-key)
    * [Performant Children Rendering – children.command](#performant-children-rendering--childrencommand)
  * [Binding Observables](#binding-observables)
  * [Other Binders](#other-binders)
* [Conditional Rendering](#conditional-rendering)
* [ClassName and Other Special Keys](#classname-and-other-special-keys)
  * [cls](#cls)
  * [Other Composite Keys](#other-composite-keys)
* [Event System: Emitters, Processors, Buses](#event-system-emitters-processors-buses)
  * [Registering a DOM Event Listener](#registering-a-dom-event-listener)
  * [EventBus](#eventbus)
  * [Arrow Methods Syntax Sugar](#arrow-methods-syntax-sugar)
  * [`:=> Unit` Sinks](#-unit-sinks)
  * [Transforming Observers](#transforming-observers)
  * [Event Processors](#event-processors)
    * [preventDefault & stopPropagation](#preventdefault--stoppropagation)
    * [useCapture](#usecapture)
    * [Obtaining Typed Event Target](#obtaining-typed-event-target)
  * [element.events](#elementevents)
  * [Compose and flatMap Events](#compose-and-flatmap-events)
  * [Multiple Event Listeners](#multiple-event-listeners)
  * [Window & Document Events](#window--document-events)
* [Controlled Inputs](#controlled-inputs)
* [SVG](#svg)
  * [SVG Syntax and Imports](#svg-syntax-and-imports)
  * [Rendering External SVGs](#rendering-external-svgs)
* [CSS](#css)
  * [CSS Keyword Helpers](#css-keyword-helpers)
  * [Unit and Function Helpers](#unit-and-function-helpers)
  * [Vendor Prefixes](#vendor-prefixes)
  * [Approaches To CSS](#approaches-to-css)
* [Ownership](#ownership)
  * [Laminar's Use of Airstream Ownership](#laminars-use-of-airstream-ownership)
* [Memory Management](#memory-management)
* [Element Lifecycle Hooks](#element-lifecycle-hooks)
  * [What Is Mounting?](#what-is-mounting)
  * [onMountCallback](#onmountcallback)
  * [onUnmountCallback & onMountUnmountCallback](#onunmountcallback--onmountunmountcallback)
  * [onMountBind](#onmountbind)
  * [onMountInsert](#onmountinsert)
  * [Why Use Lifecycle Hooks?](#why-use-lifecycle-hooks)
  * [Lifecycle Event Timing](#lifecycle-event-timing)
  * [How Are Mount Events Propagated?](#how-are-mount-events-propagated)
* [Integrations With Other Libraries](#integrations-with-other-libraries)
* [Network Requests](#network-requests)
* [URL Routing](#url-routing)
* [Anti-patterns](#anti-patterns)
* [Browser Compatibility](#browser-compatibility)
* [Special Cases](#special-cases)
* [Troubleshooting](#troubleshooting)





## Introduction

This documentation is for Laminar version **v16.0.0**. For other versions, see below.

| Laminar                                                                            | Airstream                                                                |
|:-----------------------------------------------------------------------------------|:-------------------------------------------------------------------------|
| **[v16.0.0](https://github.com/raquo/Laminar/blob/v16.0.0/docs/Documentation.md)** | **[v16.0.0](https://github.com/raquo/Airstream/blob/v16.0.0/README.md)** |
| **[v15.0.0](https://github.com/raquo/Laminar/blob/v15.0.0/docs/Documentation.md)** | **[v15.0.0](https://github.com/raquo/Airstream/blob/v15.0.0/README.md)** |
| **[v0.14.2](https://github.com/raquo/Laminar/blob/v0.14.2/docs/Documentation.md)** | **[v0.14.2](https://github.com/raquo/Airstream/blob/v0.14.2/README.md)** |
| **[v0.13.1](https://github.com/raquo/Laminar/blob/v0.13.1/docs/Documentation.md)** | **[v0.13.0](https://github.com/raquo/Airstream/blob/v0.13.0/README.md)** |
| **[v0.12.2](https://github.com/raquo/Laminar/blob/v0.12.2/docs/Documentation.md)** | **[v0.12.2](https://github.com/raquo/Airstream/blob/v0.12.2/README.md)** |


For documentation of older versions, see git tags.

[Laminar API doc](https://javadoc.io/doc/com.raquo/laminar_sjs1_3/latest/com/raquo/laminar/index.html) • [Airstream API doc](https://javadoc.io/doc/com.raquo/airstream_sjs1_3/latest/com/raquo/airstream/index.html)

Laminar is very simple under the hood. Don't be afraid to use "Go to definition" functionality of your IDE to see how a certain method works. That said, the documentation provided here explains the mechanics of Laminar in great detail. Documentation sections progress from basic to advanced, so each next section usually assumes that you've read all previous sections.

If you're new here, watching [the Laminar video](https://www.youtube.com/watch?v=L_AHCkl6L-Q) will be time well spent – it's a good introduction to Laminar, covering both the big ideas and some inner workings.

See also: [Quick start](https://laminar.dev/quick-start), [Live examples](https://laminar.dev/examples/hello-world).

If you want to follow along with an IDE, download one of the starter kit projects from the [Resources](https://laminar.dev/resources) page, or learn how to render your app in the [Rendering](#rendering) section below.



## Dependencies

Add Laminar to `libraryDependencies` of your Scala.js project in `build.sbt`:

    "com.raquo" %%% "laminar" % "16.0.0"  // Requires Scala.js 1.13.2+

Laminar depends on Airstream. Every Laminar version includes the latest version of Airstream that was available at the time it was published. If you ever have a reason to use a slightly newer version of Airstream without upgrading Laminar, add this to your `build.sbt` as well:

    "com.raquo" %%% "airstream" % "<version>"

As you can see, Laminar and Airstream versions can diverge slightly, so don't use a single `LaminarVersion` variable for both.

The html/svg tags, attributes, props, styles, and event names in Laminar come from [Scala DOM Types](https://github.com/raquo/scala-dom-types). If there's a missing prop, consider contributing it there.

Laminar also uses [scala-js-dom](http://scala-js.github.io/scala-js-dom/). As it is a very thin interface to native JS types, you can generally use a higher version of scala-js-dom than what Laminar uses without any issue.



## Imports

You have two import choices: `import com.raquo.laminar.api.L.{*, given}` (or `L._` in Scala 2) is the easiest, it brings everything you need from Laminar in scope. Unless indicated otherwise, this import is assumed for all code snippets in this documentation.

Usually you will not need any other Laminar imports. In this documentation you will occasionally see references to some Laminar types and values that are not available with just this one import because we spell out the types for the sake of explanation. Most of those are available as aliases in the `L` object. For example, `ReactiveHtmlElement[dom.html.Element]` is aliased as simply `HtmlElement`, and `Modifier[El]` as `Mod[El]`.

Alternatively, you might want to avoid bringing so many values into scope, so instead you can `import com.raquo.laminar.api.{*, given}` (or `api._` in Scala 2), and access Laminar and Airstream values and types with `L` and `A` prefixes respectively, e.g. `A.EventStream`, `L.div`, etc.`

There are special import considerations for working with [SVG elements](#svg).

Do check out the available aliases in the `L` object. It's much more pleasant to write and read `Mod[Input]` than `Modifier[ReactiveHtmlElement[dom.html.Input]]`.

Another import you will want in some cases is `import org.scalajs.dom` – whenever you see `dom.X` in documentation, this import is assumed. This object contains [scala-js-dom](https://github.com/scala-js/scala-js-dom) native JS DOM types. I highly recommend that you import this `dom` object and not `dom.*` or `dom._` because the `dom.` prefix will help you distinguish native JS platform types from Laminar types.



## Rendering

When you create a Laminar element, the underlying real JS DOM element (`.ref`) is created at the same time. However, it is initially detached from the DOM. That is, it does not appear in the document that the user sees. Such an element is _unmounted_.

For the user to see this element we need to _mount_ it into the DOM by either adding it as a Modifier to another element that already is (or at some point will become) _mounted_, or if we're dealing with the top element in our application's hierarchy, we ask Laminar to _render_ it into some container `dom.Element` that already exists on the page. Said container must not be managed by Laminar.

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

To remove `appElement` from the DOM, simply call `root.unmount()`. You can later call `root.mount()` to bring it back.


### Waiting for the DOM to load

When and where should you call Laminar's `render` method? Assuming you want your entire application to be powered by Laminar, you want to render your application as soon as the web page containing it loads.

However, what does "loads" mean, exactly? If you just put your code in your Scala.js app's `main` method, it will execute right after the `<script>` tag containing your Scala.js bundle was downloaded. At this point, whether accessing `dom.document.querySelector("#appContainer")` will work or not depends on the position of the `<script>` tag relative to `<div id="appContainer">` in your HTML file. If the container div comes first in your HTML document, then your script will be able to access it. Usually this is all that is needed to render your application, so my recommendation is to place your Scala.js script tag at just before the closing `</body>` tag in your HTML, and place any other elements / scripts / stylesheets / resources that you need at app launch above it. Do not place the `<script>` element inside of `<div id="appContainer">`.

Alternatively, you may want to delay rendering until the browser's [DOMContentLoaded](https://developer.mozilla.org/en-US/docs/Web/API/Window/DOMContentLoaded_event) event fires. You can do that with a special `renderOnDomContentLoaded` helper:

```scala
object App {
  def main(args: Array[String]): Unit = {
    lazy val appContainer = dom.document.querySelector("#appContainer")
    val appElement = div(h1("Hello world"))
    renderOnDomContentLoaded(appContainer, appElement)
  }
}
```

Important: if you're relying on `renderOnDomContentLoaded` to delay the rendering, make sure that `appContainer` is a `lazy val` or a `def`, because if that querySelector is executed before the browser parses the div in your DOM, it will return `null`, and Laminar will complain about the null container. Of course, another reason you could be getting a `null` is if the querySelector you provided is not correct. You can manually test it in the browser dev console.

Lastly, if you want to delay app rendering until not only the DOM, but all images and iframes from the HTML have also loaded, you can wait for the [window.load](https://developer.mozilla.org/en-US/docs/Web/API/Window/load_event) event. There is no built-in helper method for that, so you will need to do it more manually:

```scala
object App {
  def main(args: Array[String]): Unit = {
    windowEvents(_.onLoad).foreach { _ =>
      val appContainer = dom.document.querySelector("#appContainer")
      val appElement = div(h1("Hello world"))
      render(appContainer, appElement)
    }(unsafeWindowOwner)
  }
}
```



## Tags & Elements

Laminar uses [Scala DOM Types](https://github.com/raquo/scala-dom-types) to generate listings of typed HTML & SVG tags, attributes, props, event props, etc. For example, this is how we know that `onClick` events produce `dom.MouseEvent` events and not `dom.KeyboardEvent`.

`div` is an `HtmlTag[dom.html.Div]`. It's a factory of `<div>` HTML elements. `HtmlTag` extends `Tag` from _Scala DOM Types_ and contains basic information needed to create such an element, such as its tag name ("div").

`div()` is a `ReactiveHtmlElement[dom.html.Div]` (aliased as `Div`). The `apply` method created a Laminar Div element that is linked to the real JS DOM element that it represents. If you need to, you can access the underlying JS DOM element via the `.ref` property. This is useful mostly for event handlers and third party integrations.

This particular `Div` type has no special features, it simply indicates that the tag name is `div`. If you don't care about the type of your HTML element, you can use the more general `HtmlElement` type (alias for `ReactiveHtmlElement[dom.html.Element]`) in your method signatures.

`div()` is **not** a virtual DOM element. When you create a Laminar element, Laminar immediately creates the underlying `dom.Element` as well. That reference is immutable, so these two instances will go together for the duration of their lifetimes. In contrast, in a virtual DOM library (which Laminar is not) you typically create new instances of virtual elements when they change, and these get loosely matched to a `dom.Element` which could actually be a different element over time depending on how the updates that you're requesting and the implementation of virtual DOM's diffing algorithm.  

Read on for how to use this element we've created.



## Modifiers

The `div()` call described above creates an empty div element – no children, no attributes, nothing. Here's how to specify desired attributes:

```scala
input(typ := "checkbox", defaultChecked := true)
```

This creates an `<input>` Laminar element and sets two attributes on it. It's mostly obvious what it does, but how?

As we've established before, `input()` call is actually `HtmlTag[El].apply(modifiers: Modifier[El]*)`. So, we can pass Modifiers to it. A `Modifier[El]` (aliased as `Mod[El]`) is a simple trait with an `apply(element: El): Unit` method. Conceptually, it's a function that you can apply to an element `El` to modify it in some way. In our case, `typ := "checkbox"` is a `Modifier` that sets the `type` attribute on an element to `"checkbox"`.

`typ` is an `HtmlAttr`, and `:=` is simply a method on it that creates a `Setter` – a `Modifier` that sets a specific key to a specific value. You can set DOM props and CSS style props the same way (e.g. `backgroundColor := "red"`)

`typ` is originally coming from _Scala DOM Types_ and represents the "type" attribute. You should consult _Scala DOM Types_ documentation for a list of [naming differences](https://github.com/raquo/scala-dom-types#naming-differences-compared-to-native-html--dom) relative to the native JS DOM API. It will also explain why the `checked` attribute is called `defaultChecked`, and why it accepts a boolean even though all HTML attributes only ever deal in strings in JS DOM ([Codecs](https://github.com/raquo/scala-dom-types#codecs)).

Note: the `apply` method on attributes and other keys is an alias for `:=` in case you prefer a slightly shorter syntax and less pain in your fingers:

```scala
input(typ("checkbox"), defaultChecked(true)) // 100% equivalent to the previous snippet
```

The documentation will mostly use the `:=` name because I think it's more obvious to beginners.



### Nesting and Children

A Laminar Element like `input(typ := "checkbox")` is also a Modifier, one that **appends** itself as a child to the element to which it is being applied. Now you can understand how nesting works in Laminar without any magic:

```scala
val inputCaption = span("First & last name:")
val inputMods = Seq(typ := "text", defaultValue := "Me")  
 
div(
  h1("Hello world", color := "red"),
  inputCaption,
  input(inputMods, nameAttr := "fullName"),
  div(
    ">>",
    button("Submit"),
    "<<"
  )
)
```

Well, a tiny bit of magic – strings are not modifiers themselves, but are implicitly converted to `TextNode` (Laminar text nodes) which _are_ modifiers that append plain JS text nodes with a given text to the element in question. They are **not** implicitly wrapped into `span` elements.

In fact, this conversion works not only for strings, but also for other primitive types like numbers and booleans. If you want Laminar to render your custom type `A` in a similar manner, define an implicit instance of `RenderableText[A]` for it.

For convenience, `Seq[Modifier[A]]` is also implicitly converted to a `Modifier[A]` that applies all the modifiers in the Seq: see `Implicits.seqToModifier`, `arrayToModifier`, etc. This is especially useful when making a component that accepts modifiers as VarArgs. Such design is one way to let users configure the component's elements without the component needing to whitelist all possible configurations in its list of inputs:

```scala
// Simple component
def TextInput(inputMods: Modifier[Input]*): HtmlElement =
  div(
    cls := "TextInput",
    input(typ := "text", inputMods)
  )

// Usage (data binding with arrows will be explained later)
div(
  TextInput(defaultValue := "Enter your age", value <-- ageVar, onInput --> ageVar)
)
```

`Implicits.optionToModifier` and `nodeOptionToModifier` perform similar conversions for `Option[Modifier[A]]`. We also have `emptyMod`, a universal Modifier that does nothing, for when you don't want to wrap your stuff in Option-s.

Modifiers are applied in the order in which they're passed to the element, so in terms of children ordering what you see in your code is what you get in the resulting DOM.

Notice that the code snippet in the beginning of this section does not require you to HTML-escape `"&"` or `"<"`. You're just writing Scala code, you're not writing HTML (or anything that will be parsed as HTML), so don't worry about HTML escaping when using Laminar API. **However**, this warranty is void when you manually invoke native Javascript APIs (e.g. anything under `.ref`, or any interfaces provided by [scala-js-dom](https://github.com/scala-js/scala-js-dom)). Then you need to know Javascript and the DOM API to know what's safe and what [isn't](https://security.stackexchange.com/questions/32347/dom-based-xss-attacks-what-is-the-most-dangerous-example).

---

Note: this section of documentation only explained how to render **static** data. You will learn to render dynamic data including dynamic lists of children in [Reactive Data](#reactive-data) section. 


### HTML Entities

Laminar does not attempt to parse your strings as HTML, so there is no need to use [HTML entities](https://developer.mozilla.org/en-US/docs/Glossary/Entity) like `"&nbsp;"` or `"&amp;"`. Instead, you just insert the actual character into your Scala strings.

Laminar actually provides a shorthand for non-breaking space – `nbsp` – because it would otherwise be visually indistinguishable from regular space in your code. For any other special characters you can just use them verbatim, e.g. `"»"` instead of  or `"&raquo;"`.

```scala
div(s"${date}${nbsp}${time}")
div(b("Posts"), "»", categoryName) // `»` instead of `&raquo;`
div("Features — not bugs") // `—` instead of `&emdash;`
```


### Empty Nodes

Thanks to the `nodeOptionToModifier` implicit mentioned above, you can use `Option[ReactiveElement]` where a `Modifier` is expected, but in other cases you might not want to deal with Option's boxing.  

And so, an empty DOM node can be created with... `emptyNode`.

```scala
val node: Node = if (foo) element else emptyNode
```

`emptyNode` outputs an empty HTML comment node, which is a `CommentNode`, not a `ReactiveElement`. Their common type is `ChildNode[dom.Node]`, aliased as simply `Node`. This type hierarchy mirrors the JS world where `dom.Comment` nodes and `dom.Text` nodes are not `dom.Element`-s, but all of them are `dom.Node`-s.


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
  TextInput().amend(onInput --> nameVar)
)
```

`amendThis` is another variation of `amend` that provides access to `thisNode` similar to `inContext`, for the sake of reducing boilerplate:

```scala
val nameVar = Var("")
def TextInput(): Input = input(typ := "text")

div(
  // Traditional style
  TextInput().amend { inContext { thisNode =>
    onInput.mapTo(thisNode.ref.value) --> nameVar
  }}
  // Use amendThis to reduce inContext boilerplate
  TextInput().amendThis { thisNode =>
    onInput.mapTo(thisNode.ref.value) --> nameVar  
  }
  // But for `ref.value` specifically, just do this:
  TextInput().amend(
    onInput.mapToValue --> nameVar
  )
)

div(
  // List[Mod[El]] is implicitly converted to Mod[El]
  // so you can specify several modifiers like this:
  TextInput().amendThis { thisNode => List(
    onInput.mapTo(thisNode.ref.value) --> nameVar, // Note: `mapToValue` is easier, does not require thisNode
    thisNode.events(onClick).delay(0) --> clickObserver  
  )}
)
```

And of course, every Modifier has a public `apply(element: El)` method that you can use to manually apply it to any element, for example: `(color := "red")(element)`.

Using `amend` judiciously is the backbone of good Laminar component patterns, but make sure that you don't over-rely on manual applications of modifiers as this can result in a messy imperative codebase. Learn to use [Reactive Data](#reactive-data) effectively.


### inContext

Sometimes you need to have a reference to the element before you can decide which modifier to apply to it. For example, one use case is getting dimensions or coordinates in response to some event.

```scala
div(inContext(thisNode => onClick.mapTo(getCoordinates(thisNode)) --> bus))
```

The general syntax of this feature is `inContext(El => Modifier[El]): Modifier[El]`. `thisNode` refers to the element to which this modifier will be applied. In our example its type is properly inferred to be `Input`, which is why you can call `.ref.value` on it without `.asInstanceOf`.

Without getting ahead of ourselves too much, `onClick.mapTo(getCoordinates(thisNode)) --> bus)` is a Modifier that handles `onClick` events on an element, but as you see it needs a reference to that element. You could get that reference in a couple _other_ ways, for example:

```scala
// Make sure to write out the type ascription
val thisNode: Div = div(onClick.mapTo(getCoordinates(thisNode)) --> bus)
```

Or

```scala
val thisNode = div()
thisNode.amend(
  thisNode.events(onClick).mapTo(getCoordinates(thisNode)) --> bus
)
```

But these are of course rather cumbersome. `inContext` provides an easy, inline solution that always works.

Note: all the unfamiliar syntax used here will be explained in subsequent sections of this documentation. 


#### When not to use `inContext`

`inContext` is rarely needed in modern Laminar. 

You don't need to use `inContext` to get an input element's current value: use the `mapToValue` processor, e.g. `input(onInput.mapToValue --> stringBus)`.

Similarly, don't use `inContext` to get a checkbox's `checked` status, use the `mapToChecked`, e.g. `input(typ := "checkbox", onClick.mapToValue --> stringBus)`.

Under the hood, `mapToValue` and `mapToChecked` look at `event.target.value` and `event.target.checked`.

You also don't need `inContext` to apply stream operators to events. In older versions of Laminar if you wanted to delay an event, you would need to do this:

```scala
div(inContext { _.events(onClick).delay(10) --> observer })
```

or use a weirdly curried `composeEvents` method:

```scala
div(composeEvents(onClick)(_.delay(10)) --> observer)
```

Nowadays we have a more straightforward `compose` method that lets you use stream operators with DOM events:

```scala
div(onClick.compose(_.delay(10)) --> observer)
```

You can also use `delay` on the observer side:

```scala
div(onClick --> observer.delay(10))
```


### Reusing Elements

**Do not.** An element can only have one parent element at a time, so you can't put the same element into two different locations in the DOM at the same time – instead of being "copied" to the new location, the element will be **removed** from its old location and **moved** into its new location. If you feel the need to "copy" an element like this, you probably just want to make the element a `def` instead of a `val`, so that accessing it the second time would create a new element with the same props / attrs / etc. instead of resuing the original instance which is already used elsewhere. 

For example, this contrieved code will not render "hello" twice. It will put the `span` inside the outer div, then immediately move it to the inner div, so "hello" will only be rendered once, inside the inner div.

```scala
val el = span("hello")
div(el, div(el))
```

If that is what you wanted, that's fine, Laminar will not break, it's just weird. However, you probably intended to render "hello" twice, in both the outer and inner div, and for that you want to make `el` a `def` instead of a `val`, so that when it's used the second time, a new element is created instead of reusing the first instance of `el`.


### Missing Keys

Laminar gets the definition of HTML and SVG DOM tags, attributes, properties, events and CSS styles from [Scala DOM Types](https://github.com/raquo/scala-dom-types). These definitions provide hundreds of keys, but they are not exhaustive. For example, we don't have touch events like `touchmove` defined, so those are not available to be put on the left hand side of `:=` methods.

To work around this, you can contribute definitions of missing attrs / props / etc. to Scala DOM Types. It's easy – you don't even need to code any logic, just need to specify the names and types of the things you want to add. For example, the `value` property is defined at the bottom of [PropDefs.scala](https://github.com/raquo/scala-dom-types/blob/master/shared/src/main/scala/com/raquo/domtypes/defs/props/PropDefs.scala). Read [this](https://github.com/raquo/scala-dom-types/#reflected-attributes) to understand the difference between props and attributes and reflected attributes.

Alternatively, you can define the keys locally in your project in the same manner as Laminar does it, for example:

```scala
val superValue: HtmlProp[String] = htmlProp("superValue", StringAsIsCodec) // imaginary prop
val onTouchMove: EventProp[dom.TouchEvent] = eventProp("touchmove")

div(
  superValue := "imaginary prop example",
  onTouchMove --> eventBus
)
```

And similarly with `styleProp`, `htmlAttr`, `htmlTag`, `svgAttr`, `svgTag`.

To clarify, you don't have to do this for touch events specifically, because [@Busti](https://github.com/busti) already added the superior [pointer-events](https://github.com/raquo/scala-dom-types/pull/49) to address this particular shortcoming. Unless you want touch events regardless of that, in which case, you're welcome to it.


### Modifiers FAQ

1. Yes, you can create custom Modifiers by simply extending the Modifier trait. However, if your modifier creates **multiple** subscriptions / binds **multiple** observables, you should look at the implementation of `Modifier.apply`, and if applicable, wrap the part of your code that creates several subscriptions at once in `Transaction.onStart.shared`.

2. No, Modifiers are not guaranteed to be idempotent. Applying the same Modifier to the same element multiple times might have different results compared to applying it only once. `Setter`s like `key := value` _are_ idempotent, but for example a `Binder` like `onClick --> observer` isn't – applying it twice will create two independent subscriptions. We will talk more about those modifier types later.

4. Yes, Modifiers are generally reusable. The same modifier can usually be applied to multiple nodes without any conflict. But again, you have to understand what the modifier does. Setting an attribute to a particular value – sure, reusable. Adding a particular element as a child – no, not reusable – see [Reusing Elements](#reusing-elements). If you're writing your own Modifier and want to make it reusable, don't store element-specific state outside of its `apply` method.



## Reactive Data

In the code snippets above we're mounting a completely static element – it will not change over time. This section is about creating elements that will one way or another change over time. All of the above were just the basic mechanics, here we're digging into the meat of Laminar, the fun parts.

In a virtual DOM library like React or Snabbdom, changing an element over time is achieved by creating a new **virtual** element that contains the information on what the element should look like, then running a **diffing algorithm** on this new virtual element and the previous virtual element representing the same real JS DOM element. Said diffing algorithm will come up with a **list of operations** that need to be performed on the underlying JS DOM element in order to bring it to the state prescribed by the new virtual element you've created.

Laminar does **not** work like that. We use reactive observables to represent things changing over time. Let's see how this is much simpler than any virtual DOM.

Laminar uses [Airstream](https://github.com/raquo/Airstream) observables (event streams and signals). Airstream is an integral part of Laminar, and a huge part of what makes it unique.

**To use Laminar effectively, you need to understand the principles of conventional lazy streams, and have basic knowledge of Airstream. Please do read [Airstream documentation](https://github.com/raquo/Airstream), it will save you a lot of guesswork.**


### Attributes and Properties

Dynamic attributes / properties / css properties are the simplest use case for Laminar's reactive layer:

```scala
val prettyColorStream: EventStream[String] = ???
val myDiv: Div = div(color <-- prettyColorStream, "Hello")
```

This creates a div element with the word "Hello" in it, and a dynamically updated `color` CSS property. Any time the `prettyColorStream` emits an event, this element's `color` property will be updated to the emitted value. 

The method `<--` is defined on `class StyleProp`, which is the type of `color`. It creates a `Binder` modifier that subscribes to the given stream. This subscription is automatically enabled when the div element is added into the DOM, and automatically disabled when the div is removed from the DOM. More on that in the sections [Ownership](#ownership) and [Element Lifecycle Hooks](#element-lifecycle-hooks) way below. Don't worry about that for now.

The important takeaway here is that `color <-- prettyColorStream` is a Modifier, and so simply writing out that expression does not activate any logic or create any subscription. For the modifier to do its job, you need to pass `color <-- prettyColorStream` to the element on which you want to update the `color` property, like we pass it to `div` in the code snippet above.

To make sure you don't accidentally leave modifiers unused, you might want to enable the Scala compiler's options to warn you about unused and discarded values.


### Event Streams and Signals

What happens before `prettyColorStream` emits its first event? To understand that, we need a primer on Airstream reactive variables:

- **EventStream** is a lazy Observable without a concept of "current value". It represents events that happen at discrete points in time. Philosophically, there is no such thing as a "current event" or "initial event", so streams have no initial or "current" value.

- **Signal** are also lazy Observables, but unlike streams they do have a current value. Signal represents state – a value persisted over time. It always has a current value, and therefore it must have an initial value.

The `<--` method subscribes to the given observable and updates the DOM whenever the observable emits. `prettyColorStream` is an EventStream, so it will only emit if and when the next event comes in. So, until that time, Laminar will not set the color property to any initial or default value.

On the other hand, if `prettyColorStream` was a Signal, the subscription inside the `<--` method would have set the color property to the observable's current value as soon as the element was added to the DOM, and would behave similarly to a stream subscription afterwards.

We said that EventStreams and Signals are lazy – that means they wouldn't run without an Observer. By creating a subscription, the `<--` method creates an Observer of the observable that it is given, so we know for sure that it will run (while the element is mounted into the DOM).


### Individual Children

One very important takeaway from the `val myDiv: Div = div(color <-- prettyColorStream, "Hello")` example is that **`myDiv` is an element, not a stream of elements, even though it depends on a stream**. Think about it: you are not creating new elements when `prettyColorStream` emits an event, you are merely setting a style property to a new value on an existing element. This is very efficient and is in stark contrast to the amount of work a virtual DOM needs to do in order to perform a simple change like this.

So, **Laminar elements manage themselves**. A `ReactiveElement` encapsulates all the reactive updates that are happening inside. This means that you can add an element as a child without knowing how it's implemented or whether it's even static or dynamic. So a simple component could look like this:

```scala
// Define your "Component"
def Caption(caption: String, colorStream: EventStream[String]): HtmlElement = {
  span(color <-- colorStream, caption)
}
 
// Now use it
val prettyColorStream: EventStream[String] = ???
val myDiv: HtmlElement = div(
  "Hello ",
  Caption("World", prettyColorStream)
)
```

This lets you build loosely coupled applications very easily.

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
val app: HtmlElement = div(
  "Hello, I have ",
  child <-- MaybeBlogUrl(maybeBlogUrlSignal),
  ", isn't it great?"
)
```

It should be pretty obvious what this does: `MaybeBlogUrl` maps input Signal to a Signal of Laminar elements, and `child <-- MaybeBlogUrl(maybeBlogUrlSignal)` creates a subscription that puts these elements into the div.

The elements are put in the obvious order – what you see is what you get – so, between _"Hello, I have "_ and _", isn't it great?"_ text nodes. Each next emitted element replaces the previously emitted one in the DOM.

This example uses Signals, but EventStreams work similarly.


#### Efficiency

You might notice that this is not as efficient as it could be. Any time `maybeBlogUrlSignal` emits a new value, a new element is created, even if we're moving from `Some(url1)` to `Some(url2)`. And those are real JS DOM elements, so **if** this method is hit often we might want to optimize for that. For example, we could do this:

```scala
def renderBlogLink(urlSignal: Signal[String]): HtmlElement = {
  a(href <-- urlSignal, "a blog")
}
 
def MaybeBlogUrl(maybeUrlSignal: Signal[Option[String]]): Signal[HtmlElement] = {
  lazy val noBlog = i("no blog")
  
  val maybeLinkSignal: Signal[Option[HtmlElement]] =
    maybeUrlSignal.splitOption((_, urlSignal) => renderBlogLink(urlSignal))
  
  maybeLinkSignal.map(_.getOrElse(noBlog))
}
```

The main hero of this pattern is Airstream's `split` operator, actually its `splitOption` variation to be more precise. It's designed specifically to avoid re-rendering of elements in this kind of situation. We will explain its operation in detail when talking about performant `children` rendering below, but in short, thanks to `splitOption`, `renderBlogLink` is called only when maybeUrlSignal switches from `None` to `Some(url)`. When it goes from `Some(url1)` to `Some(url2)`, `renderBlogLink` is **not** called, instead, `urlSignal` is updated, and `href <-- urlSignal` updates the blog url inside the already-created `a` element.

Also note that we only create one `noBlog` element, and reuse it every time maybeUrlSignal emits `None`, instead of creating a new such element every time.

---

You don't actually need to unmount elements in order to hide them from view. You can achieve a similar effect by rendering both the link and no-link elements at the same time inside a container, and hiding one of them with CSS (`display <-- ...`). That way both elements will remain mounted all the time, but one of them will be hidden.

When engaging in such optimizations, you have to ask yourself – is it worth it? Re-creating a JS DOM element – what we were trying to avoid in this example – is only a problem when you need to do it often (performance), or when that element needs to maintain some JS DOM state (e.g. `input` elements will lose focus if re-created). To put this in the context of our example – if `maybeUrlSignal` is expected to emit only a few times during a user session, just use the dumbest shortest version of `MaybeBlogUrl` instead of optimizing it for no reason. Less code – less bugs. But if you're building some kind of BlogRoulette app that updates the blog URL a hundred times per second, or if the element being re-created is very complex (e.g. it's your entire app or a big section thereof) then yeah, better optimize it.


#### Optional children

If you have an `Observable[Option[Node]]` instead of `Observable[Node]`, and you don't want to render anything when the observable emits `None`, you can use `child.maybe` in a similar manner:

```scala
val maybeBlogLink: Signal[Option[HtmlElement]] = ???
div(
  child.maybe <-- maybeBlogLink
)
```


### Text and text-like values

If you want to render dynamic text, do **not** do this:

```scala
val textStream: EventStream[String] = ???
div(child <-- textStream.map(str => span(str)) // No! Bad!
```

Technically that would work, but you are needlessly creating a new `span` element every time you want to update the text. Instead, use `child.text`:

```scala
val textStream: EventStream[String] = ???
div(child.text <-- textStream)
```

This is the most efficient way to render dynamic text in Laminar, and you should prefer it every time to using `child <--` whenever possible.

`child.text` works not only with strings, but also other primitive types like numbers and booleans. Laminar can render any `A` as a string as long as there is an implicit instance of `RenderableText[A]` for it. You can define such instances for your custom types, or you can define such instances for built-in types like `Int` to render them differenly from the default, e.g. by adding formatting.

```scala
implicit val intRenderable0000: RenderableText[Int] = RenderableText("%04d".format(_))

class Foo { ... }
implicit val fooRenderable: RenderableText[Foo] = RenderableText(fooToString)

div(
  // will render as "true" or "false" using built-in bool renderable implicit
  true,
  child.text <-- streamOfBooleans,
  
  // will render numbers with leading zeroes (e.g. "0001") because of `intRenderable0000`
  1,
  child.text <-- streamOfInts
  
  // will render Foo-s to string using `fooRenderable`
  new Foo {},
  child.text <-- streamOfFoos
)
```


### Rendering Custom Components

Laminar has no built-in notion of a component, but for the purposes of this section let's say it's some custom type `C` that exposes a Laminar element, e.g. `trait Component { val node: HtmlElement = ??? }`.

You could render such components in Laminar simply by calling `.node` on them, however if you are making heavy use of this pattern, it might get repetitive. Instead of defining an implicit conversion from `Component` to `HtmlElement`, you should define an implicit instance of `RenderableNode[Component]`, then you'll be able to use `Component` in any Laminar context that expects an element, i.e. you would be able to say `child <-- streamOfComponents` etc.

To create a `RenderableNode[Component]`, you need to provide a `Component => Node` function, in our case that would be `_.node`. As explained in RenderableNode's scaladoc, this function must return a constant. Basically your `node` should be either a `val` or a `lazy val`, it must not be a `var` or a `def`, otherwise you might run into unexpected behaviour or performance issues.



### Lists of Children

Rendering dynamic lists of children efficiently is one of the most challenging parts in UI libraries, and Laminar has a few solutions for that.


#### children <-- observableOfElements

```scala
val childrenSignal: Signal[immutable.Seq[Node]] = ??? // immutable only!
div("Hello, ", children <-- childrenSignal)
```

If you have an Observable of desired children elements, this is the most straightforward way to subscribe to it.

Internally, this `<--` method keeps track of prevChildren and nextChildren. It then reconciles the two lists, but it does not do heavy, recursive diffing that virtual DOM libraries do. Laminar only compares elements by reference equality, we do not diff any attributes / props / state / children, and we do not "re-render" anything like React does.

We make a single pass through nextChildren, and detect any inconsistencies by checking that `nextChildren(i).ref == childrenInDom(i)` (simplified). If this is false, we look at prevChildren to see what happened – whether the child was created, moved, or deleted. The algorithm is short (<50LoC total) but efficient, scaling up to thousands of elements – more than you can feasibly show to a user anyway.

Laminar will perform absolutely minimal operations that make sense – for example if a new element needs to be inserted, that is the only thing that will happen, _and_ this new element is the only element for which Laminar will spend time figuring out what to do, because all others will match. Reorderings are probably the worst case performance-wise, but still no worse than virtual DOM. You can read the full algorithm in `ChildrenInserter.updateChildren`.

**However, this algorithm has a catch** – since it's only doing reference equality checks on the elements, and since Laminar has no concept of **virtual** elements, it would be very inefficient with naive code like `children <-- modelsSignal.map(models => models.map(model => div(model.name))`, because such an implementation would create N new `div` elements (one for each model in the list) every time `modelsSignal` emits, regardless of how the list has changed. Of course, we have an easy solution to that – read on.

Note: a given DOM node / element can not exist in multiple locations in the DOM at the same time. Therefore, the Seq-s you provide to `children <--` must not contain several references to the same element. This is on you, as Laminar does not spend CPU cycles to verify this. Generally this isn't a problem unless you're deliberately caching elements for reuse in this manner. If you _are_ doing that, consider using the `split` operator explained below to automate that.



#### Performant Children Rendering – split

**Note: This mechanism is important to understand. If the explanation below is not clear, try the explanation in [the video](https://www.youtube.com/watch?v=L_AHCkl6L-Q&t=767s).**

We've now established how `children <-- childrenObservable` algorithm works, now let's see how to compile `childrenObservable` to get efficient updates. Basically, we just need to use Airstream's `split` method. But before we dive into that, let's see what the problem is without it.

Suppose you want to render a dynamic list of users. The set of users, their order, and the attributes of individual users can all change, except for user id, which uniquely identifies a given user. You also know how to render an individual user element:

```scala
case class User(id: String, name: String)
 
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

This will work, but it is inefficient. Every time `usersStream` emits a new list of users, Laminar re-creates DOM elements for every single user in the list, and then replaces all of the old elements with those new ones.

This is the kind of situation that virtual DOM was designed to avoid. While this pattern is perfectly fine for small lists that don't update often (and assuming you don't care about losing DOM element state such as text in an input box), with larger lists and frequent updates there is a lot of needless work being done.

So does this mean that virtual DOM is more efficient than Laminar when rendering large lists of dynamic children? Yes, if you render children **as described above**. This might be a surprising admission, but this default is necessary to uphold Laminar's core value: simplicity. There is no magic in Laminar. Laminar does what we tell it to do, and in this case we very explicitly tell it to create a list of elements every time the stream emits a new list of users, and then replace the old elements with brand-new ones.

So let's write smarter code. It's actually very easy.

All we need to do is adjust our `renderUser` function to take a different set of parameters:

```scala
def newRenderUser(userId: String, initialUser: User, userSignal: Signal[User]): HtmlElement = {
  div(
    p("user id: " + userId),
    p("name: ", child.text <-- userSignal.map(_.name))
  )
}
```

And with this, efficient rendering is as simple as:

```scala
val userElementsSignal: EventStream[List[HtmlElement]] =
  usersStream.split(_.id)(newRenderUser)
 
div(
  children <-- userElementsSignal
)
```

Airstream's `split` operator was designed specifically for this use case. On a high level, you need three things to use it:

* `usersStream` – a stream (or signal) of `immutable.Seq[User]`
* `_.id` – a way to uniquely identify users
* `newRenderUser` – a way to render a user into a single element given its unique key (`id`), the initial instance of that user (`initialUser`), and a signal of updates to that same user (`userSignal`).

To clarify, Airstream-provided `userSignal` is a safe and performant equivalent to `usersStream.map(_.find(_.id == userId).get)`. It provides you with updates of one specific user.

---

Here is how `split` works in more detail:

* As soon as a `User` with id="123" first appears in `usersStream`, we call `newRenderUser` to render it. This gives us a `div` element **that we will reuse** for all future versions of this user.
* We remember this `div` element. Whenever `usersStream` emits a list that includes a new version of the id="123" User, the `userSignal` in `newRenderUser` emits this new version.
* Similarly, when other users are updated in `usersStream` their updates are scoped to their own invocations of `newRenderUser`. The grouping happens by `key`, which in our case is the `id` of `User`.
* When the list emitted by `usersStream` no longer contains a User with id="123", we remove its `div` element from the output and forget that we ever made it.
* Thus `userElementsSignal` contains a list of `div` elements matching one-to-one to the users in the `usersStream`.

So, the `split` operator lets us build an `Observable[immutable.Seq[HtmlElement]]` without needlessly re-creating html elements. For any given `userId`, we will only create and render a single element, and any subsequent updates to this user will be channeled into its individual `userSignal`, so that the div element that we already created for this user can be efficiently *updated* (using `child.text` in this case), instead of being re-created every time.

This is exactly what Laminar's `children <-- observableOfElements` needs to work efficiently.

---

The list of models you're providing to the split operator must not have items with duplicate keys in it – each key in the list must be unique. By default, if Airstream detects duplicate IDs, it will print an error in the browser console listing the offending IDs, however with or without the warning, you code will fail – at the moment `split` does not tolerate such input. For performance-critical applications, you can disable these checks for a small gain, either wholesale or (preferably) on a case-by-case basis – see `DuplicateKeysCondig` in Airstream.


#### Splitting Without a Key

The `split` operator requires a unique key for each item, such as `_.id`. But what if you don't have such a key?

One option is to change your model to create an ephemeral key, that is generated on the frontend and never sent to the backend. Airstream does not need the key to be meaningful or consistent across user sessions – the key simply needs to identify a certain item in the dynamic list for as long as Laminar is rendering that list.

Another, simpler solution, is often quite workable – with `splitByIndex` you can use the index of the item in the list as its key. That way, when you append an item to the list, existing items will not be re-rendered, or if you update the data in some item(s), their elements won't be re-created. However, I don't recommend using this if you will be inserting new items in the beginning or middle of the list, or if you will be reordering the list of items – such cases really call for a proper key.


#### Splitting Options

If you are rendering an `Observable[Option[HtmlElement]]`, you might want to use `splitOption` as shown in the [Rendering Individual Children](#efficiency) section above. Now that we have a better understanding of how `split` works, it's easy to see how `splitOption` is simply using the Option's `.isDefined` as a key, creating a new element when switching from `None` to `Some(model)`, and retaining the same element when switching from `Some(model1)` to `Some(model2)`.


#### Splitting Individual Items

Now that you know how the `split` operator works, it's only a small leap to understand its special-cased cousin `splitOne`. Where `split` works on observables of collections like `List[Foo]`, `splitOne` works on observables of `Foo` itself, that is, on any observable:

```scala
case class Editor(text: Boolean, isMultiLine: Boolean)
   
def renderEditor(
  isMultiLine: Boolean,
  initialEditor: Editor,
  editorSignal: Signal[Editor]
): HtmlElement = {
  val tag = if (isMultiLine) textArea else input
  tag(value <-- editorSignal.map(_.text))
}
 
val inputSignal: Signal[Editor] = ???
 
val outputSignal: Signal[HtmlElement] = 
  inputSignal.split(key = _.isMultiLine)(project = renderEditor)
```

The high-level purpose of this code is to avoid creating a new element when `inputSignal` emits a new value. The interesting part is **when** we do need to create the new element: when the Editor's `isMultiline` property changes, we need to switch to using either a single-line `<input>` element, or a multi-line `textArea`. Changing the type of the element is one kind of update that is impossible to do without re-creating the element.

Another use case for this is when you want to reset a complex component's state, for example:

```scala
case class Document(id: String, content: DocumentContent, ...)
   
def editor(
  documentId: Boolean,
  initialDocument: Document,
  documentSignal: Signal[Document]
): HtmlElement = {
  val documentState = Var(initialDocument)
  ... // More complex setup here, with some internal state specific to a particular document
  div(
    someInput --> documentState,
    documentSignal.map(...) --> documentState,
    documentState --> ...
  )
}
 
val inputSignal: Signal[Document] = ???
 
val outputSignal: Signal[HtmlElement] = 
  inputSignal.split(key = _.id)(project = editor)
```

In this case we don't have a strict technical constraint like changing the element type that we need to work around. In principle, we don't absolutely need `splitOne`. However, imagine that this component is rendering an editor for a complex document, similar to e.g. Google Docs. This kind of component has a lot of internal state which is all tied to a specific document, to a specific document ID. When `inputSignal` emits an update to the current document (documentId is the same), we want `documentSignal` to update as usual, but when `inputSignal` emits a new document ID... we don't want to manually clear / reset all that complex `documentId`-specific state inside `editor` – with all the redundant state / caches / etc. in it, this could easily become prone to bugs. It would be much easier to simply discard the old editor component and create a new one for the new document. And this is exactly what the code above does. Less code – less bugs.

In other words, our `splitOne` code above guarantees that every instance of `documentSignal` will always have unchanging `document.id` matching `documentId`, and that the `editor` method will be called whenever we switch to rendering a new `documentId`.

This all might seem similar to what a `distinct` operator would do, and indeed there is some conceptual overlap, because `distinct` is a fundamental part of `split` semantics, but you will be hard pressed to implement this pattern with `distinct` instead of `splitOne`, at least if you have a single observable like `Signal[Document]` as your input.


#### Performant Children Rendering – children.command

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
  button(
    "Add a child",
    onClick.map { _ =>
      counter += 1
      CollectionCommand.Append(span(s"Child # $counter"))
    } --> commandBus
  )
)
```

We'll explain the `-->` method and the rest of event handling in later sections. What's important is that in this example we're sending `CollectionCommand.Append(span(s"Child # $counter"))` events to `commandBus` on every button click. 

The ChildrenCommand approach offers the best performance on Laminar side. Since you are directly telling Laminar what to do, no logic stands between your code and Laminar executing your requests. There is no diffing involved.

That said, don't go crazy trying to adapt your logic to it. Airstream's `split` operator described in the previous section offers great performance too. Choose the method that feels more natural for your needs, and micro-optimize later, if needed.

One downside of ChildrenCommand is that you don't have an observable of the list of children naturally available to you, so if you want to display e.g. the number of children somewhere, you will need to create an observable for that yourself. For example:

```scala
val commandBus = new EventBus[ChildrenCommand.Append[HtmlElement]]
val commandStream = commandBus.events
val countSignal = commandStream.scanLeft(initial = 0)(_ + 1)

div(
  "Hello, ",
  children.command <-- commandStream,
  button(
    "Add a child",
    onClick.map( _ => CollectionCommand.Append(span(s"Just another child"))) --> commandBus),
  div(
    "Number of children: ",
    child.text <-- countSignal.map(_.toString)    
  )
)
```

This would need more work if we wanted to support other actions such as removals, and you need to be careful to not overcomplicate things here, especially if you're only doing this for performance reasons. Make sure you actually have a performance problem first.

---

You can mix both `child <-- ...` and `children <-- ...` and `children.command <-- ...` and any other children or modifiers in any order inside one element, and they will happily coexist with no interference. Correct order of elements will be maintained even in edge cases such as showing zero elements. Laminar adds a sentinel HTML comment node into the DOM for each `child* <-- ...` receiver to make this possible. These nodes effectively "reserve the spot" for children provided by a particular receiver.

---

As of Laminar 15, you can generally safely move elements from one `child* <-- ...` block into another – to do that you would need to remove the child from the source observable and add it to the new observable. Although, you don't _really_ need to proactively remove the child from the old observable if the old observable will not emit this child again, Laminar is resilient to this kind of thing.


### Binding Observables

By now you must have realized that `-->` and `<--` methods are the main syntax to connect / bind / subscribe observables to observers in Laminar.

To be more precise, such methods always return a `Modifier` that needs to be applied to an element. And when it is applied, a dynamic subscription is created such that it activates when the element is mounted, and deactivates when the element gets unmounted. The types of such modifiers are `Binder` for those that deal with props / attributes / events / etc., and `Inserter` for those that add children to the element.

Inserters are special because they reserve a spot in the element's children where the children provided by the Inserter will be inserted. How does that work? When an Inserter is applied to an element, it appends a sentinel node to the element – just an empty, invisible `CommentNode`. When it's time to insert some children, the inserter either replaces the sentinel node with the next child noode (in case of `child.text <-- ...`), or inserts the new child(ren) right after the sentinel node (in case of `child <-- ...`, `child.maybe <-- ...`, and `children <-- ...`). That way your dynamic children will always appear where you expect them to, and you can mix and match any number of child inserters in a single parent element, and you can move children from one inserter to another without re-creating the element.

So far we've talked about Binders and Inserters provided by Laminar. But what if you want to subscribe an arbitrary Observable to an arbitrary Observer? First of all – why would you even need Laminar's help in this? The answer is [Ownership](#ownership) – in order to create an Airstream Subscription, you need an `Owner`, and in 99.9% cases you don't want to create those manually. You want to use owners provided by Laminar. In fact, what you really want is to avoid the ownership boilerplate altogether. When you sent click events to an observer using `onClick --> observer`, you didn't need to think about owners. That was nice.

It should come naturally that Laminar offers a similar way to bind any observable to any observer:

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

Now, what's your intuition for how long any of these subscriptions will live? Subscriptions can't just live forever, they are leaky resources and need to be shut down when they are no longer needed. Airstream offers a way to manage this automatically using `Owners`. But you don't see any `Owner`s here (there's nothing `implicit` either).

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



## Conditional Rendering

This section offers examples of rendering elements based on some condition, whether static or dynamic.

A few patterns of static conditionals:

```scala
val bool: Boolean = ???
val option: Option[String] = ???
div(
  "Hello",
  if (bool) b(" world") else emptyNode
)

div(
  "Hello ",
  if (bool) 
    b("world") :: Nil
  else
    div("foo") :: div("bar") :: Nil 
)

a(
  option.map(str => href := str), // conditionally apply a modifier 
  option.map(str => span(str))
)

div(
  option.map(str => span(str)).getOrElse("– nothing –")
)

val element = div(/*...*/)

if (bool) {
  // apply modifiers to existing element
  element.amend(
    rel := "foo",
    span("Append another child")
  )
}
```

For reactive data, you often need to compose observables to get conditional logic, e.g.:

```scala
val boolSignal: Signal[Boolean] = ???
val userStream: EventStream[User] = ???
val maybeElementSignal: Signal[Option[HtmlElement]] = ???

div(
  child <-- maybeElementSignal.map(_.getOrElse(span("no data")))
)

div(
  child.maybe <-- maybeElementSignal
)

{
  // Cache these to avoid re-creating them on repeated events
  lazy val trueDiv = div("true")
  lazy val falseDiv = div("false")

  div(
    child <-- boolSignal.map(if (_) trueDiv else falseDiv)
  )
}

{
  val maybeActiveUserSignal: Signal[Option[User]] =
    userStream
      .startWithNone
      .map(maybeUser => maybeUser.filter(_.isActive))
  
  val emptyElement = span("no active user")
  
  // using .splitOption instead of .map for efficiency
  div(
    child <-- maybeActiveUserSignal.splitOption(
      (initialActiveUser, activeUserSignal) => {
        div(
          idAttr := s"user-${initialActiveUser.id}",
          child.text <-- activeUserSignal.map(_.name)
        )
      },
      ifEmpty = emptyElement
    )
  )
}

{
  val maybeUserSignal: Signal[Option[User]] =
    userStream
      .startWithNone
      .combineWithFn(boolSignal, {
        case (Some(user), true) => Some(user)
        case _ => None
      })
  
  val emptyCommentNode = emptyNode 
  
  // using .splitOption instead of .map for efficiency
  div(
    child <-- maybeUserSignal.splitOption(
      (initialUser, userSignal) => {
        div(
          idAttr := s"user-${initialUser.id}",
          child.text <-- userSignal.map(_.name)
        )
      },
      ifEmpty = emptyCommentNode
    )
  )
}
```



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
val someBoolean: Boolean = ???

// Add multiple class names
cls := ("class1", "class2")
cls := List("class1", "class2")
 
// Remember that apply method is alias for :=. Below is the same as above
cls("class1", "class2")
cls(List("class1", "class2"))
 
// Add class name conditionally (true = add, false = do nothing)
cls := ("class1" -> true, "class2" -> false)
cls := Seq("class1" -> true, "class2" -> someBoolean)
cls := (Seq("class1" -> true, "class2" -> someBoolean), Seq("class3" -> someBoolean))
cls := Map("class1" -> true, "class2" -> false)
 
// Add class names conditionally (true = add, false = do nothing)
cls("class1") := true
cls("class1 class2") := someBoolean
cls("class1", "class2") := true
```

Of course, the reactive layer is similarly considerate in regard to `cls`. Consider this use case:

```scala
val classesStream: EventStream[Seq[String]] = ???
val isSelectedSignal: Signal[Boolean] = ???
 
div(
  cls := "MyComponent",
  cls <-- classesStream,
  cls("class1", "class2") <-- isSelectedSignal,
  cls <-- boolSignal.map { isSelected =>
    if (isSelected) "always x-selected" else "always"
  },
  cls <-- isSelectedSignal.map { isSelected =>
    List("always" -> true, "x-selected" -> isSelected)
  },
  cls <-- isSelectedSignal.map { isSelected =>
    Map("always" -> true, "x-selected" -> isSelected)
  } 
)
``` 

Once again, we don't want the CSS class names coming in from `classesStream` to override (remove) `MyComponent` class name. We want that one to stay forever, and for the class names coming from `classStream` to **override the previous values emitted by that stream**. And this is exactly how Laminar behaves. The Modifier `cls <-- classesStream` keeps track of the class names that it last added to the element's `cls` attribute. When a new event comes in, we remove those previously added class names and add new ones that are not already on the element.

So for example, when `classesStream` emits `List("class1", "class2")`, we will _add_ those classes to the element. When it subsequently emits `List("class1", "class3")`, we will remove `class2` and add `class3` to the element's class list.

The **`<--`** method can be called with Observables of `String`, `Seq[String]`, `Seq[(String, Boolean)]`, `Map[String, Boolean]`, `Seq[Seq[String]]`, `Seq[Seq[(String, Boolean)]]`. The ones involving booleans let you issue events that instruct Laminar to remove certain classes **that were previously added by this same modifier** (by setting their value to `false`). **Importantly, cls modifiers never remove classes added by other modifiers.** So if you said `cls := "foo"` somewhere, no other modifier can later remove this `foo` class. If you need to add and remove `foo` over time, use `cls("foo") <-- shouldUseFooStream` or similar dynamic modifiers.

If you (or a third party library you're using) are adding or removing class names without Laminar, using native JS APIs like `ref.className = ???` and `ref.classList.add(???)`, and you are **also** using `cls` modifiers on this **same** element, you must take care to avoid manually adding or removing the same classes as you're setting using the `cls` modifiers. Doing so may cause unexpected behaviour. Basically, **a given class name on a given element should be managed either via Laminar `cls` modifiers or externally via JS APIs, but not both**. See the `cls - third party interference` test in `CompositeKeySpec` for a simple example.


### Other Composite Keys

**`rel`** and **`role`** are two other space-separated attributes. Their API in Laminar is exactly identical to that of `cls` (see right above).



## Event System: Emitters, Processors, Buses 


### Registering a DOM Event Listener

Laminar uses native JS DOM events. We do not have a synthetic event system like React.js. There is no event pooling, no magic going on. This means there is nothing Laminar-specific to learn about events themselves.

To start listening for DOM events, you need to register a listener for a specific event type on a specific element. In Laminar, `EventListener` is a `Modifier` that performs this action.

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

See also: [Sources & Sinks](https://github.com/raquo/Airstream/#sources--sinks) in Airstream docs.

Now let's see how we can use EventBus for the same logic:

```scala
val clickBus = new EventBus[dom.MouseEvent]
val coordinateStream: EventStream[Int] = clickBus.events.map(ev => ev.screenX)
val coordinateObserver = Observer[Int](onNext = x => dom.console.log(x))
 
val element: Div = div(
  onClick --> clickBus.writer,
  "Click me",
  coordinateStream --> coordinateObserver
)
```

`onClick --> clickBus.writer` works exactly the same as the previous example, except that `clickBus.writer` is now the Observer that we send the events to.

What's new here is how we subscribe `coordinateObserver` to `onClick` events. As we established, we first send onClick events to `clickBus`. Then we have a `coordinateStream` that maps every click event from `clickBus` to its `screenX` coordinate.

Finally, we subscribe `coordinateObserver` to `coordinateStream` to print out those coordinates on every click. This completes our contrived pipeline. In real life this component might only be exposing the stream of coordinates, and consuming components would provide their own observers. Or this same component could be deriving some of its own state from the click stream, such as tracking and displaying the number of clicks.

It's not a coincidence that `coordinateStream --> coordinateObserver` syntax looks very similar to `onClick --> clickBus.writer`. Both of these `-->` methods create Modifiers that send events from the left hand side to the right hand side for as long as the element is mounted, conceptually speaking.


#### Feeding Multiple Sources into EventBus

Note that Airstream Observers, including `eventBus.writer`, can subscribe to multiple observables. So you can, for example, render a dynamic list of children, each of which sends its own events to the same Observer, possibly with information included in the event needed to identify which child the event came from. You can see an example of this pattern used in `TaskListView.taskViewsStream` in [laminar-examples](https://github.com/raquo/laminar-examples).

This can be achieved either with `addSource` or `addObserver`. Basically, in terms of laziness, with `addSource` the EventBus behaves like a stream that merges other streams – much like `stream1.mergeWith(stream2)`, but with the ability to add and remove source streams at any time. So, if you do `eventBus.writer.addSource(sourceStream)(childOwner)`, `sourceStream` will not be started until and unless `eventBus.stream` is started. Conversely, `sourceStream.addObserver(eventBus.writer)(childOwner)` will immediately start `sourceStream` even if `eventBus` does not have any observers.

The best part of this pattern is that you don't need to write custom logic to call `removeSource` or `subscription.kill` when removing a child from the list of children. `childOwner` will take care of that. Typically, that would be provided by the child Laminar element, via `onMountCallback`. Such an owner kills its subscriptions when the corresponding element gets removed from the DOM.


### Arrow Methods Syntax Sugar

All `-->` methods accept `Sink` on the right hand side. `Sink[A]` is anything that can be converted to an `Observer[A]`. `EventBus[A]` and `Var[A]` are both `Sink[A]`, and Laminar also treats `A => Unit` and `js.Function1[A, Unit]` as if they were `Sink[A]`.

Similarly, all the `<--` methods accept `Source[A]`, which is anything that can be converted to an `Observable[A]`. `EventBus[A]` and `Var[A]` are both `Source[A]`, and of course streams and signals are also `Source[A]`, being observables themselves.

So, you don't need to spell out an explicit observer every time you write the arrow methods, everything below is valid syntax:

```scala
val intBus = new EventBus[Int]
val textVar = Var("")
input(
  onClick.mapTo(1) --> intBus,
  value <-- textVar,
  onInput.mapToValue --> textVar,
  onMouseOver --> { ev => println(ev) },
  onChange --> { _ => println("committed") },
  onBlur.mapTo("blur") --> println
)
```

Airstream `Var`-s also offer several `updater` helpers that simplify updating their value where an Observer is expected, such as on the right hand side of `-->` methods. See [Airstream docs on this](https://github.com/raquo/Airstream/#observers-feeding-into-var).


### `:=> Unit` Sinks

Sometimes the syntax sugar shown above is just not good enough – not consise enough for advanced users, and not intuitive enough for beginners. For example:
* `myVar.updater` is a weird variation of its `update` method that returns an Observer,
* `onClick --> { _ => println("hello") }` is annoyingly verbose, and
* `onClick.mapTo("hello") --> println` splits the responsibility in a rather weird way.

To simplify some of these usage patterns, Laminar is now treating side effects that are typed simply as `:=> Unit` as valid sinks, for example:

```scala
import com.raquo.laminar.api.features.unitArrows // enable this feature
input(
  onClick --> println("hello"),
  onClick --> myVar.update(_.append(""))
)
```

There is no magic to it, these `--> methods` simply accept a `Unit` expression, which is then re-evaluated on every event (that's what `:=>` does in the function signature).

Unfortunately, while this kind of API is perfectly safe in Scala 2, that is not the case in Scala 3. Specifically, in Scala 2, the expression `if (true) observer` is typed as `Any` and returns `observer`, whereas in Scala 3, it is typed as `Unit` and returns `()`. 

So, in Scala 3, code like `div(onClick --> if (bool) observer)` would compile without warnings, but will not actually call the observer, because Scala 3 does not return the `observer` from the `else`-less `if` branch. This is unfortunate for our use case, because beginners might legitimately try to write such code when what they really want is e.g. `div(onClick.filter(_ => bool) --> observer)`.

To prevent such accidents, this new unit-based API requires a special import. If you forget the import, the compiler will give you an error pointing to this section of the documentation. It's up to you to choose between extra brevity and extra safety. With IntelliJ, importing the required implicit is only one command away, so it's not much of an annoyance.


### Transforming Observers

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



### Event Processors

Event processors let you spice up the standard `onClick --> eventBus` syntax with simple transformations. This API is different from observables API – it does not have complex / async operators, but it does offer DOM event specific handlers like `preventDefault`, `mapToValue`, etc.

```scala
val diffBus = new EventBus[Int]
val incrementButton = button("more", onClick.mapTo(1) --> diffBus)
val decrementButton = button("less", onClick.mapTo(-1) --> diffBus)
val diffStream: EventStream[Int] = diffBus.events // emits 1 or -1
```

This `mapTo` method is defined on `EventProcessor`, which `onClick` (`EventProp`) is implicitly converted to. Also available are `mapToStrict`, `map`, `filter`, `collect`, `orElse`,`preventDefault`, `stopPropagation`, `mapToEvent`, `mapToValue`, `mapToChecked`,  etc. and you can chain them in any order. See the API docs for these methods [here](https://javadoc.io/doc/com.raquo/laminar_sjs1_3/latest/com/raquo/laminar/keys/EventProcessor.html).

More examples:

```scala
input(onInput.mapToValue --> textObserver)
 
input(typ := "checkbox", onClick.mapToChecked --> textObserver)
 
div("Click me", onClick.map(getClickCoordinates) --> clickCoordinatesBus)
 
form(onSubmit.preventDefault.map(getFormData) --> formSubmitObserver)
 
div(onClick.useCapture --> captureModeClickObserver)
 
input(onKeyUp.filter(_.keyCode == dom.KeyCode.Enter).preventDefault --> enterPressBus)
 
div(onClick.collect { case ev if ev.clientX > 100 => "yes" } --> yesStringBus)
 
// @TODO[Docs] Come up with more relatable examples
```

Just like `EventProp`-s, `EventProcessor`-s are immutable and contain no element-specific state, so you can reuse them freely across multiple elements. For example:

```scala
val onEnterKeyUp = onKeyUp.filter(_.keyCode == dom.KeyCode.Enter)
// and then
input(onEnterKeyUp.preventDefault --> enterPressBus)
textArea(onEnterKeyUp.preventDefault --> enterPressBus)
```

This kind of casual flexibility is what Laminar is all about.


#### preventDefault & stopPropagation

```scala
form(onSubmit.preventDefault.map(getFormData) --> formSubmitObserver)
div(onScroll.stopPropagation.filter(throttle) --> filteredScrollEventBus)
div(
  onClick.stopImmediatePropagation --> theOnlyClickObserver,
  onClick --> ignoredObserver
)

```

These methods correspond to invocations of the corresponding native JS `dom.Event` methods. MDN docs: [preventDefault](https://developer.mozilla.org/en-US/docs/Web/API/Event/preventDefault), [stopPropagation](https://developer.mozilla.org/en-US/docs/Web/API/Event/stopPropagation), [stopImmediatePropagation](https://developer.mozilla.org/en-US/docs/Web/API/Event/stopImmediatePropagation)

Importantly, these are just ordinary processors, and happen in the order in which you have chained them. For example, in the code snippet above `ev.preventDefault` will only be called on events that pass `filter(_.keyCode == dom.KeyCode.Enter)`. Internally all processors have access to both the latest processed value, and the original event, so it's fine to call the `.preventDefault` processor even after you've used `.map(_.keyCode)`.


#### useCapture

```scala
div(onClick.useCapture --> captureModeClickObserver)
```

JS DOM has two event modes: capture, and bubbling. Typically and by default everyone uses the latter, but capture mode is sometimes useful for event listener priority/ordering/efficiency (not specific to Laminar, standard JS DOM rules/limitations apply).

`.useBubbleMode` is the opposite of `.useCapture`. The former is the default, so you don't need to call it unless you're calling it on an EventProcessor that you've previously enabled capture mode for.

See MDN [addEventListener](https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener) page for details (see "useCapture" section).


#### Obtaining Typed Event Target

Due to dynamic nature of Javascript, `dom.Event.target` is typed only as `dom.EventTarget` for most events, which is not useful when you want to get `ev.target.value` from a target that is a `dom.html.Input` (but doesn't know it). So, you can't do this:

```scala
// Does not work because .value is defined on dom.html.Input, but not on dom.EventTarget, which is what `ev` is :(
input(typ := "text", onChange.map(ev => ev.target.value) --> inputStringBus)
```

Instead, use `mapToValue`, which essentially gives you `ev.target.value`:

```scala
input(typ := "text", onChange.mapToValue --> inputStringBus)
```

There's `mapToChecked` for checkboxes too by the way.

For a more general solution, you can use `inContext`:

```scala
input(inContext(thisNode => onChange.mapTo(thisNode.ref.value) --> inputStringBus))
```

This feature is not specific to events at all. The general syntax is `inContext(El => Modifier[El]): Modifier[El]`. `thisNode` refers to the element to which this modifier will be applied. Its type is properly inferred to be `Input`, which is an alias for `ReactiveHtmlElement[dom.html.Input]`, thus its `.ref` property is precisely typed as `dom.html.Input`, which is a native JS type that has a `.value` property that holds the current text in the input.

Because `thisNode.ref` refers to the element on which the event listener is **registered**, in JS DOM terms, it is actually equivalent to `dom.Event.currentTarget`, not `dom.Event.target` – the latter refers to the node at which the event **originates**. When dealing with inputs these two targets are usually the same since inputs don't have any child elements, but you need to be aware of this conceptual difference for other events. MDN docs: [target](https://developer.mozilla.org/en-US/docs/Web/API/Event/target), [currentTarget](https://developer.mozilla.org/en-US/docs/Web/API/Event/currentTarget).



### element.events

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

The `events` method accepts `EventProcessor`, not just an `EventProp`. This allows for very flexible composition:
 
```scala
// You can define this once for reusability
val onEnterPress: EventProcessor[dom.KeyboardEvent, dom.KeyboardEvent] =
  onKeyPress.filter(_.keyCode == dom.KeyCode.Enter)
 
input(onEnterPress --> observer)
 
textArea(onEnterPress.preventDefault --> observer)
 
val changeEventStream = myInputNode.events(onEnterPress)
```



### composeEvents

Deprecated in favor of `compose`. use `ev.compose(f)` instead of `composeEvents(ev)(f)` – see below.



### Compose and flatMap Events

Typically, you subscribe to DOM events without explicitly creating any streams. This is simple and convenient, but it lacks the full palette of observables operators. For example, you might want to throttle the events, or propagate an event only if a certain other signal contains `true`. We can use `inContext` to access those fancy operators in Laminar:

```scala
div(
  onClick.throttle(100) --> observer, // does not compile
  inContext(_.events(onClick).throttle(100) --> observer) // works
)
```

however, the boilerplate can get annoying. Use the `compose` method instead. It works just like the `compose` method on streams, except it also creates the required stream of DOM events under the hood.

```scala
val allowClick: Signal[Boolean] = ???

button(
  onClick
    .preventDefault
    .compose(_.withCurrentValueOf(allowClick).collect { case (ev, true) => ev } 
    --> eventObserver
)
```

If you want to create a new stream for every event, you can use the `flatMap` method, for example:

```scala
def makeAjaxRequest(): EventStream[Response] = AjaxStream.get(...).map(...)

input(
  onInput
    .mapToValue
    .flatMap(txt => makeAjaxRequest(txt)) --> observer
)
```

If you use this `flatMap` method in IntelliJ IDEA, you'll be annoyed to find that it causes the IDE to incorrectly report a false type error, at least with Scala 2. Hopefully they'll fix that some day, but for now I added more specialized `flatMapStream` and `flatMapSignal` methods which use simpler types, and don't trigger the false error in the IDE. As another workaround, you can also use `onInput.compose(_.flatMap(...))` instead of `onInput.flatMap(...)`.



### Multiple Event Listeners
 
Just like in native JS DOM, nothing is stopping you from registering two or more event listeners for the same event type on the same element:
 
```scala
div(onClick --> clickBus1, onClick --> clickBus2, "Click me")
``` 

In this case, two event listeners will be registered on the DOM node, one of them sending events to `clickBus1`, the other sending the exact same events to `clickBus2`. Of course, this works the same regardless of what syntax you use to register event listeners (see previous section).

This feature is rarely needed, but it can simplify composition in certain cases. For example, consider the `TextInput` component mentioned above. That component itself might have an internal need to listen to its own `onChange` events (e.g. for some built-in validation), and it will not interfere with any other `onChange` event listeners the end user might want to add to it.



### Window & Document Events

In Javascript, `window` and `document` support their own custom sets of events. You can obtain streams of those events like this: `documentEvents(_.onDomContentLoaded).map(ev => renderApp())`.

`windowEvents` provides similar access to `window` events.

All the standard event processors work for these events as well, so if you wanted to get document events in capture mode, you could say `documentEvents(_.onDomContentLoaded.useCapture)`.

Depending on your desired logic, you might not have a Laminar element to act as an Owner for subscriptions of these event streams. For example, if you implement an Ajax service based on observables, you would probably want it to make your requests regardless of observers. For such app-wide subscriptions you can use `unsafeWindowOwner`. It will never kill its possessions, so needless to say – use with caution. Any subscriptions created with this owner will never be cleaned up by Laminar, so you will need to clean them up manually if needed.



## Controlled Inputs

A controlled input is an HTML element whose `value` property (or `checked` property in case of checkboxes) is **locked** to a certain observable. You might be familiar with this concept from [React.js](https://reactjs.org/docs/forms.html#controlled-components). Here is how you do this in Laminar:

```scala
val zipVar = Var("")
val zipValueSignal = zipVar.signal
val zipInputObserver = zipVar.writer
input(
  placeholder := "Enter zip code: ",
  controlled(
    value <-- zipValueSignal,
    onInput.mapToValue --> zipInputObserver
  )
)
```

You need to put both the event listener and value reader inside a `controlled` block. This is how Laminar knows where the interaction loop for this input is described, and enables Laminar to make sure that the value property follows the current value of `zipVar`.

This leads us to the main requirement of controlled inputs: your logic must respond to `onInput` events by making `zipValueSignal` emit the new value. In our example we do exactly that – `onInput` we write the new text (`mapToValue` grabs the user's input from `event.target.value`) to `zipVar`, and we make value listen to `zipVar`'s signal.

This is the simplest loop possible, but we can complicate it using all normal Laminar functionality. For example, we could disallow non-digit inputs:

```scala
input(
  placeholder := "Enter zip code: ",
  controlled(
    value <-- zipValueSignal,
    onInput.mapToValue.map(_.filter(Character.isDigit)) --> zipValueObserver
  )
)
```

We could also route the `onInput` event in any other way, we could even update zipVar asynchronously. When Laminar detects that `onInput` event happened and you didn't synchronously update the observable that the `value` is locked to, `zipValueSignal` in this case, we set the value property to the previous value emitted by `zipValueSignal`. This happens synchronously, so the user will feel as if their input was ignored, they will not even see it flash.

Then if your logic updates the `value` prop at some point in the future, it will work as you'd expect. Your updates to `zipValueSignal` don't actually need to come exclusively from user input, you can emit a value into that observable any time, and the `value` prop will update to match.

Note that Laminar can't do anything if you manually set the element's value, either via `inputEl.ref.value = "newValue"` or using the `setAsValue` EventProcessor. You should not use either of these methods with controlled inputs. Speaking of, you shouldn't call `preventDefault` on controlled events either, even on events that are cancelable (`onInput` is not), because for example for checkboxes the rollback of checked state happens **after** the event listener has finished executing, potentially undoing Laminar's changes to the `checked` property and de-syncing it from the controlling observable.


### Life Without Controlled Inputs

You can use `setAsValue` and `setAsValue` event processors when you don't need the input to be controlled. The point of having controlled inputs is being able to update their `value` from two sources: user input, and by updating the observable you're locking the value into. With controlled inputs, we solve the conflicts between these two sources by requiring that user input updates the observable to which value is locked if you want such user input to be allowed. 

But if you only care about user input, and don't need to lock the value to an observable, you can achieve the same without controlled inputs, for example:

```scala
input(
  placeholder := "Enter zip code: ",
  onInput
    .mapToValue
    .map(_.filter(Character.isDigit))
    .setAsValue --> zipValueObserver
)
```

And we only need `setAsValue` in this case because we want to transform the user's input. If we didn't need to filter for digits, we wouldn't need `setAsValue` as the browser would do the job for us.

One limitation of this approach is that we can't "undo" invalid user input because the `onInput` event is not `cancelable`. Well, you could ugly-hack it, but you'd be reinventing controlled inputs to some extent. So, `setAsValue` can't really **filter** user input events, it is limited to **transforming** them.


### Why Controlled Inputs, Again?

So, how is **not** using the `controlled` block different from controlled inputs?

```scala
input(
  placeholder := "Enter zip code: ",
  value <-- zipValueSignal.distinct,
  onInput.mapToValue.map(_.filter(Character.isDigit)) --> zipValueObserver
)
```

This might look and often work a lot like controlled inputs, but it doesn't give you the guarantees that you want, and because of this you can run into issues. For example, without the `controlled` block, `value` is not actually locked to `zipValueSignal.distinct`. All that happens is that when `zipValueSignal.distinct` emits, we update the element's value to the emitted value. Ok, so what if the user types "123", and then later types another "a"? You'd expect "a" to be filtered out and the input to contain "123" after all this, but it will actually contain "123a", despite the Var containing "123". This is because `zipValueSignal` never emitted "123a", this value was transformed into "123" before it reached it, and so the `distinct` operator has filtered out the duplicate "123" value. But at the same time, the user did type "123a", and so that's what the browser put in the input box. And before you can think of using `preventDefault` to solve this, you can't, because `onInput` events are not cancelable. There's a more elaborate explanation in [#79](https://github.com/raquo/Laminar/issues/79), although it's outdated because as of Laminar 15 signals don't have built-in `==` checks (but we replaced that with `.distinct` operator for the sake of this example).

So, bottom line is, if you need to transform or filter user input, use either controlled inputs or `setAsValue`.


### Types of Controlled Inputs 

Our example used `onInput` event and `value` property. Certainly would be nice if `onInput` worked on all input types, but that's not the case in Internet Explorer (of course).

So here are the events that work in all browsers of interest:

| Element type | Input Prop | Controlled Prop | Comment |
|-|:-:|:-:|-|
| input[type=text,email,etc.] | onInput | value |  |
| input[type=checkbox,radio] | onClick | checked | Do NOT use preventDefault on this |
| textarea | onInput | value |  |
| select | onChange | value | Does not work with multi-select |


### Controlled Inputs Caveats

* You can't have conflicting `controlled` blocks on the same element. You also can't have a `value <-- ???` binder outside of a controlled block on an element that already has `value` controlled. Trying to do that will throw and exception. If the `controlled` block is added to the element, it defines the sole source of truth for the `value` prop.

* The `onInput` (or `onClick` etc.) event listener defined inside of the `controlled` block will be run before any other event listeners on this element, even if you registered those other listeners before adding the controlled block. If the controlled block is added to an element after it has already been mounted, under the hood this is achieved by removing all the Laminar listeners from the element and re-adding them in the same relative order, but **after** the controlled event listener. This is done to ensure that other event listeners do not interfere with the controlled input functionality (e.g. by stopping propagation of the event).

* Remember that Laminar's `controlled` block sets the input's `value` (or `checked`, whichever you use) property to the controlling observable's last emitted value after each input event. This means that all other input event listeners will not see the user's real input, they will see whatever input value was set by the controlled block. This is because the event itself doesn't carry the information about the user's input, it's only stored in the input element's `value` property. So as Laminar updates that property to match the controlled observable, there is no chance for other event listeners to see the user's raw input.



## SVG

Laminar lets you work with SVG elements (almost) just as well as HTML. Everything works pretty much the same, just need to understand the required imports:


### SVG Syntax and Imports

* SVG elements and attributes are available in the `com.raquo.laminar.api.L.svg` object

* Some of the attributes of SVG and HTML have the same names, for example there are two instances of the `className` attribute, one is `L.className` and is an HTML attribute, the other is `L.svg.className` and is an SVG attribute. You can only apply SVG attributes to SVG elements (and not HTML elements), and similarly for HTML attributes.

* Therefore, you generally should **not** import both `com.raquo.laminar.api.L.svg.*` and `com.raquo.laminar.api.L.{*, given}` as that would cause name collisions.

* On the other hand, event prop keys such as `com.raquo.laminar.api.L.onClick` apply universally to all elements, both HTML and SVG.

* If you are working mostly with HTML elements in a given file, you can just import `com.raquo.laminar.api.L.{*, given}` and refer to SVG elements and attributes with an `svg` prefix, like this:

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

* On the other hand, if you are working mostly with SVG elements in a given file, you can use a different set of imports:

  ```scala
  import com.raquo.laminar.api.L.svg.* // get svg keys without the svg prefix
  import com.raquo.laminar.api.{*, given} // get `L` and standard implicits like `textToTextNode`
  // DO NOT IMPORT: com.raquo.laminar.api.L.{*, given}
  
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

### Rendering External SVGs

The browsers are able to render SVGs from a URL with a simple `img` tag just like any other image:

```scala
img(src := "https://example.com/image.svg", role := "img")
```

For SVGs, the `role := "img"` is apparently needed for accessibility due to a [VoiceOver bug](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/img#identifying_svg_as_an_image), nothing to do with Laminar.

Rendering SVGs this way makes the browser download them separately, and only when they're included in the document. In contrast, if you create SVGs with Laminar, they will be included in the JS bundle, and will be available immediately without an extra network request, but will need to be downloaded upfront, and will increase the size of the bundle.

You can also render native SVG elements provided by other libraries, or even create SVG elements from `<svg>...</svg>` strings in Laminar: see [Rendering Third Party HTML or SVG Elements](#rendering-third-party-html-or-svg-elements) and [Parsing HTML or SVG Strings into Elements](#parsing-html-or-svg-strings-into-elements).



## CSS

Laminar lets you set CSS style properties in all the same ways as HTML attributes, but since CSS values are more complex, we offer additional conveniences.


### CSS Keyword Helpers

```scala
div(
  fontSize.inherit, // same as `fontSize := "inherit"`
  display.none, // same as `display := "none"`
  flexDirection.column // same as `flexDirection := "column"`
)
```

The available keywords are different for every CSS property according to the values that are valid for that property. IDE auto-complete should reduce the need to look up the correct values online.

Of course, you can still set any value manually, e.g. `display := "block flow"`.

You can get the string value of a CSS setter like `display.none` with `.value`, so `display.none.value` returns "none". You can use this when you want assurance that you're using the right values in observables, e.g.:

```scala
div(
  // You can spell out the values manually...
  textAlign <-- streamOfBooleans.map(
    if (_) "left" else "right"
  ),
  // Or refer to defined constants:
  textAlign <-- streamOfBooleans.map(
    if (_) textAlign.left.value else textAlign.right.value
  )
)
```

If you want less boilerplate for this use case, define a trivial implicit conversion from `StyleSetter[String]` to `String`, and you won't need to call `.value` manually. This is a bit too magicky to be included in Laminar core though.


### Unit and Function Helpers

CSS properties that accept lengths, like `marginTop`, require you to provide strings that include the units of measure (most commonly, pixels). Doing this manually can get annoying, and might require the overhead of creating a new observables, e.g. `intStream.map(s"${_}px")`) when working with dynamic data.

To help with that, Laminar offers several ways to set units like `px`, `vh`, `percent`, `ms`, etc.:

```scala
div(
  margin.px := 12,
  marginTop.px := 12,
  marginTop.px(12), // remember that all `:=` methods in Laminar are aliased to `apply` for convenience!
  marginTop.calc("12px + 50%"),
  marginTop.px <-- streamOfInts,
  marginTop.px <-- streamOfDoubles
)
```

The new API is type-safe, so for example `backgroundImage.px` does not exist, but `.url` does:

```scala
div
  // Equivalent to CSS:  background-image: url("https://example.com/image.png")
  backgroundImage := """url("https://example.com/image.png")""",
  backgroundImage.url := "https://example.com/image.png",
  backgroundImage.url("https://example.com/image.png"), // same
  backgroundImage.url <-- streamOfUrls
)
```

Currently, complex CSS properties don't have all applicable helpers defined, which means that you can do e.g. `borderWidth.px := 12`, but you can't do `border.px := 12` yet.

For such cases, you can use `style` string helpers: `style.px(12)` returns a plain "12px" string which you can use like `border := style.px(12)`.


### Vendor Prefixes

Not a super relevant feature these days, but you can now do this to set a style property along with several prefixes:

```scala
div(
  transition.withPrefixes(_.moz, _.ms, _.webkit) := "all 4s ease"
  // and similarly for `<--`.
)
```


### Approaches To CSS

Laminar's CSS API lets you set styles that apply to a given element. You can also reuse groups of styles by passing around style setter modifiers or even lists of them. In Javascript world this general approach is known as "inline styles" or "CSS in JS".

Various CSS Frameworks like Tailwind and Bootstrap can also be used with Laminar – such frameworks define a bunch of CSS classes in external stylesheets, and if you load those stylesheets, you can add their classes to elements with Laminar to style them (see the [cls](#cls) attribute).

Of course, instead of, or in addition to, using a CSS file provided by a CSS framework, you can provide your own CSS file (or several). You can write CSS manually, or use a CSS preprocessor like SASS or LESS. All of this is done using standard methods, not specific to Laminar.

[ScalaCSS](https://japgolly.github.io/scalacss/book/) needs a special mention. Laminar does not provide an integration with it out of the box, however the two can certainly be used together. See [this discussion](https://github.com/raquo/Laminar/issues/20). Note that ScalaCSS is pretty heavy machinery. It offers a lot of features and safeties, however all that comes at a cost in regard to JS bundle size and compile time.

One common approach in Laminar apps is to use external stylesheets to provide the bulk of the styling in your application, and using Laminar's API only to set styles dynamically (in response to user actions or changing data, e.g. showing / hiding elements onclick). This way, you can set up your build config to do very quick CSS hot reloads, which is very useful for pixel pushing / quick iteration on styling. In contrast, if you use inline styles for all styling, then you need to reload the whole Scala.js bundle to see even a small change in your CSS.

Keep in mind that CSS, being a visual design language, is fundamentally different from a programming language like Scala. To a significant extent, the correctness of a typical Scala program can be checked with types alone, however the correctness of CSS has very little to do with types. Yes, with Laminar or ScalaCSS you can easily make sure that you're not passing invalid values to CSS properties, however having technically-valid values is far from having **correct** values when it comes to CSS.

Your compiler has no idea if `50px` is a good `top-margin` for `h1` elements, and even **you** the designer don't know that upfront. You need to actually see the visual result of this top-margin first, then you'll probably want to adjust it by a few pixels, look at the result again, and so on, until you're happy with the result. I highly recommend that you optimize your CSS build setup for such fast-paced iterative development over anything else. In CSS, it's the visual correctness that matters, and long term, that's what takes the most time to get right. Typechecking is a trivial concern, in comparison. Always remember that type safety is but a tool, not the end-goal.  



## Ownership

Ownership is a core Airstream concept. You **really** need to read about it in [Airstream docs](https://github.com/raquo/Airstream#ownership) before proceeding with this section.

Basically, Airstream does not let you create a leaky resource without specifying an `Owner` that is responsible for eventually killing said resource. For example, adding an Observer to an EventStream is a potential memory leak. Once a subscription is established like that, the stream in question obtains a reference to that observer, and the stream's parent stream obtains a reference to this stream, etc. Depending on how long that chain is and how it's used, there is a good chance it will either never be garbage collected or will be garbage collected much later than optimal.

With other libraries you would need to manually call `subscription.kill()` for every `addObserver` call you make to avoid memory leaks like that. In Airstream however, you do not need to do that. Instead, when adding an observer you need to specify not just the observer, but also the Owner of this new subscription, and that Owner assumes the responsibility for killing the subscription when it's time.


### Laminar's Use of Airstream Ownership

In Laminar versions **prior to v0.8.0** every Laminar element was an `Owner`. In fact, when you used `<--` methods on elements or in their modifiers, the element in question was the owner of the subscription that the `<--` method created. Laminar elements killed their subscriptions when they were unmounted. Sounds simple and effective enough, yet this old design had two known flaws:

1. The subscriptions belonging to an element would be created when the element was instantiated, and killed when it was unmounted. That means that elements that were created but never mounted into the DOM would never kill their subscriptions, since the elements were technically never unmounted. Because of that, you needed to be careful not to create any elements that you weren't going to immediately put into the DOM.

2. Once you unmounted an element, its subscriptions would be killed with no way to resurrect them. So essentially you weren't able to reuse previously unmounted elements as their subscriptions wouldn't be re-activated when the element was put back into the DOM.

Laminar v0.8.0 introduced dynamic subscriptions with a new contract: an element is no longer an Owner, instead an element creates a new Owner every time it's mounted, and kills that owner and all of its subscriptions when it's unmounted. This means that the element's subscriptions are not activated until it's mounted, solving problem 1. And we now also track your subscriptions in a way that allows Laminar to re-activate them after the element is re-mounted, solving problem 2.

Let's see how ownership in the new Laminar works under the hood by analyzing a simple snippet. You might want to follow along by reading the source code of the methods involved.

```scala
val urlBus: EventBus[String] = new EventBus
val urlStream: EventStream[String] = urlBus.events
val element = a(href <-- urlStream) // create the element
render(element) // mount the element
```

1. `href <-- urlStream` creates a `Binder` (see `HtmlProp.<--`) 

2. That `Binder` being a `Modifier` is applied to the `a` element immediately after it's created by the `a(...)` call.

3. That Binder's apply method does this (actual source is split between `KeyUpdater.bind` and `HtmlProp.<--`):
  
    ```scala
    ReactiveElement.bindFn(element, valueSource) { value =>
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

  Of course, Laminar's API does not let you do this _easily_. Also of course, Laminar's API does let you do this if you directly ask for this by getting the parent's `Owner` from an `onMount*` hook and passing that owner to children. With Laminar you can always shoot yourself in the foot, but you have to explicitly ask for it. So you should prefer less manual methods: a simple `foo <-- bar` instead of messing with owners manually.

Lastly, a warning about `textContent` and `innerHTML`:

* You must not set [textContent](https://developer.mozilla.org/en-US/docs/Web/API/Node/textContent) or [innerHTML](https://developer.mozilla.org/en-US/docs/Web/API/Element/innerHTML) properties on Laminar elements that have any other children given to them by Laminar. We deliberately do not provide an API to set these properties because of their messy side effects. If you need this functionality, it is up to you to use native JS functionality available on `element.ref` to set those properties, **and** ensure that you either manage the contents of a particular element with Laminar, or you do it manually with `innerHtml` / `textContent`, not both. The two approaches don't mix on the same element. Needless to say, when using native JS methods including `innerHtml` you need to understand [XSS](https://www.owasp.org/index.php/Cross-site_Scripting_(XSS)) and other risks as well.

  Note: if you think you want innerHTML, you might want to settle for Laminar's [unsafeParseHtmlString](#parsing-html-or-svg-strings-into-elements) method, which is allows for a smoother integration of random HTML code snippets into Laminar.



## Element Lifecycle Hooks

Lifecycle hooks let you do stuff when the element is being mounted or unmounted, as well as obtain and use an `Owner` associated with the element when it element is mounted. 


### What Is Mounting?

**In JS DOM** an element is said to be mounted when one of its ancestors is the document being displayed: `dom.document`. In other words, a mounted element is present in the DOM, or "attached" to the DOM. On the contrary, elements that are not mounted are called "unmounted" or "detached". Those elements are not present in the DOM. They might have none, one or more ancestors, but none of them are `dom.document`, so the highest level ancestor of this element must have no parent.

**Laminar** does not get notified by the browser when elements change parents and become mounted / unmounted. Therefore, technically we use a slightly different definition of mounted / unmounted elements. A Laminar element is considered mounted when one of its ancestors is a **mounted** Laminar `RootNode`. A `RootNode` is created with `val rootNode = render(containerEl, contentEl)`. A `RootNode` is mounted immediately when it's initialized if `containerEl` was present in the DOM when `render` is called. If you manually remove one of RootNode's ancestor elements from the DOM, you should call `unmount()` on the `RootNode`, otherwise Laminar will still consider the RootNode mounted, which is probably not be what you want.

In practice the distinction between JS DOM mounting and Laminar mounting meaning is only important when Laminar is not managing the entire DOM in your app. For example, when you are rendering Laminar elements inside a React.js components. If you're rendering your whole app in Laminar, you're likely rendering it into some `<div id="appContainer"></div>` element that's always present in the DOM so there is no practical difference between Laminar's definition of mounting and JS DOM definition of mounting.

Laminar elements that are mounted are also called "active", because the subscriptions associated with them are also active while they're mounted. Every Laminar element starts out unmounted because it has no parent at initialization time. An element can become mounted if its parent becomes mounted – either if its parent is changed to a different one, or if the same parent becomes mounted. The latter is a recursion that terminates at the `RootNode`.

You can check if a node is mounted in JS DOM terms with `DomApi.isDescendantOf(node.ref, dom.document)`.

You can check if a Laminar element is mounted in Laminar terms using `ReactiveElement.isActive`.

Laminar offers a few helpers to react to element lifecycle events:


### onMountCallback

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

To reiterate for – you don't necessarily need `onMountCallback` if you want to do something on mount only. For example, you could say `onMountBind(_ => observable --> onNext)` to get similar behaviour without messing with the owners manually. More on other such `onMount*` helpers below.
 

### onUnmountCallback & onMountUnmountCallback

Works just like `onMountCallback`, with the same caveats.

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



### onMountBind

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

So, now we know why `onMountCallback` fails us on a technical level, but why does it have to be that way? Well, Laminar is just doing what we're telling it to do: "Every time this element mounts, create a new click listener and add it to this element". And the reason you're even able to tell this to Laminar is: _what if this was what you actually wanted to do_? What would be a simpler way to express that? I can't think of any, without introducing completely arbitrary helpers that are dependent on fleeting fashions on frontend dev.

And so, `onMountBind` exists as the simplest way to do what it does, and `onMountCallback` as the simplest way to do the other thing.


### onMountInsert

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

Essentially `onMountInsert` is to `Inserter` what `onMountBind` is to `Binder`, and what `onMountCallback` is to `() => Unit`.

Thus the callback can return any `Inserter`, including those created by modifiers `children <-- ???`, `children.command <-- ???`, `child <-- ???`, `child.maybe <-- ???`, and `child.text <-- ???`.

We also have implicit conversions defined in `Implicits.LowPriorityImplicits` that let you return an individual element or a collection of elements from the onMountInsert callback – they would be converted into an Inserter.

So why does `onMountInsert` need to exist, and why doesn't Laminar allow us to put `child <-- childStream` into `onMountBind`?

`onMountInsert` offers the same deal as `onMountBind` in terms of cancelling the subscription on unmount to prevent a conflict between N `child <-- childStream` modifiers after mounting the element N times. Note that cancelling this subscription does **not** mean that the previously inserted child is removed. If you want that, emit `emptyNode` into childStream while it's still mounted.

But as you might recall, Inserters like `child <-- ...` and `children <-- ...` reserve a stable spot in the parent element where they will insert new children. That spot is located after the last child of the parent element _at the time of reservation_.

This would obviously not work inside `onMountBind` – the callback is executed only on mount, so the `child <-- childStream` modifier is added only on mount, and at that point the element already has two child nodes in it: `div(TextNode("Hello, "), TextNode("!"))`. Therefore `child <-- childStream` would have reserved a spot **after** "!", which is not what we want, we obviously want it to reserve the spot where the `onMountInsert` modifier itself is visually located, i.e. between "Hello, " and "!".

The `onMountInsert` modifier exists to solve this – it reserves a spot when it's applied to the parent element – before any mount events are fired – and provides information about that reserved spot to the `Inserter` returned by the callback. And that reserved spot persists after mounting and unmounting, of course.


### Why Use Lifecycle Hooks?

* If you legitimately need to run arbitrary code on mount and/or on unmount, for example to integrate with a third party library.

  * Note: If you need your callback to run on mount **but also** to run immediately in case the element is already mounted, use `ReactiveElement.bindFn` / `bindObserver` / etc. methods. Generally you shouldn't need these, but they might be useful for integration or extensions. 

* If you want an `Owner` to _simplify_ some code. For example, if you have a complex component where you don't want to fiddle with ownership everywhere, you can pass a parent's owner to it using `onMountInsert(ctx => makeComponent(ctx.owner)`, but you have to know what you're doing to avoid memory leaks. Read more about Ownership in Laminar and Airstream docs.

  * Note: if you want an `Owner` because you're upgrading from Laminar v0.7 where each element was an Owner, this might not be the best way to achieve what you want. We've redesigned the API to reduce the need mess around with owners manually. For example, you can have modifiers like `stream --> observer` now.

* If you need to decide which binders / inserters to use at mount time. For example, with `onMountInsert` you could switch between `child <-- stream` and `children <-- otherStream` every time the element is mounted.

* If you just need a reference to `thisNode`, **don't** use lifecycle hooks, use `inContext` – it is simpler and more performant.

* If you can get away with `stream --> observer` binding syntax, use that, **don't** bother with lifecycle hooks.

  * Note: Remember you can often use streams like `EventStream.unit()` and `EventStream.delay(100)` if you want the observer to be called "on mount" or 100ms after it's mounted.


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



## Integrations With Other Libraries

Laminar is very flexible and magic-free. Its declarative-feeling API is easy to decompose into low-level JS primitives like elements and callbacks, and it offers integration helpers for such third party primitives, so it's generally very easy to make use of other libraries with Laminar.

Note: This section is not very complete. We need more examples.


### React.js

See the [video chapters](https://www.youtube.com/watch?v=L_AHCkl6L-Q&t=3865s) on rendering Laminar elements inside React and the other way.

This is definitely possible, but might get a bit annoying due to the difference in approaches between Laminar and React. Do this only if you have a pre-existing React application. For greenfield development, if you want to use pre-made components from the JS ecosystem, my advice is don't use React components, find good Web Components instead (see below).


### Web Components

[Web Components](https://developer.mozilla.org/en-US/docs/Web/Web_Components) is a technology that lets one implement a custom component using any JS library they want, and expose an API which is similar to native HTML DOM API, so that end users can interact with said component using attributes / properties / methods much like they would interact with any other HTML element. Well, except that all those attributes and properties are defined by the Web Component's author, and instead of the browser processing the user's declarations, they are passed over to the web component's JS implementation.

With such an API, any UI library that can manage attributes and properties of HTML elements can make use of Web Components. Laminar is great at managing attributes and properties of HTML elements, so using Web Components from Laminar is very easy. All you need to do is create a typed interface to the JS Web Component – this is conceptually similar to making Scala.js interfaces to JS libraries, except you need to define the interface in a Laminar-specific format, because you want to use Laminar's typed API to talk to the web component, not the low-level untyped JS DOM API.

You can see some examples of such interfaces and their usage in [live examples](https://laminar.dev/examples/web-components), and there is a [whole library](https://github.com/sherpal/LaminarSAPUI5Bindings) of such Laminar bindings for SAP UI5 web components.

If you review the source code of all these bindings, you'll see that there's no special API for Web Components in Laminar, all we do is define custom attributes / properties / slots / methods that the given Web Component exposes, and use one simple pattern for accessing them in a scoped manner.


### Passing Laminar Elements to Third Party Libraries

Some libraries expect you to pass them a DOM element, that they will "render" (attach to their DOM) themselves. For example, a Javascript modal library might expect you to specify the content to render in this way.

```scala
object JsModalLibrary {
  def renderModal(element: dom.Element) = ???
}
```

All Laminar elements have a `.ref` property that returns the JS DOM node, so it _seems_ as if you can just pass that node to the JS library:

```scala
val content = div(b("Important"), " message")
JsModalLibrary.renderModal(content.ref)
```

However, this only works so long as your `content` Laminar element is static, that is, does not contain any bindings / subscriptions like `<--` and `-->`. A Laminar element's subscriptions only get activated when it is mounted into the DOM (by Laminar), but here we're delegating this mounting responsibility to `JsModalLibrary`, who on its own knows nothing about Laminar's needs.

For such cases, Laminar offers a special `renderDetached` method that lets you manually control the activation and deactivation of subscriptions on a Laminar element:

```scala
val content = div(
  b("Important"), " message",
  button(onClick --> { _ => dom.console.log("click") }, "Log")
)

val contentRoot: DetachedRoot[Div] = renderDetached(
  content,
  activateNow = true
)

JsModalLibrary.renderModal(contentRoot.ref)
```

Now, this `contentRoot` detached root is managing the lifecycle of `content` element's subscriptions. We said `activateNow = true`, so the `onClick` subscription will be activated immediately upon creation of the `contentRoot`, and so it will log "click" when the user clicks the button.

There is one last issue to take care of: since we activated the element's subscriptions manually, we also need to deactivate them manually when we don't need the element anymore, otherwise they will never be deactivated and garbage collected. Typically, the Javascript modal library will provide some kind of `onClose` hook, which is exactly what we need. So, our final code could look something like this:

```scala
val contentRoot: DetachedRoot[Div] = renderDetached(
  content, // Laminar element
  activateNow = false // !!! see activation code below
)

def openModal() {
  JsModalLibrary.renderModal(
    contentRoot.ref,
    onClose = contentRoot.deactivate // !!!
  )
  // re-activate subscriptions every time the modal is opened
  contentRoot.activate()
}
```


### Rendering Third Party HTML or SVG Elements

If you want to render a native HTML element (`dom.html.Element`) created a third party library, you can convert it into a Laminar element using `foreignHtmlElement` or `foreignSvgElement`. Once you have that, you can perform all the usual Laminar operations on it:

```scala
// Render in Laminar
div(foreignSvgElement(myJsSvgElement))

// Modify the element before rendering:
div(
  foreignHtmlElement(myJsHtmlElement).amend(
    onClick --> ???,
    aria.labelledBy := ???,
    span("Add new child")
  )
)
```

Of course, it's on you to ensure that any such operations on the element will not upset the third party library or whoever provided the element to you.


### Parsing HTML or SVG Strings into Elements

Laminar also has `unsafeParseSvgString(dangerousSvgString: String): dom.svg.Element` to help render SVG strings (and a similar method for HTML). Rendering such an element requires two steps, but that inconvenience is by design, appropriate for such an unsafe API:

```scala
div(
  // SVG elements:
  foreignSvgElement(DomApi.unsafeParseSvgString("<svg>....</svg>")),
  // HTML elements:
  foreignHtmlElement(DomApi.unsafeParseHtmlString("<div onclick="alert('pwned')"></div>"))
)
```

**Warning:** These `unsafeParse*` methods expose you to [XSS attacks](https://owasp.org/www-community/attacks/xss/), so you absolutely must not run them on untrusted strings. Use them only for including trusted content such as hardcoded SVG icon strings. If you really need to include attacker-provided content into those strings, you must escape those values to prevent them from being interpreted as code, or breaking your HTML in other ways.


### Observables Interop

You can easily convert any `js.Promise` or `scala.Future` to Airsream observables using `{EventStream,Signal}.{fromJsPromise,fromFuture}` methods, but you can also create a deeper integration with a different observable system – see [Custom Event Sources](https://github.com/raquo/Airstream/#custom-event-sources) in Airstream docs. If you make something useful I encourage you to publish it as a library or at least as a gist. Personally, on the frontend I only use Airstream, so I wouldn't be a good maintainer of such an integration myself.

Integration of observables with callback-driven APIs is usually achieved by providing `observer.onNext` as the callback (e.g. you can pass something like `onclick = clickEventBus.writer.onNext` to React). You might want to check out various Airstream helpers like [EventStream.withJsCallback](https://github.com/raquo/Airstream/#eventstreamwithcallback-and-withobserver) for this.



## Network Requests

On the frontend, the primary method to make network requests is the [Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API). In Laminar, you can use Airstream's [FetchStream](https://github.com/raquo/Airstream/#fetchstream):

```scala
div(
  // Make fetch request when this div element is mounted:
  FetchStream.get(url) --> { responseText => doSomething },
  // Make fetch request on every click:
  onClick.flatMap(_ => FetchStream.get(url)) --> { responseText => doSomething },
  // Same, but also get the click event:
  onClick.flatMap(ev => FetchStream.get(url).map((ev, _))) --> {
    case (ev, responseText) => doSomething
  }
)
```

`FetchStream` also supports raw (non-text) responses and codecs – see Airstream docs for their usage.

[AJAX](https://developer.mozilla.org/en-US/docs/Web/Guide/AJAX) (XmlHttpResponse) is an older API for making network requests from the browser. One good reason to use AJAX over Fetch is when you need to track the progress of file uploads – the browsers' Fetch API does not yet provide this functionality. In Laminar you can use AJAX via Airstream's [AjaxStream](https://github.com/raquo/Airstream/#ajax):

```scala
div(
  AjaxStream.get("/api/kittens") --> { xhr =>
    dom.console.log(xhr.responseText)
  }
)
```

[Websockets](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API) is a browser API that lets you set up a bidirectional communication session between frontend and backend. For example, you would almost certainly use WebSockets to implement a realtime chat app. Airstream itself [does not yet have](https://github.com/raquo/Airstream/issues/49) a canonical websockets implementation, but [Laminext](https://laminext.dev/websocket) does offer a Laminar API for websockets.


### Network Requests Security

Please note that browsers have several security mechanisms that dictate whether you're allowed to make certain network requests, and whether you're allowed to see the response content. For security reasons, when your request is blocked, your code often gets only a generic error message with no details (e.g. just "NetworkError when attempting to fetch resource"). This is most typical for cross-origin (cross-domain) requests that violate [CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS). To solve the problem, you first need to understand CORS, and you will likely need your backend server code to specify a safe CORS policy. The browser dev tools (the network tab and the console) will help too, of course.

Your requests can also be blocked by [CSP](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP), although for that to be an issue, you need to configure the CSP restrictions on your own backend server, it's usually permissive by default.

Adblockers can also block network requests, image / CSS downloads, and hide DOM elements. You may want to add a popular adblocker extension like uBlock Origin to your dev browser, to avoid surprises in production.


### Scala HTTP Libraries

All Scala HTTP libraries that are published for Scala.js use one of the above browser APIs to make network requests when compiled for Scala.js. You don't _need_ to use them to make network requests. If you do want to use those libraries for any reason, you will probably want to convert the `scala.Future`-s that they produce to observables (see Airstream docs for this) to integrate them smoothly into Laminar. Keep in mind that libraries that were designed for the JVM first can significantly increase JS bundle size if they use either heavy Java types or functional effect libraries. You can check the contribution of various classes and packages to your JS bundle size using npm package [source-map-explorer](https://www.npmjs.com/package/source-map-explorer).


### Codecs and Type-Safe Requests

Network requests are data-heavy touch points between your code and the outside world. Request data is typically encoded in URL params (for GET requests) and / or as JSON in the request body (for POST requests), and response data is typically encoded as a JSON string. (Of course, you can use MessagePack or any other encoding instead of JSON). Typically, you would use a JSON library like [jsoniter-scala](https://github.com/plokhotnyuk/jsoniter-scala) or [uPickle](https://github.com/com-lihaoyi/upickle) to derive codecs for Scala case classes or GADTs representing requests and responses, and with that, your requests and responses are already pretty type-safe.

However, two important components of safety are missing still:

1. Matching request params / request type to the response type, for a given endpoint
2. Matching of endpoint URLs and requests / response types in the frontend codebase to those in the backend codebase

You can solve both or those in a DIY manner (often my preference), for example by having a single RPC-style endpoint URL, and defining GADTs for request and response types, however you can also use one of the libraries designed for this purpose, such as [tapir](https://tapir.softwaremill.com/en/latest/) or [endpoints4s](https://endpoints4s.github.io/).


### GraphQL

You can use [Caliban](https://ghostdogpr.github.io/caliban/docs/client.html) as a type-safe [GraphQL](https://graphql.org/) client – they have a [Laminar integration](https://ghostdogpr.github.io/caliban/docs/laminext.html) that uses Laminext's WebSockets implementation mentioned above.



## URL Routing

[Waypoint](https://github.com/raquo/Waypoint) – My URL router for Laminar.

[frountroute](https://github.com/tulz-app/frontroute) – Alternative router for Laminar with API inspired by Akka HTTP

These routers are designed for Laminar, but don't actually depend on it, only on Airstream, so you could potentially use them without Laminar.



## Anti-patterns

Some day I'll have enough time to write a more detailed cookbook with Laminar component patterns and examples (you can [expedite this](https://github.com/sponsors/raquo)), but until then, I will at least list some things that you really shouldn't do.


### flatMap All The Things!

When programming with Scala collections or certain functional libraries, you might be used to using the flatMap operator liberally, often using Scala's [for-comprehensions](https://docs.scala-lang.org/tour/for-comprehensions.html) for syntactic convenience.

`flatMap` is the obvious tool to reach for when you want to combine `Option[A]` with `Option[B]` to get `Option[(A, B)]`, however that is only the case due to the simplicity of static collections. The algorithm to combine two optional values is obvious, and flatMap being a suitable implementation is also obvious.

**However, `flatMap`-ing Airstream observables merely to combine their values is a very bad idea**. Take this snippet, for example:

```scala
val intSignal: Signal[Int] = ???
val boolSignal: Signal[Boolean] = ???
val resultSignal: Signal[(Int, Boolean)] = (
  for {
    int <- intSignal
    bool <- boolSignal
  } yield (int, bool)
)
```

You might think that `resultSignal` will combine the two observables as desired, after all, it has the correct type. However, the type of observables only indicates their content, **it says nothing of the timing of their events**.

To understand the timing of resultSignal, let's desugar its definition first:

```scala
val resultSignal: Signal[(Int, Boolean)] = (
  intSignal.flatMap { int =>
    boolSignal.map(bool => (int, bool))
  }
)
```

Now, we have a better idea of what's really going on: instead of combining two observables **as equals**, we wait for `intSignal` to update before creating a new observable that actually combines its last emitted value (`int`) with the current value of `bool`. There is an asymmetry here: every time `intSignal` emits, this inner observable is torn down and replaced with a new copy, but every time `boolSignal` emits, nothing like that happens.

In some streaming libraries, this asymmetry might be an implementation detail, invisible to the user. Sounds nice, but such libraries must inevitably suffer from [FRP glitches](https://github.com/raquo/Airstream/#frp-glitches). `flatMap` is an operator that allows you to create a loop of observables (i.e. an observable that depends on itself), so to prevent unpredictable FRP glitches, in Airstream it emits every event in a new [Transaction](https://github.com/raquo/Airstream/#transactions).

Technically, transaction boundaries like this can cause glitch-like behaviour. For example, our `flatMap` code above would have a glitch if it was plugged into the canonical [double-diamond example](https://github.com/raquo/Airstream/#other-libraries). However, in practice, **this glitch-like behaviour only happens if you abuse transaction-creating methods, using them where they are not required.**

As Airstream documentation explains, avoiding the glitch in this situation is trivial – just use the correct operator, `combineWith` in this case. It is even easier to do than messing with `flatMap`-s.

```scala
val resultSignal: Signal[(Int, Boolean)] = intSignal.combineWith(boolSignal)
```

How do you know whether you **need** `flatMap` or not? Follow the principle of least power. flatMaps lets you create a new observable on every incoming event. Do you **need** this, semantically? No? Then there must be an operator that you can use instead: `combineWith`, `withCurrentValueOf`, `sample`, `filterWith`, `map`, `collect`, `split`, `distinct` etc. 

Here's one example when you legitimately can't avoid firing events in a new transaction:

```scala
val responseStream = stream.flatMap(request => AjaxStream.get(request.url))
```

Do you need `request` to get a `response`? Yes. Can you get `response` from `request` synchronously, without creating a new Observable / future / etc.? No. Therefore, you need `flatMap` (or another transaction-creating equivalent like `EventBus.emit`). Will this cause FRP glitches? Absolutely not.

Bottom line:
1) Make sure you understand which operators and methods create a new transaction, and why it is needed.
2) Do not abuse transaction-creating methods like `flatMap`, `Var.set`, and `EventBus.emit` to achieve outcomes that do not require transaction boundary.
3) Feel free to use these methods when they are actually needed, as this will not cause glitches.


### Creating Elements Instead of Updating Them

Don't automatically reach for `child <-- stream`, look for more efficient solutions. Consider this code:

```scala
val userSignal: Signal[User] = ???
div(
  child <-- userSignal.map(user => span(color := user.prefColor, user.name))
)
```

This is inefficient. You are creating a new `span` element (a real one, we don't do virtual elements in Laminar) every time `userStream` emits, and replacing the previous element you created with it.

As you see from the code, the `span` tag always stays the same, so just keep it static, and update its innards instead:

```scala
div(
  span(
    color <-- userSignal.map(_.prefColor),
    child.text <-- userSignal.map(_.name)
  )
)
```

Now, you only have one `span` element, and you are updating its `color` CSS property and text contents whenever `userSignal` emits.

This is good practice not just for performance, but for user experience: in the original `child <-- ...` code snippet above, your span element would lose all its DOM state whenever `userSignal` updated. If the user has selected its text in the browser, the selection would disappear. If the span element had `<input>` or `<button>` elements in it, they would be re-created as well, and their DOM state would be lost too – all inputted text would be lost, and the focus, if any, would be lost.

Getting rid of `child <-- ...` also simplified the structure of your application: if you want to add any other components to that element, you no longer need to worry about those components getting unmounted and losing their internal state whenever `userSignal` emits.

That said, if these concerns are not relevant to your use case, feel free to use `child <-- ...`. For example, if `userSignal` emits very rarely, e.g. only when the logged-in user changes their preferences, it is probably perfectly fine to use `child <-- userSignal.map(...)` to re-render the whole component displaying their avatar and name.

Note that we only showed a very simple case here. Laminar and Airstream have lots of features to help with efficiently rendering all kinds of observables and data structures – see the docs of both libraries.

Bottom line:
1. If you're used to virtual DOM, lose the habit of creating virtual elements whenever, because all elements in Laminar are real.
2. Prefer `attr <-- source` and `child.text <-- source` to `child <-- source.map(s => div(/*...*/))`


### Redundant Vars

The graph of observables is the core of your Laminar application. If you design that graph poorly, your code will suffer from excessive complexity, state-syncing boilerplate, and even FRP glitches.

While EventBus-es are typically the sources of actions / events, Airstream Var-s are typically the sources of state in your application. It is very important to put state in the right places (think child vs parent component), and it is very important to avoid creating redundant state.

If you are a backend developer, think of all the state in your Var-s as data in the database. RDBMS performance issues aside, you want to have a [normalized database](https://stackoverflow.com/questions/246701/what-is-normalisation-or-normalization) to reduce duplication of data, because if you have any duplication in your database, you need some way to keep the duplicated data in sync, and that is extra complexity, extra code, extra bugs.

Similarly, when it comes to Airstream Var-s, you don't want to store redundant data in them, because then you will need to manually sync it together. If you have two Var-s updating each other, chances are that they should be combined into just one Var, or at least the mutually-dependent part of their data should be in one Var.

You can create [derived vars](https://github.com/raquo/Airstream/#derived-vars) using the `zoom` method, but remember that you can also map over the Var's `signal`, and transform the Var's `writer` separately, you don't need to have these transformations linked.

For example, this unnecessarily complicated component is supposed to show the user's name from `userVar`, and update `userVar` when the user types a new name:

```scala
def nameInput(userVar: Var[User]): HtmlElement = {
  val nameVar = Var(userVar.now())
  input(
    value <-- nameVar,
    onInput.mapToValue --> nameVar,
    nameVar.signal --> { newName => 
      userVar.update(user => user.copy(name = newName)) 
    }
  )
}
```

It doesn't even work well, because if `userVar` updates after initializing this component, the displayed name will not update. And if you try to fix that, you will run into an infinite loop of two Vars updating each other. You could break that loop with a strategically placed `.distinct` filter, but instead of fighting with redundant state using such manual syncing, consider a much simpler solution:

```scala
def nameInput(userVar: Var[User]): HtmlElement = {
  input(
    value <-- userVar.signal.map(_.name),
    onInput.mapToValue --> { newName =>
      userVar.update(user => user.copy(name = newName)) 
    }
  )
}
```

We eliminated the redundant local state, and replaced it with a simple map over `userVar.signal`.

Bottom line: Eliminate redundancy from Var-s, express state dependencies via signals / observers / derived vars instead.


### Observables of Modifiers

I get asked relatively often whether it's possible to use an `Observable[Modifier]`. The intent is to get something like this to work:

```scala
div(
  userSignal.map { user => 
    if (user.isActive) {
      cls := "active" 
    } else {
      opacity := 0.5 
    }
  }
)
```

This kind of pattern does not work, because Laminar's DOM updates are granular down to the property, so you need to pipe observables into individual properties, like this:

```scala
div(
  cls("active") <-- userSignal.map(_.isActive),
  opacity <-- userSignal.map(if (_.isActive) 1 else 0.5)
)
```

Now, for educational purposes only (do not try this at home!), this will compile:

```scala
// NO!!!
div(
  inContext { thisNode =>
    userSignal --> { user =>
      if (user.isActive) {
        thisNode.amend(cls := "active", opacity := 1)
      } else {
        thisNode.amend(opacity := 0.5)
      }
    }
  }
)
```

However, that will not work the way you want. For example, the absence of `cls := "active"` in the `else` branch will not remove the "active" class name, nor is there any other way to remove that class name with Laminar APIs after you set it this way, because [cls](#cls) is a composite attribute with special semantics optimized for different usage patterns. Basically, this weird pattern only happens to work for modifiers that are both idempotent and undoable. Anything other than trivial `KeySetter`-s like `attr := value` will break.

But what if `user.isActive` call is expensive, or you just want to keep related values in one place? You can do something like this:

```scala
val clsWithOpacitySignal = userSignal.map(if (_.isActive) ("active", 1) else ("", 0.5))
div(
  cls <-- clsWithOpacitySignal.map(_._1),
  opacity <-- clsWithOpacitySignal.map(_._2)
)
```

Bottom line: `Observable[Modifier]` is heresy. Pipe observable updates to individual props instead.


### Creating Redundant Observables

In Airstream, observables have shared execution, that is, the code in every observable is executed only once per incoming event, no matter how many listeners the observable has. Therefore, it is more efficient to share an observable than to create multiple copies of it.

For example, consider this snippet, rendering a document as a list of interactive input elements:

```scala
val renderConfigSignal: Signal[RenderConfig] = ???
val blocks = Signal[List[DocumentBlock]]
div(
  display.flex,
  flexDirection.column,
  children <-- blocks.split(_.blockId)((blockId, _, blockSignal) => {
    input(
      value <-- (
        blockSignal
          .combineWith(renderConfigSignal.map(_.computeStyle))
          .mapN((block, style) => block.formatWith(style))
      ),
      onInput --> ???
    )
  })
)
```

If we have 1000 of those input elements, we are creating 1000 copies of `renderConfigSignal.map(_.computeStyle)`, so when renderConfigSignal updates, we would call `computeStyle` on the same config instance 1000 times. That computation does not require any block-specific information, so we can instead create a shared instance of this observable. You can think of observables as function calls – you wouldn't call the same function 1000 times with the same parameters, you would just do it once, and save the value in a `val`. Same principle here.

Additionally, there is another small optimization available in this code snippet: `mapN` creates a new observable just to map over the combined result, but Airstream actually has a `combineWithFn` operator that does both `combineWith` and `map` in one observable. It's more compact and saves the overhead of an extra observable.

Improved version:

```scala
val renderConfigSignal: Signal[RenderConfig] = ???
val blocks = Signal[List[DocumentBlock]]
val renderStyleSignal = renderConfigSignal.map(_.computeStyle) 
div(
  display.flex,
  flexDirection.column,
  children <-- blocks.split(_.blockId)((blockId, _, blockSignal) => {
    input(
      value <-- blockSignal.combineWithFn(renderStyleSignal)(_ formatWith _),
      onInput --> ???
    )
  })
)
```

Bottom line: observables can be shared just like the results of a function call, to eliminate redundant computation, and to reduce the number of observable instances.



## Browser Compatibility

Laminar supports all modern browsers as well as Internet Explorer 11, with minor caveats (listed below).

However, I do not guarantee continued support for Internet Explorer. If your company needs reliable IE support, please subscribe as a [GOLD sponsor](https://github.com/sponsors/raquo) and let me know that you need this.

Note that Laminar, Airstream, and scala-js-dom provide access to browser APIs that Internet Explorer might not support – for example, just because we defined a typed interface to use a certain CSS property, it does not mean that IE supports said property.

For IE, you will need to shim / polyfill some browser APIs to make use of certain Laminar features:

* [Promise](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise), if you want to use Airstream's `scala.Future` integrations (e.g. `EventStream.fromFuture`)
* [Fetch](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API), if you want to use FetchStream (alternatively, you can use AjaxStream without any polyfill)

Also, the current implementation of `DomApi.unsafeParseHtmlString` method does not support IE. See its scaladoc for an alternative.



## Special Cases

Laminar is conceptually a simple layer that brings a lightweight reactive API to native Javascript DOM. In general there is no magic to it, what goes in goes out, transformed in some obvious way. However, in a few cases we do some ugly things under the hood so that you don't need to pull your hair and still do said ugly things in your own code.

Please let me know via github issues if any of this magic caused you grief. It's supposed to be almost universally helpful.


### tbody

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



## Troubleshooting

Hiccups are inevitable in development, especially when you're trying a new paradigm (observables and lack of virtual DOM for a React.js developer, or the whole concept of frontend development for a backend developer). Here is some assorted advice for dealing with Laminar issues, in no particular order.

* Understand the separation of concerns between the browser runtime, Scala.js, Laminar, Airstream, Scala DOM Types. For many issues, you just need to understand how to do something on the frontend in plain HTML / JS / CSS, and then it will become obvious how to do it in Scala.js / Laminar. This is actually great news, because there are a ton of resources about plain HTML / JS / CSS online. They are very helpful to us, because Scala.js exposes all the JS APIs almost verbatim, and Laminar itself is a relatively thin layer for manipulating the [DOM](https://developer.mozilla.org/en-US/docs/Web/API/Document_Object_Model/Introduction).

* Especially if you're completely new to frontend development, make sure to manage the scope of uncertainty. Start small, and don't let the sheer scope of things-to-learn demotivate you (reduce that scope / pace your ambitions as necessary). That said, aim to understand all the foundational concepts _eventually_. Once you do, frontend development is pretty easy.

* Review Laminar documentation, including [Airstream docs](https://github.com/raquo/airstream/) and [Scala DOM Types](https://github.com/raquo/scala-dom-types). See the table of contents, and Ctrl+F. Documentation contains both

* Review the content and examples linked on the [resources](https://laminar.dev/resources).

* Search [discord chat](https://discord.gg/JTrUxhq7sj) and [Github issues](https://github.com/search?q=owner%3Araquo+signal+current+value&type=issues) for Laminar, Airstream, and Scala DOM Types, as applicable.

* Don't be afraid to look at Laminar code if needed. It has some complicated areas (e.g. the `children <--` algorithm), but mostly it's very reasonable, with few implicits, no macros, no functional effects, no category theory.

* Use [scribble.ninja](https://scribble.ninja/) to test things out in a clean(ish) context. If stuff works here, but not in your own codebase, now you have a lead, try to reconcile the difference.

* Use the browser dev tools. Always have the dev console open during development, because both Airstream, other libraries, and the browser itself dumps certain types of errors in there.

* Observable/Task/IO-style systems tend to produce complicated stack traces in case of exceptions. Use Airstream's [debugging functionality](https://github.com/raquo/Airstream/#debugging) to diagnose errors that happen within the observable graph.

* Speaking of exceptions, understand Airstream's [error handling](https://github.com/raquo/Airstream/#error-handling), as it is different from other streaming/task/IO libraries.

* Ask for advice on [Discord](https://discord.gg/JTrUxhq7sj). Be specific, show your code, or your desired pseudocode. Try to minimize. Check and report any errors in the console. The act of writing a good question, properly describing the problem, can actually lead you to the solution.



## Fin

Hey, you've read all of this? Amazing. Join us in [Discord](https://discord.gg/JTrUxhq7sj)!
