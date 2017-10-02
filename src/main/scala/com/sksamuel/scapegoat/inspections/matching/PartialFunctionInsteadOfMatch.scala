package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class PartialFunctionInsteadOfMatch extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def warn(tree: Tree) {
        context.warn("Match instead of partial function",
          tree.pos,
          Levels.Info,
          "A map match can be replaced with a partial function for greater readability: " + tree.toString().take(500),
          PartialFunctionInsteadOfMatch.this)
      }

      private def isPFBind(name: TermName) = {
        val b = name.toString.matches("x0\\$\\d+")
        b
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          // _ match { case ...; case ... }
          // need to not warn on the partial function style, they use x0$1
          case Apply(_, List(Function(List(ValDef(mods, name1, _, EmptyTree)), Match(name2, _)))) if name1.toString == name2.toString() => if (!isPFBind(name1)) warn(tree)
          case Apply(TypeApply(_, _), List(Function(List(ValDef(mods, name1, _, EmptyTree)), Match(name2, _)))) if name1.toString == name2.toString() => if (!isPFBind(name1)) warn(tree)
          case TypeApply(_, List(Function(List(ValDef(mods, name1, _, EmptyTree)), Match(name2, _)))) if name1.toString == name2.toString() => if (!isPFBind(name1)) warn(tree)
          // a => a match { case ...; case ... }
          //          case Apply(_, List(Function(List(ValDef(mods, x1, TypeTree(), EmptyTree)), Match(x2, _))))
          //            if x1.toString == x2.toString() =>
          //            warn(tree)
          case _ => continue(tree)
        }
      }
    }
  }
}
