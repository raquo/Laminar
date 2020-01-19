package com.raquo.laminar.fixtures

import com.raquo.airstream.ownership.{Subscription, Owner}

import scala.scalajs.js

// @TODO[Elegance] This duplicates a fixture defined in Airstream
class TestableOwner extends Owner {

  def _testSubscriptions: js.Array[Subscription] = subscriptions

  override def killSubscriptions(): Unit = {
    super.killSubscriptions()
  }
}
