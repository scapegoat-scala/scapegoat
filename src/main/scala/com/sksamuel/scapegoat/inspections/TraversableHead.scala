package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class TraversableHead extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(left, TermName("head")) =>
          println(left.tpe.typeSymbol.fullName.toString)
          if (left.tpe.typeSymbol.fullName.toString == "scala.collection.Iterable")
            feedback.warn("Use of Option.head", tree.pos, Levels.Error, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
