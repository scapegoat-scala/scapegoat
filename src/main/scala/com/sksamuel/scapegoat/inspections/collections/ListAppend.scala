package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class ListAppend extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      private def isList(tree: Tree) = tree.tpe <:< typeOf[List[_]]
      private val Append = TermName("$colon$plus")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(lhs, Append), _), _) if isList(lhs) =>
            context.warn("List append is slow",
              tree.pos,
              Levels.Info,
              "List append is O(n). For large lists, consider using prepend and reversing the list or another data structure such as ListBuffer or Vector",
              ListAppend.this)
          case _ => continue(tree)
        }
      }
    }
  }
}