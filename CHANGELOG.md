# Changelog

Breaking changes in **bold**.

#### v0.8 – TBD

* **API: New modifier types: `Setter` and `Inserter` (old `Setter` renamed to `KeySetter`)**
* **API: ReactiveElement.maybeChildren is not mutable anymore (was never intended to be)**
* New: amend method (same as bind essentially)
* **API: subscribe* methods renamed and moved to companion object (also use onMount, amend(), and the new --> extension methods on RichObservable and friends)*
* New: onMountFocus
* API: forthis instead of inContext
* **New: Brand new mount Event system renamed to Lifecycle Events**
  - eliminate `mountEvents` stream `MountEvent`, and all related machinery
  - New: onMount / onUnmount / onLifecycle methods
  - Order of unmount events might have changed (see test)
* **API: eliminate auxiliary syntax `myElement <-- child <-- childSignal`**
  * Use the new `bind` method instead: `myElement.bind(child <-- childSignal)`
* **API: Lifecycle events & Ownership overhaul**
  * <TODO: Elaborate> Use Airstream's new `DynamicOwner` and `DynamicSubscription` features: `ReactiveElement` subscriptions are now activated only when the element is mounted, not when it is created.
  * <TODO: Elaborate> Transaction delay for `mountEvents` now applies to all element subscriptions
  * <TODO: Elaborate> Allow remounting of unmounted components (document pilot subscription memory management gotcha) 
  * `NodeWasDiscarded` event is not fired anymore. See `NodeWillUnmount`.
* **API: Hide `parentChangeEvents` and `maybeParentSignal`. This functionality is not available anymore as it requires undesirable tradeoffs.**
* **API: Hide `willSetParent` and `setParent` methods. Use Laminar's `parentNode.appendChild(childNode)` or similar.**
* **API: Rename types:** `ReactiveHtmlBuilders` -> `HtmlBuilders`, `ReactiveSvgBuilders` -> `SvgBuilders`, `ReactiveRoot` -> `RootNode`, `ReactiveComment` -> `CommentNode`, `ReactiveText` -> `TextNode`, `ReactiveChildNode` -> `ChildNode`
* **API: Move `ChildrenCommand` out of the poorly named `collection` package**
* **API: Eliminate `EventPropEmitter` (merged into EventPropSetter)**
  * **Migration:** Replace usages of `EventPropEmitter` with `EventPropSetter`. Also, if you were using `EventPropEmitter` as an `EventPropTransformation`, those methods will not be available anymore as `EventPropSetter` does not extend `EventPropTransformation`, so you will need to rewrite those calls differently, more manually. This usage wasn't explicitly documented, so I hope no one actually runs into this.
* **API: Eliminate dependency on _Scala DOM Builder_**
  * DOM Builder is capable of supporting different DOM backends and even JVM rendering. We have no plans to use either of these in Laminar, so the indirection required by these abstractions is not pulling its weight.
  * `DomApi`
    * Remove the old `DomApi` trait and companion object.
      * Combine `domapi.*Api` traits into a single `DomApi` object
      * Use the new `DomApi` object directly instead of passing implicit `domapi.*Api` parameters.
  * Move `Setter` and `EventPropSetter` into Laminar and simplify type signatures
  * Merge into relevant Laminar subtypes: `Node` -> `ReactiveNode` (add Ref type param), `Comment` -> `ReactiveComment`, `Text` -> `TextNode`, `ParentNode` -> `ParentNode`, `ChildNode` -> `ChildNode`, `Root` -> `RootNode`, `TagSyntax` -> `HtmlTag` and `SvgTag`
  * Merge `EventfulNode` trait into `ReactiveElement` (split members between the trait and the object)
    * Change type of `maybeEventListeners` to have `List` instead of `mutable.Buffer`
  * **Migration:** If you reference any of the affected types directly, you will need to import them from Laminar, or use their corresponding Laminar replacements listed above. Other than that, everything should just work.
* API: `ReactiveElement` and other node types that take type params now have `type Base` defined on their companion objects containing the most generic version of that type, e.g. `ReactiveElement[dom.Element]` for `ReactiveElement`.

#### v0.7.2 – Dec 2019

* Build: Scala 2.13 support

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

#### v0.7 – Apr 2019

* **New: Airstream v0.5.1 -> v0.7 – `split`, `composeChanges`, `flatMap`, etc.**
  * See Laminar docs for using `split` to efficiently render dynamic lists of children
* **API: Hide `ancestorMountEvents` and `thisNodeMountEvents` (#42)**
  * Migration: use `mountEvents`, `maybeParentSignal` or `parentChangeEvents` instead

#### v0.6 – Dec 2018

* **New: Airstream v0.4 -> v0.5.1 – improved Vars, no more State, etc.**
  * Big update, see [Airstream changelog](https://github.com/raquo/Airstream/blob/master/CHANGELOG.md) for details and migration guide 

#### v0.5.1 – Dec 2018

* Fix: Bump Airstream to v0.4.1 to fix NPE in error handling

#### v0.5 – Nov 2018

* **New: Airstream v0.4 – now with error handling (see [Airstream changelog](https://github.com/raquo/Airstream/blob/master/CHANGELOG.md))**
* **Build: Drop Scala 2.11 support**
* New: window and document event streams now available via `windowEvents` and `documentEvents` objects
* New: `unsafeWindowOwner` that never kills its possessions (careful there, see docs)
* **API: `api/Laminar` / `api/L` object no longer includes event props from `WindowOnlyEventProps`**  

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

#### v0.3 – Apr 2018

* **Switch to Airstream for reactive layer (previously XStream.js)**
* **Rework event system and subscription logic and API**
* **Organize required imports under `api` package**
* Write more documentation and publish laminar-examples


#### v0.2 – Dec 2017

* **Multiple API improvements for a smoother developer experience**


#### v0.1 – Oct 2017

Initial release.

