package com.sksamuel.scapegoat

/**
 * @author
 *   Stephen Samuel
 */
trait InspectionBase {

  val text: String
  val defaultLevel: Level
  val description: String
  val explanation: String

  def isEnabled: Boolean = true

  def name: String = getClass.getSimpleName
}
