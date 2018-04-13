enablePlugins(ScalaJSPlugin)

enablePlugins(ScalaJSBundlerPlugin)

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.4",
  "com.raquo" %%% "dombuilder" % "0.5.1-SNAPSHOT",
  "org.scalatest" %%% "scalatest" % "3.0.4" % Test,
  "com.raquo" %%% "domtestutils" % "0.5.1-SNAPSHOT" % Test
)

useYarn := true

requiresDOM in Test := true

scalaJSUseMainModuleInitializer := true

emitSourceMaps := false
