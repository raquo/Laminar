---
title: Airstream v0.11.1 
author: Nikita
authorURL: https://twitter.com/raquo
---

I have just released a bugfix for Airstream which fixes [#45](https://github.com/raquo/Airstream/issues/42).

If you used `SwitchStreamStrategy` or `SwitchFutureStrategy` when `flatMap`-ping observables (see docs for when exactly each is used), or used `SwitchEventStream` directly, the output stream was not propagating errors in the _parent_ observable. It would propagate errors from observables created inside the `flatMap` method callback, but not from the actual parent observable that you were calling `flatMap` on. Such errors were swallowed, which is a violation of documented Airstream behavior.

Thanks to [Ajay](https://github.com/ajaychandran) for finding this issue and even the root cause.

Airstream v0.11.1 fixes this issue. All errors are now being propagated. This might be a breaking change if you implicitly relied on errors being swallowed here, but I'm releasing this as a patch because previous behavior was an obvious bug.

I expect this to be a fairly uncommon issue, so I will not be releasing Laminar v0.11.1 yet. Just add Airstream v0.11.1 to your build.sbt to get this update now.
