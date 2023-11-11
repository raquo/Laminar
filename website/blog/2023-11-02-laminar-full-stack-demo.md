---
title: Laminar & Scala.js Full Stack Demo & More
author: Nikita
authorURL: https://twitter.com/raquo
---

I built a full stack Laminar & Scala.js showcase, made some useful Vite plugins on the way, and more.

<!--truncate-->

> Laminar is a Scala.js library for building web application interfaces and managing UI state. Learn more in [a few short examples](https://demo.laminar.dev) or [one long video](https://www.youtube.com/watch?v=L_AHCkl6L-Q).

As I'm trying to get more Laminar examples and learning materials out there, I wanted a better platform for hosting the examples. Both this website, which used mdoc, and my old laminar-examples repo, are not great for authoring examples, and are certainly not great for people who are trying to run the examples, or transfer them into a more realistic, full stack app environment.

I also wanted to make starting with Scala.js even easier, to reduce the amount of rediscovery every new developer needs to do when it comes to sbt and vite build config, Scala.js facades, shared code & datastructures, JSON data sharing, URL routing, packaging for production, etc.

And so, say hello to <span style="font-size:120%">✨ **[demo.laminar.dev](https://demo.laminar.dev/)** ✨</span>. Here's what it is:

- A set of Laminar examples, from basic [hello world](https://demo.laminar.dev/app/basic/hello) including to [a full stack weather app](https://demo.laminar.dev/app/weather/gradient/squamish).
- A fully working client + server, dev + prod setup, that you can immediately run, start experimenting with, and deploy to the cloud for free, without worrying about build or server setup.
- A new [documentation system](https://demo.laminar.dev/app/integrations/code-snippets) letting me write [long narratives like this](https://demo.laminar.dev/app/integrations/waypoint-url-routing) while showing relevant snippets of source code of the very application that the user is looking at, with links to full sources on Github.
- Some [opinionated ramblings](https://github.com/raquo/laminar-full-stack-demo/#suggested-css-styling-strategy) that badly need further editing, that I nevertheless hope will be useful to backend developers venturing into the frontend.

For more details, see the project's [Features](https://github.com/raquo/laminar-full-stack-demo/#features) listing.


## New Vite Plugins for Scala.js

I wanted to replicate a [good CSS organization strategy](https://github.com/raquo/laminar-full-stack-demo/#suggested-css-styling-strategy) from the Javascript world, where you have one CSS file per component, and you just `import "./ComponentName.css"` from `ComponentName.js`. Turns out, we can't readily do this particular pattern from Scala.js, because:

1) Scala.js can't import resources for their side effects only, it needs to assign the imported object to a variable, which  Vite, the module bundler, [disagrees with](https://discord.com/channels/632150470000902164/635668814956068864/1161984220814643241) when it comes to CSS.

2) Scala.js can't import resources from relative paths, or [to be more precise](https://discord.com/channels/632150470000902164/635668814956068864/1161932392009826314), the relative paths will resolve relative to `scalaJSOutputDirectory`, instead of resolving relative to the source file in which the imports appear, as is the case in JS.

So, I wrote two Vite plugins to work around these limitations.

First, the [import-side-effect](https://github.com/raquo/vite-plugin-import-side-effect) plugin lets you import CSS resources in Vite without double-loading in production builds, and without warnings. It also provides an optional more concise syntax for such imports.

Then, the [glob-resolver](https://github.com/raquo/vite-plugin-glob-resolver) plugin lets you import relative paths, or to be more precise, it lets you import a file in your project matching a glob pattern. It throws a graceful error if it finds more than one matching file, so you can just say `JSImport("@find/**/ComponentName.css")`, and it will find `ComponentName.css` wherever it is in your project, and if there happens to be more than one such file, you can just make the glob pattern more specific to disambiguate, e.g. `JSImport("@find/**/foo/ComponentName.css")`.

For what I want to do, I need both of these plugins working together, so I say `JSImportSideEffect("@find/**/ComponentName.css")` to get a nice clean `import "/absolute/path/to/ComponentName.css"` in the JS output, which Vite is happy to oblige. The source maps are preserved, everything works both in dev and prod builds.

~~Currently, these plugins live in this Laminar demo project as source files that you can copy-paste into your own project, but if there's enough interest, I could publish them to npm, or if the Scala.js team sees fit, they could be included into the official [scalajs-vite-plugin](https://github.com/scala-js/vite-plugin-scalajs).~~

**Update:** I've published my vite plugins to npm, and updated the import-side-effect plugin with a new (better) approach.


## Forward-Looking Statements

This new demo project is a solid MVP, but it could be so much more.

I want to make a comprehensive collection of Laminar mini-tutorials, showcasing various features, techniques, patterns, etc. Some of this is technically covered by our extensive documentation, but not the more complex stuff (such as nested state management). And people have been asking me to create more bite-sized/example/tutorial-style learning materials over and over, since forever. I do what I can with the time that I have, but I'm hoping that with this new platform, adding more examples over time will be easier.  

I also want to create more Scala.js <> JS and Scala.js <> JVM integration examples. Everything from making facades for more JS libraries manually to show how it's done, to showing patterns for sanely dealing with complex types that are incompatible between JS and JVM, such as DateTime.

Last but not least, I recently played around with [Shoelace web components](https://demo.laminar.dev/app/integrations/web-components/shoelace), and I must say that I like them quite a bit more than [SAP UI5](https://github.com/sherpal/LaminarSAPUI5Bindings). Both libraries are solid, respectable, and well documented, but Shoelace has a nicer (IMO) modern-web look to it, and crucially, is much more flexible when it comes to theming and customization. I made Laminar bindings for a few Shoelace components that you can see at the link above, and I think it would be great to cover their entire collection of components, and publish it for people to use. Personally I like to create my own bespoke components because I'm very particular about the UIs that I build, but I understand that most people and most business applications will be better off with high-quality premade components, like what Shoelace offers.


## Sustainability Report

My continued work on Laminar and Laminar things is made possible by [21 sponsors](https://github.com/sponsors/raquo/). Their support lets me spend more time doing what I love – designing and building useful tools for people to enjoy – and for that, I am eternally grateful.

Laminar sponsorships have been gradually growing for a long time, thanks both to the new people and companies coming in (Cheers [Aurinko](https://www.aurinko.io/)! [HeartAI](https://heartai.net/)!), and the same diehards who've been with us since day one ([Iurii](https://github.com/yurique)! [Eason](https://github.com/doofin)! [Anton](https://github.com/keynmol)!, [Binh](https://github.com/ngbinh)!, [Paul-Henri](https://github.com/phfroidmont)!), and everyone in between.

Recently, HeartAI have upgraded their Laminar sponsorship to an extent that I had to create a new tier for such a level of support. Frankly, this one event is what prompted me to create this new Laminar demo project. I have wanted to do it for a long time, but up until now it has always been an unattainable nice-to-have, buried in the ever-growing todo list. But as my support base has grown to a new level, so has my ability to work on things that will help grow our community even further.


### DIAMOND sponsor:

<div class="-sponsorsList x-alignItemsStart x-justifyContentCenter">
<div class="-sponsor x-diamond x-company x-heartai">
  <a class="x-noHover" href="https://www.heartai.net/">
    <img class="-logo" src="/img/sponsors/heartai.svg" alt="" />
    <div class="-tagline"><u>HeartAI</u> is a data and analytics platform for digital health and clinical care.</div>
  </a>
</div>
</div>

**GOLD sponsors**:

<div class="-sponsorsList x-alignItemsEnd">
  <div class="-sponsor x-person x-yurique">
    <img class="-avatar x-rounded" src="/img/sponsors/yurique.jpg" alt="" />
    <div class="-text">
      <div class="-name"><a href="https://github.com/yurique">Iurii Malchenko</a></div>
    </div>
  </div>
  <div class="-sponsor x-company x-aurinko">
    <a class="x-noHover" href="https://www.aurinko.io/">
      <img class="-logo" src="/img/sponsors/aurinko-light-250px.png" alt="" />
      <div class="-tagline"><u>Aurinko</u> is an API platform for workplace addons and integrations.</div>
    </a>
  </div>
</div>
