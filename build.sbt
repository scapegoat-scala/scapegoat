// compiler plugins
addCompilerPlugin("org.scalameta" % "semanticdb-scalac" % "4.4.23" cross CrossVersion.full)

name := "scalac-scapegoat-plugin"
organization := "com.sksamuel.scapegoat"
description := "Scala compiler plugin for static code analysis"
homepage := Some(url("https://github.com/sksamuel/scapegoat"))
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/sksamuel/scapegoat"),
    "scm:git@github.com:sksamuel/scapegoat.git",
    Some("scm:git@github.com:sksamuel/scapegoat.git")
  )
)
developers := List(
  Developer(
    "sksamuel",
    "sksamuel",
    "@sksamuel",
    url("https://github.com/sksamuel")
  )
)

scalaVersion := "2.13.6"
crossScalaVersions := Seq("2.11.12", "2.12.13", "2.12.14", "2.13.5", "2.13.6")
autoScalaLibrary := false
crossVersion := CrossVersion.full
crossTarget := {
  // workaround for https://github.com/sbt/sbt/issues/5097
  target.value / s"scala-${scalaVersion.value}"
}

// https://github.com/sksamuel/scapegoat/issues/298
ThisBuild / useCoursier := false

val scalac13Options = Seq(
  "-Xlint:adapted-args",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:nullary-unit",
  "-Yrangepos",
  "-Ywarn-unused"
)
val scalac12Options = Seq(
  "-Xlint:adapted-args",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Xlint:nullary-override",
  "-Xlint:nullary-unit",
  "-Xmax-classfile-name",
  "254"
)
val scalac11Options = Seq(
  "-Ywarn-adapted-args",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-dead-code",
  "-Ywarn-nullary-unit",
  "-Ywarn-numeric-widen",
  "-Xmax-classfile-name",
  "254"
  //"-Ywarn-value-discard"
)
scalacOptions := {
  val common = Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-encoding",
    "utf8",
    "-Xlint"
  )
  common ++ (scalaBinaryVersion.value match {
    case "2.11" => scalac11Options
    case "2.12" => scalac12Options
    case "2.13" =>
      scalac13Options ++ (scalaVersion.value.split('.') match {
        case Array(_, _, patch) if Set("0", "1", "2")(patch) => Seq("-Xlint:nullary-override")
        case _                                               => Seq.empty[String]
      })
  })
}
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

// because that's where "PluginRunner" is
Compile / console / fullClasspath ++= (Test / fullClasspath).value
console / initialCommands := s"""
import com.sksamuel.scapegoat._
def check(code: String) = {
  val runner = new PluginRunner { val inspections = ScapegoatConfig.inspections }
  // Not sufficient for reuse, not sure why.
  // runner.reporter.reset
  val c = runner compileCodeSnippet code
  val feedback = c.scapegoat.feedback
  feedback.warnings map (x => "%-40s %s %s".format(x.text, x.explanation, x.snippet.getOrElse(""))) foreach println
  feedback
}
"""

libraryDependencies ++= Seq(
  "org.scala-lang"          % "scala-reflect"           % scalaVersion.value % "provided",
  "org.scala-lang"          % "scala-compiler"          % scalaVersion.value % "provided",
  "org.scala-lang.modules" %% "scala-xml"               % "1.3.0" excludeAll ExclusionRule(organization = "org.scala-lang"),
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.4" excludeAll ExclusionRule(organization =
    "org.scala-lang"
  ),
  "org.scala-lang" % "scala-compiler" % scalaVersion.value % "test",
  "org.scalatest" %% "scalatest"      % "3.2.9"            % "test",
  "org.mockito"    % "mockito-all"    % "1.10.19"          % "test",
  "joda-time"      % "joda-time"      % "2.10.10"          % "test",
  "org.joda"       % "joda-convert"   % "2.2.1"            % "test",
  "org.slf4j"      % "slf4j-api"      % "1.7.32"           % "test"
)

// Test
Test / run / fork := true
Test / logBuffered := false
Test / parallelExecution := false

// ScalaTest reporter config:
// -o - standard output,
// D - show all durations,
// T - show reminder of failed and cancelled tests with short stack traces,
// F - show full stack traces.
Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oDTF")

// Assembly
// include the scala xml and compat modules into the final jar, shaded
assembly / assemblyShadeRules := Seq(
  ShadeRule.rename("scala.xml.**" -> "scapegoat.xml.@1").inAll,
  ShadeRule.rename("scala.collection.compat.**" -> "scapegoat.compat.@1").inAll
)
Compile / packageBin := crossTarget.value / (assembly / assemblyJarName).value
makePom := makePom.dependsOn(assembly).value
assembly / test := {} // do not run tests during assembly
Test / publishArtifact := false

// Scalafix
ThisBuild / scalafixDependencies += "com.nequissimus" %% "sort-imports" % "0.3.1"
addCommandAlias("fix", "all compile:scalafix test:scalafix; fixImports")
addCommandAlias("fixImports", "compile:scalafix SortImports; test:scalafix SortImports")
addCommandAlias("fixCheck", "compile:scalafix --check; test:scalafix --check; fixCheckImports")
addCommandAlias("fixCheckImports", "compile:scalafix --check SortImports; test:scalafix --check SortImports")

// Scalafmt
ThisBuild / scalafmtOnCompile := sys.env.get("GITHUB_ACTIONS").forall(_.toLowerCase == "false")
