package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{ChildTextInserter, Inserter, Renderable}
import com.raquo.laminar.nodes.ReactiveElement

object ChildTextReceiver {

  def <--[T](textSource: Source[T])(implicit renderable: Renderable[T]): Inserter[ReactiveElement.Base] = {
    ChildTextInserter(textSource.toObservable, renderable)
  }
}
