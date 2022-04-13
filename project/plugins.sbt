resolvers += Classpaths.sbtPluginReleases


addSbtPlugin("com.github.sbt"  % "sbt-ci-release" % "1.5.10")
addSbtPlugin("com.eed3si9n"  % "sbt-assembly"   % "0.14.10")
addSbtPlugin("org.scalameta" % "sbt-scalafmt"   % "2.4.6")
addSbtPlugin("org.scoverage" % "sbt-scoverage"  % "1.9.3")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix"   % "0.10.0")


if (System.getProperty("add-scapegoat-plugin") == "true")
  addSbtPlugin(s"com.sksamuel.scapegoat" % "sbt-scapegoat" % "1.1.0")
else Seq.empty
