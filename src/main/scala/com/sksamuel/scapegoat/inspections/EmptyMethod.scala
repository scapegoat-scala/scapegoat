package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

/** @author Stephen Samuel */
class EmptyMethod extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case DefDef(mods, _, _, _, _, Literal(Constant(()))) if !mods.hasFlag(Flag.SYNTHETIC) =>
          reporter.warn("Empty method", tree, Levels.Warning, "Empty if statement " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
