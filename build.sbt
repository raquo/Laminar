
// @TODO[Security] Is this a good idea to leave this here long term?
// resolvers += Resolver.sonatypeRepo("snapshots")

val scalaVersions = Seq(ScalaVersions.v3RC1, ScalaVersions.v3M3, ScalaVersions.v213, ScalaVersions.v212)

lazy val websiteJS = project
  .in(file("websiteJS"))
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.1.0",
    scalaVersion := ScalaVersions.v213,
    crossScalaVersions := scalaVersions,
    skip in publish := true,
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    //webpackBundlingMode := BundlingMode.LibraryAndApplication(),
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.CommonJSModule)
    },
    scalaJSLinkerConfig ~= {
      _.withSourceMap(false) // Producing source maps throws warnings on material web components complaining about missing .ts files. Not sure why.
    },
    scalaJSUseMainModuleInitializer := true,
    npmDependencies in Compile ++= Seq(
      "@material/mwc-button" -> "0.18.0",
      "@material/mwc-linear-progress" -> "0.18.0",
      "@material/mwc-slider" -> "0.18.0"
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(laminar)

lazy val website = project
  .settings(
    scalaVersion := ScalaVersions.v213,
    crossScalaVersions := scalaVersions
  )
  .enablePlugins(MdocPlugin, DocusaurusPlugin)
  .settings(
    mdocIn := file("website/docs"),
    mdocJS := Some(websiteJS),
    mdocJSLibraries := webpack.in(websiteJS, Compile, fullOptJS).value,
    skip in publish := true,
    mdocVariables := Map(
      "js-mount-node" -> "containerNode"
      //  // Use these as @VERSION@ in mdoc-processed .md files
      //  "LAMINAR_VERSION" -> version.value.replace("-SNAPSHOT", ""), // This can return incorrect version too easily
      //  "SCALA_VERSION" -> scalaVersion.value
    )
  )

lazy val laminar = project.in(file("."))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .settings(
    name := "Laminar",

    libraryDependencies ++= Seq(
      "com.raquo" %%% "airstream" % "0.12.0-SNAPSHOT",
      ("com.raquo" %%% "domtypes" % "0.11.0").withDottyCompat(scalaVersion.value),
      ("com.raquo" %%% "domtestutils" % "0.13.0" % Test).withDottyCompat(scalaVersion.value),
      ("org.scalatest" %%% "scalatest" % "3.2.0" % Test).withDottyCompat(scalaVersion.value),
    ),

    version in installJsdom := "16.4.0",

    useYarn := true,

    requireJsDomEnv in Test := true,

    parallelExecution in Test := false,

    scalaJSUseMainModuleInitializer := true,

    scalaJSLinkerConfig in(Compile, fastOptJS) ~= (_.withSourceMap(false)),

    scalaVersion := ScalaVersions.v213,

    crossScalaVersions := scalaVersions,

    scalacOptions ~= (_.filterNot(Set(
      "-Wunused:params",
      "-Ywarn-unused:params",
      "-Wunused:explicits"
    ))),
    scalacOptions in (Compile, doc) ~= (_.filterNot(
      Set(
        "-scalajs",
        "-deprecation",
        "-explain-types",
        "-explain",
        "-feature",
        "-language:existentials,experimental.macros,higherKinds,implicitConversions",
        "-unchecked",
        "-Xfatal-warnings",
        "-Ykind-projector",
        "-from-tasty",
        "-encoding",
        "utf8",
      )
    )),
    scalacOptions in (Compile, doc) ++= Seq(
      "-no-link-warnings", // Suppress scaladoc "Could not find any member to link for" warnings
    )
  )
