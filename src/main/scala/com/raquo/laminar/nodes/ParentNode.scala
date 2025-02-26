package com.raquo.laminar.nodes

import com.raquo.airstream.ownership.DynamicOwner
import com.raquo.laminar.domapi.DomApi
import org.scalajs.dom

trait ParentNode[+Ref <: dom.Element] extends ReactiveNode[Ref] {

  private[laminar] val dynamicOwner: DynamicOwner = new DynamicOwner(() => {
    val path = DomApi.debugPath(ref).mkString(" > ")
    throw new Exception(s"Attempting to use owner of unmounted element: $path")
  })

}

object ParentNode {

  type Base = ParentNode[dom.Element]

}
