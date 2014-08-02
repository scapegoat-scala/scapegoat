package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat._

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class ArraysToString extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    private def isArray(tree: Tree) = tree.tpe <:< typeOf[Array[_]]

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(lhs, TermName("toString")), Nil) if isArray(lhs) =>
          feedback.warn("Use of Array.toString", tree.pos, Levels.Warning,
            "toString on an array does not perform a deep toString: " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
