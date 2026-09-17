package com.raquo.laminar.receivers

import com.raquo.airstream.core.{Observable, Source}
import com.raquo.laminar.inserters.{ChildInserter, ChildTextInserter, DynamicInserter}
import com.raquo.laminar.modifiers.{RenderableNode, RenderableText}
import com.raquo.laminar.nodes.{CommentNode, TextNode}

import scala.scalajs.js

object ChildTextOptionReceiver {

  def <--(textOptSource: Source[Option[String]]): DynamicInserter = {
    <--[String](textOptSource)(using RenderableText.stringRenderable)
  }

  def <--[TextLike](
    textSource: Source[Option[TextLike]]
  )(implicit
    renderable: RenderableText[TextLike]
  ): DynamicInserter = {
    if (renderable == RenderableText.textNodeRenderable) {
      // #Note: Special case: since we already have TextNode-s, using them in ChildTextInserter would be
      //  inefficient, so we redirect this case to ChildInserter (child <-- textSource) instead.
      // #Note: this is also relevant for correctness: if textSource gives us TextNode-s, that means
      //  something else external to this inserter might have references to them, meaning that such nodes
      //  can be stolen. `ChildTextInserter.option` uses simpler logic in some branches that assumes that
      //  its text nodes can't be stolen – if we were to make it use externally created TextNode-s without
      //  adjusting its logic correspondingly, this could cause a bug.
      //  We have a test for stealing externally provided TextNode-s from text <--.
      // #TODO[Perf] Test performance vs regular `text <--`, see if we need to improve this.
      // This .asInstanceOf is safe because `textNodeRenderable` only applies if `TextLike` is `TextNode`.
      lazy val emptyNode = new CommentNode("")
      val nodes = textSource.toObservable.asInstanceOf[Observable[Option[TextNode]]].map(_.getOrElse(emptyNode))
      ChildInserter(nodes, RenderableNode.nodeRenderable, initialSlotName = ())
    } else {
      ChildTextInserter.option(textSource.toObservable, renderable)
    }
  }
}
