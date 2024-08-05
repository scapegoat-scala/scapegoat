package com.sksamuel.scapegoat

import dotty.tools.dotc.Driver
import dotty.tools.dotc.core.Contexts
import dotty.tools.dotc.core.Contexts.ContextBase
import dotty.tools.dotc.plugins.{Plugin, PluginPhase, Plugins}

import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.charset.StandardCharsets
import dotty.tools.dotc.reporting.Reporter
import dotty.tools.dotc.reporting.Reporter.NoReporter

class TestingScapegoatPlugin extends ScapegoatPlugin {

  var scapegoatPhase: Option[ScapegoatPhase] = None

  override def init(options: List[String]): List[PluginPhase] = {
    val phases = super.init(options)
    scapegoatPhase = phases.headOption.map(_.asInstanceOf[ScapegoatPhase])
    phases
  }

  def feedback: FeedbackDotty = scapegoatPhase.get.feedback.get

}

class TestingScapegoat extends ContextBase with Plugins {

  private val scapegoat: TestingScapegoatPlugin = new TestingScapegoatPlugin

  override protected def loadRoughPluginsList(using Contexts.Context): List[Plugin] =
    scapegoat :: super.loadRoughPluginsList

  def feedback: FeedbackDotty = scapegoat.feedback
}

class DottyRunner(val inspection: Class[? <: Inspection]) extends Driver {

  private val dottyVersion: String = dotty.tools.dotc.config.Properties.versionNumberString
  private val scalaVersion: String = util.Properties.versionNumberString

  private val classPath: List[String] = getScalaJars.map(_.getAbsolutePath) :+ sbtCompileDir.getAbsolutePath

  private val testingContext: TestingScapegoat = new TestingScapegoat

  override protected def initCtx: Contexts.Context = testingContext.initialCtx

  def compileCodeSnippet(source: String): FeedbackDotty = {
    val targetDir = Files.createTempDirectory("scapegoat").toFile
    val sourceFile = Files
      .write(Files.createTempFile("scapegoat_snippet", ".scala"), source.getBytes(StandardCharsets.UTF_8))
      .toFile
    sourceFile.deleteOnExit()
    targetDir.deleteOnExit()
    val _ = process(
      Array[String](
        "-Xplugin-require:scapegoat",
        "-P:scapegoat:enabledInspections:" + inspection.getSimpleName,
        "-P:scapegoat:verbose:true",
        "-P:scapegoat:reports:none",
        "-d",
        targetDir.getAbsolutePath,
        "-classpath",
        classPath.mkString(File.pathSeparator),
        sourceFile.getAbsolutePath
      ),
      // Silence the compiler output and only rely on feedback results
      NoReporter
    )
    testingContext.feedback
  }

  private def getScalaJars: List[File] = {
    val scalaJars = List("scala3-library_3")
    findIvyJar("org.scala-lang", "scala-library", scalaVersion) :: scalaJars.map(findScalaJar)
  }

  private def findScalaJar(artifactId: String): File = findIvyJar("org.scala-lang", artifactId, dottyVersion)

  private def findIvyJar(groupId: String, artifactId: String, version: String): File = {
    val userHome = System.getProperty("user.home")
    val sbtHome = userHome + "/.ivy2"
    val jarPath =
      sbtHome + "/cache/" + groupId + "/" + artifactId + "/jars/" + artifactId + "-" + version + ".jar"
    val file = new File(jarPath)
    if (file.exists) file
    else throw new FileNotFoundException(s"Could not locate [$jarPath].")
  }

  private def sbtCompileDir: File = {
    val dir = new File("./target/scala-" + dottyVersion + "/classes")
    if (dir.exists) dir
    else throw new FileNotFoundException(s"Could not locate SBT compile directory for plugin files [$dir]")
  }

}
