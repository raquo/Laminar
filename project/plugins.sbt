addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.21.0-SNAPSHOT")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.11.2")

addSbtPlugin("com.github.sbt" % "sbt-git" % "2.1.0")

addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.8.2")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.6")

// #TODO Removed pending https://github.com/typelevel/sbt-tpolecat/issues/102
// addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.4.11")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "0.0.0+108-1bef3aeb+20260205-2238-SNAPSHOT"
