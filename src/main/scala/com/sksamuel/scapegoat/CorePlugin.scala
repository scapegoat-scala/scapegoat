package com.sksamuel.scapegoat

/**
 * Shareable logic for the compiler plugin between Scala 2 and 3
 */
abstract trait ScapegoatBasePlugin {

  var configuration: Configuration
  val inspections: Seq[Inspection]

  // Tests override inspections which doesn't play nice with initialization order.
  private lazy val allInspections = inspections ++ configuration.customInspectors

  def disableAll: Boolean = configuration.disabledInspections.exists(_.compareToIgnoreCase("all") == 0)

  def activeInspections: Seq[Inspection] = {
    if (configuration.enabledInspections.isEmpty)
      allInspections.filterNot(inspection => configuration.disabledInspections.contains(inspection.name))
    else
      allInspections.filter(inspection => configuration.enabledInspections.contains(inspection.name))
  }

}
