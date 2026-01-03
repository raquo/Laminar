package com.raquo.laminar.receivers

import com.raquo.airstream.core.{Observable, Source}
import com.raquo.laminar.inserters.{ChildInserter, ChildTextInserter, DynamicInserter}
import com.raquo.laminar.modifiers.{RenderableNode, RenderableText}
import com.raquo.laminar.nodes.TextNode

import scala.scalajs.js

object ChildTextReceiver {

  val maybe: ChildTextOptionReceiver.type = ChildTextOptionReceiver

  def <--(textSource: Source[String]): DynamicInserter = {
    ChildTextInserter(textSource.toObservable, RenderableText.stringRenderable)
  }

  def <--[TextLike](textSource: Source[TextLike])(implicit renderable: RenderableText[TextLike]): DynamicInserter = {
    if (renderable == RenderableText.textNodeRenderable) {
      // #Note: Special case: since we already have TextNode-s, using them in ChildTextInserter would be
      //  inefficient, so we redirect this case to ChildInserter (child <-- textSource) instead.
      // #TODO[Perf] Test performance vs regular `text <--`, see if we need to improve this.
      // This .asInstanceOf is safe because `textNodeRenderable` only applies if `TextLike` is `TextNode`.
      val nodes = textSource.toObservable.asInstanceOf[Observable[TextNode]]
      ChildInserter(nodes, RenderableNode.nodeRenderable, initialHooks = js.undefined)
    } else {
      ChildTextInserter(textSource.toObservable, renderable)
    }
  }

}
