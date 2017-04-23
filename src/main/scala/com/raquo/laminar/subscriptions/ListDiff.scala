package com.raquo.laminar.subscriptions

sealed trait ListDiff[Item, Container[_]]

// @TODO[Naming] this does not belong in `subscriptions` namespace

object ListDiff {

  case class Append[Item, Container[_]](item: Item)
    extends ListDiff[Item, Container]

  case class Prepend[Item, Container[_]](item: Item)
    extends ListDiff[Item, Container]

  case class Insert[Item, Container[_]](item: Item, atIndex: Int)
    extends ListDiff[Item, Container]

  case class Remove[Item, Container[_]](item: Item)
    extends ListDiff[Item, Container]

  case class Replace[Item, Container[_]](item: Item, withItem: Item)
    extends ListDiff[Item, Container]

  case class ReplaceAll[Item, Container[_]](newItems: Container[Item])
    extends ListDiff[Item, Container]
}
