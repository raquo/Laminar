package com.raquo.laminar

object PlatformSpecific {

  // #nc
  // type StringOr[V] = V match {
  //   case String => String
  //   case _ => V | String
  // }

  type StringOr[A] = A | String
}
