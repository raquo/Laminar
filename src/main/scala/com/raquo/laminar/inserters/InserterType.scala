package com.raquo.laminar.inserters

import com.raquo.laminar.nodes.ChildNode

/** Inserter type is tracked in [[InsertContext.lastInserterType]]
  * and is used to make decisions on trailing sentinel ndoes etc.
  */
sealed abstract class InserterType(
  val needsTrailingSentinel: Boolean
)

object InserterType {

  /** [[ChildrenCommandInserter]] only.
    * It works the same as [[ChildrenType]] – even tracks the
    * content nodes in [[InsertContext.contentMap]], but is
    * tracked as a separate type because when switching to this
    * inserter, we clear the previous content nodes for any
    * OTHER inserter type.
    */
  case object ChildrenCommandType extends InserterType(
    needsTrailingSentinel = true
  )

  /** [[ChildrenInserter]], [[HookableChildrenInserter]],
    * and potentially anything else that might call
    * [[ChildrenInserter.switchToChildren]] in the future.
    */
  case object ChildrenType extends InserterType(
    needsTrailingSentinel = true
  )

  /** [[ChildInserter]], [[ChildTextInserter]], [[ChildNode]],
    * [[HookableChildInserter]], and potentially anything else
    * that might call [[ChildInserter.switchToChild]] in the future.
    */
  case object ChildType extends InserterType(
    needsTrailingSentinel = false
  )
}
