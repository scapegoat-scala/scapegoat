package com.sksamuel.scapegoat

import org.scalatest.{FreeSpec, Matchers}

class ReadmeTest extends FreeSpec with Matchers {

  val readme =
    scala.io.Source.fromFile("README.md")
      .getLines().toSeq

  val inspectionNamesFromReadme =
    readme
      .dropWhile(l => l.trim.distinct != "|-")
      .drop(1)
      .takeWhile(l => l.trim.nonEmpty)
      .map(_.split("\\|"))
      .collect {
        case Array(_, className, _) => className.trim
      }

  val inspectionNames = ScapegoatConfig.inspections.map(_.getClass.getSimpleName).toSet

  "README" - {
    "should be up to date" in {
      val inCodeOnly = inspectionNames.diff(inspectionNamesFromReadme.toSet).toSeq.sorted
      val inReadmeOnly = inspectionNamesFromReadme.toSet.diff(inspectionNames).toSeq.sorted

      if (inCodeOnly.nonEmpty || inReadmeOnly.nonEmpty)
        fail(
          s"""
             |README file need to be updated:
             | It misses following inspections found in code: ${inCodeOnly.mkString("[", ",", "]")}
             | It has following inspections not found in code: ${inReadmeOnly.mkString("[", ",", "]")}
             |""".stripMargin)
    }

    "should have inspections listed in order" in {
      inspectionNamesFromReadme.sorted shouldBe inspectionNamesFromReadme
    }

    "should have correct number of inspections" in {
      val Pattern = raw"There are currently (\d+?) inspections.*".r
      readme.collect {
        case Pattern(n) => n.toInt shouldBe inspectionNames.size
      }
    }
  }
}
