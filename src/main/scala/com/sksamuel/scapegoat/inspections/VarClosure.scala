package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{ Levels, Inspection, InspectionContext, Inspector }

/** @author Stephen Samuel */
class VarClosure extends Inspection {

  override def inspector(context: InspectionContext): Inspector = new Inspector(context) {

    import context.global._

    override def postTyperTraverser = Some apply new context.Traverser {

      private def capturesVar(tree: Tree): Unit = tree match {
        case Block(stmt, expr) => (stmt :+ expr).foreach(capturesVar)
        case Apply(Select(_, _), args) =>
          args.filter(_.symbol != null)
            .foreach(arg => if (arg.symbol.isMethod && arg.symbol.isGetter && !arg.symbol.isStable) {
              context.warn("VarClosure",
                tree.pos,
                Levels.Warning,
                "Closing over a var can lead to subtle bugs: " + tree.toString().take(500),
                VarClosure.this)
            })
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
