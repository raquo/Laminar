enablePlugins(ScalaJSPlugin)

enablePlugins(ScalaJSBundlerPlugin)

// @TODO[Security] Is this a good idea to leave this here long term?
// resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.raquo" %%% "airstream" % "0.9.0",
  "com.raquo" %%% "domtypes" % "0.10.0",
  "com.raquo" %%% "domtestutils" % "0.12.0" % Test,
  "org.scalatest" %%% "scalatest" % "3.1.1" % Test
)

scalacOptions ++= Seq("-deprecation", "-feature", "-language:higherKinds", "-language:implicitConversions")

version in installJsdom := "16.2.0"

useYarn := true

requireJsDomEnv in Test := true

parallelExecution in Test := false

scalaJSUseMainModuleInitializer := true

scalaJSLinkerConfig in (Compile, fastOptJS) ~= { _.withSourceMap(false) }

// @Warning remove this when scalajs-bundler > 0.17 is out https://github.com/scalacenter/scalajs-bundler/issues/332#issuecomment-594401804
Test / jsEnv := new tempfix.JSDOMNodeJSEnv(tempfix.JSDOMNodeJSEnv.Config((Test / installJsdom).value))
