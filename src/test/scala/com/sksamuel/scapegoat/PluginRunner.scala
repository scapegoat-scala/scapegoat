package com.sksamuel.scapegoat

import java.io.{ File, FileNotFoundException }
import java.net.URL

import scala.tools.nsc.reporters.ConsoleReporter

/**
 * @author Stephen Samuel
 * @author Eugene Sypachev (Axblade)
 */
trait PluginRunner {

  val scalaVersion = util.Properties.versionNumberString
  val shortScalaVersion = scalaVersion.dropRight(2)

  val classPath = getScalaJars.map(_.getAbsolutePath) :+ sbtCompileDir.getAbsolutePath

  val settings = {
    val s = new scala.tools.nsc.Settings
    for (dummy <- Option(System.getProperty("printphases"))) {
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
    val file = File.createTempFile("scapegoat_snippet", ".scala")
    org.apache.commons.io.FileUtils.write(file, code)
    file.deleteOnExit()
    file
  }

  def addToClassPath(groupId: String, artifactId: String, version: String): Unit = {
    settings.classpath.value = settings.classpath.value + ":" + findIvyJar(groupId, artifactId, version).getAbsolutePath
  }

  def compileCodeSnippet(code: String): ScapegoatCompiler = compileSourceFiles(writeCodeSnippetToTempFile(code))
  def compileSourceResources(urls: URL*): ScapegoatCompiler = {
    compileSourceFiles(urls.map(_.getFile).map(new File(_)): _*)
  }
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

  def findScalaJar(artifactId: String): File = findIvyJar("org.scala-lang", artifactId, scalaVersion)

  def findIvyJar(groupId: String, artifactId: String, version: String): File = {
    val userHome = System.getProperty("user.home")
    val sbtHome = userHome + "/.ivy2"
    val jarPath = sbtHome + "/cache/" + groupId + "/" + artifactId + "/jars/" + artifactId + "-" + version + ".jar"
    val file = new File(jarPath)
    if (file.exists) {
      // println(s"Located ivy jar [$file]")
      file
    } else throw new FileNotFoundException(s"Could not locate [$jarPath].")
  }

  def sbtCompileDir: File = {
    val dir = new File("./target/scala-" + shortScalaVersion + "/classes")
    if (dir.exists) dir
    else throw new FileNotFoundException(s"Could not locate SBT compile directory for plugin files [$dir]")
  }
}

class ScapegoatCompiler(settings: scala.tools.nsc.Settings,
  inspections: Seq[Inspection],
  reporter: ConsoleReporter)
    extends scala.tools.nsc.Global(settings, reporter) {

  val scapegoat = new ScapegoatComponent(this, inspections)
  scapegoat.disableHTML = true
  scapegoat.disableXML = true
  scapegoat.disableScalastyleXML = true
  scapegoat.verbose = false
  scapegoat.summary = false

  override def computeInternalPhases() {
    super.computeInternalPhases()
    phasesSet.add(scapegoat)
    phasesDescMap.put(scapegoat, "scapegoat")
  }
}
