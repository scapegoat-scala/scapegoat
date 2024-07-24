package com.sksamuel.scapegoat

/**
 * @author
 *   Stephen Samuel
 */
abstract class Inspection(
  override val text: String,
  override val defaultLevel: Level,
  override val description: String,
  override val explanation: String
) extends InspectionBase {

  val self: Inspection = this

  def inspector(ctx: InspectionContext): Inspector

}
