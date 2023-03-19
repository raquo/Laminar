logLevel := Level.Warn

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.13.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.11")

addSbtPlugin("com.github.sbt" % "sbt-git" % "2.0.1")

addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.3.7")

// #TODO Removed pending https://github.com/typelevel/sbt-tpolecat/issues/102
// addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.4.11")
