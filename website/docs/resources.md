---
title: Resources
---



## Guides

* [Quick Start](https://laminar.dev/quick-start)

* [Video Introduction to Laminar](https://www.youtube.com/watch?v=L_AHCkl6L-Q)

* [Live Examples](https://laminar.dev/examples/hello-world)

* See also the "Tutorials, Videos & Blog Posts" section below.



## Documentation

* [Documentation](https://laminar.dev/documentation)

* [Laminar API doc](https://javadoc.io/doc/com.raquo/laminar_sjs1_2.13/latest/com/raquo/laminar/index.html) • [Airstream API doc](https://javadoc.io/doc/com.raquo/airstream_sjs1_2.13/latest/index.html)

* [Changelog](https://github.com/raquo/Laminar/blob/master/CHANGELOG.md)



## Videos & Conference Talks

* [Video Introduction to Laminar](https://www.youtube.com/watch?v=L_AHCkl6L-Q)

* [Kit Langton – Stockholm Syndrome Escape Velocity](https://www.youtube.com/watch?v=kLZr87CGY-U)

* [Kit Langton – Deriving the Frontend](https://www.youtube.com/watch?v=JHriftPO62I)


## Community & Support

* [Gitter](https://gitter.im/Laminar_/Lobby) for support, advice, showing off your Laminar projects, etc.

* [Github discussions](https://github.com/raquo/laminar/discussions) for how-tos, whys, and any questions or discussions that other people would benefit from easily finding.

* [Github issues](https://github.com/raquo/laminar/issues) for bugs and feature requests.

* Please consider [sponsoring Laminar development](https://github.com/sponsors/raquo) on Github.



## Useful Tools

* **[Laminext Scribble](https://scribble.ninja/)** to try things on the fly.

* [Html to Scala Converter](https://simerplaha.github.io/html-to-scala-converter/) can convert HTML strings to Laminar code.



## Open Source Laminar Apps

* **[Twotm8](https://github.com/keynmol/twotm8)** – Twitter clone. See [demo](https://twotm8-web.fly.dev/login) and also the [detailed tutorial](https://blog.indoorvivants.com/2022-03-07-twotm8-part-5-building-the-frontend).

* [Skłable](https://github.com/przemekd/sklable) – multiplayer Scrabble-like game. [Demo](https://sk%C5%82able.pl)

* [Laika-Laminar](https://github.com/i10416/laika-laminar) – converts Markdown and ReStructuredText to HTML using [Laika](https://planet42.github.io/Laika/). [Demo](https://i10416.github.io/demo/)

* [NPC Generator](https://gitlab.com/scala-js-games/npc-generator/)

* [Hippo](https://github.com/indoorvivants/hippo) – web-based heap dump viewer.



## Starter Kits & More Examples

* [Live examples](https://laminar.dev/examples/hello-world) on this website

* [keynmol/http4s-laminar-stack](https://github.com/keynmol/http4s-laminar-stack) – Laminar setup with http4s, sttp, docker, and other niceties

* [b12consulting/Laminar-Play-Slick-ZIO](https://github.com/b12consulting/Laminar-Play-Slick-ZIO)

* [sherpal/full-scala-scala-heroku](https://github.com/sherpal/full-scala-scala-heroku) – Laminar setup with Play & Heroku

* [keynmol/laminar-static-gh-pages](https://github.com/keynmol/laminar-static-gh-pages/) – Template for publishing a static site with Laminar

* [keynmol/scalajs-tauri-example](https://github.com/keynmol/scalajs-tauri-example) – Example of using Laminar with [Tauri](https://tauri.studio/) to build a Desktop application, similar to Electron

* [Quafadas/cask-laminar](https://github.com/Quafadas/cask-laminar) – Laminar with [Cask](https://com-lihaoyi.github.io/cask/) setup, based on keynmol/http4s-laminar-stack

* [vic/laminar_cycle/examples](https://github.com/vic/laminar_cycle/tree/master/examples) – Examples of Cycle.js style apps in Laminar

* [raquo/laminar-examples](https://github.com/raquo/laminar-examples) – an old repo with some Laminar examples, most of which you can see on this website already.



## Examples without Scalajs-bundler

* **[sjrd/scalajs-sbt-vite-laminar-chartjs-example](https://github.com/sjrd/scalajs-sbt-vite-laminar-chartjs-example)** – Setup with [Vite](https://vitejs.dev/), Laminar and Chart.js (via [ScalablyTyped](https://scalablytyped.org/docs/readme.html))

* [lolgab/scala-fullstack](https://github.com/lolgab/scala-fullstack) – Laminar setup with Akka HTTP, [Mill](https://github.com/lihaoyi/mill), [Sloth](https://github.com/cornerman/sloth)

* [yurique/scala-js-laminar-starter.g8](https://github.com/yurique/scala-js-laminar-starter.g8) – Laminar setup with Akka HTTP, Tailwind CSS, [Waypoint](https://github.com/raquo/Waypoint/), and a pure webpack config instead of scalajs-bundler 

* [kitlangton/zio-app](https://github.com/kitlangton/zio-app) – Quickly create apps with ZIO and Laminar

* [yurique/laminar-vite2-example](https://github.com/yurique/laminar-vite2-example) – Example of a [Vite](https://vitejs.dev/) build for a Scala.js + Laminar + [frontroute](https://github.com/tulz-app/frontroute/) app.

* [yurique/laminar-snowpack-example](https://github.com/yurique/laminar-snowpack-example) - Laminar setup with [Snowpack](https://www.snowpack.dev/) (instead of Webpack) and [frontroute](https://github.com/tulz-app/frontroute)

Warning: Using `ModuleKind.ESModule` (e.g when using Vite or Snowpack) results in bigger JS bundle sizes than usual because Scala.js is unable to use gcc in this case. [Scala.js #3893](https://github.com/scala-js/scala-js/issues/3893)

* [keynmol/scalajs-tauri-example](https://github.com/keynmol/scalajs-tauri-example) – Example of using Laminar with [Tauri](https://tauri.studio/) and Vite to build a Desktop application, similar to Electron


## Addons & Extensions

* **[Laminext](https://laminext.dev/)** – A collection of useful extensions, utilities and components for Laminar and Airstream

* [Waypoint](https://github.com/raquo/Waypoint) – URL router for Laminar

* [frontroute](https://github.com/tulz-app/frontroute) – Alternative router for Laminar with API inspired by Akka HTTP

* [Formula](https://github.com/kitlangton/formula) – Derive functional, reactive, Laminar forms at compile-time with this type-safe, composable form combinator library

* [Laminar.cycle](https://github.com/vic/laminar_cycle) – Cycle.js dialogue abstraction in Laminar



## Component Libraries

* **[SAP UI5 Laminar Web Components](https://github.com/sherpal/LaminarSAPUI5Bindings)**

* [Material UI Laminar Web Components](https://github.com/uosis/laminar-web-components)

* Or, easily make your own interfaces: [runnable example](https://github.com/raquo/laminar-examples/tree/master/src/main/scala/webcomponents)



## Tutorials, Videos & Blog Posts

* [My Four Year Quest For Perfect Scala.js UI Development](https://dev.to/raquo/my-four-year-quest-for-perfect-scala-js-ui-development-b9a) – the history and ideological foundations of Laminar

* **[[VIDEO] Laminar – Smooth UI Development with Scala.js](https://www.youtube.com/watch?v=L_AHCkl6L-Q)** – the why and the how of building with Laminar

* **[[VIDEO] Stockholm Syndrome Escape Velocity](https://www.youtube.com/watch?v=kLZr87CGY-U)** – Kit Langton sets an impossible standard in conference talk delivery

* [[VIDEO] Deriving the Frontend – Form Combinator Fun with Scala.js](https://www.youtube.com/watch?v=JHriftPO62I)

  * See [updated repo](https://github.com/kitlangton/formula) for safe management of Owner-s

* **[Twotm8 – Building the Frontend](https://blog.indoorvivants.com/2022-03-07-twotm8-part-5-building-the-frontend)** walks you through creating a twitter clone in full stack Scala. The chapter on frontend explains how to use Laminar, implement routing with [Waypoint](https://github.com/raquo/waypoint), and styling with [ScalaCSS](https://github.com/japgolly/scalacss). See also the [source code](https://github.com/keynmol/twotm8) and [demo](https://twotm8-web.fly.dev/login).

* [Cross-Platform Mobile dev with Scala and Capacitor](https://medium.com/geekculture/cross-platform-mobile-dev-with-scala-and-capacitor-54e69b62b50c)

* SoftwareMill's [Hands-on Laminar](https://blog.softwaremill.com/hands-on-laminar-354ddcc536a9?gi=167c9cdb6442) article includes form validation, and integration with ScalaCSS and Monocle

* Alvin Alexander's [Laminar 101](https://alvinalexander.com/scala/laminar-101-hello-world-example-static/), [102](https://alvinalexander.com/scala/laminar-102-reactive-hello-world-example/), and [103](https://alvinalexander.com/scala/laminar-103-reactive-routing-example/) tutorials take you from setting up a static Laminar page to implementing a basic router.



## Other Related Projects

* [Airstream](https://github.com/raquo/Airstream/) – The reactive layer of Laminar. Its docs explain everything about streams, signals, event buses, state vars, etc.

* [Scala DOM Types](https://github.com/raquo/scala-dom-types) – Type definitions that we use for all the HTML tags, attributes, properties, and styles.

* [ew](https://github.com/raquo/ew) – Scala.js interfaces to native JavaScript data structures (such as arrays and maps) that offer sometimes significant performance advantages by sacrificing Scala semantics (such as structural equality of case classes).
