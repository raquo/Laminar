package com.raquo.laminar.exceptions

// @TODO[API] Do we need this? It's not a sealed trait anyway...

abstract class LaminarException(message: String = "", cause: Option[Throwable] = None)
  extends RuntimeException(message)
{
  cause.foreach(initCause)
}
