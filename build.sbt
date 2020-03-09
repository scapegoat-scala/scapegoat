import com.typesafe.sbt.pgp.PgpKeys
import sbt.Keys._

name := "scalac-scapegoat-plugin"

organization := "com.sksamuel.scapegoat"

crossVersion := CrossVersion.full
crossTarget := {
  // workaround for https://github.com/sbt/sbt/issues/5097
  target.value / s"scala-${scalaVersion.value}"
}
releaseCrossBuild := true

sbtVersion in Global := "1.1.6"

SbtPgp.autoImport.useGpg := true

SbtPgp.autoImport.useGpgAgent := true

val scalac13Options = Seq(
  "-Xlint:adapted-args",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:nullary-override",
  "-Xlint:nullary-unit",
)

val scalac12Options = Seq(
  "-Xlint:adapted-args",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Xlint:nullary-override",
  "-Xlint:nullary-unit",
  "-Xmax-classfile-name", "254"
)

val scalac11Options = Seq(
  "-Ywarn-adapted-args",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-dead-code",
  "-Ywarn-nullary-unit",
  "-Ywarn-numeric-widen",
  "-Xmax-classfile-name", "254",
  //"-Ywarn-value-discard"
)

scalacOptions := {
  val common = Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-encoding", "utf8",
    "-Xlint"
  )

  common ++ (scalaBinaryVersion.value match {
    case "2.11" => scalac11Options
    case "2.12" => scalac12Options
    case "2.13" => scalac13Options
  })
}

fullClasspath in console in Compile ++= (fullClasspath in Test).value // because that's where "PluginRunner" is

initialCommands in console := s"""
import com.sksamuel.scapegoat._
def check(code: String) = {
  val runner = new PluginRunner { val inspections = ScapegoatConfig.inspections }
  // Not sufficient for reuse, not sure why.
  // runner.reporter.reset
  val c = runner compileCodeSnippet code
  val feedback = c.scapegoat.feedback
  feedback.warnings map (x => "%-40s  %s".format(x.text, x.snippet getOrElse "")) foreach println
  feedback
}
"""

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  "org.scala-lang"                  %     "scala-reflect"           % scalaVersion.value      % "provided",
  "org.scala-lang"                  %     "scala-compiler"          % scalaVersion.value      % "provided",
  "org.scala-lang.modules"          %%    "scala-xml"               % "1.2.0" excludeAll(
    ExclusionRule(organization = "org.scala-lang")
  ),
  "org.scala-lang.modules"          %%    "scala-collection-compat" % "2.1.2" excludeAll(
    ExclusionRule(organization = "org.scala-lang")
  ),
  "org.scala-lang"                  %     "scala-compiler"          % scalaVersion.value      % "test",
  "commons-io"                      %     "commons-io"              % "2.6"                   % "test",
  "org.scalatest"                   %%    "scalatest"               % "3.0.8"                 % "test",
  "org.mockito"                     %     "mockito-all"             % "1.10.19"               % "test",
  "joda-time"                       %     "joda-time"               % "2.10.5"                 % "test",
  "org.joda"                        %     "joda-convert"            % "2.2.1"                 % "test",
  "org.slf4j"                       %     "slf4j-api"               % "1.7.30"                % "test"
)

sbtrelease.ReleasePlugin.autoImport.releasePublishArtifactsAction := PgpKeys.publishSigned.value

sbtrelease.ReleasePlugin.autoImport.releaseCrossBuild := true

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

publishMavenStyle := true

publishArtifact in Test := false

parallelExecution in Test := false

pomIncludeRepository := {
  _ => false
}

pomExtra := {
  <url>https://github.com/sksamuel/scapegoat</url>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:sksamuel/scapegoat.git</url>
      <connection>scm:git@github.com:sksamuel/scapegoat.git</connection>
    </scm>
    <developers>
      <developer>
        <id>sksamuel</id>
        <name>sksamuel</name>
        <url>http://github.com/sksamuel</url>
      </developer>
    </developers>
}

// include the scala xml and compat modules into the final jar, shaded
assemblyShadeRules in assembly := Seq(
  ShadeRule.rename("scala.xml.**" -> "scapegoat.xml.@1").inAll,
  ShadeRule.rename("scala.collection.compat.**" -> "scapegoat.compat.@1").inAll
)

// do not run tests during assembly
test in assembly := {}

autoScalaLibrary := false
makePom := makePom.dependsOn(assembly).value
packageBin in Compile := crossTarget.value / (assemblyJarName in assembly).value

// debug assembly process
//logLevel in assembly := Level.Debug
