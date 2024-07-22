package com.sksamuel.scapegoat

/**
 * Shareable logic for the compiler plugin between Scala 2 and 3
 */
abstract trait ScapegoatBasePlugin {

  var configuration: Configuration
  val inspections: Seq[Inspection]

  def disableAll: Boolean = configuration.disabledInspections.exists(_.compareToIgnoreCase("all") == 0)

  def activeInspections: Seq[Inspection] = {
    if (configuration.enabledInspections.isEmpty)
      (inspections ++ configuration.customInspectors)
        .filterNot(inspection => configuration.disabledInspections.contains(inspection.name))
    else
      (inspections ++ configuration.customInspectors)
        .filter(inspection => configuration.enabledInspections.contains(inspection.name))
  }

}
