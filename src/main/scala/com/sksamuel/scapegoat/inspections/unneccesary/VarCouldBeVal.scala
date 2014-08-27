package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

import scala.collection.mutable

/** @author Stephen Samuel */
class VarCouldBeVal extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def unwrittenVars(tree: Tree, vars: mutable.HashSet[String]): List[String] = {
        tree match {
          case Block(stmt, expr) => containsUnwrittenVar(stmt :+ expr, vars)
          case _                 => containsUnwrittenVar(List(tree), vars)
        }
      }

      private def containsUnwrittenVar(trees: List[Tree], vars: mutable.HashSet[String]): List[String] = {
        // add var to the set when it is defined, then remove it when it's written to
        // what's left are unwritten vars
        trees.foreach {
          case ValDef(mods, name, _, _) if mods.isMutable => vars.add(name.toString)
          case Assign(lhs, _) =>
            if (lhs.symbol != null)
              vars.remove(lhs.symbol.name.toString)
          case DefDef(_, _, _, _, _, rhs) => unwrittenVars(rhs, vars)
          case block: Block               => unwrittenVars(block, vars)
          case ClassDef(_, _, _, Template(_, _, body)) =>
            containsUnwrittenVar(body, vars)
          case ModuleDef(_, _, Template(_, _, body)) => containsUnwrittenVar(body, vars)
          case If(cond, thenp, elsep) =>
            unwrittenVars(thenp, vars)
            unwrittenVars(elsep, vars)
          case tree =>
            containsUnwrittenVar(tree.children, vars)
        }
        vars.toList
      }

      private def containsUnwrittenVar(trees: List[Tree]): List[String] = {
        containsUnwrittenVar(trees, mutable.HashSet[String]())
      }

      override final def inspect(tree: Tree): Unit = {
        tree match {
          case d @ DefDef(_, _, _, _, _, Block(stmt, expr)) =>
            for (unwritten <- containsUnwrittenVar(stmt :+ expr)) {
              context.warn("Var could be val",
                tree.pos,
                Levels.Warning,
                s"$unwritten is never written to, so could be a val: " + tree.toString().take(200),
                VarCouldBeVal.this)
            }
          case _ => continue(tree)
        }
      }
    }
  }
}
