package com.raquo.laminar.inserters

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

  case class Replace[+Item](oldItem: Item, newItem: Item) extends CollectionCommand[Item] {

    @inline override def map[A](project: Item => A): Replace[A] = {
      Replace(project(oldItem), project(newItem))
    }
  }

  // @TODO[Performance,Integrity] Is Vector an appropriate data structure for our use case? Maybe use SortedSet, or TreeSet?
  // @TODO[Performance] Actually, seeing as this interface is intended for performance, js.Array is probably a better choice

  // @TODO[Test] vectorProcessor needs unit testing
  // @TODO Why do we even keep this `vectorProcessor` thing around? We don't use it.

  def vectorProcessor[Item](prevItems: Vector[Item], command: CollectionCommand[Item]): Vector[Item] = {
    command match {
      case Append(item) =>
        prevItems :+ item

      case Prepend(item) =>
        item +: prevItems

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

      case Replace(oldItem, newItem) =>
        val index = prevItems.indexOf(oldItem)
        if (index == -1) {
          prevItems
        } else {
          prevItems.updated(index, newItem)
        }
    }
  }
}
