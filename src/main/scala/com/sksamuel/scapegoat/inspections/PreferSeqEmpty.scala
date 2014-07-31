package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class PreferSeqEmpty extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case TypeApply(Select(Select(_, TermName("Seq")), TermName("apply")), _) =>
          feedback.warn("Prefer Seq.empty", tree.pos, Levels.Info,
            "Seq[T]() creates a new instance. Consider Set.empty which does not allocate a new object. " +
              tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
