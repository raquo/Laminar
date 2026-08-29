package com.raquo.laminar.fixtures

import com.raquo.airstream.ownership.{Owner, Subscription}
import com.raquo.ew.JsArray

// @TODO[Elegance] This duplicates a fixture defined in Airstream
class TestableOwner extends Owner {

  def _testSubscriptions: List[Subscription] = subscriptions.toList

  override def killSubscriptions(): Unit = {
    super.killSubscriptions()
  }
}
