enablePlugins(ScalaJSPlugin)

enablePlugins(ScalaJSBundlerPlugin)

// @TODO[Security] Is this a good idea to leave this here long term?
// resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.raquo" %%% "airstream" % "0.7.3-SNAPSHOT",
  "com.raquo" %%% "domtypes" % "0.9.6",
  "com.raquo" %%% "domtestutils" % "0.10" % Test,
  "org.scalatest" %%% "scalatest" % "3.1.0" % Test
)

scalacOptions ++= Seq("-deprecation", "-feature", "-language:higherKinds", "-language:implicitConversions")

version in installJsdom := "16.0.1"

useYarn := true

requireJsDomEnv in Test := true

parallelExecution in Test := false

scalaJSUseMainModuleInitializer := true

emitSourceMaps := false
