package com.raquo.laminar.collection

// @TODO[API] Not sure if this should be part of our API.
// This method was inspired by https://github.com/cyclejs/collection, and I know I've used it at some point but it
// just seems like an awkward, ad-hoc solution. I don't think we need it in Laminar. Or at least it doesn't make sense
// to me anymore now that we have children diffing.

//import com.raquo.laminar.experimental.airstream.eventstream.EventStream
//import com.raquo.laminar.experimental.airstream.signal.Signal
//
//class ReactiveCollection[Model, Node](
//  makeCommandStream: ReactiveCollection[Model, Node] => EventStream[CollectionCommand[Model]],
//  makeItemCommandStream: Model => EventStream[CollectionCommand[Model]],
//  modelToNode: Model => Node
//) {
//
//  // @TODO[Performance] $items is probably slow to generate because of processCommand
//
//  // @TODO[Performance] $items should be optional API, not be required to generate $command
//  // @TODO[Integrity] although in that case... $items could get out of date if not subscribed to?
//
//  // @TODO[Performance] processCommand internal are suboptimal
//
//  // @TODO[Integrity] We rely on processCommand behaving the same as Laminar treats $command
//
//
//
//  // @TODO[Airstream] This needs radical rethinking – use some kind of EventBus I guess?
//
//  private val $internalCommandProxy: EventStream[CollectionCommand[Model]] = EventStream.create()
//
//  val $command: EventStream[CollectionCommand[Model]] = {
//    EventStream.merge(makeCommandStream(this), $internalCommandProxy)
//  }
//
//  // Will be used by Laminar
//  val $nodeCommand: EventStream[CollectionCommand[Node]] = {
//    $command.map(command => command.map(modelToNode))
//  }
//
//  val $items: Signal[Vector[Model]] = $command.fold(Vector[Model]())(CollectionCommand.vectorProcessor)
//
//  val $internalCommand: EventStream[CollectionCommand[Model]] = $items.map(latestItems => {
//    val latestCommandStreams = latestItems.map(makeItemCommandStream)
//    val $latestCombinedRemoveStream = EventStream.merge(latestCommandStreams: _*)
//    $latestCombinedRemoveStream
//  }).flatten
//
//  $internalCommandProxy.imitate($internalCommand)
//}
