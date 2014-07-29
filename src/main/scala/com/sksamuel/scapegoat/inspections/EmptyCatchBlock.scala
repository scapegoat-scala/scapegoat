package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class EmptyCatchBlock extends Inspection {

  override def traverser(reporter: Reporter) = new universe.Traverser with SuppressAwareTraverser {

    import scala.reflect.runtime.universe._

    def checkCatch(cd: CaseDef): Unit = {
      if (cd.body.toString == "()")
        reporter.warn("Empty finalizer", cd, Levels.Warning,
          "Empty finalizer near " + cd.toString().take(100))
    }

    def checkCatches(defs: List[CaseDef]) = defs.foreach(cd => checkCatch(cd))

    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Try(body, catches, finalizer) => checkCatches(catches)
        case _ => super.traverse(tree)
      }
    }
  }
}
