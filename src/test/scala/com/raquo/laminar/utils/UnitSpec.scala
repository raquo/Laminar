package com.raquo.laminar.utils

import com.raquo.airstream.core.AirstreamError
import com.raquo.domtestutils.Utils
import com.raquo.domtestutils.scalatest.{Matchers, MountSpec}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec

class UnitSpec
extends AnyFunSpec
with LaminarSpec
with MountSpec
with Matchers
with Utils
with BeforeAndAfterAll {

  // These help detect and track unexpected unhandled errors.
  // Tests that want to

  override protected def beforeAll(): Unit = {
    AirstreamError.unregisterUnhandledErrorCallback(AirstreamError.consoleErrorCallback)
    AirstreamError.registerUnhandledErrorCallback(AirstreamError.unsafeRethrowErrorCallback)
  }

  override protected def afterAll(): Unit = {
    AirstreamError.registerUnhandledErrorCallback(AirstreamError.consoleErrorCallback)
    AirstreamError.unregisterUnhandledErrorCallback(AirstreamError.unsafeRethrowErrorCallback)
  }

}
