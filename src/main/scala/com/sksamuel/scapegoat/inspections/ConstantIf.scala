package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

/** @author Stephen Samuel */
class ConstantIf extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case If(cond, thenp, elsep) =>
          if (cond.toString() == "false" || cond.toString() == "true")
            reporter.warn("Constant if expression", tree, level = Levels.Warning,
              "Constant if expression " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
