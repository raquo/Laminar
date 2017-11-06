package com.raquo.laminar.collection

import com.raquo.xstream.XStream

class ReactiveCollection[Model, Node](
  makeCommandStream: ReactiveCollection[Model, Node] => XStream[CollectionCommand[Model]],
  makeItemCommandStream: Model => XStream[CollectionCommand[Model]],
  modelToNode: Model => Node
) {

  // @TODO[Performance] $items is probably slow to generate because of processCommand

  // @TODO[Performance] $items should be optional API, not be required to generate $command
  // @TODO[Integrity] although in that case... $items could get out of date if not subscribed to?

  // @TODO[Performance] processCommand internal are suboptimal

  // @TODO[Integrity] We rely on processCommand behaving the same as Laminar treats $command

  private val $internalCommandProxy: XStream[CollectionCommand[Model]] = XStream.create()

  val $command: XStream[CollectionCommand[Model]] = {
    XStream.merge(makeCommandStream(this), $internalCommandProxy)
  }

  // Will be used by Laminar
  val $nodeCommand: XStream[CollectionCommand[Node]] = {
    $command.map(command => command.map(modelToNode))
  }

  val $items: XStream[Vector[Model]] = $command.fold(CollectionCommand.vectorProcessor, Vector())

  val $internalCommand: XStream[CollectionCommand[Model]] = $items.map(latestItems => {
    val latestCommandStreams = latestItems.map(makeItemCommandStream)
    val $latestCombinedRemoveStream = XStream.merge(latestCommandStreams: _*)
    $latestCombinedRemoveStream
  }).flatten

  $internalCommandProxy.imitate($internalCommand)
}
