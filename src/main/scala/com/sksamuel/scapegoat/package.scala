package com.sksamuel

package object scapegoat {
  val scalaVersion = util.Properties.versionNumberString
  val shortScalaVersion = scalaVersion.split('.').dropRight(1).mkString(".")

  val isScala213 = shortScalaVersion == "2.13"
}
