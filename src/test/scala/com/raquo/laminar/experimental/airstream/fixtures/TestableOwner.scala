package com.raquo.laminar.experimental.airstream.fixtures

import com.raquo.laminar.experimental.airstream.ownership.Owner

class TestableOwner extends Owner {

  override def killPossessions(): Unit = {
    super.killPossessions()
  }
}
