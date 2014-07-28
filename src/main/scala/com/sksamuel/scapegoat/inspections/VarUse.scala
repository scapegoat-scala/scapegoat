package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

/** @author Stephen Samuel */
class VarUse extends Inspection {

  import scala.reflect.runtime.universe._
  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case ValDef(modifiers, name, tpt, rhs) if modifiers.hasFlag(Flag.SYNTHETIC) =>
        case ValDef(modifiers, name, tpt, rhs) if modifiers.hasFlag(Flag.MUTABLE) =>
          reporter.warn("Use of var", tree, Levels.Warning, "var used: " + tree.toString().take(300))
        case _ => super.traverse(tree)
      }
    }
  }
}
