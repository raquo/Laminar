package com.raquo.laminar.experimental.airstream.propagation

// @TODO So we ended up not using this in Streams, their lazyness works differently...

trait Parent[Child] {

  // @TODO "children" functionality probably belongs in the Observable trait (yes, really, even despite different memory management for streams)
  // @TODO this is "protected" only for testing, otherwise could be private. Maybe use proper test tools
  /** Note: This is enforced to be a set outside the type system (@TODO[Integrity]: or is it? It should.). #performance */
  protected[this] var linkedChildren: Seq[Child] = Nil

//  private[airstream] def hasChild(child: Child): Boolean = {
//    children.indexOf(child) != -1
//  }

  // @TODO[WTF] Why can't this be simply "protected"? Because specialization?
  /** Warning: It is up to the caller to ensure that it doesn't add a duplicate child.
    * concrete ComputedSignal subclasses and Stream have different ways of achieving that. */
  private[airstream] def linkChild(child: Child): Unit = {
    linkedChildren = linkedChildren :+ child
  }

  private[airstream] def unlinkChild(child: Child): Unit = {
    val index = linkedChildren.indexOf(child)
    if (index != -1) {
      val parts = linkedChildren.splitAt(index)
      parts._1 ++ parts._2
    }
  }

  private[airstream] def unlinkAllChildren(): Unit = {
    linkedChildren = Nil
  }
}
