package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.inspections.option._

object Inspections {

  final val inspections: Seq[Inspection] = List(
    new OptionGet
  )

}
