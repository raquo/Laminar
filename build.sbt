enablePlugins(ScalaJSPlugin)

enablePlugins(ScalaJSBundlerPlugin)

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.4",
  "com.raquo.xstream" %%% "xstream" % "0.3.1",
  "com.raquo" %%% "dombuilder" % "0.5",
  "org.scalatest" %%% "scalatest" % "3.0.4" % Test,
  "com.raquo" %%% "domtestutils" % "0.5" % Test
)

useYarn := true

requiresDOM in Test := true

scalaJSUseMainModuleInitializer := true

emitSourceMaps := false
