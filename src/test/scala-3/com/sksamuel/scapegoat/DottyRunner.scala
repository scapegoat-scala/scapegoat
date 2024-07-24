package com.sksamuel.scapegoat

import dotty.tools.dotc.Driver
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.charset.StandardCharsets

class DottyRunner(val inspection: Class[? <: Inspection]) extends Driver {

  private val dottyVersion: String = dotty.tools.dotc.config.Properties.versionNumberString
  private val scalaVersion: String = util.Properties.versionNumberString

  private val classPath: List[String] = getScalaJars.map(_.getAbsolutePath) :+ sbtCompileDir.getAbsolutePath
  private def getScalaJars: List[File] = {
    // val scalaJars = List("scala3-compiler_3", "scala3-library_3")
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

  def compileCodeSnippet(source: String): Unit = {
    val targetDir = Files.createTempDirectory("scapegoat").toFile
    val sourceFile = Files
      .write(Files.createTempFile("scapegoat_snippet", ".scala"), source.getBytes(StandardCharsets.UTF_8))
      .toFile
    sourceFile.deleteOnExit()
    targetDir.deleteOnExit()
    val reporter = process(
      Array[String](
        "-Xplugin:" + sbtCompileDir.getAbsolutePath(),
        "-Xplugin-require:scapegoat",
        "-P:scapegoat:enabledInspections:" + inspection.getSimpleName(),
        "-P:scapegoat:verbose:true",
        "-classpath",
        classPath.mkString(File.pathSeparator),
        sourceFile.getAbsolutePath()
      )
    )
  }
}
