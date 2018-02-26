package com.raquo.laminar.experimental.airstream.ownership

import com.raquo.laminar.experimental.airstream.fixtures.{TestableOwned, TestableOwner}
import org.scalatest.{FunSpec, Matchers}

class OwnerSpec extends FunSpec with Matchers {

  it("Owner kills all possessions when discarded and continues to function") {

    val owner = new TestableOwner

    val possession1 = new TestableOwned(owner)
    val possession2 = new TestableOwned(owner)

    owner._testPossessions.toSeq shouldEqual List(possession1, possession2)
    possession1.killCount shouldBe 0
    possession2.killCount shouldBe 0

    owner.killPossessions()

    // Killing possessions calls kill on each of them exactly once, and then clears the list of possessions
    owner._testPossessions.toSeq shouldEqual Nil
    possession1.killCount shouldBe 1
    possession2.killCount shouldBe 1

    val possession3 = new TestableOwned(owner)

    // Owner still functions as normal even after the killing spree
    owner._testPossessions.toSeq shouldEqual List(possession3)
    possession1.killCount shouldBe 1
    possession2.killCount shouldBe 1
    possession3.killCount shouldBe 0

    owner.killPossessions()
    possession1.killCount shouldBe 1
    possession2.killCount shouldBe 1
    possession3.killCount shouldBe 1

    // Double-check that killing again does not result in double-kill
    owner.killPossessions()
    possession1.killCount shouldBe 1
    possession2.killCount shouldBe 1
    possession3.killCount shouldBe 1

    // @TODO[Airstream] Check that killing manually does not result in double-kill
  }
}
