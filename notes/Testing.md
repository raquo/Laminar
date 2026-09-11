# Writing tests (dynamic inserters & lifecycle)

Conventions for tests in this suite.

## Temporary notes (active now)
- Don't write speculative tests. Add coverage for gaps identified in the plan
  (`../test-review/REPORT.md`, `test-review/coverage-by-suite.md`).

## Framing

- Test desirable **behaviour**, not implementation details. Name the guarantee the test defends (e.g. "a move relocates the span without re-mounting its content") and explain the _why_ behaviourally, not the mechanism.

## The EventTracker fixture

`createEventTracker()` returns an [`EventTracker`](../src/test/scala/com/raquo/laminar/utils/EventTracker.scala). It records an ordered log of events. Build tracked things through it so their events are captured:

- `tracker.createDiv(id, mods*)` / `tracker.createSpan(id, mods*)` — a tagged element; logs `element-create:<id>` when built, `mount:<id>` / `unmount:<id>` on lifecycle.
- `tracker.text(id, source)` — a `text <--` whose every rendered value logs `text-update:<id>:<value>`.
- `tracker.logRaw(event)` — push an arbitrary event string

Add more helpers to `tracker` as needed if you want to track more things in future tests. Instead of counters, prefer logs of events.

## Assertions should be thorough, strict, specific, and debuggable

For example, use `tracker.assertEvents` to assert specific sequences of events, instead of asserting the final state. We care a lot about not emitting unwanted or redundant events (e.g. we may want to assert that a moved element wasn't re-mounted).

Split the tests into `withClue` sections, or just consecutive groups of code separated with a simple `// --` comment, and prefer to make assertions about what happened within the time span of those groups, clear()-ing the tracker's state in between the groups. This is in addition to asserting the desired final state, e.g. with `expectNode`.

When creating multiple tracked elements upfront, .clear() the tracker right afterwards so that we don't waste time asserting the _obvious_ elementCreated log entries. We do however want to assert elementCreated when it's Laminar or Airstream logic that determines if/when these elements are created (e.g. in a `signal.map` or `splitSeq` callbacks).

To be clear, in addition to the final DOM state we generally want to assert the lifecycles and events that happened to bring it there. 

Assertion messages should include enough details to pinpoint and diagnose the issue effectively if they fail.

## DOM shape and error paths

- Pair event assertions with `expectNode(...)` for DOM structure (`sentinel`, `div of "x"`, …).
- For error-path behaviour, wrap the triggering emit in
  `withCollectedAirstreamErrors { errors => …; assert(errors.size == n) }` and assert on the
  collected errors rather than letting them rethrow.

## Ordering is empirical

Exact teardown / swap order is behaviour worth pinning but not always obvious — e.g. `child <--` swaps **unmount-old-then-mount-new** (self-replace) but a takeover of a foreign span is **mount-new-then-unmount-old**; `children <--` teardown walks the contentMap in **insertion order**, not current DOM order. Write your best guess, run the spec, and encode the order the failure reports, with a one-line // #Note: comment explaining it.

## Misc style

- Cross-compiles to **Scala 2.13**: use `mods: _*` (NOT `mods*`) for vararg splices. The `x: _*` deprecation warning under Scala 3 is expected and intentional. Do **not** run the Scala 2 suite.
- Scaladoc comments on the test suite class itself should be short, and should explain the intent and scope of the tests at a high level, they should not duplicate the comments you add for individual tests. 
- Comments beginning with `--` must be **single-line** only (`// -- section --` is fine; no multi-line `--` comment blocks).

## Running

The working directory resets between commands, so always prefix the `cd` in the same compound command:

```bash
cd <project_directory> && sbt -batch -Dsbt.color=false "testOnly *YourSpec"
```

Use `"test"` for the full suite.
