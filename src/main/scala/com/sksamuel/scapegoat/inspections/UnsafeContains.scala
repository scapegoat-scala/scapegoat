package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Feedback, Inspection}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class UnsafeContains extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(TypeApply(Select(lhs, TermName("contains")), List(tpe)), List(arg)) =>
          if (lhs.tpe <:< typeOf[Seq[_]] && !(arg.tpe <:< lhs.tpe.typeArgs.head)) {
            feedback.warn("Unsafe contains", tree.pos, Levels.Error, tree.toString().take(300))
          }
        case _ => super.traverse(tree)
      }
    }
  }
}
