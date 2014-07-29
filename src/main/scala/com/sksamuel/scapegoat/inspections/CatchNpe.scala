package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

/** @author Stephen Samuel */
class CatchNpe extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser with SuppressAwareTraverser {

    private def catchesNpe(cases: List[CaseDef]): Boolean = {
      cases.exists(_.pat.tpe.toString == "NullPointerException")
    }

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Try(block, catches, finalizer) if catchesNpe(catches) =>
          reporter.warn("Catching NPE", tree, level = Levels.Error)
        case _ => super.traverse(tree)
      }
    }
  }
}
