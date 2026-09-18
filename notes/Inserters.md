# Laminar inserters: intended behaviour of dynamic children

## Purpose of this document

This is a consolidation task. Laminar is growing a more advanced algorithm for `children <--` that supports arbitrarily nested inserters (a dynamic inserter rendered _as an item_ of another dynamic list). Along the way we have hit a high rate of edge cases, inconsistencies, and outright bugs, in large part because there was no single written account of how Laminar is _supposed_ to behave when children are added, removed, moved, and updated — the intent was scattered across code comments and tests, and each change was being reviewed in isolation.

This document extracts and consolidates that intent into one place, so that:

- We (the author, and future contributors including future selves and AI assistants) can reason about correctness against a stated model rather than re-deriving it each time.
- Further work can start from the clean context of this document rather than from a long conversation.

It documents the _intended_ behaviour, and separately notes _known deviations_ (bugs, and cases where the ideal is provably unachievable). Where behaviour is suspicious, inconsistent, or of uncertain intent, it is flagged as such rather than presented as settled.

**This document is a draft for review.** The author will check whether the intent described here matches their own intent for Laminar, and refine from there. Nothing below should be treated as authoritative until that review happens — in particular, the "uncertain intent" flags are genuine questions.

### Scope

Everything reachable through the `com.raquo.laminar.inserters` package: `child <--`, `child.maybe <--`, `text <--`, `text.maybe <--`, `children <--`, `children.command <--`, static single nodes and static `Seq[Node]` as children, `onMountInsert`, and web-component slots as they interact with the above. The primary sources consolidated here are:

- The inserter sources in `src/main/scala/com/raquo/laminar/inserters/`.
- `PROGRESS.md` (the working note for the current PR #206 nested-group fixes).
- The behavioural test suites, especially `InserterMoveSpec` (the move/transfer taxonomy), plus `InserterTakeoverSpec`, `InserterRemountSpec`, `InserterExternalMutationSpec`, `NestedInsertersSpec`, and `InserterInvariantSpec`.

---

## 1. The north star: the plain-element analogy

The single most important principle, cited throughout the code (e.g. `NestedGroup.moveToParent` scaladoc: _"mirroring how a plain element can be moved between two parents"_):

> **A dynamic inserter should behave like a plain element wherever possible.**

A plain Laminar element (`val el = div(...)`) can be referenced by multiple parents over time. You can move it from one parent to another; it takes its current children with it; it is not re-created; and if it is moved seamlessly (while it always has an active parent) it is not even re-mounted. A dynamic inserter (`val dyn = children <-- signal`) is likewise a _value_ that can be placed, moved, and re-placed, and should exhibit the same relocation semantics.

Concretely, the analogy dictates:

- **Identity is stable.** The same inserter `val` placed in a new location is _moved_, not rebuilt. (`DynamicInserter.apply` / `addToDynamicList` detect an already-placed group and call `moveToParent` instead of constructing.)
- **A move carries current content, and only current content.** Moving a `div` relocates the children it currently has — it does not reclaim children that were previously removed or stolen from it. Moving a group relocates exactly the nodes currently in its span. See §5.
- **A seamless move does not re-mount.** When an element/group always has an active parent throughout the transition, its subscriptions/owner are _transferred_, not torn down and rebuilt, so no unmount/mount fires. See §6.
- **Last write wins for a contested node.** If two hosts both want the same node, whichever acts last owns it — exactly as re-parenting a plain element to B removes it from A. See §4.

Two operations must not be conflated (they answer different questions):

1. **Moving a group** (`moveToParent`) — triggered by a re-emission whose payload _is_ the group (a list re-emitting `[G]`, or `G` applied to an element). It relocates the group's span. It says nothing about the group's _inner_ membership.
2. **Stealing / re-stealing a node** — governed by whichever list re-emits _that node_; last write wins.

Blurring these is a recurring source of bugs (see §5 and the PROGRESS ③④⑤ history).

---

## 2. Vocabulary and the structural model

### Inserter
A `Modifier` that inserts child node(s). Static ones (a plain `ChildNode`, a static `Seq[Node]`) render once; **dynamic** ones (`DynamicInserter`) subscribe to an observable and keep updating. `DiffableInserter` is the subset that participates in `children <--` diffing (everything except `SlottableChildrenInserter`, which inlines its nodes individually — see §8).

### Sentinel nodes
Invisible comment nodes that mark an inserter's territory in the DOM. They are the mechanism that makes moves and external mutation survivable.

- **Leading sentinel** — every dynamic inserter has one. Content always lives _after_ it. It stays put for the life of the context; it is what lets a user move content between inserters, or externally remove an inserter's content, without the inserter losing its place. (`InsertContext.sentinelNode`.)
- **Trailing sentinel** — a stable end marker for the span. Present when EITHER: the inserter type is multi-node (`children <--`, `children.command <--` — `InserterType.needsTrailingSentinel`), OR the inserter is rendered as an item inside a `children <--` list (`_forceTrailingSentinel`). Absent for a plainly-applied single-node `child <--` / `text <--`.

Because of `_forceTrailingSentinel`, the trailing sentinel is **sticky**: once a `child <--` has been a list item it keeps its trailing sentinel forever, even after being demoted back onto a plain element (`InserterMoveSpec` "demote: a single-node `child <--` item retains its (sticky) trailing sentinel"). This is deliberate.

### Span
The stretch of DOM from an inserter's leading sentinel to its `lastNode` inclusive. `lastNode` is: the trailing sentinel if present; else the single content node _while it still sits right after the leading sentinel_; else the leading sentinel itself (empty span). That "still in place" guard matters: once another host steals the single tracked node (last-write-wins), the stale `contentMap` entry no longer reflects our span, so `lastNode` must not point at a node living under a different parent — it reports the leading sentinel (empty span) instead. The DOM between the sentinels is the **single source of truth** for both the order and the membership of an inserter's current content.

A DOM walk steps over each child inserter's whole span at once by jumping to `lastNode.nextSibling`. This jump is DOM-accurate for every inserter that can appear in a `contentMap`: a plain node's `lastNode` is itself, and a _dynamic_ child is always a `children <--` list item carrying a **sticky trailing sentinel**, so its `lastNode` is that sentinel — a real in-span node that is present even if the item's own content was stolen. The only `lastNode` that can fall back to a tracked node is a plainly-applied single-node inserter, which is never in a `contentMap` and so is never walked; even there the fallback is guarded on the node still sitting right after the sentinel (§10 ⑥).

### InsertContext
The mutable per-inserter state describing its span: the sentinel(s), the current parent, the current slot, the last inserter type, and `contentMap`. One context per inserter — except `onMountInsert`, which reuses one context across all inserters so that content survives unmount/remount (see §7).

### contentMap  (`stableFirstNode -> inserter`)
A **lookup index only.** It maps the stable first DOM node of each child inserter to that inserter, for O(1) identity lookup during diffing. It is explicitly **not** a maintained model of the DOM:

- Its **iteration order can differ from DOM order** (`children.command <--` Prepend/Insert/Replace mutate the DOM out of insertion order while appending to the map).
- It can **retain departed nodes** (a node stolen by another host stays in this map until the next emission reconciles it).

Therefore any operation that must act on the _current_ span (relocate it, re-slot it, tear it down) reads the **DOM**, walking sentinel-to-sentinel, not the map. The map is used only to recognise which nodes are "ours" and to fetch their inserters. This asymmetry is the crux of the §5 bug class.

The two correct DOM-walk helpers are `InsertContext.removeContentMapNodesFromDom` (the destructive walk) and `currentContentInsertersFromDom` (its read-only twin). Any code that iterates `contentMap.forEach` to touch _live_ content is suspect (see §11).

### NestedGroup
The rendering vehicle for a `DynamicInserter` — used both when it is applied plainly and when it is a `children <--` item, to keep one consistent code path (state + subscription management in one place, and moveability between arbitrary contexts). It owns the leading sentinel, the `InsertContext`, a `DynamicOwner` + `TransferableSubscription` pair (the "pilot" that mounts/unmounts the inner inserter with the group), and implements `moveToParent` / `removeFromParent`.

### Stealing
When node/group X is tracked by inserter A but gets placed by inserter B, we say B _stole_ X from A. Steals happen because the observables feeding A and B propagate in some order, and the add (into B) can be processed before the remove (from A). Laminar's contract: **inserter code must never fail on the resulting stale state, and must self-correct on the next emission** (`InsertContext` scaladoc, lines 31–51). `removeFromDynamicList` is a no-op on parent mismatch precisely so a stale removal after a steal does nothing (`DynamicInserter.removeFromDynamicList`, `DomApi.removeChild` no-op on parent mismatch).

---

## 3. Adding and removing (the baseline)

### Add
A new item is inserted at the cursor position during diffing, its subscriptions set up, its content mounted. For a plain node this is one `insertChildAfter`; for a dynamic inserter it is `addToDynamicList`, which builds the `NestedGroup` (leading + trailing sentinels) on first placement.

### Remove (genuine)
An item that leaves the list is removed from the DOM and its per-item lifecycle torn down (`removeFromDynamicList` → for a group, `removeFromParent`: disconnect the pilot subscription first, then remove content, then remove sentinels — mirroring `willSetParent(None)` order). Its content **unmounts exactly once**.

### Remove (no-op after a steal)
If the node/group is no longer under this parent (already stolen), `removeFromDynamicList` does nothing — the new host owns it now. This is the "last write wins" safety valve.

### External mutation
Laminar tolerates a user/third-party removing an inserter's node from the DOM directly: the next reconciliation walks the live span and simply doesn't find it, correcting the count (`InserterExternalMutationSpec`; `ChildrenInserter.updateChildren` count-correction, referencing issue #120). External _insertion_ into a bracketed span (between our sentinels) is reported as an error on teardown/removeAll paths (`removeContentMapNodesFromDom` with a trailing sentinel), because we can't tell an intruder from our own content otherwise. External insertion into an unbracketed (`child <--`) span is silently treated as "the next sibling after our span" — we stop the walk there.

---

## 4. Stealing and last-write-wins

When the same node or inserter instance is emitted by two different lists/bindings, the intended result is:

- **The item ends up in whichever list emitted it last** ("last write wins"), with the loser's tracking self-correcting on its next emission or no-op removal.
- **A same-transaction add-first ("steal")** — list 2 emits `[X]` while X is still in list 1, then list 1 emits `Nil` — is a **true transfer**: X moves to list 2 with **no re-mount** (`InserterMoveSpec` REFERENCE test, `probe(addFirst=true) == (1,0)`; and the many "add-first / steal" tests).
- **A remove-first move** — list 1 emits `Nil`, then list 2 emits `[X]`, in two separate transactions — **does re-mount** (`probe(addFirst=false) == (2,1)`). Nothing links the two transactions, so X is genuinely unmounted then re-mounted. This matches plain-element behaviour and is currently considered acceptable, though see #163 in §10 for the case where it is _not_ desired and only one direction is fixable.

Re-emitting a `child <--` / `text <--` / `text.maybe <--` whose node another binding stole **steals it back** (last write wins), covered by the three "re-emitting … steals its node back" tests.

Last-write-wins must survive a group **move**: relocating a group leaves a sibling-stolen node with its thief, but must not sever the inner list's _claim_ on it — when the inner list re-emits, it reclaims the node to the group's NEW host (`InserterMoveSpec` "last-write-wins survives a partial-span move").

---

## 5. Moving (the hard part)

Four distinct relocation scenarios, all of which should be **seamless (no re-mount)** because the moved thing always has an active parent throughout:

1. **Reorder within one list** — `moveWithinDynamicList`, same-parent branch: a raw DOM reposition of the span, then a slot re-affirm. No owner transfer needed.
2. **Transfer between two lists** (add-first steal) — `addToDynamicList` sees an already-placed group and calls `moveToParent`: relocate the span, transfer the pilot subscription to the new parent's owner, update `currentParentNode` + `currentSlotName` so future emissions target the new home.
3. **Promote** a plainly-applied dynamic inserter INTO a `children <--` list, and **demote** a list item back onto a plain element (`element.amend(inserter)`) — both routed through `moveToParent` / `apply`. Seamless. The trailing sentinel is added on promote and stickily retained on demote (§2).
4. **Steal-back / re-steal** — a group stolen into a sibling, then re-emitted by its original list. Same-parent layout takes `moveWithinDynamicList`'s raw-reposition branch; cross-parent takes `moveToParent`.

### The governing rule for `moveToParent`

> **A move relocates exactly the nodes currently in the group's span, in DOM order — never the order or membership recorded in `contentMap`.**

This follows directly from the plain-element analogy: a moved `div` takes the children it has _now_, in the order they sit, and does not reclaim children that left it. Consequences the implementation must honour:

- **DOM order, not map order.** `children.command <--` builds its DOM out of insertion order, so the map lists nodes differently than the DOM. The move must preserve DOM order (`InserterMoveSpec` "a stolen `children.command <--` span preserves its DOM order").
- **Live membership, not map membership.** A node stolen out of the span by a sibling, or absorbed into the group's own nested `child <--`, has a stale map entry that the move must NOT drag back (the ④/⑤ bug class).
- **Nested spans move as a unit.** The DOM walk jumps by each inserter's `lastNode.nextSibling`, so an inner group (with its own sentinels) is stepped over whole and relocated by its own recursive `moveToParent` (handles depth-2/3, ⑤).

Accordingly, `moveToParent` **snapshots the live span (via `currentContentInsertersFromDom`) BEFORE moving the sentinels** (the walk starts at the leading sentinel, which is about to move), then re-adds each collected inserter in DOM order, then commits the new parent/slot, then transfers the subscription. The move deliberately does **not** touch `contentMap` — stale inner tracking is tolerated and self-corrects on the inner inserter's next emission, matching existing behaviour.

### Re-placing an inserter whose group was already torn down

If the previous host genuinely _removed_ the group (set `nestedGroupOpt = js.undefined`) and then the original list re-emits it, there is no group to move — it must be **placed afresh** (re-inserted + re-mounted), like a plain element that was removed and re-added. `DynamicInserter.moveWithinDynamicList` detects the missing group and falls back to `addToDynamicList`; the list's item count already counted this inserter (it was in the previous map), so the rebuild changes no count. This is the counterpart to "move the live span": when there is no live span, rebuild. Covered by `InserterMoveSpec` section 4d.

---

## 6. Lifecycle guarantees

- **No needless re-mount.** The headline guarantee of the #157 work: a move relocates a span and transfers subscriptions without re-running the inserter's observer or re-firing mount/unmount. Asserted pervasively via `EventTracker` (`InserterMoveSpec` "moving elements … never re-mounts them", "reordering never re-mounts items", the add-first/steal family).
- **Exactly-once unmount** on genuine removal; **zero** events on a steal that is later dropped by the thief only after the origin already un-tracked it (`InserterMoveSpec` section 4d, the "steals then drops" step).
- **Per-item lifecycle is preserved across a transfer** — the owner is transferred, not rebuilt, so per-item `onMount`/`onUnmount` and internal subscriptions stay live (`InserterMoveSpec` "add-first / steal keeps the item's per-item lifecycle intact").
- **Ordering is empirical but pinned.** Some teardown/swap orders are not obvious and are deliberately encoded by tests (see `notes/Testing.md`):
  - `child <--` self-replace swaps **unmount-old-then-mount-new**.
  - A takeover of a _foreign_ span mounts-new-then-unmounts-old.
  - `children <--` teardown walks `contentMap` in **insertion order**, not current DOM order (several `#Note` comments; e.g. `InserterMoveSpec:890`, `InserterExternalMutationSpec:148`). This is teardown only — relocation uses DOM order (§5).
  - `Replace` (command) unmounts the old node then mounts the new (`InserterMoveSpec:1718`).

  These orderings are "behaviour worth pinning but not sacred" — if an implementation change flips one and the new order is still coherent, the test encodes the new reality with a `#Note`.

- **Content survives unmount/remount** in a shared `onMountInsert` context, and reconciles against values that changed _while unmounted_ (`InserterRemountSpec` — the `text.maybe`, `child.maybe`, `children <--` remount-reconcile cases; this is where #201 lived).

---

## 7. `onMountInsert` and the shared context

`onMountInsert` creates **one** `InsertContext` reused for every inserter it produces across mounts. This gives intuitive persistence: `onMountInsert(child <-- stream)` keeps the last emitted child in the DOM across unmount/remount. It also means one inserter type can **take over** a context previously populated by a different type; the takeover rules:

- Switching TO `child <--` / `text <--` clears prior multi-node content down to (at most) the one node being kept, and drops the trailing sentinel.
- Switching TO `children.command <--` clears any content left by a _non-command_ inserter (commands can't patch foreign content to a target state), but **preserves** content it built itself across a mere remount, and preserves content built by a _different_ command inserter (`InserterTakeoverSpec` "children.command → children.command (different inserter) keeps the previous content").
- A takeover that tears down a span reports an externally-inserted intruder (§3).
- A takeover does **not** blindly tear down everything: `children <--` (a,b) → `child <-- b` keeps b mounted and unmounts only a (`InserterTakeoverSpec`).

### The `setNextInserterType` invariant
Whenever there is no trailing sentinel, `contentMap` holds **at most one** node. The guard in `setNextInserterType` throws if we ever switch to a trailing-sentinel type while lacking a trailing sentinel but already holding >1 content node (we wouldn't know where the pre-existing content ends). This is structurally unreachable via the public API; `InserterInvariantSpec` pins both the near-miss safety and the white-box guard firing.

---

## 8. Per-inserter-type reference

| Inserter | Trailing sentinel? | contentMap entries | Notes |
|---|---|---|---|
| `child <--`, `text <--`, `text.maybe <--`, plain `ChildNode`, `SlottableChildInserter` | No (unless made a list item → sticky) | 0 or 1 | `ChildType`. Single-node fast paths. Only elements are slotted — text and comment nodes never are (§9). |
| `children <--`, static `Seq[Node]` (`SlottableChildrenInserter`) | Yes | 0..n | `ChildrenType`. Diffed by `updateChildren`. |
| `children.command <--` | Yes | 0..n | `ChildrenCommandType`. Imperative; builds DOM out of map order; tracked in `contentMap` but cleared when taking over a non-command type. |

Special cases:

- **`SlottableChildrenInserter` is the only non-`DiffableInserter`.** It has no stable unique identity (no sentinel, first content node not unique), so it stays transparent to diffing: it writes its nodes _individually_ into the map (as bare nodes, or `SlottableChildInserter` wrappers if slotted) rather than itself. The diff then handles them as ordinary nodes. `NestedInsertersSpec` "static seq item" and the mutable-collection resilience test cover it.
- **`SlottableChildInserter`'s `stableFirstNode` is an intentional abstraction leak** — it equals the wrapped node's `ref`, colliding with the bare `ChildNode`'s identity. Tolerated because the only difference is the slot, and `updateChildren`'s same-inserter branch always re-applies the incoming inserter's slot for exactly this reason.
- **`text <--` reuses `ChildInserter.switchToChild`** for structural changes but updates `textContent` in place on subsequent same-node emissions (no node churn).

---

## 9. Slots (web components)

Slotting sets the `slot` attribute on a child so a web component's shadow DOM can distribute it into a named slot. The slot is a property of the **insertion mechanism, not the node** (hence `SlottableChildInserter` wrapping rather than making elements slottable).

### Only elements are slotted

Slotting applies to elements only. The other node types opt out explicitly:

- **Comment nodes are never slotted** — `CommentNode.applySlot` is a silent no-op. (This is what lets sentinels sit inside a slotted span without themselves being distributed; a comment carries no `slot` attribute.)
- **Text nodes are never slotted** — named slots only accept elements. `TextNode.applySlot` with a non-empty slot name reports an error and the node falls into the component's default slot. The inserters therefore always pass `slotName = ()` for text (`text <--`, raw text nodes).
- **Elements** (`ReactiveElement.applySlot`) are the only nodes that actually get a `slot` attribute written.

### Two ways to set an element's slot, and who wins

There are two independent mechanisms:

1. **`Slot(name)(...)`** — the web-component slot destination. `Slot.apply` copies each inserter with `withSlotName(name)`, so on insertion the destination applies `name` to the child (`applySlot`). Call this the _Slot-applied_ slot; the element records it in `_appliedSlotName`.
2. **`slot := "x"` / `slot <-- signal`** — the user setting the `slot` attribute directly. `SlotHtmlProp.set` writes the attribute AND calls `forgetAppliedSlotName()`, clearing `_appliedSlotName` so the Slot no longer regards itself as the manager.

**Between these two it is last-write-wins** (`ReactiveElement._appliedSlotName` scaladoc says so explicitly; `SlotAttributeStealingSpec` pins it — "Ownership is last-write-wins, like element-stealing"). Concretely:

- The Slot applies its slot when it inserts/reconciles the element; a manual `slot :=` / `slot <--` write after that overrides it (and forgets the Slot record). A later Slot reconcile that touches the element (a reorder, a re-diff, a move into another Slot) is a fresh Slot write that overrides again and restores the Slot's slot — **without re-mounting** (`SlotAttributeStealingSpec` "a slotted-list reconcile re-asserts the Slot's slot over a manual override"). A subsequent `slot <--` emission overrides once more. Each write simply wins in turn.
- So `_appliedSlotName` is **not** an ownership lock — it is bookkeeping with two jobs: (a) **dedup** redundant Slot re-writes (`applySlot` skips the DOM write when `_appliedSlotName.contains(newSlotName)`), and (b) **scope the move-out cleanup**: when an element leaves a Slot (destination slot is `()`), `applySlot` clears the `slot` attribute _only if the Slot applied it_ (`_appliedSlotName.isDefined`). A user's manual slot (which cleared `_appliedSlotName`) therefore survives the element leaving the Slot — the user manages it now.

### Innermost `Slot` wins (a separate rule, resolved earlier)

Distinct from the last-write-wins contest above: when a slot could come from an inserter's _own_ `Slot` wrapper vs. the _destination list's_ slot, the inserter's own slot wins (`slotName.orElse(listSlotName)`, applied throughout `addToDynamicList` / `applySlot` / `moveToParent`). This picks the effective slot name _before_ `applySlot` runs; that name then contests with any user `slot :=`/`<--` on the element under last-write-wins.

### Persistence and live-span re-slotting

- A dynamic inserter's slot is **persistent**, not just applied to current content: it is stored on the context (`currentSlotName`) so future emissions are slotted the same way. A `NestedGroup` move resolves the destination list's slot against its own and stores the result, redirecting the inner inserter's future emissions.
- Re-slotting on a move/diff reads the **live DOM span**, never `contentMap` — so a node that has left the span is never re-slotted out from under its new host. `NestedGroup.applySlot` iterates `currentContentInsertersFromDom` and skips departed nodes; `updateChildren`'s same-place branch also re-affirms slot, because a different wrapper may now slot the same node. Covered by `SlotSpec` "re-slotting a moved group re-slots only its live span, leaving a sibling-stolen node's slot with its new host" (a re-steal between two sibling `Slot`s hits `NestedGroup.applySlot` via `moveWithinDynamicList`'s same-parent branch).

---

## 10. Known deviations from the ideal

### Provably unachievable (not bugs we can fix)

- **Issue #163 — moving an element between two sibling `child <--` bindings re-mounts in one direction only.** When one element is shown via one of two independent `child <--` bindings toggled by a single signal, whether the toggle re-mounts is _order-dependent_: the binding that GAINS the element must fire before the one that LOSES it for the transfer to be seamless. When the losing binding fires first, the element is detached to `None` (unmount) before re-attachment (mount). This is inherent to synchronous propagation order; a `delaySync` workaround only fixes one direction. Characterized (not "fixed") in `InserterMoveSpec` "CHARACTERIZATION (issue #163)". The `probe(addFirst=false)` remove-first re-mount in the REFERENCE test is the same underlying limitation.

### Open bugs (currently being worked in PR #206)

Per `PROGRESS.md`, six issues were reproduced on master; all six (①②, the ③④⑤ class, and ⑥) are now fixed on this branch. See below.

### Recently fixed on this branch / adjacent history

- **#201** — `text.maybe` failed reconciliation on remount (fixed; `InserterRemountSpec`). The `option` branch now OR-s `maybeTextNode.nonEmpty || ctx.contentMap.size > 0` because `maybeLastSeenChild` resets on remount.
- **#202** — removing composite key values with multiple reasons.
- **① / ②** — re-emit after another list genuinely tore the group down no longer throws. After a genuine removal sets `nestedGroupOpt = js.undefined`, the original list still tracks the inserter; a re-emit routes to `moveWithinDynamicList` (①) or the overflow branch (②). Both now fall back to a fresh `addToDynamicList` when the group is gone (re-insert + re-mount, like a plain element — §5 "re-placing … torn-down"), instead of hitting `nestedGroupOpt.getOrElse(throw)`. Count bookkeeping stays correct (the inserter was already counted in the previous map). Covered by `InserterMoveSpec` section 4d.
- **③④⑤** — the `moveToParent` DOM-order / live-membership fixes described in §5.
- **⑥** — `InsertContext.lastNodeInDom` (renamed from `lastNode` to advertise its DOM-wins contract) no longer trusts a single tracked node once another host has stolen it: the `singleContentMapItem` branch is gated on the node still sitting right after the sentinel, else the span ends at the sentinel itself (empty). This is what lets `ensureTrailingSentinel` promote a stolen plain `child <--` into a `children <--` list without a `NotFoundError`. Covered by `InserterMoveSpec` §5 (promote empty-span, promote-with-following-sibling, next-emission-into-new-host, and the `amend` relocate path).

---

## 11. Suspicious / uncertain intent (flagged for review)

These are places where the code's intent is unclear, defensive-but-untested, or potentially inconsistent. They are questions for the author, not assertions of bugs.

- **Error-report vs. warning for intruder nodes.** `removeContentMapNodesFromDom` sends an unhandled Airstream error when it finds an untracked node inside a bracketed span. There's a `#TODO[nested-dyn]` questioning whether this should be an error, a warning, or silent. _Question: is reporting the intended long-term behaviour, especially now that nested inserters can legitimately place foreign-looking spans?_
- **`contentMap` iteration order is relied upon in teardown but is not a maintained invariant.** Teardown order (`children <--`) is documented as "insertion order", and several tests pin it — yet `PROGRESS.md` (FINDINGS) establishes that map order equals DOM order for _every_ inserter except `children.command <--`, and that this coincidence is not an invariant anyone maintains. So teardown order is _incidentally_ insertion order for most inserters and _incidentally_ command order for commands. _Question: is teardown order a guarantee we intend to keep, or an accident we tolerate? If a guarantee, it should probably be DOM order too, for consistency with relocation._
- **`parentNode` mutability on `InsertContext`.** `currentParentNode` is mutable solely to support a `NestedGroup` move between lists, and the author notes (scaladoc) this is a shape they'd like to isolate to `NestedGroup` but can't given `insertFn`. _Flagged as known technical debt, not a bug._
- ✅ **The `removeFromDynamicList` "probably" genuine-removal comment.** `DynamicInserter.removeFromDynamicList` distinguishes genuine removal from a post-steal no-op by comparing `leadingSentinel.parentNode == parent.ref`. The `#Note: stealing can also happen without changing parent!` block reasons (with two "I think…"s) about why same-parent steals still don't reach this path. _Confirmed safe, and the "I think…"s hold — in fact more strongly than the comment states: after ANY steal the stolen group's span physically leaves the origin list's walked region (a sibling/cross-parent thief relocated it into the thief's span; a child thief nested it inside that child's span, which the origin's sentinel-to-sentinel walk steps over via `lastNode`), so the origin never calls `removeFromDynamicList` on it at all — the `else` no-op branch is not the mechanism that saves the sibling case; not-calling is. Both callers (`ChildrenInserter.updateChildren`, `InsertContext.removeContentMapNodesFromDom`) only ever look up a group by a node currently in their own span, whose `leadingSentinel.parentNode` is therefore always `parent.ref`, so the `else` branch appears structurally unreachable and is untested: instrumenting the whole inserter suite recorded 18/18 calls with `parentNode == parent.ref`, zero no-ops. Pinned (observably) by `InserterMoveSpec` section 4b-bis — a same-parent sibling, cross-parent sibling, and child-inserter steal each followed by the origin's removal, none tearing the group down. Note this makes the `addToDynamicList` comment ("the previous list … will call `removeFromDynamicList` … a no-op due to parent mismatch") inaccurate: that call does not happen. Possible cleanup: keep the `else` as a defensive assert, or drop it and document that a stolen group is simply never revisited._
- **`child <--` clears stale tracked nodes AFTER inserting the new one** (`ChildInserter.switchToChild`, `#TODO - would be nice if we could remove those before inserting`). Not known to be wrong, but the ordering is admitted-as-suboptimal.
- ✅ **Slot cleanup can wipe a user's manual slot in one conflicting-instructions case.** `ReactiveElement.applySlot(None)` clears the `slot` attribute when `_appliedSlotName.isDefined`. If an element carries BOTH a `Slot()` wrapper and a manual `slot :=`, and the `Slot()` write landed last (so `_appliedSlotName` holds the Slot's name), then moving the element out of the Slot removes the attribute entirely — dropping the user's manual slot too (`ReactiveElement.applySlot` comment: _"Technically this CAN also clear the `slot` attribute set by the user manually … only if the user provided conflicting `slot :=` and `Slot()` instructions"_). _Reviewed and confirmed acceptable — an intended edge of last-write-wins, not a bug._ Once the Slot's write lands last, the DOM attribute already holds the Slot's name (each `setAttribute` is paired with the `_appliedSlotName =` assignment), so the manual value was overwritten at that point, not at move-out; and only the Slot's name is remembered (`_appliedSlotName`), never the prior manual value, so restoring it on move-out is impossible without storing extra state purely to service a self-contradictory instruction. The alternatives are worse: keeping the stale `prefix` would distribute the element into a slot its new host never asked for, so clearing to the default slot is correct. The clean path already works — when the manual `slot :=` lands last it calls `forgetAppliedSlotName()`, so `_appliedSlotName` is `()`, the move-out guard is false, and the manual slot survives. All three cases are pinned in `SlotSpec` ("a Slot wrapper overrides a manual slot attribute, and moving out clears it entirely"; "a manual slot := that lands after the Slot survives moving out of the Slot"; "leaves a manual slot attribute untouched on an element that never entered a Slot"), alongside `SlotAttributeStealingSpec` for the ping-pong between the two writers.
- **Slot is applied inside `willSetParent`/insert ordering with an open question.** `DomTree` has `#TODO[Integrity] should we apply the slot before or after willSetParent?` at the insertion sites. _Flagged: the ordering is currently unverified intent._

---

## 12. Quick-reference checklist for future changes

When touching inserter code, check the change against these:

1. Does it match the **plain-element analogy** (§1)? If a plain `div` wouldn't do it, an inserter probably shouldn't either.
2. Does it read the **DOM span** (sentinel-to-sentinel) for anything touching _live_ content, and use `contentMap` only as a lookup index (§2)?
3. Does it keep **moves seamless** (no re-mount) while an active parent exists, and only re-mount when there genuinely isn't one (§5, §6)?
4. Does it honour **last-write-wins** without failing on stale tracking (§4)?
5. Does it preserve the **`setNextInserterType` invariant** and sentinel bookkeeping (§7)?
6. Does it keep **innermost-slot-wins** and slot persistence (§9)?
7. Are new behaviours (especially teardown/swap **ordering**) pinned with `EventTracker` assertions and `#Note` comments per `notes/Testing.md`?
