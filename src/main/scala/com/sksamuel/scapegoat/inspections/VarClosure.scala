package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class VarClosure
    extends Inspection(
      text = "Var in closure",
      defaultLevel = Levels.Warning,
      description = "Finds closures that reference variables (var).",
      explanation = "Closing over a var can lead to subtle bugs."
    ) {

  override def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {

      import context.global._

      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          private def capturesVar(tree: Tree): Unit =
            tree match {
              case Block(stmt, expr) => (stmt :+ expr).foreach(capturesVar)
              case Apply(Select(_, _), args) =>
                args
                  .filter(_.symbol != null)
                  .foreach(arg =>
                    if (arg.symbol.isMethod && arg.symbol.isGetter && !arg.symbol.isStable)
                      context.warn(tree.pos, self, tree.toString.take(500))
                  )
              case _ =>
            }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Function(List(ValDef(_, _, _, _)), body) => capturesVar(body)
              case _                                        => continue(tree)
            }
          }
        }
    }
}
