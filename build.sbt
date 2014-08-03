name := "scalac-scapegoat-plugin"

organization := "com.sksamuel.scapegoat"

version := "0.7.0"

scalaVersion := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

publishMavenStyle := true

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

libraryDependencies ++= Seq(
  "org.scala-lang"                  %     "scala-reflect"         % "2.11.2",
  // we must include the compiler in order for travis to download it into its local ivy cache
  "org.scala-lang"                  %     "scala-compiler"        % "2.11.2"      % "test",
  "commons-io"                      %     "commons-io"            % "2.4"         % "test",
  "org.scalatest"                   %%    "scalatest"             % "2.1.6"       % "test",
  "com.typesafe.scala-logging"      %%    "scala-logging-slf4j"   % "2.1.2"       % "test",
  "org.mockito"                     %     "mockito-all"           % "1.9.5"       % "test",
  "joda-time"                       %     "joda-time"             % "2.3"         % "test",
  "org.joda"                        %     "joda-convert"          % "1.3.1"       % "test",
  "org.slf4j"                       %     "slf4j-api"             % "1.7.7"       % "test"
)

libraryDependencies += {
  "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided"
}

publishTo <<= version {
  (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

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
      <url>git@github.com:sksamuel/scam.git</url>
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
