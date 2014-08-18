package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel
  *
  *         Inspired by Intellij
  * */
class ExistsSimplifableToContains extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply  new context.Traverser {

      import context.global._

      private val Equals = TermName("$eq$eq")
      private def isTraversable(tree: Tree) = tree.tpe <:< typeOf[Traversable[_]]
      private def isContainsType(container: Tree, value: Tree): Boolean = {
        val l = value.tpe.underlying.typeSymbol.tpe
        container.tpe.underlying.typeArgs.headOption match {
          case Some(t) =>
            l <:< t || l =:= t
          case None => false
        }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("exists")), List(Function(_, Apply(Select(_, Equals), List(x)))))
            if isTraversable(lhs) && isContainsType(lhs, x) =>
            context.warn("Exists simplifable to contains",
              tree.pos,
              Levels.Info,
              "exists(x => x == y) can be replaced with contains(y): " + tree.toString().take(500),
              ExistsSimplifableToContains.this)
          case _ => continue(tree)
        }
      }
    }
  }
}