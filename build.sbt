enablePlugins(ScalaJSPlugin)

enablePlugins(ScalaJSBundlerPlugin)

// @TODO[Security] Is this a good idea to leave this here long term?
// resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.raquo" %%% "airstream" % "0.7.2",
  "com.raquo" %%% "domtypes" % "0.9.6",
  "com.raquo" %%% "domtestutils" % "0.9.1" % Test,
  "org.scalatest" %%% "scalatest" % "3.0.8" % Test
)

useYarn := true

requiresDOM in Test := true

scalaJSUseMainModuleInitializer := true

emitSourceMaps := false
