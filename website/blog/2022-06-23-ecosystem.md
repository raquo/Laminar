---
title: 2022 Laminar Ecosystem Updates
author: Nikita
authorURL: http://twitter.com/raquo
---

Here is a deluge of new libraries, web components, tutorials, sponsors – all the news that couldn't wait until the next Laminar release. Hope I didn't miss anything!

<!--truncate-->

> Laminar is a native Scala.js library for building web application interfaces. Learn more in [a few short examples](https://laminar.dev/examples/hello-world) or [one long video](https://www.youtube.com/watch?v=L_AHCkl6L-Q).




## New GOLD Sponsor

[![Aurinko logo](/img/sponsors/aurinko-light-300px.png)](https://www.aurinko.io/)

[Aurinko](https://www.aurinko.io/) is an API platform for workplace addons and integrations. They have [built](https://twitter.com/yoxeldotcom/status/1537415506801700865) a number of apps using Laminar, for example this [appointment scheduler widget](https://calendar.aurinko.io/aurDemo60).




## New Tutorials

**Anton Sviridov**'s [Twotm8](https://blog.indoorvivants.com/2022-03-07-twotm8-part-5-building-the-frontend) epic walks you through creating a twitter clone in full stack Scala. The chapter on frontend explains how to use Laminar, implement routing with [Waypoint](https://github.com/raquo/waypoint), and styling with [ScalaCSS](https://github.com/japgolly/scalacss). See also the [source code](https://github.com/keynmol/twotm8) and [demo](https://twotm8-web.fly.dev/login).

**Alvin Alexander**'s [Laminar 101](https://alvinalexander.com/scala/laminar-101-hello-world-example-static/), [102](https://alvinalexander.com/scala/laminar-102-reactive-hello-world-example/), and [103](https://alvinalexander.com/scala/laminar-103-reactive-routing-example/) tutorials take you from setting up a static Laminar page to implementing a basic router.



## New Useful Tools

[Laminext Scribble](https://scribble.laminext.dev) by **Iurii Malchenko** is like ScalaFiddle, but simplified and preloaded with Laminar and [Laminext](https://laminext.dev/). For instance, here's the [Hello World](https://scribble.laminext.dev/u/raquo/cqnkcloptolsneddzxifkmwkrsnr) example. It's actually been around for a while, I just realized I haven't posted about it yet.

[Html to Scala Converter](https://simerplaha.github.io/html-to-scala-converter/) by **Simer Plaha** can now output Laminar code.



## New Apps and Demos

[Twotm8](https://twotm8-web.fly.dev/login) is the Laminar Twitter clone by **Anton Sviridov** mentioned above. [Source](https://github.com/keynmol/twotm8). [Blog post](https://blog.indoorvivants.com/2022-03-07-twotm8-part-5-building-the-frontend).

[Skłable](https://sk%C5%82able.pl) is a multiplayer Scrabble-like game made by **@przemekd** using Laminar. [Source](https://github.com/przemekd/sklable).

[Laika-Laminar](https://i10416.github.io/demo/) made by **@i10416** converts Markdown and ReStructuredText to HTML. Web app is built using Laminar, transformation is done using [Laika](https://planet42.github.io/Laika/). [Source](https://github.com/i10416/laika-laminar).

[Scala.school](https://scala.school/) Laminar web app made by **Kit Langton** serves as a nice index to Zymposium videos.

[NPC Generator](https://gitlab.com/scala-js-games/npc-generator/) by **Michel Daviot** 

[Hippo](https://github.com/indoorvivants/hippo) by **Anton Sviridov** is a web-based heap dump viewer made with Laminar.



## New Libraries

[SAP UI5 Web Components Bindings](https://github.com/sherpal/LaminarSAPUI5Bindings) by **Antoine Doeraene** is a new alternative to Laminar [Material UI Web Components](https://github.com/uosis/laminar-web-components) bindings.

[Declarative Data Viz 4 Scala](https://github.com/Quafadas/dedav4s) by **Simon Parten** – a thin shim to [Vega](https://vega.github.io/vega/). Can work with any Scala.js UI library [including](https://quafadas.github.io/dedav4s/ScalaVersions/scalaJS.html) Laminar.

[ew](https://github.com/raquo/ew) is a set of high-performance low-usability interfaces to native JS data types that poor souls such as myself may occasionally need. The next version of Laminar uses it internally.



## New Templates & Starter Kits

[keynmol/scalajs-tauri-example](https://github.com/keynmol/scalajs-tauri-example) shows how to use Laminar with [Tauri](https://tauri.studio/) to build a Desktop application, similar to Electron.

[sjrd/scalajs-sbt-vite-laminar-chartjs-example](https://github.com/sjrd/scalajs-sbt-vite-laminar-chartjs-example) puts together sbt, Vite, Laminar and Chart.js (via ScalablyTyped)

[Quafadas/cask-laminar](https://github.com/Quafadas/cask-laminar) shows how to use [Cask](https://com-lihaoyi.github.io/cask/) backend with Laminar. 




## Laminar Releases

Last fall I published Laminar and Airstream 0.14.2 which I unfortunately didn't have time to properly post about. These have two notable changes:

* Support for scala-js-dom 2.0.0 introduced in 0.14.0
* [Performance fix](https://github.com/raquo/Laminar/issues/108) for very large collections of elements in 0.14.2 

As mentioned above, I also released a small Scala.js library: [ew](https://github.com/raquo/ew).

Over the winter I worked a lot on the next version of Laminar, which will be tagged 15.0.0. I will resume working on it in September, expecting a release in October. I am very excited about this upcoming release, it will be the best Laminar to date with major new features and improvements.




## Thank You

Huge thanks to everyone who helped make Laminar a richer ecosystem this year.

The development of Laminar itself is kindly sponsored by [20 people](https://github.com/sponsors/raquo) who use it in their businesses, or just for fun.

**GOLD** sponsors supporting Laminar:

<div class="-sponsorsList x-alignItemsEnd">
  <div class="-sponsor x-person x-yurique">
    <img class="-avatar x-rounded" src="/img/sponsors/yurique.jpg" alt="" />
    <div class="-text">
      <div class="-name"><a href="https://github.com/yurique">Iurii Malchenko</a></div>
    </div>
  </div>
  <div class="-sponsor x-company x-aurinko">
    <a class="x-noHover" href="https://www.aurinko.io/">
      <img class="-logo" src="/img/sponsors/aurinko-light-300px.png" alt="" />
      <div class="-tagline"><u>Aurinko</u> is an API platform for workplace addons and integrations.</div>
    </a>
  </div>
</div>

Thank you for supporting me! ❤️
