package com.sksamuel.scapegoat

/**
 * @author
 *   Stephen Samuel
 */
abstract class Inspection(
  val text: String,
  val defaultLevel: Level,
  val description: String,
  val explanation: String
) {

  val self: Inspection = this

  // fixme(johan): Where is this used?
  // def inspector(ctx: InspectionContext): Inspector

  def isEnabled: Boolean = true

  def name: String = getClass.getSimpleName
}
