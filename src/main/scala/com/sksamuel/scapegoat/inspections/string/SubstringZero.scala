package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class SubstringZero
    extends Inspection(
      text = "String.substring(0)",
      defaultLevel = Levels.Info,
      description = "Checks for String.substring(0).",
      explanation = "Use of String.substring(0) will always return the same string."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val Substring = TermName("substring")
          private val StringType = typeOf[String]

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(lhs, Substring), List(Literal(Constant(0)))) if lhs.tpe <:< StringType =>
                context.warn(tree.pos, self, tree.toString.take(100))
              case _ => continue(tree)
            }
          }
        }
    }
}
