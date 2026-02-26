import sbt.Keys._

ThisBuild / scalaVersion := "3.3.7"

// Shared settings for all sub-projects
lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:implicitConversions,higherKinds",
  ),
  scalaJSUseMainModuleInitializer := false,
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
)

// ---------------------------------------------------------------------------
// Full benchmarks: ~50 files each, ~200 LOC per file
// ---------------------------------------------------------------------------

lazy val `bench-v17` = project
  .in(file("bench-v17"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    name := "bench-v17",
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "17.2.0",
    ),
  )

lazy val `bench-v18` = project
  .in(file("bench-v18"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    name := "bench-v18",
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "18.0.0-M2",
    ),
  )

// ---------------------------------------------------------------------------
// Micro benchmarks: 4 tiny files each, isolating one pattern
// ---------------------------------------------------------------------------

lazy val `bench-v18-A` = project
  .in(file("bench-v18-A"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    name := "bench-v18-A",
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "18.0.0-bench-A",
    ),
  )

lazy val `bench-v18-B` = project
  .in(file("bench-v18-B"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    name := "bench-v18-B",
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "18.0.0-bench-B",
    ),
  )

// ---------------------------------------------------------------------------
// Micro benchmarks: 4 tiny files each, isolating one pattern
// ---------------------------------------------------------------------------

lazy val `micro-v17` = project
  .in(file("micro-v17"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    name := "micro-v17",
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "17.2.0",
    ),
  )

lazy val `micro-v18` = project
  .in(file("micro-v18"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    name := "micro-v18",
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "18.0.0-M2",
    ),
  )
