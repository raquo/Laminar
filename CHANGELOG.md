# Changelog

Breaking changes in **bold**.

#### v0.7 – TBD

* **New: Airstream v0.5.1 -> v0.6 – `split`, `composeChanges`, `flatMap`, etc.**
  * See Laminar docs for using `split` to to efficiently render dynamic lists of children
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

