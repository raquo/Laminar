package com.raquo.laminar.receivers

import com.raquo.airstream.core.Source
import com.raquo.laminar.modifiers.{ChildTextInserter, Inserter, RenderableText}

object ChildTextReceiver {

  def <--[TextLike](textSource: Source[TextLike])(implicit renderable: RenderableText[TextLike]): Inserter.Base = {
    ChildTextInserter(textSource.toObservable, renderable)
  }

  // #TODO[API] I disabled this method because the more general <-- method below
  //  covers this use case as well. Retaining both methods eliminates the need for
  //  implicit resolution (user code compilation should be a bit faster), however
  //  it also degrades user experience with hard-to-parse "overloaded method with
  //  alternatives..." compiler errors when the user provides the wrong type of
  //  `textSource`.

  // def <--(textSource: Source[String]): Inserter.Base = {
  //   ChildTextInserter(textSource.toObservable, RenderableText.stringRenderable)
  // }
}
