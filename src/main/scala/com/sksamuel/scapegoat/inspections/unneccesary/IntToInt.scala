package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class IntToInt extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._
      import definitions._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(lhs, TermName("toInt")) if lhs.tpe <:< IntClass.tpe =>
            context.warn("IntToInt", tree.pos, Levels.Warning,
              "Unncessary conversion of int to int " + tree.toString().take(100), IntToInt.this)
          case _ =>
        }
        continue(tree)
      }
    }
  }
}
