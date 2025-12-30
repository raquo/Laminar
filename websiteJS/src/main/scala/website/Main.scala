package website

import webcomponents.material

/** Note: The purpose of this file is to prevent dead-code elimination
  *       of JS dependencies required by the examples in the website
  *       project. Make sure to use each of the dependencies in the main
  *       method here.
  *
  *       See https://scalameta.org/mdoc/docs/js.html#bundle-npm-dependencies
  */
object Main {

  def main(args: Array[String]): Unit = {
    material.Button
    material.LinearProgressBar
    material.Slider
  }
}
