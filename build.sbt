import com.raquo.buildkit.sbt.BuildKitOnLoadPlugin.autoImport.buildKitOnLoadActions

ThisBuild / version := buildKitDynVer.version.value // Auto-increment version for local development

ThisBuild / dynver := buildKitDynVer.dynver.value // Auto-increment version for local development

ThisBuild / scalaVersion := Versions.Scala_3

ThisBuild / crossScalaVersions := Seq(Versions.Scala_2_13, Versions.Scala_3)

ThisBuild / buildKitDownloads := Seq(
  _.fromGithubTag(
    repo = "raquo/scalafmt-config",
    filePath = ".scalafmt.shared.conf",
    tag = "v0.1.0"
  ).withDoNotEditComment(_.`#`)
)

Global / buildKitOnLoadActions += { (_: Extracted) =>
  DomDefsGenerator.cachedGenerate()
}

// https://github.com/JetBrains/sbt-ide-settings
SettingKey[Seq[File]]("ide-excluded-directories").withRank(KeyRanks.Invisible) := Seq(
  ".buildkit", ".idea", ".metals", ".bloop", ".bsp",
  "target", "project/target", "project/project/target", "project/project/project/target",
  "node_modules",
  "website/build", "website/target", "websiteJS/target"
).map(file)

lazy val websiteJS = project
  .in(file("websiteJS"))
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % Versions.ScalaJsDom,
    (publish / skip) := true,
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    (installJsdom / version) := Versions.JsDom,
    (webpack / version) := Versions.Webpack,
    (startWebpackDevServer / version) := Versions.WebpackDevServer,
    // webpackBundlingMode := BundlingMode.LibraryAndApplication(),
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
    ),
    scalacOptions ~= { options: Seq[String] =>
      options.filterNot { o =>
        o.startsWith("-Wvalue-discard") || o.startsWith("-Ywarn-value-discard") || o.startsWith("-Ywarn-unused") || o.startsWith("-Wunused")
      }
    },
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
  .enablePlugins(ScalaJSPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.raquo" %%% "airstream" % Versions.Airstream,
      // "com.raquo" %%% "domtypes" % Versions.ScalaDomTypes, #Note this is a compile-time dependency. See `project/build.sbt`
      "com.raquo" %%% "ew" % Versions.Ew,
      "com.raquo" %%% "domtestutils" % Versions.ScalaDomTestUtils % Test,
      "org.scalatest" %%% "scalatest" % Versions.ScalaTest % Test,
    ),

    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-language:implicitConversions,higherKinds,existentials",
    ),

    scalacOptions ~= { options: Seq[String] =>
      options.filterNot(Set(
        "-Ywarn-value-discard",
        "-Wvalue-discard"
      ))
    },

    scalacOptions += pointScalaJsSourceMapsToGithub("raquo/Laminar").value,

    //  We do have the stub defined in Airstream, but it throws deprecation errors in Laminar for some reason as if
    //  the unused value is in fact used, but that doesn't seem right.
    (Test / scalacOptions) ~= { options: Seq[String] =>
      options.filterNot { o =>
        o.startsWith("-Ywarn-unused") || o.startsWith("-Wunused")
      }
    },

    (Compile / doc / scalacOptions) ~= (_.filterNot(
      Set(
        "-deprecation",
        "-explain-types",
        "-explain",
        "-unchecked",
        "-Xfatal-warnings",
        "-Ykind-projector",
        "-from-tasty",
        "-encoding",
        "utf8",
      )
    )),

    (Compile / doc / scalacOptions) ++= Seq(
      "-no-link-warnings" // Suppress scaladoc "Could not find any member to link for" warnings
    ),

    (Test / parallelExecution) := false,

    scalaJSUseMainModuleInitializer := true,

    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
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
        url = url("https://github.com/raquo")
      )
    ),
    (Test / publishArtifact) := false,
    pomIncludeRepository := { _ => false },
  )
