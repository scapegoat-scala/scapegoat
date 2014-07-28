package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

/** @author Stephen Samuel */
class EmptySynchronizedBlock extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(TypeApply(Select(_, TermName("synchronized")), _), List(Literal(Constant(())))) =>
          reporter.warn("Empty synchronized block", tree, level = Levels.Warning)
        case _ => super.traverse(tree)
      }
    }
  }
}
