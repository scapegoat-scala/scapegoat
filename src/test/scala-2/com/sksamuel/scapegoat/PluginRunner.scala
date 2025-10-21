package com.sksamuel.scapegoat

import java.io.{File, FileNotFoundException}
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Files

import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.ConsoleReporter

/**
 * @author
 *   Stephen Samuel
 */
trait PluginRunner {

  val scalaVersion: String = util.Properties.versionNumberString

  val classPath: List[String] = getScalaJars.map(_.getAbsolutePath) :+ sbtCompileDir.getAbsolutePath

  val settings: Settings = {
    val s = new scala.tools.nsc.Settings
    for (_ <- Option(System.getProperty("printphases"))) {
      s.Xprint.value = List("all")
      s.Yrangepos.value = true
      s.Yposdebug.value = true
    }
    s.stopAfter.value = List("refchecks") // no need to go all the way to generating classfiles
    s.classpath.value = classPath.mkString(File.pathSeparator)
    s.feature.value = true
    s
  }

  val inspections: Seq[Inspection]
  val reporter = new ConsoleReporter(settings)
  lazy val compiler = new ScapegoatCompiler(settings, inspections, reporter)

  def writeCodeSnippetToTempFile(code: String): File = {
    val file = Files
      .write(Files.createTempFile("scapegoat_snippet", ".scala"), code.getBytes(StandardCharsets.UTF_8))
      .toFile
    file.deleteOnExit()
    file
  }

  def addToClassPath(groupId: String, artifactId: String, version: String): Unit =
    settings.classpath.value =
      settings.classpath.value + ":" + findIvyJar(groupId, artifactId, version).getAbsolutePath

  def compileCodeSnippet(code: String): ScapegoatCompiler =
    compileSourceFiles(writeCodeSnippetToTempFile(code))
  def compileSourceResources(urls: URL*): ScapegoatCompiler =
    compileSourceFiles(urls.map(_.getFile).map(new File(_)): _*)
  def compileSourceFiles(files: File*): ScapegoatCompiler = {
    reporter.flush()
    val command = new scala.tools.nsc.CompilerCommand(files.map(_.getAbsolutePath).toList, settings)
    new compiler.Run().compile(command.files)
    compiler
  }

  def getScalaJars: List[File] = {
    val scalaJars = List("scala-compiler", "scala-library", "scala-reflect")
    scalaJars.map(findScalaJar)
  }

  def findScalaJar(artifactId: String): File = findIvyJar("org/scala-lang", artifactId, scalaVersion)

  def findIvyJar(groupId: String, artifactId: String, version: String): File = {
    val coursierCache =
      sys.env.getOrElse("COURSIER_CACHE", System.getProperty("user.home") + "/.cache/coursier/v1")
    val mavenProxy = sys.env.getOrElse("MAVEN_PROXY", "https/repo1.maven.org/maven2")
    val jarPath = s"$coursierCache/$mavenProxy/$groupId/$artifactId/$version/$artifactId-$version.jar"
    val file = new File(jarPath)
    if (file.exists) file
    else throw new FileNotFoundException(s"Could not locate [$jarPath].")
  }

  def sbtCompileDir: File = {
    val dir = new File("./target/scala-" + scalaVersion + "/classes")
    if (dir.exists) dir
    else throw new FileNotFoundException(s"Could not locate SBT compile directory for plugin files [$dir]")
  }
}

class ScapegoatCompiler(
  settings: scala.tools.nsc.Settings,
  inspections: Seq[Inspection],
  reporter: ConsoleReporter
) extends scala.tools.nsc.Global(settings, reporter) {

  val scapegoat = new ScapegoatComponent(this, inspections)
  scapegoat.summary = false
  scapegoat.configuration = TestConfiguration.configuration

  override def computeInternalPhases(): Unit = {
    super.computeInternalPhases()
    phasesSet.add(scapegoat)
    phasesDescMap.put(scapegoat, "scapegoat")
  }
}
