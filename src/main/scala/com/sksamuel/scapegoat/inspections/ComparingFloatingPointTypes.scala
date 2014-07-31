package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class ComparingFloatingPointTypes extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(left, TermName("$eq$eq")), List(right)) =>
          val leftType = Option(left.tpe).map(_.typeSymbol).map(_.fullName).orNull
          val rightType = Option(left.tpe).map(_.typeSymbol).map(_.fullName).orNull
          val leftFloating = leftType == "scala.Double" || leftType == "scala.Float"
          val rightFloating = rightType == "scala.Double" || rightType == "scala.Float"
          if (leftFloating && rightFloating)
            feedback.warn("Floating type comparison", tree.pos, Levels.Error)
        case _ => super.traverse(tree)
      }
    }
  }
}
