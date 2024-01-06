import VersionHelper.{versionFmt, fallbackVersion}

// Lets me depend on Maven Central artifacts immediately without waiting
resolvers ++= Resolver.sonatypeOssRepos("public")

// Replace default sbt-dynver version with a simpler one for easier local development
// ThisBuild / version ~= (_.replaceFirst("(\\+[a-z0-9-+]*-SNAPSHOT)", "-NEXT-SNAPSHOT"))

// Makes sure to increment the version for local development
ThisBuild / version := dynverGitDescribeOutput.value
  .mkVersion(out => versionFmt(out, dynverSonatypeSnapshots.value), fallbackVersion(dynverCurrentDate.value))

ThisBuild / dynver := {
  val d = new java.util.Date
  sbtdynver.DynVer
    .getGitDescribeOutput(d)
    .mkVersion(out => versionFmt(out, dynverSonatypeSnapshots.value), fallbackVersion(d))
}

ThisBuild / scalaVersion := Versions.Scala_3

ThisBuild / crossScalaVersions := Seq(Versions.Scala_2_12, Versions.Scala_2_13, Versions.Scala_3)

lazy val precompile = taskKey[Unit]("runs Laminar-specific pre-compile tasks")

precompile := DomDefsGenerator.cachedGenerate()

(Compile / compile) := ((Compile / compile) dependsOn precompile).value

lazy val websiteJS = project
  .in(file("websiteJS"))
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % Versions.ScalaJsDom,
    (publish / skip) := true,
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    (installJsdom / version) := Versions.JsDom,
    (webpack / version) := Versions.Webpack,
    (startWebpackDevServer / version) := Versions.WebpackDevServer,
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
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
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
      "-language:implicitConversions,higherKinds,existentials",
    ),

    scalacOptions ~= { options: Seq[String] =>
      options.filterNot(Set(
        "-Ywarn-value-discard",
        "-Wvalue-discard"
      ))
    },

    scalacOptions ++= sys.env.get("CI").map { _ =>
      val localSourcesPath = (LocalRootProject / baseDirectory).value.toURI
      val remoteSourcesPath = s"https://raw.githubusercontent.com/raquo/Laminar/${git.gitHeadCommit.value.get}/"
      val sourcesOptionName = if (scalaVersion.value.startsWith("2.")) "-P:scalajs:mapSourceURI" else "-scalajs-mapSourceURI"

      s"${sourcesOptionName}:$localSourcesPath->$remoteSourcesPath"
    },

    // @TODO[Build,Scala 2.12] This should only be disabled for tests, but I can't figure out how to make @unused work in 2.12
    //  We do have the stub defined in Airstream, but it throws deprecation errors in Laminar for some reason as if
    //  the unused value is in fact used, but that doesn't seem right.
    //(Test / scalacOptions) ~= { options: Seq[String] =>
    scalacOptions ~= { options: Seq[String] =>
      options.filterNot { o =>
        o.startsWith("-Ywarn-unused") || o.startsWith("-Wunused")
      }
    },

    (Compile / doc / scalacOptions) ~= (_.filterNot(
      Set(
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

    (Compile / doc / scalacOptions) ++= Seq(
      "-no-link-warnings" // Suppress scaladoc "Could not find any member to link for" warnings
    ),

    (Test / parallelExecution) := false,

    (Test / requireJsDomEnv) := true,

    (installJsdom / version) := Versions.JsDom,

    (webpack / version) := Versions.Webpack,

    (startWebpackDevServer / version) := Versions.WebpackDevServer,

    useYarn := true,

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
        url = url("https://github.com/raquo")
      )
    ),
    (Test / publishArtifact) := false,
    pomIncludeRepository := { _ => false },
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
  )
