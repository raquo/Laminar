package com.raquo.laminar.receivers

import com.raquo.airstream.core.{Observable, Source}
import com.raquo.laminar.inserters.{ChildInserter, ChildTextInserter, DynamicInserter}
import com.raquo.laminar.modifiers.{RenderableNode, RenderableText}
import com.raquo.laminar.nodes.{CommentNode, TextNode}

import scala.scalajs.js

object ChildTextOptionReceiver {

  def <--(textOptSource: Source[Option[String]]): DynamicInserter = {
    <--[String](textOptSource)(RenderableText.stringRenderable)
  }

  def <--[TextLike](
    textSource: Source[Option[TextLike]]
  )(implicit
    renderable: RenderableText[TextLike]
  ): DynamicInserter = {
    if (renderable == RenderableText.textNodeRenderable) {
      // #Note: Special case: since we already have TextNode-s, using them in ChildTextInserter would be
      //  inefficient, so we redirect this case to ChildInserter (child <-- textSource) instead.
      // #TODO[Perf] Test performance vs regular `text <--`, see if we need to improve this.
      // This .asInstanceOf is safe because `textNodeRenderable` only applies if `TextLike` is `TextNode`.
      lazy val emptyNode = new CommentNode("")
      val nodes = textSource.toObservable.asInstanceOf[Observable[Option[TextNode]]].map(_.getOrElse(emptyNode))
      ChildInserter(nodes, RenderableNode.nodeRenderable, initialHooks = js.undefined)
    } else {
      ChildTextInserter.option(textSource.toObservable, renderable)
    }
  }
}
