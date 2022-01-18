package com.sksamuel.scapegoat.inspections.unneccesary

import scala.collection.mutable

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class VarCouldBeVal
    extends Inspection(
      text = "Var could be val",
      defaultLevel = Levels.Warning,
      description = "Checks for vars that could be declared as vals.",
      explanation = "A variable (var) that is never written to could be turned into a value (val)."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def unwrittenVars(tree: Tree, vars: mutable.HashMap[String, Tree]): List[(String, Tree)] = {
            tree match {
              case Block(stmt, expr) => containsUnwrittenVar(stmt :+ expr, vars)
              case _                 => containsUnwrittenVar(List(tree), vars)
            }
          }

          private def containsUnwrittenVar(
            trees: List[Tree],
            vars: mutable.HashMap[String, Tree]
          ): List[(String, Tree)] = {
            // As we scan the tree, in `vars: HashMap[String, Tree]` we store an entry for each var
            // that we encounter. The key gives the name and the value gives the tree of the ValDef
            // that defines it. Whenever a var is written to, we remove its entry. What remains are
            // vars that are never written to (and the trees corresponding to the places where they
            // were defined).
            trees.foreach {
              case defn @ ValDef(mods, name, _, _) if mods.isMutable =>
                vars.put(name.toString, defn)
              case Assign(lhs, _) =>
                if (lhs.symbol != null)
                  vars.remove(lhs.symbol.name.toString)
              case DefDef(_, _, _, _, _, rhs) => unwrittenVars(rhs, vars)
              case block: Block               => unwrittenVars(block, vars)
              case ClassDef(_, _, _, Template(_, _, body)) =>
                containsUnwrittenVar(body, vars)
              case ModuleDef(_, _, Template(_, _, body)) => containsUnwrittenVar(body, vars)
              case If(cond, thenp, elsep) =>
                unwrittenVars(cond, vars)
                unwrittenVars(thenp, vars)
                unwrittenVars(elsep, vars)
              case tree =>
                containsUnwrittenVar(tree.children, vars)
            }
            vars.toList
          }

          private def containsUnwrittenVar(trees: List[Tree]): List[(String, Tree)] =
            containsUnwrittenVar(trees, mutable.HashMap[String, Tree]())

          override def inspect(tree: Tree): Unit = {
            tree match {
              case DefDef(_, _, _, _, _, Block(stmt, expr)) =>
                for ((_, definitionTree) <- containsUnwrittenVar(stmt :+ expr))
                  context.warn(definitionTree.pos, self, tree.toString.take(200))
              case _ => continue(tree)
            }
          }
        }
    }
}
