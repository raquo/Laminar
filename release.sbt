ThisBuild / organization := "com.raquo"
ThisBuild / homepage := Some(url("https://laminar.dev"))
ThisBuild / licenses += "MIT" -> url("https://github.com/raquo/Laminar/blob/master/LICENSE.md")
ThisBuild / developers += Developer("raquo", "Nikita Gazarov", "nikita@raquo.com", url("http://raquo.com"))
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/raquo/Laminar"), "scm:git@github.com/raquo/Laminar.git"))
ThisBuild / sonatypeProfileName := "com.raquo"
ThisBuild / publishArtifact in Test := false
