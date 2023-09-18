package com.sksamuel

package object scapegoat {
  val scalaVersion: String = util.Properties.versionNumberString
  private val shortScalaVersion = scalaVersion.split('.').dropRight(1).mkString(".")

  val isScala213: Boolean = shortScalaVersion == "2.13"
  val isScala21312: Boolean = scalaVersion == "2.13.12"
}
