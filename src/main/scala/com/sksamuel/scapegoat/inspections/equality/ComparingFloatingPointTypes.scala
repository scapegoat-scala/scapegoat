package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ComparingFloatingPointTypes extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(left, TermName("$eq$eq")), List(right)) =>
            val leftType = Option(left.tpe).map(_.typeSymbol).map(_.fullName).orNull
            val rightType = Option(left.tpe).map(_.typeSymbol).map(_.fullName).orNull
            val leftFloating = leftType == "scala.Double" || leftType == "scala.Float"
            val rightFloating = rightType == "scala.Double" || rightType == "scala.Float"
            if (leftFloating && rightFloating)
              context.warn("Floating type comparison", tree.pos, Levels.Error)
          case _ => continue(tree)
        }
      }
    }
  }
}
