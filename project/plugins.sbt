resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.geirsson"  % "sbt-ci-release" % "1.5.2")
addSbtPlugin("com.eed3si9n"  % "sbt-assembly"   % "0.14.10")
addSbtPlugin("org.scalameta" % "sbt-scalafmt"   % "2.3.4")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix"   % "0.9.13")
addSbtPlugin("org.scoverage" % "sbt-scoverage"  % "1.6.1")

if (System.getProperty("add-scapegoat-plugin") == "true")
  addSbtPlugin(s"com.sksamuel.scapegoat" % "sbt-scapegoat" % "1.1.0")
else Seq.empty
