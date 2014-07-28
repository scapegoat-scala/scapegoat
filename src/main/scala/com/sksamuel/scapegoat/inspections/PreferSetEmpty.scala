package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

/** @author Stephen Samuel */
class PreferSetEmpty extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case TypeApply(Select(Select(_, TermName("Set")), TermName("apply")), _) =>
          reporter.warn("Prefer Set.empty", tree, Levels.Info,
            "Set[T]() creates a new instance. Consider Set.empty which does not allocate a new object. " +
              tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
