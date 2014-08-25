package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ListSize extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(lhs, TermName("size")) if lhs.tpe <:< typeOf[List[_]] =>
            context.warn("List.size is O(n)", tree.pos, Levels.Info,
              "List.size is O(n). Consider using a different data type with O(1) size lookup such as Vector or Array.",
              ListSize.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
