package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class UnnecessaryToString extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._
      import definitions._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(lhs, TermName("toString")) if lhs.tpe <:< StringClass.tpe =>
            context.warn("Unnecessary toString", tree.pos, Levels.Warning,
              "Unnecessary toString on instanceo of String: " + tree.toString().take(200), UnnecessaryToString.this)
          case _ =>
        }
        continue(tree)
      }
    }
  }
}
