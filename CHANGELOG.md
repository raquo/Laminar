# Changelog

Breaking changes in **bold**.

#### TO BE RELEASED – v0.4 – ETA Sep 2018

* **New: Airstream v0.3 – integration with Futures and other improvements (see [Airstream changelog](https://github.com/raquo/Airstream/blob/master/CHANGELOG.md))**
* **Naming: Match naming changes in Airstream v0.3 (`mapTo` -> `mapToValue`, new `mapTo` method)**
* **API: Add currying to `subscribe*` methods on `ReactiveElement`, rename some of those to `subscribeO`**
* New: Subscribe and --> methods can now accept an onNext function in addition to Observer
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

