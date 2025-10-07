addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.11.2")
addSbtPlugin("com.eed3si9n"   % "sbt-assembly"   % "0.14.10")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"   % "2.5.5")
addSbtPlugin("org.scoverage"  % "sbt-scoverage"  % "2.3.1")
addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix"   % "0.14.3")

if (System.getProperty("add-scapegoat-plugin") == "true")
  addSbtPlugin(s"org.johnnei.scapegoat" % "sbt-scapegoat" % "1.3.1")
else Seq.empty
