enablePlugins(ScalaJSPlugin)

enablePlugins(ScalaJSBundlerPlugin)

libraryDependencies ++= Seq(
  "com.raquo" %%% "airstream" % "0.1.1-SNAPSHOT",
  "com.raquo" %%% "dombuilder" % "0.7",
  "org.scala-js" %%% "scalajs-dom" % "0.9.5",
  "com.raquo" %%% "domtestutils" % "0.7" % Test,
  "org.scalatest" %%% "scalatest" % "3.0.5" % Test
)

useYarn := true

requiresDOM in Test := true

scalaJSUseMainModuleInitializer := true

emitSourceMaps := false
