resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.geirsson"  % "sbt-ci-release" % "1.5.3")
addSbtPlugin("com.eed3si9n"  % "sbt-assembly"   % "0.15.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt"   % "2.4.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix"   % "0.9.17")
addSbtPlugin("org.scoverage" % "sbt-scoverage"  % "1.6.1")

if (System.getProperty("add-scapegoat-plugin") == "true")
  addSbtPlugin(s"com.sksamuel.scapegoat" % "sbt-scapegoat" % "1.1.0")
else Seq.empty
