package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Levels, Feedback, Inspection}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class ListSize extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(lhs, TermName("size")) if lhs.tpe <:< typeOf[List[_]] =>
          feedback.warn("List.size is O(n)", tree.pos, Levels.Info,
            "List.size is O(n). Consider using a different data type with O(1) size lookup such as Vector or Array.")
        case _ => super.traverse(tree)
      }
    }
  }
}
