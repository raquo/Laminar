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

### Event System: Emitters, Transformations, Buses 

#### Registering an DOM Event Listener

To start listening to DOM events, you need to register a listener for a specific event type (`EventProp`) on a specific DOM Element (`ReactiveElement`). `EventPropEmitter` is a `Modifier` that performs this action. 

This is how it's done in the simplest case:

```scala
val clickBus = new EventBus[dom.MouseEvent]
val $click: XStream[dom.MouseEvent] = clickBus.$ // resulting event stream that you can access any time  
val element: ReactiveElement[dom.html.Div] = div(onClick --> clickBus, "Click me")
```

What this does line-by-line:

1. Create an `EventBus` – an object that accepts any kind of event, and forwards it to the stream that it exposes (see next line). EventBus simply encapsulates a an XStream `Producer` that fires events when its sendNext method is called. However, we hide both the producer and the sendNext method from you, the end user, to prevent mistakes if you decided to manually manage those. 

2. `$click` is simply the stream of events received (and therefore produced) by the `clickBus`. You can use it immediately after the event bus is created, no need to wait until the eventBus is attached to the element (see next line). You don't actually need this line for event registration to work. But you will want to read the events you capture at some point, and I'm just showing how that stream is exposed.

3. Last line creates a div element with a text node, and registers an event listener on it that listens to click events and forwards them to the clickBus. 
 
 `onClick --> clickBus` returns an `EventPropEmitter` which is a `Modifier`. Modifiers are applied to elements immediately after the element is created, and in the order in which they were defined on the element. An `EventPropEmitter` carries no element-specific state in its instance, so it can be reused on multiple elements if needed.
 
 Two conditions need to be met for `$click` to start firing events: 1) It needs to have at least one listener (standard for XStream streams), and 2) the `EventPropEmitter` that is built out of `clickBus` needs to be applied to some element.
 
 The implicit third condition is that the JS DOM actually produces click events on the div in question, so (without going into DOM event simulation) the div needs to be mounted and the user needs to click on it.
 
 Currently the event instances that you get in the output stream are native JS events. There are no magic synthetic events, no event pooling, nothing like that. There is one exception for certain `onClick` events on checkboxes, see "Special Cases" at the bottom of this document.

#### Alternative Event Listener Registration Syntax

Imagine you're building `TextInput`, a component that wraps an `input` element into a `div` with some styles added on top:

```scala
class TextInput private (
  val wrapperNode: ReactiveElement[dom.html.Div],
  val inputNode: ReactiveElement[dom.html.Input]
)
 
object TextInput {
  def apply(caption: String): TextInput = {
    val inputNode = input(typ := "text", color := "grey")
    val wrapperNode = div(caption, inputNode)
    new TextInput(wrapperNode, inputNode)
  }
}
```

This generic component does not necessarily know what events on `inputNode` the developer will care about when using it – `onKeyUp`, `onKeyPress`, `onChange`, `onInput`, others? There's just too many possibilities. And of course you wouldn't want to clutter your `TextInput`'s API by e.g. exposing all possibly useful event streams as vals on the `TextInput` class.

That's what `ReactiveElement.$event` method was made for:

```scala
val textInput = TextInput("Full name:")
val $changeEvent: XStream[dom.Event] = textInput.inputNode.$event(onChange)
```

Under the hood, `element.$event(eventProp)` creates an `eventBus`, applies an `eventProp --> eventBus` modifier to `element`, and returns `eventBus.$`. That's all there is to it, no magic – just alternative syntax that makes it easier to compose your components without tight coupling.

#### Multiple Event Listeners
 
Just like in native JS DOM, nothing is stopping you from registering two or more event listeners for the same event type on the same element:
 
 ```scala
div(onClick --> clickBus1, onClick --> clickBus2, "Click me")
``` 

In this case, two event listeners will be registered on the DOM node, one of them sending events to clickBus1, the other sending the exact same events to clickBus2. Of course, this works the same regardless of what syntax you use to register event listeners (see "Alternative Syntax" section above).

In most cases you could simply add another listener to `clickBus.$` to achieve whatever you needed multiple event listeners bound to the same element for – it's easier and more performant in extreme cases (thousands of nodes, I'd guess).

However, sometimes simpler composition is more important. For example, consider the `TextInput` component mentioned above. If the component itself had an internal need to listen to its own `onChange` events (e.g. for some built-in validation), that would be a case when adding a second `onChange` event listener to `inputNode` would make sense (the first one was added externally by end user of the component, as shown in the `TextInput` code snippet in the section above).

#### Event Transformations

Often times you don't really need a stream of e.g. click events – you know well in advance what each click event means, or which of the events you care about, etc. With the Alternative Syntax described above you would just use XStream operators to transform the stream of events, like this:

```scala
val incrementButton = button("+1")
val decrementButton = button("-1")
 
val $diff: XStream[Int] = XStream.merge(
  incrementButton.$event(onClick).mapTo(1), 
  decrementButton.$event(onClick).mapTo(-1) 
) // this stream emits +1 or -1
```

However, when using the standard `onClick --> eventBus` syntax, there is no stream that you could operate on before the events hit `eventBus`. Instead, we provide a different way to transform events:

First, you need to create an instance of `EventPropTransformation` by calling `apply` on your `EventProp` (via an implicit conversion to `EventPropOps`), e.g. `onClick()`. Then you can call a bunch of transformation methods on the resulting object like `mapTo` or `filter` which would return new instances of `EventPropTransformation`. Lastly, you call the `-->` method as before. So the example above would translate into:

```scala
val diffBus = new EventBus[Int]
val incrementButton = button("+1", onClick().mapTo(1) --> diffBus)
val decrementButton = button("-1", onClick().mapTo(-1) --> diffBus)
val $diff: XStream[Int] = diffBus.$ // this stream emits +1 or -1
```

More syntax examples:

```scala
div("Click me", onClick().map(getClickCoordinates) --> clickCoordinatesBus)
 
div(onScroll().filter(throttle) --> filteredScrollEventBus)
 
div(onClick(useCapture = true) --> captureModeClickBus)
 
input(onKeyUp().filter(_.keyCode == KeyCode.Enter).preventDefault --> enterPressBus)
 
div(onClick().collect { case ev if ev.clientX > 100 => "yes" } --> yesStringBus)
 
// TODO[Docs] Come up with more relatable examples
```

`EventPropTransformation` instances are immutable and contain no element-specific state, so you can reuse them freely across multiple elements.

##### preventDefault & stopPropagation

These methods correspond to invocations of the corresponding native JS `dom.Event` methods. MDN docs: [preventDefault](https://developer.mozilla.org/en-US/docs/Web/API/Event/preventDefault), [stopPropagation](https://developer.mozilla.org/en-US/docs/Web/API/Event/stopPropagation)

Importantly, these are just ordinarily transformations, and happen in the order in which you have chained them. For example, in the code snippet above `ev.preventDefault` will only be called on events that pass `filter(_.keyCode == KeyCode.Enter)`. Internally all transformations have access to both the latest processed value, and the original event, so it's fine to call the `.preventDefault` transformation even after you've used `.map(_.keyCode)` for example.

##### useCapture

JS DOM has two event modes: capture, and bubbling. Typically and by default we use the latter, but capture mode is sometimes useful for event listener priority/ordering (not specific to Laminar, standard JS DOM rules/limitations apply).

You need to specify whether to use capture mode the moment you register an event listener on the element, so it's passed as a parameter to `onClick(useCapture = true)` instead of being a method on `EventPropTransformation`.

See MDN [addEventListener](https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener) page for details ("useCapture" section).

##### Obtaining Typed Event Target

Due to dynamic nature of Javascript, `dom.Event.target` is typed only as `dom.EventTarget` for most events, which is not useful when you want to get `ev.target.value` from a target that is a `dom.html.Input` (but doesn't know it). So, you can't do this:

```scala
// Does not work because .value is defined on dom.html.Input, but not on dom.EventTarget :(
input(typ := "text", onChange().map(_.target.value) --> inputStringBus)
```

Easiest hackiest solution would be to use `.map(_.target.asInstanceOf[dom.html.Input].value)` but you should reconsider using Scala if you aren't cringing looking at this.

You could use our Alternative Syntax for registering events (see section above) for a somewhat safer solution:

```scala
val inputNode = input(typ := "text")
val $inputString = inputNode.$event(onChange).map(_ => inputNode.ref.value)
```

However, this is often cumbersome, and introduces the risk of referencing the wrong input node of the same type. We have a better way to get a properly typed target node, using a transformation:

```scala
input(onChange().mapToThisNode.map(_.ref.value) --> inputStringBus)
```

Or if you for example need to both filter events by .value and then grab the event.

```scala
input(onChange().mapToThisNode.filter(_.ref.value == "").mapToEvent --> filteredEventBus)
```

Lastly, if you need access to both the current node and the event, you can use the `.zipWithThisNode` method that gives you `(event, thisNode)` tuples.

Under the hood this works similar to `preventDefault` and `stopPropagation` (see above), relying on the transformation having access to both the original event and the processed value. And since all this eventually ends up as part of an `EventPropEmitter`, it also has access to the element to which we apply this `Modifier`.  

Note: "thisNode" in the method names refers to the element on which the event listener is **registered**. In JS DOM terms, this is `dom.Event.currentTarget`, not `dom.Event.target` – the latter refers to the node at which the event **originates**. When dealing with inputs these two targets are usually the same since inputs don't have any child elements, but you need to be aware of this conceptual difference for other events. MDN docs: [target](https://developer.mozilla.org/en-US/docs/Web/API/Event/target), [currentTarget](https://developer.mozilla.org/en-US/docs/Web/API/Event/currentTarget).

You might have noticed that some `EventProp`s like `onClick` promise somewhat peculiar event types like `TypedTargetMouseEvent` instead of the expected `MouseEvent` – these refined types come from _Scala DOM Types_, and merely provide more specific types for `.target` (as much as is reasonably possible). These types are optional – if you don't care about `.target`, you can just treat such events as simple `MouseEvent`s because `TypedTargetMouseEvent` do in fact extend `MouseEvent`.

#### Reusing an Event Bus
 
What if you want to render a few elements, and combine all of their `onClick` events into a single event bus? Just add a `onClick --> clickBus` modifier to all of them in whatever way is most convenient.
 
 The event bus itself is not tied to any element by any means, the `-->` method simply provides a way for an element to pass events to an event bus. If multiple elements are doing that, well, then your event bus is receiving events from all those elements.

#### MergeBus

`EventBus` is great when your event source comes directly from _Laminar_'s own `EvenPropEmitter`, as shown above. But this Subject/Proxy-like concept has one more useful application, for which we have a special subclass: `MergeBus`. 

`XStream.merge(stream2, stream2, ...)` can merge a fixed set of streams into one stream that re-emits all of the events that are fired on each of the input streams. This is useful for simple cases when you know which streams you need to merge in advance, like in the code snippet with increment/decrement buttons in the "Event Transformations" section above.

`MergeBus` acts very much like `XStream.merge`, except it lets you add and remove source streams dynamically. This is invaluable to avoid complicated and often inefficient data structures like streams-of-lists-of-streams when dealing with changing lists of things.

For example, if you're rendering a list of child components each of which exposes a stream of events, and you want to get a stream that merges all those streams into one stream, you would create a `MergeBus` in the parent component, and whenever you create an instance of a child component you call `childNode.subscribeBus(childStream, mergeBus)`. What this does is calls `mergeBus.addSource(childStream)` under the hood, making sure to call `mergeBus.removeSource(childStream)` when `childNode` is discarded.

That way as you create and destroy child components, your `mergeBus` continues to receive events from only the currently relevant (in this case, mounted) child components, discarding unused streams with no memory leaks.

Note that `MergeBus` behaves more like a complex operator than an XStream `Listener` in terms of laziness and memory management. Under the hood it adds a listener to every source stream if/when its output stream (.$) acquires its first listener, and removes the listener from every source stream when its output stream loses its last listener. So if the output stream has no listeners, the input streams will not get a listener either.

When passing down a `MergeBus` to child components, you're exposing its output stream to the children, which is usually undesired because you don't want to give child components access to read all the events sent into `MergeBus` by other child components, you only want to give them write access into that bus. It is recommended to upcast your `MergeBus` to `WriteMergeBus` (e.g. using `MergeBus.asWriteBus`). `WriteMergeBus` does not expose an output stream.

TODO[Docs]: Too many words. Provide or link to concrete examples that I developed.

##### MergeBus Transformations

To reduce boilerplate and simplify composition, we provide a few methods that let you create new `MergeBus`-es from an existing `MergeBus`. For example:

```scala
val requestBus = new MergeBus[AJAXRequest]
val modelDiffToRequest: ModelDiff => AJAXRequest
val modelDiffBus: MergeBus[ModelDiff] = requestBus.map(modelDiffToRequest)
```

Now, all events that are outputted by `modelDiffBus.$` will be forwarded into `requestBus` after being processed by `modelDiffToRequest`, so you can pass `modelDiffBus` down to a child component that knows how to output model diffs into a bus, but doesn't / shouldn't know how to convert them into AJAX requests.

There's also a `compose` method for more complicated transformations. We might add `filter` and `collect` in the future.


### Stream Memory Management

Streams are not garbage collected as long as they have listeners. So, if you call `myStream.addListener` or `myStream.subscribe`, you need to also call `myStream.removeListener` or `myStream.unsubscribe` respectively when you are done using `myStream` and would like it to be garbage collected (normal JS GC rules apply as well, of course).

Some subscriptions you might want to exist for the whole lifetime of your app, so you don't need to do anything about them, but most of the subscriptions in your app at some point should be discarded.

For example, when _Laminar_ subscribes to a stream internally, e.g. via `color <-- myColorStream` or `myNode <-- color <-- myColorStream`, that subscription should only exist for as long as the relevant DOM node is used. Once the node is discarded, we can also discard the subscription. Laminar handles this automatically (read more on this below).

However, you're responsible for cleaning up subscriptions that you create manually. Currently you can manually tie the lifecycle of a subscription to the lifecycle of a particular element using `ReactiveElement.subscribe` or `ReactiveElement.subscribeBus`. Future versions of _Laminar_ will provide a way to tie subscriptions to a lifecycle context automatically.


### Node Lifecycle Events

_Laminar_ nodes that are elements expose streams of lifecycle events that can be useful for integrating third party DOM libraries and other tasks. This events are similar in spirit to React.js lifecycle hooks like `componentDidMount` or `componentWillUnmount`, but are implemented very differently.

All lifecycle events are fired synchronously, with no async delay.

#### Parent Change Events

##### `$parentChange: XStream[ParentChangeEvent]`

This stream fires any time when this element's direct parent changes. Actually, two events are fired at that time: immediately before the change is applied to both the real DOM and Laminar's DOM tree, and immediately after that. In addition to the `alreadyChanged` flag, these events carry references to `maybePrevParent` and `maybeNextParent`, letting you know exactly what happened.

This stream only fires when direct parent of this element is changed. It does not track changes in the parent's parent and higher up ancestors. This stream fires regardless of whether this element is mounted or not (see definition below).

#### Mount Events

An element is considered mounted if it is present in the DOM, i.e. if its real DOM node is a descendant of `org.scalajs.dom.document`. All elements that are not mounted are considered unmounted.

_Laminar_ has three kinds of mount events:

**`NodeDidMount`** – fired when the element **becomes mounted ** (i.e. it was previously unmounted). Specifically, this happens immediately after the corresponding parent change event with `alreadyChanged=true` (see above) is fired. At this point, the element is **already** present in the DOM.

 When an element is mounted, its subscriptions are activated. That means that emitters like `onClick --> myClickBus` and `element.$events(onClick)` will actually start listening for and relay DOM events to your listeners, and receivers like `href <-- $url` will start listening for your streams. To further clarify: when you create the element the desired subscriptions are only created and recorded in `ReactiveElement.subscriptions`. They only start working once you mount your element into the DOM. 

**`NodeWillUnmount`** – fired when the element **becomes unmounted** (i.e. it was previously mounted). Specifically, this happens immediately after the corresponding parent change event with `alreadyChanged=false` (see above) is fired. At this point, the element is **still** present in the DOM, but will be removed from the DOM immediately after this event.

When an element is unmounted, its subscriptions are deactivated. That is, emitters stop listening for and relaying events, and receivers unsubscribe from input streams and stop updating the element's node. This means that the element will miss all events that happened while it is unmounted, so if you were to mount it after sending some events to it, the state of its DOM node could be stale.

If you want to temporarily "remove" the element from the DOM, but still keep its subscriptions active, you should hide it using CSS `display: none` instead of unmounting it. 

When an element is unmounted, one subscription, `ReactiveElement.mountEventSubscription`, remains active – it listens for mount events, ready to re-activate all the other subscriptions if this element gets re-mounted. However, we don't want this subscription to exist indefinitely, otherwise we would leak memory every time an element is unmounted never to be used again, which brings us to the last type of mount events:

**`NodeWillBeDiscarded`** – fired when the end user has indicated that they will not mount this element ever again. Currently, this is always fired right after the `NodeWillUnmount` event, so currently unmounting an element means that you can't re-mount it again (its subscriptions will not re-activate). Future versions of _Laminar_ will include a way for end users to specify that they intend to re-mount a given element after unmounting it.

#### Order of Lifecycle Events

For extra clarity, lifecycle events triggered by the same underlying parent change event are fired in the following order:

1) `ParentChangeEvent(alreadyChanged=false, maybePrevParent, maybeNextParent)`,
2) `NodeWillUnmount` (if we're unmounting),
3) `NodeWillBeDiscarded` (if we're unmounting),
4) `ParentChangeEvent(alreadyChanged=true, maybePrevParent, maybeNextParent)`,
5) `NodeDidMount` (if mounting)

#### Mount Event Streams

So how do you actually listen for mount events? _Laminar_ exposes the following streams on each `ReactiveElement`:

**`$mountEvent`** – fires a full stream of mount events that affect this node. If the node was mounted or will be unmounted, directly or indirectly, this event will be here. This stream is a simple merge of the two mutually exclusive streams below: 

**`$thisNodeMountEvent`** – fires mount events that were caused by this element changing its parent only. Does not include mount events triggered by changes higher in the hierarchy (grandparent and up).

**`$thisNodeMountEvent`** – fires mount events that were caused by changes in this element's grandparent or any of its parents. Does not include mount events triggered by changes in this elements's parent.

#### Lifecycle Events Performance

Maintaining multiple lifecycle event streams for every single element in the DOM would be needlessly costly, especially given that `$mountEvent` is recursive – a child's `$mountEvent` stream is in part derived from all of its ancestors' `$mountEvent` streams. _Laminar_ has a few optimizations to make this efficient. Here is how it works:

1) All streams are defined as `lazy val`-s, and are not even initialized until you access them. Then when you do, only the required dependencies of those streams are initialized. So when you ask for a `$mountEvent` stream of one element, only then will _Laminar_ initialize `$mountEvent` streams of this child's parents, if they weren't initialized already. 

2) Until the streams are initialized, the underlying event buses don't even receive events. `$parentChange` gets its events from the event bus stored inside `maybeParentChangeBus`, but that `Option` remains `None` until `$parentChange` is accessed. Similarly with `$thisNodeMountEvent` and `maybeThisNodeMountEventBus`. All other lifecycle streams are derived from these two streams using simple transformations.

3) We avoid redundant computations as much as possible. All XStream streams are multicast (shared execution), which works well for this case because elements high up in the DOM hierarchy will typically have many listeners on their `$mountEvent` streams. Also, some of the most expensive calculations like determining whether an element is mounted or not are performed only once per original `ParentChangeEvent` – the same event instance is reused downstream with no additional allocations or DOM access required.

Importantly, you don't need to be accessing `$mountEvent` directly in order to trigger initialization of all the lazy streams. Every element that has any subscriptions (either listens to streams or emits events) will be listening to `$mountEvent` already.

There's more to this system, but for now this will have to do as an MVP summary. All in all, this system should work quite well, and if you run into performance problems on huge DOM trees with many subscriptions, this should give you some understanding of which bottleneck you could be hitting, and how to work around it. 

One way to simplify your code that is _Laminar_-specific is to use `ReactiveElement.isMounted` method instead of subscribing to `$mountEvent`. It is provided mostly as an escape hatch for third party integrations that do not map  well to FRP design patterns, but using it could also potentially improve performance over using `$mountEvent` (but again, don't worry about it if you're not actually hitting a performance problem). 

### Special Cases

_Laminar_ is conceptually a simple layer adding a reactive streaming API to Scala DOM Builder. In general there is no magic to it, what goes in goes out, transformed in some obvious way. However, in a few cases we do some ugly things under the hood so that you don't need to pull your hair and still do said ugly things in your own code.

Please let me know via github issues if any of this magic caused you grief. It's supposed to be almost universally helpful.

##### 1. checkbox.onClick + event.preventDefault() = async event stream

All event streams in _Laminar_ emit events synchronously – as soon as they happen – **except** if the stream in question is a stream of `onClick` events on an `input(typ := "checkbox")` element, and you generated wthis stream using `preventDefault = true` option in Laminar's API.

Such streams fire events after a `setTimeout(0)` instead of firing immediately after the browser triggers the event. Without this hack/magic, you would have been unable to update the `checked` property of this checkbox from a stream that is (synchronously) derived from the given stream of `onClick` events, and this is a common practice when building controlled components.

The underlying issue is described in [this StackOverflow answer](https://stackoverflow.com/a/32710212/2601788).

**Escape hatch:** to bypass this magic, call `ev.preventDefault()` manually on the event object instead of using Laminar's `preventDefault` option.

**Watch out:** If you are reading the `checked` property of the checkbox in an affected stream, it will contain the original, unchanged value. This behaviour could be surprising  if you don't know about this stream being async, but do know about the native DOM behaviour of temporarily updating this value and then resetting it back. This is deemed a smaller problem than the original issue because it's easier to debug, and better matches the commonly-expected semantics of `preventDefault`.


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
