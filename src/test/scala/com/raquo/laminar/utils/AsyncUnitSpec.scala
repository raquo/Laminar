package com.raquo.laminar.utils

import com.raquo.domtestutils.Utils
import com.raquo.domtestutils.scalatest.AsyncMountSpec
import org.scalatest.funspec.AsyncFunSpec

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.util.Try

class AsyncUnitSpec
  extends AsyncFunSpec
  with LaminarSpec
  with AsyncMountSpec
  with Utils {

  // @TODO[Test] Extract this to a more generic place
  def delay[V](value: => V): Future[V] = {
    delay(0)(value)
  }

  def delay[V](millis: Int)(value: => V): Future[V] = {
    val promise = Promise[V]()
    js.timers.setTimeout(millis.toDouble) {
      promise.complete(Try(value))
    }
    promise.future
  }
}
