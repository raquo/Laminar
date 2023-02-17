package com.raquo.laminar.receivers

import com.raquo.airstream.core.{Observable, Source}
import com.raquo.laminar.modifiers.{ChildInserter, ChildTextInserter, Inserter, RenderableNode, RenderableText}
import com.raquo.laminar.nodes.TextNode

object ChildTextReceiver {

  def <--(textSource: Source[String]): Inserter.Base = {
    ChildTextInserter(textSource.toObservable, RenderableText.stringRenderable)
  }

  def <--[TextLike](textSource: Source[TextLike])(implicit renderable: RenderableText[TextLike]): Inserter.Base = {
    if (renderable == RenderableText.textNodeRenderable) {
      // #Note: Special case: since we already have TextNode-s, using them in ChildTextInserter would be
      //  inefficient, so we redirect this case to ChildInserter (child <-- textSource) instead.
      // #TODO[Perf] Test performance vs regular child.text, see if we need to improve this.
      // This .asInstanceOf is safe because `textNodeRenderable` only applies if `TextLike` is `TextNode`.
      val nodes = textSource.toObservable.asInstanceOf[Observable[TextNode]]
      ChildInserter(nodes, RenderableNode.nodeRenderable)
    } else {
      ChildTextInserter(textSource.toObservable, renderable)
    }
  }

  // #TODO[Scala] For some reason I am unable to put the above <-- method inside RichTextReceiver
  //  - Scala 2 compiler explodes with "Error while emitting ChildrenReceiverSpec.scala"
  //  - Scala 3 stops compiling some basic usage patterns of `child.text <--`

  // implicit class RichTextReceiver(val self: ChildTextReceiver.type) extends AnyVal {
  //
  //   def <--(textSource: Source[TextNode]): Inserter.Base = {
  //     ChildTextInserter(textSource.toObservable, RenderableText.textNodeRenderable)
  //   }
  //
  //   def <--[TextLike](textSource: Source[TextLike])(implicit renderable: RenderableText[TextLike]): Inserter.Base = {
  //     if (renderable == RenderableText.textNodeRenderable) {
  //       // Special case: since we already have TextNode-s, using them in ChildTextInserter would be
  //       // inefficient, so we redirect this case to ChildInserter (child <-- textSource) instead.
  //       // #TODO[Perf] Test performance vs regular child.text, see if we need to improve this.
  //       // #Note: this .asInstanceOf is safe because `RenderableText.textNodeRenderable`
  //       //  will only apply is `TextLike` type is `TextNode`.
  //       val nodes = textSource.toObservable.asInstanceOf[Observable[TextNode]]
  //       ChildInserter(nodes, RenderableNode.nodeRenderable)
  //     } else {
  //       ChildTextInserter(textSource.toObservable, renderable)
  //     }
  //   }
  // }

}
