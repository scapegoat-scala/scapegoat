package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

import scala.runtime.{ RichLong, RichInt }

/** @author Stephen Samuel */
class AvoidToMinusOne extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._
      import definitions._

      private val Foreach = TermName("foreach")
      private val Minus = TermName("$minus")
      private val To = TermName("to")

      private def isIntegral(tree: Tree): Boolean = {
        tree.tpe <:< IntTpe || tree.tpe <:< LongTpe || tree.tpe <:< typeOf[RichInt] || tree.tpe <:< typeOf[RichLong]
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(Apply(Select(lhs, To),
            List(Apply(Select(loopvar, Minus), List(Literal(Constant(1)))))), Foreach), _), _) if isIntegral(lhs) && isIntegral(loopvar) =>
            context.warn("Avoid To Minus One", tree.pos, Levels.Info,
              "j to k - 1 can be better written as j until k: " + tree.toString().take(200),
              AvoidToMinusOne.this)
          case _ => continue(tree)
        }
      }
    }
  }
}

