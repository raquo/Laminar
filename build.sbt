enablePlugins(ScalaJSPlugin)

enablePlugins(ScalaJSBundlerPlugin)

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.3",
//  "org.scala-js" %% "scalajs-env-selenium" % "0.1.3",
  "com.raquo.xstream" %%% "xstream" % "0.2.2-SNAPSHOT",
  "com.raquo" %%% "dombuilder" % "0.3",
  "org.scalatest" %%% "scalatest" % "3.0.3" % Test,
  "com.raquo" %%% "domtestutils" % "0.3" % Test
)

//jsEnv := new org.scalajs.jsenv.selenium.SeleniumJSEnv(org.scalajs.jsenv.selenium.Firefox()).withKeepAlive()

useYarn := true

requiresDOM in Test := true

scalaJSUseMainModuleInitializer := true

// Note: we need to run `npm install jsdom` for this to work
//jsDependencies += RuntimeDOM % Test

//fastOptJS in testHtmlFastOpt in Test := {
//  val f = (fastOptJS in testHtmlFastOpt in Test).value
//  val fd = new File(f.data.getPath.stripSuffix(".js") + "-bundle.js")
//  f.copy(fd)(f.metadata)
//}



//jsEnv := JSDOMNodeJSEnv().value

// Webpack bundle is not being generated? Remember that you need to run `sbt fastOptJS::webpack`, not just `sbt fastOptJS`.

//webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack-config.js")

//scalaJSModuleKind := ModuleKind.CommonJSModule // not needed if using scalajs-bundler

emitSourceMaps := false
