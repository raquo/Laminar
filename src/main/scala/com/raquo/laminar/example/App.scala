package com.raquo.laminar.example

import com.raquo.laminar._
import com.raquo.laminar.example.components.{Counter, Table}
import com.raquo.laminar.example.pseudotests.{MultiSetters, MultiStyleProp, NestedStyleProp, NodeTypeChange}
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Event

import scala.scalajs.js

object App extends js.JSApp {

  def main(): Unit = {
    document.addEventListener("DOMContentLoaded", (e: Event) => {
      dom.console.log("=== DOMContentLoaded ===")

      render(document.getElementById("laminar-container"), MultiSetters())

//      ManualTest()
    })
  }}
