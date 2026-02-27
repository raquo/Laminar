// #Note this is /project/build.sbt – see /build.sbt for the main build config.

// Compile-time dependencies
libraryDependencies ++= Seq(
  "com.raquo" %% "buildkit" % ProjectVersions.BuildKit,
  "com.raquo" %% "domtypes" % ProjectVersions.ScalaDomTypes,
  "com.github.sbt" %% "dynver" % "5.1.1", // needed for sbtdynver types in build.sbt
)
