package com.raquo.laminar.inserters

import com.raquo.airstream.core.AirstreamError
import com.raquo.laminar.nodes.{ChildNode, ParentNode}

import scala.scalajs.js

/** #TODO This API is experimental, and is likely to change in the future.
  *
  * We currently use it only for slotting elements into web components,
  * but will likely use it more broadly later.
  *
  * NOTE: Currently hooks do not run on _some_ text nodes. They are also
  * not run on some sentinel comment nodes, because only element nodes are
  * slottable. So, this is fine for slotting purposes, but that's the
  * kind of thing that will need a more principled contract if this API
  * is to be used more widely.
  *
  * WARNING: Your hooks should not throw!
  * Any thrown errors will be sent to Airstream unhandled errors.
  *
  * @param _onWillInsertNode Called before the child will be inserted
  *                          into the parent.
  *                           - NOTE: this includes moving the child from
  *                             one position to another in the same parent.
  *                           - NOTE: this can be called when no action
  *                             is necessary (i.e. child is already at the
  *                             target location)
  */
class InserterHooks(
  val _onWillInsertNode: (ParentNode.Base, ChildNode.Base) => Unit
) { self =>

  def onWillInsertNode(parent: ParentNode.Base, child: ChildNode.Base): Unit = {
    try {
      _onWillInsertNode(parent, child)
    } catch {
      case err: Throwable =>
        AirstreamError.sendUnhandledError(err)
    }
  }

  def concat(newHooks: InserterHooks): InserterHooks = {
    new InserterHooks(
      _onWillInsertNode = { (newParent, newChild) =>
        self._onWillInsertNode(newParent, newChild)
        newHooks._onWillInsertNode(newParent, newChild)
      }
    )
  }

  def appendTo(currentHooks: js.UndefOr[InserterHooks]): InserterHooks = {
    currentHooks.fold(this)(_.concat(this))
  }
}
