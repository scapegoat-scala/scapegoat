package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

import scala.collection.mutable

/** @author Stephen Samuel */
class VarCouldBeVal extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      private def containsUnwrittenVar(trees: List[Tree]): Boolean = {
        def containsUnwrittenVar(trees: List[Tree], vars: mutable.HashSet[String]): Boolean = {
          // add var to the set when it is defined, then remove it when it's written to
          // what's left are unwritten vars
          trees.foreach {
            case ValDef(mods, name, _, _) if mods.isMutable => vars.add(name.toString)
            case Assign(lhs, _) =>
              vars.remove(lhs.toString())
            case DefDef(_, _, _, _, _, rhs) => rhs match {
              case Block(stmt, expr) => containsUnwrittenVar(stmt :+ expr, vars)
              case _ => containsUnwrittenVar(List(rhs), vars)
            }
            case tree => containsUnwrittenVar(tree.children, vars)
          }
          vars.nonEmpty
        }
        containsUnwrittenVar(trees, mutable.HashSet[String]())
      }

      override final def inspect(tree: Tree): Unit = {
        tree match {
          case d@DefDef(mods, _, _, vparamss, _, Block(stmt, expr)) if containsUnwrittenVar(stmt :+ expr) =>
            context.warn("Var could be val",
              tree.pos,
              Levels.Warning,
              s"Var is never written to, so could be a val: " + tree.toString().take(200),
              VarCouldBeVal.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
