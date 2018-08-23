enablePlugins(ScalaJSPlugin)

enablePlugins(ScalaJSBundlerPlugin)

libraryDependencies ++= Seq(
  "com.raquo" %%% "airstream" % "0.2.1-SNAPSHOT",
  "com.raquo" %%% "dombuilder" % "0.8",
  "org.scala-js" %%% "scalajs-dom" % "0.9.5",
  "com.raquo" %%% "domtestutils" % "0.8" % Test,
  "org.scalatest" %%% "scalatest" % "3.0.5" % Test
)

useYarn := true

requiresDOM in Test := true

scalaJSUseMainModuleInitializer := true

emitSourceMaps := false
