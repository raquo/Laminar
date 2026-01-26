package com.raquo

package object laminar {

  @deprecated("com.raquo.laminar.DomApi was moved to com.raquo.laminar.domapi.DomApi", since = "18.0.0-M1")
  lazy val DomApi: domapi.DomApi.type = domapi.DomApi
}
