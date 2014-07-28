package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

/** @author Stephen Samuel */
class RedundantFinalizer extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case dd@DefDef(mods, name, _, _, tpt, _)
          if mods.hasFlag(Flag.OVERRIDE) && name.toString == "finalize" && tpt.toString() == "Unit" =>
          reporter.warn("Redundant finalizer", tree, Levels.Warning, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
