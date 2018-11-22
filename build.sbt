
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.ebiznext",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "anatomie-typeclass"
  )

enablePlugins(TutPlugin)

lazy val trainingMarkdowns = project in file("presentation/markdown")

tutSourceDirectory := baseDirectory.in(trainingMarkdowns).value / "in"
tutTargetDirectory := baseDirectory.in(trainingMarkdowns).value / "out"