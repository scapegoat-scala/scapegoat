package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.inspections.AvoidRequire
import com.sksamuel.scapegoat.inspections.option._

object Inspections {

  final val inspections: List[Inspection] = List(
    new OptionGet,
    new AvoidRequire
  )

}
