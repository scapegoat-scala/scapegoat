package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class OptionSize extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(Apply(option2Iterable, List(opt)), TermName("size")) =>
          if (option2Iterable.symbol.fullName == "scala.Option.option2Iterable")
            feedback.warn("Prefer Option.isDefined instead of Option.size", tree.pos, Levels.Error, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
