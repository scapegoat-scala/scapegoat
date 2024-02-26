package com.sksamuel

import scala.util.Try

package object scapegoat {
  private val scalaVersion: String = util.Properties.versionNumberString
  private val (major, minor, patch) = extractComponents(scalaVersion)

  val isScala213: Boolean = major == 2 && minor == 13
  val isScala21312OrLater: Boolean = isScala213 && patch >= 12

  private[scapegoat] def extractComponents(version: String) = {
    def parseInt(s: String) = Try(s.toInt).getOrElse(0)
    version.split('.').toList.map(parseInt) match {
      case List(major, minor, patch) => (major, minor, patch)
      case _                         => (1, 0, 0)
    }
  }
}
