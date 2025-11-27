package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.inspections.AvoidRequire
import com.sksamuel.scapegoat.inspections.equality._
import com.sksamuel.scapegoat.inspections.option._
import com.sksamuel.scapegoat.inspections.traits._

object Inspections {

  final val inspections: List[Inspection] = List(
    new EitherGet,
    new OptionGet,
    new AvoidRequire,
    new AbstractTrait,
    new ComparingFloatingPointTypes
  )

}
