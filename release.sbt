name := "Laminar"

normalizedName := "laminar"

organization := "com.raquo"

scalaVersion := "2.12.10"

crossScalaVersions := Seq("2.12.10", "2.13.1")

homepage := Some(url("https://github.com/raquo/Laminar"))

licenses += ("MIT", url("https://github.com/raquo/Laminar/blob/master/LICENSE.md"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/raquo/Laminar"),
    "scm:git@github.com/raquo/Laminar.git"
  )
)

developers := List(
  Developer(
    id = "raquo",
    name = "Nikita Gazarov",
    email = "nikita@raquo.com",
    url = url("http://raquo.com")
  )
)

sonatypeProfileName := "com.raquo"

publishMavenStyle := true

publishArtifact in Test := false

publishTo := sonatypePublishTo.value

releaseCrossBuild := true

pomIncludeRepository := { _ => false }

releasePublishArtifactsAction := PgpKeys.publishSigned.value
