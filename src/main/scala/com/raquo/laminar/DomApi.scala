package com.raquo.laminar

import com.raquo.dombuilder.jsdom.domapi.{JsCommentApi, JsEventApi, JsHtmlElementApi, JsSvgElementApi, JsTextApi, JsTreeApi}
import com.raquo.laminar.nodes.ReactiveNode

trait DomApi {
  implicit val htmlElementApi: JsHtmlElementApi[ReactiveNode] = DomApi.htmlElementApi
  implicit val svgElementApi: JsSvgElementApi[ReactiveNode] = DomApi.svgElementApi
  implicit val eventApi: JsEventApi[ReactiveNode] = DomApi.eventApi
  implicit val commentApi: JsCommentApi[ReactiveNode] = DomApi.commentApi
  implicit val textApi: JsTextApi[ReactiveNode] = DomApi.textApi
  implicit val treeApi: JsTreeApi[ReactiveNode] = DomApi.treeApi
}

object DomApi {
  val htmlElementApi: JsHtmlElementApi[ReactiveNode] = new JsHtmlElementApi[ReactiveNode] {}
  val svgElementApi: JsSvgElementApi[ReactiveNode] = new JsSvgElementApi[ReactiveNode] {}
  val eventApi: JsEventApi[ReactiveNode] = new JsEventApi[ReactiveNode] {}
  val commentApi: JsCommentApi[ReactiveNode] = new JsCommentApi[ReactiveNode] {}
  val textApi: JsTextApi[ReactiveNode] = new JsTextApi[ReactiveNode] {}
  val treeApi: JsTreeApi[ReactiveNode] = new JsTreeApi[ReactiveNode] {}
}
