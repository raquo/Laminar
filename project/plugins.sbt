addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.20.2")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1") // for website only

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.11.2")

addSbtPlugin("com.github.sbt" % "sbt-git" % "2.1.0")

addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.5.4")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

// #TODO Removed pending https://github.com/typelevel/sbt-tpolecat/issues/102
// addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.4.11")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.1"
