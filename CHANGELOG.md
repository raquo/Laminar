# Changelog

Breaking changes in **bold**.

_You can now [sponsor](https://github.com/sponsors/raquo) Laminar development!_

---

#### v0.10.3 – Sep 2020

* New: Airstream v0.10.1, see [Airstream changelog](https://github.com/raquo/Airstream/blob/master/CHANGELOG.md)
* New: Easily toggle composite attr values
  * e.g. `cls.toggle("class1") := true` and `cls.toggle("class1") <-- $bool`
* New: Easily remove composite attr values: `cls.remove("class1")`
* New: Another `cls.set` method with varargs (same for other composite attrs)
* New: Add pointer events to Laminar (thanks, [@doofin](https://github.com/doofin)!)
* API: Make `separator` field on `CompositeAttr` public 

**News:**

* I published a comprehensive video introduction to Laminar, check it out: [Laminar – Smooth UI Development with Scala.js](https://www.youtube.com/watch?v=L_AHCkl6L-Q) 
* Iurii released initial version of [tulz-app/laminar-router](https://github.com/tulz-app/laminar-router), an alternative Laminar router with API inspired by Akka HTTP.
* Iurii also shared a template for server side rendering any frontend apps (including Laminar) using Puppetteer: [yurique/spa-ssr-proxy](https://github.com/yurique/spa-ssr-proxy)
* Anton is working on a static documentation website for Laminar ([#61](https://github.com/raquo/Laminar/issues/61), [#63](https://github.com/raquo/Laminar/pull/63))
* @uosis started work on auto-generating Laminar facades for Web Components: [uosis/laminar-web-components](https://github.com/uosis/laminar-web-components). We also have a complete example of Web Components in [laminar-examples](https://github.com/raquo/laminar-examples).

—

Laminar & Airstream development is sponsored by [people like you](https://github.com/sponsors/raquo).

GOLD sponsors supporting this release: ✨ **[Iurii Malchenko](https://github.com/yurique)**

Thank you for supporting me! ❤️

---

#### v0.10.2 – Aug 2020

* New: Support custom CSS props (for Web Components)
  * No change to Laminar API, but we're using a different method of setting and unsetting all CSS props under the hood.
* New: Upgrade _Scala DOM Types_ to v0.10.1
  * Adds `slot` attribute (for Web Components)

---

#### v0.10.1 – Aug 2020

* New: `eventProp --> var` alias for `eventProp --> var.writer` 

---

#### v0.10.0 – Aug 2020

* Build: Upgrade Airstream to v0.10.0: [changelog](https://github.com/raquo/Airstream/blob/master/CHANGELOG.md)

---

#### v0.9.2 – Jul 2020

* New: Airstream v0.9.2, fixes throttle and adds a few convenience methods, see [Airstream changelog](https://github.com/raquo/Airstream/blob/master/CHANGELOG.md).
* New: `onMountUnmountCallbackWithState` which is like `onMountUnmountCallback` but can remember user-defined state and provide it to the unmount callback.
* New: `observable --> var` alias for `observable --> var.writer`
* New: `FormElement` type alias

---

#### v0.9.1 – May 2020

* **Fix: Remove one of the conflicting `amend` methods (#54)**
  * This change should be almost always source-compatible so no changes required.
* New: `nbsp` character now available for convenience
* New: `emptyMod` now available (a universal Modifier that does nothing)

**News:**

* Check out [vic/laminar_cycle](https://github.com/vic/laminar_cycle), a small library that lets you build Laminar applications using [Cycle.js dialogue abstraction](https://cycle.js.org/dialogue.html).

---

#### v0.9.0 – April 2020

* **Build: Upgrade _Scala DOM Types_ to v0.10.0: [changelog](https://github.com/raquo/scala-dom-types/blob/master/CHANGELOG.md)**
  * A few naming changes to attributes and tags
  * This also bumps scala-js-dom to v1.0.0
* Build: Upgrade Airstream to v0.9.0: [changelog](https://github.com/raquo/Airstream/blob/master/CHANGELOG.md)
* API: `ReactiveElement.amend()` returns `this` now
* Fix: Incorrect SVG composite attributes types

**News:**

* Check out [yurique/scala-js-laminar-starter.g8](https://github.com/yurique/scala-js-laminar-starter.g8), a new giter8 template that sets up a full stack application with Laminar on the frontend, and uses a plain webpack config instead of scalajs-bundler.

---

#### v0.8.0 – Mar 2020

This release is a significant improvement to both usability and safety of Laminar. We overhauled the lifecycle event system, and reworked how Laminar makes use of Airstream ownership, fixing longstanding design flaws in the process. We also simplified many things e.g. we eliminated the whole `Scala DOM Builder` layer. Below is a comprehensive list of changes with migration tips sprinkled throughout.

* Addons
  * New: [Waypoint](https://github.com/raquo/Waypoint) – efficient URL router for Laminar
* Documentation
  * The entire documentation was of course updated for v0.8.0. For those already familiar with Laminar v0.7 API, the following sections contain new or significantly updated conceptual material:
    * Manual Application
    * Reusing Elements
    * Efficiency
    * Binding Observables
    * Ownership
    * Memory Management
    * Element Lifecycle Hooks
    * See also the Changelog for Airstream v0.8.0, and the new Dynamic Ownership section in Airstream docs.
  * New blog post expanding on the rationale behind Laminar: [My Four Year Quest For Perfect Scala.js UI Development](https://dev.to/raquo/my-four-year-quest-for-perfect-scala-js-ui-development-b9a)
* **New: Ownership & Lifecycle Events overhaul**
  * This is the flagship feature of this release.
  * Ownership
    * Laminar now uses Airstream's new `DynamicOwner` and `DynamicSubscription` features: `ReactiveElement` subscriptions are now activated every time the element is mounted, not when it is first created. This solves a long standing design flaw ([#33](https://github.com/raquo/Laminar/issues/33)).
    * You can now re-mount previously unmounted elements: their subscriptions will be re-created and will work again.
  * Lifecycle Event system
    * Eliminate `mountEvents` and `parentChangeEvents` streams, `maybeParentSignal` signal, `MountEvent` type, and all related machinery
    * New: `onMountCallback` / `onMountBind` / `onMountInsert` / `onUnmountCallback` / `onMountUnmountCallback` modifiers
    * Mount events are now propagated differently down the tree, which has minor timing differences:
      * Order of unmount events changed between child / parent components. If a subtree is being unmounted, the unmount hooks on children will fire before they fire on their parents.
      * Previously mount events had to wait for the current Airstream Transaction to finish when propagating to child nodes (similar to how EventBus always emits events in a new Transaction). This is not the case anymore. In practical terms this is unlikely to affect you, except that it fixes [#47](https://github.com/raquo/Laminar/issues/47).
    * **Migration:** Read the new docs and choose an appropriate onMount* hook instead of the old streams. Don't rush to use `onMountCallback`, check out all of the hooks first.
* **API: Merge `EventPropEmitter` and `EventPropSetter` into `EventPropBinder`)**
  * Unlike the old `EventPropSetter`, `EventPropBinder` adds and removes event listeners on mount, not on instantiation. This should not be a problem as event listeners generally don't fire on detached nodes in the first place. 
  * **Migration:** Replace usages of `EventPropEmitter` with `EventPropBinder`. If you were using `EventPropEmitter` as an `EventPropTransformation`, those methods will not be available anymore as `EventPropBinder` does not extend `EventPropTransformation`, so you will need to rewrite those calls differently, more manually. This usage wasn't explicitly documented, so I hope no one actually runs into this.
* **API: RootNode no longer automatically mounts itself when the provided `containerElement` is itself unmounted.**
  * This improves ease of integration if you want to e.g. render a Laminar element in a React component.
  * **Migration:** this probably does not affect you. If it does, you'll need to call `rootNode.mount()` when appropriate. See the new "What Is Mounting?" section in docs.
* **API: Replace `ReactiveEventProp.config(useCapture: Boolean)` with `EventPropTransformation.useCapture` and `EventPropTransformation.useBubbleMode`**
  * Migration example: `onClick.config(useCapture = true)` --> `onClick.useCapture`
  * Practically this means that you can set `useCapture` anywhere you have an `EventPropTransformation`, not just where you have the original `ReactiveEventProp`
* **API: New modifier subtypes: `Setter`, `Binder` and `Inserter` (old `Setter` renamed to `KeySetter`)**
  * The behaviour is the same except for subscription lifecycle as explained in ownership, but you need to know the distinction between the new types to use the new lifecycle hooks.
* **API: Eliminate dependency on _Scala DOM Builder_**
  * DOM Builder is capable of supporting different DOM backends and even JVM rendering. We have no plans to use either of these in Laminar, so the indirection required by these abstractions is not pulling its weight.
  * `DomApi`
    * Remove the old `DomApi` trait and companion object.
      * Combine `domapi.*Api` traits into a single `DomApi` object
      * Use the new `DomApi` object directly instead of passing implicit `domapi.*Api` parameters.
  * Move `Setter` and `EventPropSetter` into Laminar, simplify type signatures and rename to `KeySetter` and `EventPropBinder`
  * Merge into relevant Laminar subtypes: `Node` -> `ReactiveNode` (add `Ref` type param), `Comment` -> `ReactiveComment`, `Text` -> `TextNode`, `ParentNode` -> `ParentNode`, `ChildNode` -> `ChildNode`, `Root` -> `RootNode`, `TagSyntax` -> `HtmlTag` & `SvgTag`
  * Merge `EventfulNode` trait into `ReactiveElement` (split members between the trait and the companion object)
    * Change type of `maybeEventListeners` to have `List` instead of `mutable.Buffer`, it was never intended to be mutable
  * **Migration:** If you reference any of the affected types directly, you will need to import them from Laminar, or use their corresponding Laminar replacements listed above. Other than that, everything should just work.
* **API: Limit access / hide / obscure**
  * Hide `willSetParent` and `setParent` methods. Use Laminar's `ParentNode.appendChild(parent, childNode)` or similar.
  * Move `appendChild` and other similar methods from ParentNode instance to companion object.
    * Those low-level methods are still available to you as a user, but generally you should avoid them in favor of a reactive approach (various `<--` and `-->` methods).
  * ReactiveElement.maybeChildren is not mutable anymore (was never intended to be)
  * subscribe\* methods renamed and moved to ReactiveElement companion object
    * **Migration**: use the new `observable --> observer` modifier, or the new onMount* hooks, and/or the new `element.amend` method.
    * Make sure to document `nodeToInserter` @nc
* **API: eliminate auxiliary syntax `myElement <-- child <-- childSignal`**
  * Use the new `amend` method instead: `myElement.amend(child <-- childSignal)`
* **API: Remove `ChildNode.isParentMounted` method. Use a similar `ChildNode.isNodeMounted` instead.**
* **API: Move `ChildrenCommand` out of the poorly named `collection` package**
* **API: Rename types:** `ReactiveHtmlBuilders` -> `HtmlBuilders`, `ReactiveSvgBuilders` -> `SvgBuilders`, `ReactiveRoot` -> `RootNode`, `ReactiveComment` -> `CommentNode`, `ReactiveText` -> `TextNode`, `ReactiveChildNode` -> `ChildNode`
* New: `ReactiveElement.events(ept: EventPropTransformation)`, works the same as `ReactiveElement.events(p: ReactiveEventProp)`, returning a stream of events
  * Example: `div(...).events(onClick.useCapture.preventDefault)`
  * Useful to combine with the new `observable --> observer` method.
* New: `element.amend` method
* New: `onMountFocus` modifier - focus an element every time it's mounted
* API: New alias for inContext: `forthis`
* API: `ReactiveElement` and other node types that take type params now have `type Base` defined on their companion objects containing the most generic version of that type, e.g. `ReactiveElement[dom.Element]` for `ReactiveElement`.
* Build: Note that this release is version `0.8.0`, not `0.8` as I would have named it before.

---

#### v0.7.2 – Dec 2019

* Build: Scala 2.13 support

---

#### v0.7.1 – Aug 2019

* Fix: Due to a bug in _Scala DOM Builder_, it would make mistakes tracking ReactiveElements' children when Laminar was reordering them e.g. using `children <-- X`. This would then result in "cannot read `nextSibling` of null" errors and potentially other inconsistencies.
  * I fixed the issue in _Scala DOM Builder_ v0.9.2, and added another reordering test for this particular scenario in Laminar
  * Thanks to [@gabrielgiussi](https://github.com/gabrielgiussi) for reporting, and nailing the test case!
* **New: Use DOM Props to set [Reflected Attributes](https://github.com/raquo/scala-dom-types/#reflected-attributes).**
  * Better performance than setting HTML attributes
  * I do not expect a change in behaviour due to the largely symmetrical nature of reflected attributes. At least not when using reflected attributes for writing values. Reading might yield subtle differences as mentioned in the link above.
  * This change is also potentially breaking because the types of identifiers like `href` changed from `ReactiveHtmlAttr` to `ReactiveProp`. Unless you reference those types or their non-common ancestors in your code you should be fine.
* API: Return `EventStream` type for `documentEvents` and `windowEvents` props, not the unnecessarily specific `DomEventStream`.
  * **Note: small chance of breakage** if you rely on `DomEventStream` type in your code
* New: `maybe` method for keys. You can now do `attr.maybe(optionOfValue)` instead of `optionOfValue.map(attr := _)`
* New: `emptyNode` to make an empty `Node` (implemented as a comment node)
* New: `WriteBus.contracomposeWriter` and other improvements from Airstream v0.7.1
* API: Use `Child` / `Children` / `ChildrenCommand` types more consistently internally; expose aliases in Laminar.scala 
* Misc: Move code examples from main into test fixtures to remove them from the JS bundle
* Misc: Bump _Scala DOM Types_ to v0.9.4 

---

#### v0.7 – Apr 2019

* **New: Airstream v0.5.1 -> v0.7 – `split`, `composeChanges`, `flatMap`, etc.**
  * See Laminar docs for using `split` to efficiently render dynamic lists of children
* **API: Hide `ancestorMountEvents` and `thisNodeMountEvents` (#42)**
  * Migration: use `mountEvents`, `maybeParentSignal` or `parentChangeEvents` instead

---

#### v0.6 – Dec 2018

* **New: Airstream v0.4 -> v0.5.1 – improved Vars, no more State, etc.**
  * Big update, see [Airstream changelog](https://github.com/raquo/Airstream/blob/master/CHANGELOG.md) for details and migration guide 

---

#### v0.5.1 – Dec 2018

* Fix: Bump Airstream to v0.4.1 to fix NPE in error handling

---

#### v0.5 – Nov 2018

* **New: Airstream v0.4 – now with error handling (see [Airstream changelog](https://github.com/raquo/Airstream/blob/master/CHANGELOG.md))**
* **Build: Drop Scala 2.11 support**
* New: window and document event streams now available via `windowEvents` and `documentEvents` objects
* New: `unsafeWindowOwner` that never kills its possessions (careful there, see docs)
* **API: `api/Laminar` / `api/L` object no longer includes event props from `WindowOnlyEventProps`**  

---

#### v0.4 – Sep 2018

* **New: Airstream v0.3 – integration with Futures and other improvements (see [Airstream changelog](https://github.com/raquo/Airstream/blob/master/CHANGELOG.md))**
* **Naming: Match naming changes in Airstream v0.3 (`mapTo` -> `mapToValue`, new `mapTo` method)**
  * _Migration note: check every usage of `mapTo` methods, the change appears to be source compatible but the new method accepts its parameter by name_
* **API: Add currying to `subscribe*` methods on `ReactiveElement`, rename some of those to `subscribeO`**
* **New: Special handling of `cls`, `role`, `rel`, and other composite attributes ([#22](https://github.com/raquo/Laminar/issues/22))**
* **Misc: Upgrade to Scala DOM Types and Scala DOM Builder v0.9** 
* New: `subscribe` and `-->` methods can now accept an `onNext` function in addition to `Observer`
* New: Better type inference for arguments of `-->` and `subscribe*` methods
* Misc: Move `-->` methods from `ReactiveProp` to `EventPropTransformation`
* Docs: Add Changelog

---

#### v0.3 – Apr 2018

* **Switch to Airstream for reactive layer (previously XStream.js)**
* **Rework event system and subscription logic and API**
* **Organize required imports under `api` package**
* Write more documentation and publish laminar-examples

---

#### v0.2 – Dec 2017

* **Multiple API improvements for a smoother developer experience**

---

#### v0.1 – Oct 2017

Initial release.

