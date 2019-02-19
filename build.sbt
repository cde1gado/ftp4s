import sbtcrossproject.CrossPlugin.autoImport.crossProject
import sbtcrossproject.CrossType

inThisBuild(
  List(
    organization := "pure4s.github.io",
    homepage := Some(url("https://pure4s.github.io/ftp4s/")),
    licenses := List(
      "Apache-2.0" -> url("https://opensource.org/licenses/MIT")),
    developers := List(
      Developer(
        "cde1gado",
        "Cristina Delgado",
        "",
        url("https://cde1gado.github.io")
      )
    )
  ))

lazy val V = new {
  val catsVersion = "1.5.0"
  val catsEffectVersion = "1.2.0"
  val apacheCommonsNet = "3.6"
  val scalaTestVersion = "3.0.5"
  val mockFtpServer = "2.7.1"
  val mockitoAll = "1.10.19"
  val macroParadiseVersion = "2.1.1"
  val kindProjectorVersion = "0.9.9"
}

val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in publish := true
)

val buildSettings = Seq(
  organization := "io.github.pure4s",
  scalaVersion := "2.12.8",
  licenses := Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
  crossScalaVersions := Seq(scalaVersion.value)
)

val commonDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core"   % V.catsVersion,
    "org.typelevel" %% "cats-effect" % V.catsEffectVersion,
    "commons-net" % "commons-net" % V.apacheCommonsNet,
    "org.scalatest" %% "scalatest"   % V.scalaTestVersion,
    "org.mockftpserver" % "MockFtpServer" % V.mockFtpServer,
    "org.mockito" % "mockito-all" % V.mockitoAll
  )
)

val compilerPlugins = Seq(
  libraryDependencies ++= Seq(
    compilerPlugin("org.scalamacros" %% "paradise"       % V.macroParadiseVersion cross CrossVersion.full),
    compilerPlugin("org.spire-math"  %% "kind-projector" % V.kindProjectorVersion)
  )
)

lazy val ftp4s = project.in(file("."))
  .settings(buildSettings)
  .settings(noPublishSettings)
  .dependsOn(coreJVM)
  .aggregate(coreJVM)

lazy val core = crossProject(JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("core"))
  .settings(moduleName := "ftp4s-core")
  .settings(buildSettings)
  .settings(commonDependencies)
  .settings(compilerPlugins)

/*lazy val example = project
  .in(file("example"))
  .settings(buildSettings)
  .settings(noPublishSettings)
  .settings(compilerPlugins)
  .dependsOn(coreJVM)*/

lazy val coreJVM = core.jvm
