// @TODO[Security] Is this a good idea to leave this here long term?
// resolvers += Resolver.sonatypeRepo("snapshots")

ThisBuild / scalaVersion := Versions.Scala_2_13

ThisBuild / crossScalaVersions := Seq(Versions.Scala_2_12, Versions.Scala_2_13, Versions.Scala_3_RC2)

lazy val websiteJS = project
  .in(file("websiteJS"))
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % Versions.ScalaJsDom,
    (publish / skip) := true,
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    //webpackBundlingMode := BundlingMode.LibraryAndApplication(),
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.CommonJSModule)
    },
    scalaJSLinkerConfig ~= {
      _.withSourceMap(false) // Producing source maps throws warnings on material web components complaining about missing .ts files. Not sure why.
    },
    scalaJSUseMainModuleInitializer := true,
    (Compile / npmDependencies) ++= Seq(
      "@material/mwc-button" -> "0.18.0",
      "@material/mwc-linear-progress" -> "0.18.0",
      "@material/mwc-slider" -> "0.18.0"
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(laminar)

lazy val website = project
  .enablePlugins(MdocPlugin, DocusaurusPlugin)
  .settings(
    mdocIn := file("website/docs"),
    mdocJS := Some(websiteJS),
    mdocJSLibraries := (websiteJS / Compile / fullOptJS / webpack).value,
    (publish / skip) := true,
    mdocVariables := Map(
      "js-mount-node" -> "containerNode",
      "js-batch-mode" -> "true"
      //  // Use these as @VERSION@ in mdoc-processed .md files
      //  "LAMINAR_VERSION" -> version.value.replace("-SNAPSHOT", ""), // This can return incorrect version too easily
      //  "SCALA_VERSION" -> scalaVersion.value
    )
  )

lazy val laminar = project.in(file("."))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.raquo" %%% "airstream" % Versions.Airstream,
      "com.raquo" %%% "domtypes" % Versions.ScalaDomTypes,
      "com.raquo" %%% "domtestutils" % Versions.ScalaDomTestUtils % Test,
      "org.scalatest" %%% "scalatest" % Versions.ScalaTest % Test,
    ),

    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-language:higherKinds",
      "-language:implicitConversions",
      {
        val localSourcesPath = baseDirectory.value.toURI
        val remoteSourcesPath = s"https://raw.githubusercontent.com/raquo/Laminar/${git.gitHeadCommit.value.get}/"
        val sourcesOptionName = if (scalaVersion.value.startsWith("2.")) "-P:scalajs:mapSourceURI" else "-scalajs-mapSourceURI"

        s"${sourcesOptionName}:$localSourcesPath->$remoteSourcesPath"
      }
    ),

    (installJsdom / version) := Versions.JsDom,

    useYarn := true,

    (Test / requireJsDomEnv) := true,

    (Test / parallelExecution) := false,

    scalaJSUseMainModuleInitializer := true,

    (Compile / fastOptJS / scalaJSLinkerConfig) ~= {
      _.withSourceMap(false)
    }
  )
  .settings(
    name := "Laminar",
    normalizedName := "laminar",
    organization := "com.raquo",
    homepage := Some(url("https://laminar.dev")),
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
    (Test / publishArtifact) := false,
    publishTo := sonatypePublishToBundle.value,
    releaseCrossBuild := true,
    pomIncludeRepository := { _ => false },
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    releaseProcess := {
      import ReleaseTransformations._
      Seq[ReleaseStep](
        checkSnapshotDependencies,
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        releaseStepCommandAndRemaining("+publishSigned"),
        releaseStepCommand("sonatypeBundleRelease"),
        setNextVersion,
        commitNextVersion,
        pushChanges
      )
    }
  )
