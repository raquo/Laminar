package com.raquo.laminar.subscriptions

import com.raquo.laminar.RNode
import com.raquo.laminar.subscriptions.DynamicNodeList.{Append, Diff, Insert, Prepend, Remove, Replace, ReplaceAll}
import com.raquo.xstream.{Listener, ShamefulStream, XStream}

import scala.scalajs.js
import scala.scalajs.js.JavaScriptException

// @TODO[API] Make a more generic version of this and make NodeList extend that with node-related quirks?
// @TODO[API] Throw warnings or exceptions if key not found when adding items
// @TODO[Elegance] Is a better implementation possible?
// @TODO[Performance] Can we cut down on array copying? Should we use Scala collection types instead of js Arrays?

class DynamicNodeList private($diff: XStream[Diff]) {

  $diff.addListener(
    Listener[Diff, Nothing](onNext = updateList)
  )

  /** Stream of a list of nodes in [[DynamicNodeList]].
    *
    * Importantly, new events are only fired in response to [[$diff]] receiving a new command.
    *
    * Child nodes are reactive and can update themselves as needed using [[onNodeUpdate]].
    * However, those updates do not trigger an event on [[$nodes]] because no action is required
    * in response to nodes updating in this way. This is similar to how the parent node is
    * notified about such patches, but does not need to react to them other than updating a
    * reference in its child list.
    */
  val $nodes: XStream[js.Array[RNode]] = XStream.create()

  private val $varNodes = new ShamefulStream($nodes)

  private var nodes: js.Array[RNode] = js.Array()

  /** NodeList needs to have a fresh list of nodes. Child node references can get stale
    * when they are updated with new attrs / props or even when they are replaced with
    * a completely new node by ChildReceiver for example.
    *
    * So whenever a child node is updated, we need to call this method to update it in the
    * NodeList as well.
    *
    * We perform the update in-place here, without firing a new event on $nodes because this
    * is a change in a singular child, not a change in the list of children.
    */
  private[laminar] def onNodeUpdate(newNode: RNode): Unit = {
    if (newNode.key.isEmpty) {
      // @TODO[API] Throw something more useful
      throw JavaScriptException("ERROR: DynamicNodeList / ChildrenReceiver needs every child node proided to it to have a key")
    }
    nodes.update(indexOfKey(newNode.key.get), newNode)
  }

  private def updateList(diff: Diff): Unit = {
    nodes = diff match {
      case Append(node) =>
        node.maybeNodeList = this
        val newNodes = nodes.jsSlice()
        newNodes.push(node)
        newNodes
      case Prepend(node) =>
        node.maybeNodeList = this
        val newNodes = nodes.jsSlice()
        newNodes.unshift(node)
        newNodes
      case Insert(node, atIndex) =>
        node.maybeNodeList = this
        val newNodes = nodes.jsSlice()
        newNodes.splice(atIndex, deleteCount = 0, node)
        newNodes
      case Remove(key) =>
        val newNodes = nodes.jsSlice()
        newNodes.splice(indexOfKey(key), deleteCount = 1)
        newNodes
      case Replace(key, withNode) =>
        withNode.maybeNodeList = this
        val newNodes = nodes.jsSlice()
        newNodes.update(indexOfKey(key), withNode)
        newNodes
      case ReplaceAll(makeNewNodes) =>
        val newNodes = makeNewNodes(nodes)
        newNodes.foreach(_.maybeNodeList = this)
        newNodes
    }
    $varNodes.shamefullySendNext(nodes)
  }

  private def indexOfKey(key: String): Int = {
    // Ugly loop for performance
    var nodeIndex = -1
    var done = false
    while (!done && nodeIndex < nodes.length) {
      nodeIndex += 1
      if (nodes(nodeIndex).key.get == key) {
        done = true
      }
    }
    nodeIndex
  }
}

object DynamicNodeList {

  def apply($diff: XStream[Diff]): DynamicNodeList = {
    new DynamicNodeList($diff)
  }

  sealed trait Diff

  case class Append(node: RNode) extends Diff

  case class Prepend(node: RNode) extends Diff

  case class Insert(node: RNode, atIndex: Int) extends Diff

  case class Remove(key: String) extends Diff

  case class Replace(key: String, withNode: RNode) extends Diff

  /** Note: Do not modify the provided array, create a new one */
  case class ReplaceAll(replace: js.Array[RNode] => js.Array[RNode]) extends Diff

}
