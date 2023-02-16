package com.raquo.laminar

import com.raquo.laminar.nodes.ChildNode

sealed trait CollectionCommand[+Item] {

  @inline def map[A](project: Item => A): CollectionCommand[A]
}

object CollectionCommand {

  type Base = CollectionCommand[ChildNode.Base]

  case class Append[+Item](item: Item) extends CollectionCommand[Item] {

    @inline override def map[A](project: Item => A): Append[A] = {
      Append(project(item))
    }
  }

  case class Prepend[+Item](item: Item) extends CollectionCommand[Item] {

    @inline override def map[A](project: Item => A): Prepend[A] = {
      Prepend(project(item))
    }
  }

  case class Insert[+Item](item: Item, atIndex: Int) extends CollectionCommand[Item] {

    @inline override def map[A](project: Item => A): Insert[A] = {
      Insert(project(item), atIndex)
    }
  }

  case class Remove[+Item](item: Item) extends CollectionCommand[Item] {

    @inline override def map[A](project: Item => A): Remove[A] = {
      Remove(project(item))
    }
  }

  /** @param toIndex
    *    the index at which the item would end up IF it hadn't been removed from its current position.
    *    basically, this is the CURRENT index of an item in front of which the item will be moved to. */
  case class Move[+Item](item: Item, toIndex: Int) extends CollectionCommand[Item] {

    @inline override def map[A](project: Item => A): Move[A] = {
      Move(project(item), toIndex)
    }
  }

  case class Replace[+Item](oldItem: Item, newItem: Item) extends CollectionCommand[Item] {

    @inline override def map[A](project: Item => A): Replace[A] = {
      Replace(project(oldItem), project(newItem))
    }
  }

  case class ReplaceAll[+Item](newItems: Iterable[Item]) extends CollectionCommand[Item] {

    @inline override def map[A](project: Item => A): ReplaceAll[A] = {
      ReplaceAll(newItems.map(project))
    }
  }

  // @TODO[Performance,Integrity] Is Vector an appropriate data structure for our use case? Maybe use SortedSet, or TreeSet?
  // @TODO[Performance] Actually, seeing as this interface is intended for performance, js.Array is probably a better choice

  // @TODO[Test] vectorProcessor needs unit testing
  // @TODO Why do we even keep this `vectorProcessor` thing around? We don't use it.

  def vectorProcessor[Item](prevItems: Vector[Item], command: CollectionCommand[Item]): Vector[Item] = {
    command match {
      case Append(item) => prevItems :+ item
      case Prepend(item) => item +: prevItems
      case Insert(item, atIndex) =>
        // @TODO[Integrity] handle out of bound index
        val chunks = prevItems.splitAt(atIndex)
        (chunks._1 :+ item) ++ chunks._2
      case Remove(item) =>
        val index = prevItems.indexOf(item)
        if (index == -1) {
          prevItems
        } else if (index == 0) {
          prevItems.drop(1)
        } else {
          prevItems.take(index) ++ prevItems.drop(index + 1)
        }
      case Move(item, toIndex) =>
        val oldIndex = prevItems.indexOf(item)
        if (oldIndex == -1) {
          vectorProcessor(prevItems, Insert(item, toIndex))
        } else if (oldIndex == toIndex) {
          prevItems
        } else {
          val newIndex = if (oldIndex > toIndex) {
            toIndex
          } else {
            toIndex - 1
          }
          vectorProcessor(vectorProcessor(prevItems, Remove(item)), Insert(item, newIndex))
        }
      case Replace(oldItem, newItem) =>
        val index = prevItems.indexOf(oldItem)
        if (index == -1) {
          prevItems
        } else {
          prevItems.updated(index, newItem)
        }
      case ReplaceAll(newItems) =>
        newItems.toVector
    }
  }
}
