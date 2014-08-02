package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Feedback, Inspection}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class CollectionNegativeIndex extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(lhs, TermName("apply")), List(Literal(Constant(x: Int))))
          if lhs.tpe <:< typeOf[List[_]] && x < 0 =>
          feedback.warn("Collection index out of bounds", tree.pos, Levels.Warning, tree.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }
}
