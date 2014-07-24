package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
object VarUse extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case universe.ValDef(modifiers, name, tpt, rhs) if modifiers.hasFlag(universe.Flag.MUTABLE) =>
          reporter.warn("Use of var", tree.pos.line)
        case _ => super.traverse(tree)
      }
    }
  }
}
