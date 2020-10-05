
// @TODO[Security] Is this a good idea to leave this here long term?
// resolvers += Resolver.sonatypeRepo("snapshots")

val Scala212 = "2.12.10"
val Scala213 = "2.13.1"
val scalaVersions = Seq(Scala212, Scala213)

lazy val websiteJS = project
  .in(file("websiteJS"))
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.1.0",
    scalaVersion := Scala213,
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
    scalaVersion := Scala213,
    crossScalaVersions := scalaVersions
  )
  .enablePlugins(MdocPlugin, DocusaurusPlugin)
  .settings(
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
    libraryDependencies ++= Seq(
      "com.raquo" %%% "airstream" % "0.10.1",
      "com.raquo" %%% "domtypes" % "0.10.1",
      "com.raquo" %%% "domtestutils" % "0.12.0" % Test,
      "org.scalatest" %%% "scalatest" % "3.1.1" % Test,
    ),

    scalacOptions ++= Seq("-deprecation", "-feature", "-language:higherKinds", "-language:implicitConversions"),

    version in installJsdom := "16.4.0",

    useYarn := true,

    requireJsDomEnv in Test := true,

    parallelExecution in Test := false,

    scalaJSUseMainModuleInitializer := true,

    scalaJSLinkerConfig in(Compile, fastOptJS) ~= {
      _.withSourceMap(false)
    }
  )
  .settings(
    name := "Laminar",
    normalizedName := "laminar",
    organization := "com.raquo",
    scalaVersion := Scala213,
    crossScalaVersions := scalaVersions,
    homepage := Some(url("https://github.com/raquo/Laminar")),
    licenses += ("MIT", url("https://github.com/raquo/Laminar/blob/master/LICENSE.md")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/raquo/Laminar"),
        "scm:git@github.com/raquo/Laminar.git"
      )
    ),
    developers := List(
      Developer(
        id = "raquo",
        name = "Nikita Gazarov",
        email = "nikita@raquo.com",
        url = url("http://raquo.com")
      )
    ),
    sonatypeProfileName := "com.raquo",
    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishTo := sonatypePublishTo.value,
    releaseCrossBuild := true,
    pomIncludeRepository := { _ => false },
    releasePublishArtifactsAction := PgpKeys.publishSigned.value
  )
