package com.sksamuel.scapegoat

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class ReadmeTest extends AnyFreeSpec with Matchers {

  val readme =
    scala.io.Source
      .fromFile("README.md")
      .getLines()
      .toSeq

  val inspectionNamesAndLevelsFromReadme =
    readme
      .dropWhile(l => l.trim.distinct != "|-")
      .drop(1)
      .dropWhile(l => l.trim.distinct != "|-")
      .drop(1)
      .takeWhile(l => l.trim.nonEmpty)
      .map(_.split("\\|"))
      .collect { case Array(_, className, _, level, scala2, scala3) =>
        className.trim -> (level.trim, scala2.trim.contains("Yes"), scala3.trim.contains("Yes"))
      }
      .filter { case (_, (_, scala2, scala3)) =>
        scala2 == (ScalaVersion.version == 2) || scala3 == (ScalaVersion.version == 3) || (!scala2 && !scala3)
      }
      .map { case (name, (level, _, _)) => name -> level }

  val inspectionNamesAndLevels =
    Inspections.inspections
      .map(i => i.getClass.getSimpleName -> i.defaultLevel.toString)
      .toSet

  "README" - {
    "should be up to date" in {
      val inCodeOnly = inspectionNamesAndLevels.diff(inspectionNamesAndLevelsFromReadme.toSet).toSeq.sorted
      val inReadmeOnly = inspectionNamesAndLevelsFromReadme.toSet.diff(inspectionNamesAndLevels).toSeq.sorted

      if (inCodeOnly.nonEmpty || inReadmeOnly.nonEmpty)
        fail(s"""
                |README file need to be updated:
                | It misses following inspections found in code: ${inCodeOnly.mkString("[", ",", "]")}
                | It has following inspections not found in code: ${inReadmeOnly.mkString("[", ",", "]")}
                |""".stripMargin)
    }

    "should have inspections listed in order" in {
      inspectionNamesAndLevelsFromReadme.sorted shouldBe inspectionNamesAndLevelsFromReadme
    }

    "should have correct number of inspections" in {
      val Pattern = if (ScalaVersion.version == 2) {
        raw"There are currently (\d+?) inspections.*".r
      } else {
        raw"There are currently \d+ inspections.*, and (\d+?) for Scala 3\.".r
      }
      readme.collect { case Pattern(n) =>
        n.toInt shouldBe inspectionNamesAndLevels.size
      }
    }

    "should mention all existing configuration options" - {
      val existingOptions = classOf[Configuration].getDeclaredFields.map(_.getName)
      val readmeText = readme.mkString("\n")

      existingOptions.foreach { option =>
        s"$option should be listed in help" in {
          readmeText.contains(s"-P:scapegoat:$option:") shouldBe true
        }
      }
    }
  }
}
