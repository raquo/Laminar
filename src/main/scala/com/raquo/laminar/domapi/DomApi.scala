package com.raquo.laminar.domapi

object DomApi
extends DomTree
with DomEvents
with DomKeys
with DomTags
with DomInputs
with DomParser
with DomDebug {

  object raw
  extends DomTreeRaw
}
