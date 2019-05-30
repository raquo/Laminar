package com.raquo.laminar.fixtures.example

import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.example.pseudotests.Children
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Event

import scala.scalajs.js

object App extends js.JSApp {

  def main(): Unit = {
    document.addEventListener("DOMContentLoaded", (e: Event) => {
      dom.console.log("=== DOMContentLoaded ===")

      val container = document.getElementById("app-container")
      container.textContent = ""

//      render(container, MultiSetters())
      render(container, Children())
//      render(container, new TaskList().node)
    })
  }}
