package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class NegationNonEmpty extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val IsEmpty = TermName("nonEmpty")
      private val Bang = TermName("unary_$bang")
      private def isTraversable(tree: Tree) = tree.tpe <:< typeOf[Traversable[_]]

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Select(lhs, IsEmpty), Bang) if isTraversable(lhs) =>
            context.warn("!nonEmpty can be replaced with isEmpty", tree.pos, Levels.Info,
              tree.toString().take(100), NegationNonEmpty.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
