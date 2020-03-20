resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.jsuereth"      % "sbt-pgp"       % "1.1.2")
addSbtPlugin("com.github.gseitz" % "sbt-release"   % "1.0.13")
addSbtPlugin("com.eed3si9n"      % "sbt-assembly"  % "0.14.10")
addSbtPlugin("com.dwijnand"      % "sbt-travisci"  % "1.2.0")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"  % "2.3.2")
addSbtPlugin("ch.epfl.scala"     % "sbt-scalafix"  % "0.9.12")
addSbtPlugin("org.scoverage"     % "sbt-scoverage" % "1.6.1")

if (System.getProperty("add-scapegoat-plugin") == "true")
  addSbtPlugin(s"com.sksamuel.scapegoat" % "sbt-scapegoat" % "1.1.0")
else Seq.empty
