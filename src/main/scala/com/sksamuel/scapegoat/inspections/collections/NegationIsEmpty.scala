package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class NegationIsEmpty extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val IsEmpty = TermName("isEmpty")
      private val Bang = TermName("unary_$bang")
      private def isTraversable(tree: Tree) = tree.tpe <:< typeOf[Traversable[_]]

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Select(lhs, IsEmpty), Bang) if isTraversable(lhs) =>
            context.warn("!isEmpty can be replaced with nonEmpty", tree.pos, Levels.Info,
              tree.toString().take(100), NegationIsEmpty.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
