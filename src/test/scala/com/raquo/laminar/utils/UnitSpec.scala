package com.raquo.laminar.utils

import com.raquo.domtestutils.Utils
import com.raquo.domtestutils.scalatest.MountSpec
import org.scalatest.Suite
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

trait SpecLike
  extends Suite
  with Matchers
  with LaminarSpec
  with MountSpec
  with Utils

class UnitSpec extends AnyFunSpec with SpecLike
