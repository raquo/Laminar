package com.raquo.laminar.experimental.airstream.fixtures

import com.raquo.laminar.experimental.airstream.ownership.{Owned, Owner}

import scala.scalajs.js

class TestableOwner extends Owner {

  def _testPossessions: js.Array[Owned] = possessions

  override def killPossessions(): Unit = {
    super.killPossessions()
  }
}
