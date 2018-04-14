package com.raquo.laminar.fixtures

import com.raquo.airstream.ownership.{Owned, Owner}

import scala.scalajs.js

// @TODO[Elegance] This duplicates a fixture defined in Airstream
class TestableOwner extends Owner {

  def _testPossessions: js.Array[Owned] = possessions

  override def killPossessions(): Unit = {
    super.killPossessions()
  }
}
