package com.sksamuel.scapegoat.io

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

import com.sksamuel.scapegoat.{Feedback, Levels, Warning}

/**
 * Supports GitLab Code Quality report format.
 *
 * https://docs.gitlab.com/ee/ci/testing/code_quality.html#implement-a-custom-tool
 */
object GitlabCodeQualityReportWriter extends ReportWriter {

  override protected def fileName: String = "scapegoat-gitlab.json"

  override protected def generate(feedback: Feedback): String =
    toCodeQualityElements(feedback.warningsWithMinimalLevel, sys.env.get("CI_PROJECT_DIR"))
      .map(_.toJsonArrayElement)
      .mkString("[", ",", "]")

  def toCodeQualityElements(
    warnings: Seq[Warning],
    gitlabBuildDir: Option[String]
  ): Seq[CodeQualityReportElement] = warnings.map { warning =>
    // Stable hash for the same warning.
    // Avoids moving code blocks around from causing "new" detecions.
    val fingerprintRaw = warning.sourceFileNormalized + warning.snippet.getOrElse(warning.line.toString)
    val fingerprint = MessageDigest
      .getInstance("MD5")
      .digest(fingerprintRaw.getBytes(StandardCharsets.UTF_8))
      .map("%02x".format(_))
      .mkString

    val severity = warning.level match {
      case Levels.Error   => CriticalSeverity
      case Levels.Warning => MinorSeverity
      case Levels.Info    => InfoSeverity
      case _              => InfoSeverity
    }

    val gitlabCiNormalizedPath = gitlabBuildDir
      .map { buildDir =>
        val fullBuildDir = if (buildDir.endsWith("/")) buildDir else s"$buildDir/"
        val file = warning.sourceFileFull
        if (file.startsWith(fullBuildDir)) file.drop(fullBuildDir.length) else file
      }
      .getOrElse(warning.sourceFileFull)

    val textStart = if (warning.explanation.startsWith(warning.text)) {
      ""
    } else {
      if (warning.text.endsWith(".")) {
        warning.text + " "
      } else {
        warning.text + ". "
      }
    }
    val description = s"$textStart${warning.explanation}"

    CodeQualityReportElement(
      description = description,
      checkName = warning.inspection,
      severity = severity,
      location = Location(gitlabCiNormalizedPath, Lines(warning.line)),
      fingerprint = fingerprint
    )
  }
}

sealed trait CodeClimateSeverity {
  val name: String
}

case object InfoSeverity extends CodeClimateSeverity {
  override val name: String = "info"
}

case object MinorSeverity extends CodeClimateSeverity {
  override val name: String = "minor"
}

case object CriticalSeverity extends CodeClimateSeverity {
  override val name: String = "critical"
}

case class Location(path: String, lines: Lines)

case class Lines(begin: Int)

case class CodeQualityReportElement(
  description: String,
  checkName: String,
  severity: CodeClimateSeverity,
  location: Location,
  fingerprint: String
) {

  // Manual templating is a bit silly but avoids a dependency on a potentially conflicting json library.
  def toJsonArrayElement: String =
    s"""
       |  {
       |    "description": "${description.replace("\"", "\\\"")}",
       |    "check_name": "$checkName",
       |    "fingerprint": "$fingerprint",
       |    "severity": "${severity.name}",
       |    "location": {
       |      "path": "${location.path}",
       |      "lines": {
       |        "begin": ${location.lines.begin}
       |      }
       |    }
       |  }""".stripMargin
}
