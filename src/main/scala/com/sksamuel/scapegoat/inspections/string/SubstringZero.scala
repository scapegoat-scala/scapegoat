package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class SubstringZero extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Substring = TermName("substring")
      private val StringType = typeOf[String]

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, Substring), List(Literal(Constant(0)))) if lhs.tpe <:< StringType =>
            context.warn("String.substring(0)",
              tree.pos,
              Levels.Info,
              "Use of String.substring(0) will always return the same string: " + tree.toString().take(100),
              SubstringZero.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
