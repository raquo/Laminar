package com.raquo.laminar.fixtures.example

import com.raquo.laminar.api.L._
import com.raquo.laminar.fixtures.example.pseudotests.Children
import org.scalajs.dom

object App {

  def main(): Unit = {
    dom.document.addEventListener("DOMContentLoaded", (e: dom.Event) => {
      dom.console.log("=== DOMContentLoaded ===")

      val container = dom.document.getElementById("appContainer")
      container.textContent = ""

//      render(container, MultiSetters())
      render(container, Children())
//      render(container, new TaskList().node)
    })
  }
}
