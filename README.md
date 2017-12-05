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

### Stream Memory Management

Streams are not garbage collected as long as they have listeners. So, if you call `myStream.addListener` or `myStream.subscribe`, you need to also call `myStream.removeListener` or `myStream.unsubscribe` respectively when you are done using `myStream` and would like it to be garbage collected (normal JS GC rules apply as well, of course).

Some subscriptions you might want to exist for the whole lifetime of your app, so you don't need to do anything about them, but most of the subscriptions in your app at some point should be discarded.

For example, when _Laminar_ subscribes to a stream internally, e.g. via `color <-- myColorStream` or `myNode <-- color <-- myColorStream`, that subscription should only exist for as long as the relevant DOM node is used. Once the node is discarded, we can also discard the subscription. Laminar handles this automatically (read more on this below).

However, you're responsible for cleaning up subscriptions that you create manually. Currently you can manually tie the lifecycle of a subscription to the lifecycle of a particular element using `ReactiveElement.subscribe` or `ReactiveElement.subscribeBus`. Future versions of _Laminar_ will provide a way to tie subscriptions to a lifecycle context automatically.


### Node Lifecycle Events

_Laminar_ nodes that are elements expose streams of lifecycle events that can be useful for integrating third party DOM libraries and other tasks. This events are similar in spirit to React.js lifecycle hooks like `componentDidMount` or `componentWillUnmount`, but are implemented very differently.

All lifecycle events are fired synchronously, with no async delay.

#### Parent change events

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
