package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ArraysToString extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val ToString = TermName("toString")
      private def isArray(tree: Tree) = tree.tpe <:< typeOf[Array[_]]

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, ToString), Nil) if isArray(lhs) =>
            context.warn("Use of Array.toString", tree.pos, Levels.Warning,
              "toString on an array does not perform a deep toString: " + tree.toString().take(500), ArraysToString.this)
          case _ => continue(tree)
        }
      }
    }
  }
}