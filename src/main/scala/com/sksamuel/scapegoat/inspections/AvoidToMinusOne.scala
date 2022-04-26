package com.sksamuel.scapegoat.inspections

import scala.runtime.{RichInt, RichLong}

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class AvoidToMinusOne
    extends Inspection(
      text = "Avoid (j to k - 1)",
      defaultLevel = Levels.Info,
      description = "Checks for ranges using (j to k - 1).",
      explanation = "A range in the following format (j to k - 1) can be simplified to (j until k)."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._
          import definitions._

          private val Foreach = TermName("foreach")
          private val Minus = TermName("$minus")
          private val To = TermName("to")

          private def isIntegral(tree: Tree): Boolean =
            tree.tpe <:< IntTpe ||
              tree.tpe <:< LongTpe ||
              tree.tpe <:< typeOf[RichInt] ||
              tree.tpe <:< typeOf[RichLong]

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(
                    TypeApply(
                      Select(
                        Apply(
                          Select(lhs, To),
                          List(Apply(Select(loopvar, Minus), List(Literal(Constant(1)))))
                        ),
                        Foreach
                      ),
                      _
                    ),
                    _
                  ) if isIntegral(lhs) && isIntegral(loopvar) =>
                context.warn(tree.pos, self, tree.toString.take(200))
              case _ => continue(tree)
            }
          }
        }
    }
}
