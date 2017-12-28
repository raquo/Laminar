package com.raquo.laminar.experimental.airstream.ownership

import org.scalatest.{FunSpec, Matchers}

class OwnerSpec extends FunSpec with Matchers {

  class TestOwned(override val owner: Owner) extends Owned {

    var killCount = 0

    override private[airstream] def kill(): Unit = {
      killCount += 1
    }
  }

  it("Owner kills all possessions when discarded and continues to function") {

    val owner = new Owner {}

    val possession1 = new TestOwned(owner)
    val possession2 = new TestOwned(owner)

    owner.possessions.toSeq shouldEqual List(possession1, possession2)
    possession1.killCount shouldBe 0
    possession2.killCount shouldBe 0

    owner.killPossessions()

    // Killing possessions calls kill on each of them exactly once, and then clears the list of possessions
    owner.possessions.toSeq shouldEqual Nil
    possession1.killCount shouldBe 1
    possession2.killCount shouldBe 1

    val possession3 = new TestOwned(owner)

    // Owner still functions as normal even after the killing spree
    owner.possessions.toSeq shouldEqual List(possession3)
    possession1.killCount shouldBe 1
    possession2.killCount shouldBe 1
    possession3.killCount shouldBe 0

    owner.killPossessions()
    possession1.killCount shouldBe 1
    possession2.killCount shouldBe 1
    possession3.killCount shouldBe 1

    // Double-chek that killing again does not result in double-kill
    owner.killPossessions()
    possession1.killCount shouldBe 1
    possession2.killCount shouldBe 1
    possession3.killCount shouldBe 1
  }
}
