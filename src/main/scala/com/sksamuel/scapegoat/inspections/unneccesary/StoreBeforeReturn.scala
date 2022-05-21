package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

class StoreBeforeReturn
    extends Inspection(
      text = "Unnecessary store before return.",
      defaultLevel = Levels.Info,
      description = "Checks for storing a value in a block, and immediately returning the value.",
      explanation =
        "Storing a value and then immediately returning it is equivalent to returning the raw value itself."
    ) {

  override def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {
          import context.global._

          private def lastExprName(expr: Tree): Option[String] =
            expr match {
              case Return(Ident(name)) => Some(name.toString())
              case Ident(name)         => Some(name.toString())
              case _                   => None
            }

          override def inspect(tree: context.global.Tree): Unit =
            tree match {
              case DefDef(_, _, _, _, _, Block(stmts, lastExprInBody)) =>
                val maybeLastExprName = lastExprName(lastExprInBody)
                stmts.lastOption.foreach {
                  case defn @ ValDef(_, assignmentName, _, _)
                      if maybeLastExprName.contains(assignmentName.toString()) =>
                    context.warn(defn.pos, self)
                  case _ => stmts.foreach(inspect)
                }
              case _ => continue(tree)
            }
        }
    }
}
