resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.11")
addSbtPlugin("com.eed3si9n"   % "sbt-assembly"   % "0.14.10")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"   % "2.5.0")
addSbtPlugin("org.scoverage"  % "sbt-scoverage"  % "2.0.6")
addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix"   % "0.10.4")

if (System.getProperty("add-scapegoat-plugin") == "true")
  addSbtPlugin(s"com.sksamuel.scapegoat" % "sbt-scapegoat" % "1.1.0")
else Seq.empty
