resolvers += Resolver.sonatypeRepo("snapshots") // For mdoc (see also build.sbt)

logLevel := Level.Warn

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.7.1")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.1.2")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.10")

addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.2.24+18-e98df169-SNAPSHOT")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.20")
