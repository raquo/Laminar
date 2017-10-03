package com.raquo.laminar

import com.raquo.dombuilder.jsdom.domapi.{JsCommentApi, JsElementApi, JsEventApi, JsTextApi, JsTreeApi}
import com.raquo.laminar.nodes.ReactiveNode

trait DomApi {
  implicit val elementApi: JsElementApi[ReactiveNode] = DomApi.elementApi
  implicit val eventApi: JsEventApi[ReactiveNode] = DomApi.eventApi
  implicit val commentApi: JsCommentApi[ReactiveNode] = DomApi.commentApi
  implicit val textApi: JsTextApi[ReactiveNode] = DomApi.textApi
  implicit val treeApi: JsTreeApi[ReactiveNode] = DomApi.treeApi
}

object DomApi {
  val elementApi: JsElementApi[ReactiveNode] = new JsElementApi[ReactiveNode] {}
  val eventApi: JsEventApi[ReactiveNode] = new JsEventApi[ReactiveNode] {}
  val commentApi: JsCommentApi[ReactiveNode] = new JsCommentApi[ReactiveNode] {}
  val textApi: JsTextApi[ReactiveNode] = new JsTextApi[ReactiveNode] {}
  val treeApi: JsTreeApi[ReactiveNode] = new JsTreeApi[ReactiveNode] {}
}
