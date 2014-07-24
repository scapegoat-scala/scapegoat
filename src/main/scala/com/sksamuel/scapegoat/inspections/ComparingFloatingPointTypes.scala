package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Reporter}

import scala.reflect.runtime._
import scala.reflect.runtime.universe._

/** @author Stephen Samuel */
object ComparingFloatingPointTypes extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Apply(Select(left, TermName("$eq$eq")), List(right)) =>
          val leftType = Option(left.tpe).map(_.typeSymbol).map(_.fullName).orNull
          val rightType = Option(left.tpe).map(_.typeSymbol).map(_.fullName).orNull
          val leftFloating = leftType == "scala.Double" || leftType == "scala.Float"
          val rightFloating = rightType == "scala.Double" || rightType == "scala.Float"
          if (leftFloating && rightFloating) reporter.warn("Floating type comparison", tree.pos.line)
        case _ => super.traverse(tree)
      }
    }
  }
}
