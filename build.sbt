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
  "website/build", "website/target"
).map(file)

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
      // required for cross-compilation between 3.9 and 2.13 without warnings:
      "-Wconf:msg=no longer supported for vararg splices:silent",
      "-Wconf:msg=with as a type operator has been deprecated:silent",
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
