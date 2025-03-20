![](https://laminar.dev/img/brand/laminar-logo-100px-rounded.png)

# Laminar

[![Build status](https://github.com/raquo/Laminar/actions/workflows/test.yml/badge.svg)](https://github.com/raquo/Laminar/actions/workflows/test.yml)
[![Chat on https://discord.gg/JTrUxhq7sj](https://img.shields.io/badge/chat-on%20discord-7289da.svg)](https://discord.gg/JTrUxhq7sj)
[![Maven Central](https://img.shields.io/maven-central/v/com.raquo/laminar_sjs1_3.svg)](https://search.maven.org/artifact/com.raquo/laminar_sjs1_3)

Laminar is a small library that lets you build web application interfaces, keeping UI state in sync with the underlying application state. Its simple yet expressive patterns build on a rock solid foundation of [Airstream](https://github.com/raquo/Airstream) observables and the [Scala.js](https://www.scala-js.org/) platform.

Laminar is also a friendly community of passionate people from across the world who help each other learn new skills and achieve their goals. Check out all the learning materials we've put out, and chat us up on Discord if you hit a snag!

    "com.raquo" %%% "laminar" % "<version>" // Requires Scala.js 1.16.0+

Look up the latest version of Laminar [here](https://laminar.dev/blog/), or in git tags above ("v" prefix is _not_ part of the version number).



## Where Are The Docs and Everything?


### 👉 [laminar.dev](https://laminar.dev)

Sales pitch, quick start, documentation, live examples, and other resources, all there. 

### 👉 [demo.laminar.dev](https://demo.laminar.dev)

Live demo, with examples, code snippets, and a fully working client + server, dev + prod build setup that you can experiment with, and then deploy to the cloud for free.


## Contributing

Please run `sbt +test` and `sbt scalafmtAll` locally before submitting the PR.

Note that all files under `com.raquo.laminar.defs` are generated, and should not be edited directly – for how to add more attributes / props, etc., see [Missing Keys](https://laminar.dev/documentation#missing-keys) and then [contribute to Scala DOM Types](https://github.com/raquo/scala-dom-types?tab=readme-ov-file#contributing).




## Sponsorships

Huge thanks to [all of our sponsors](https://github.com/sponsors/raquo) – your backing enables me to spend more time on Laminar, Airstream, various add-ons, as well as documentation, learning materials, and community support.


### DIAMOND sponsor:

[![HeartAI.net](https://laminar.dev/img/sponsors/heartai-300px.png)](https://www.heartai.net/)

[HeartAI](https://www.heartai.net/) is a data and analytics platform for digital health and clinical care.
<br />
<br />

### GOLD Sponsors:

<br />

[![Aurinko.io](https://laminar.dev/img/sponsors/aurinko-light-250px.png)](https://www.aurinko.io/)<br />
**[Aurinko](https://aurinko.io/)** is an API platform for workplace addons and integrations.
<br />
<br />
<br />
<a href="https://tawasal.ae"><img src="https://laminar.dev/img/sponsors/tawasal.svg" width="80"></a><br />
<b><a href="https://tawasal.ae">Tawasal</a></b> is a secure multi-purpose messenger and superapp, offering free voice, text, video conferencing and lifestyle services.
<br />
<br />
<br />
<a href="https://www.ossuminc.com/"><img src="https://laminar.dev/img/sponsors/ossum-square-200px.jpg" width="80"></a><br />
<b><a href="https://www.ossuminc.com">Ossum Inc.</a></b> is dedicated to creating Ossum experiences for businesses that use software.
<br />
<br />

## Author

Nikita Gazarov – [@raquo](https://twitter.com/raquo)




## License

Laminar is provided under the [MIT license](https://github.com/raquo/laminar/blob/master/LICENSE.md).

The artwork in the `img/brand`, `img/sponsors`, and `img/blog` directories is not covered by the MIT license. No license is granted to you for these assets. However, you may still have "fair use" rights, as stipulated by law.

Comments in the `defs` directory pertaining to individual DOM element tags, attributes, properties and event properties, as well as CSS properties and their special values / keywords, are taken or derived from content created by Mozilla Contributors and are licensed under Creative Commons Attribution-ShareAlike license (CC-BY-SA), v2.5.
