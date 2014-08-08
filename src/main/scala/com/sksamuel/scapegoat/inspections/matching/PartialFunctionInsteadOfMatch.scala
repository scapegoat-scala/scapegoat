package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class PartialFunctionInsteadOfMatch extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      private def warn(tree: Tree) {
        context.warn("Match instead of partial function",
          tree.pos,
          Levels.Info,
          "A map match can be replaced with a partial function for greater readability: " + tree.toString().take(500),
          PartialFunctionInsteadOfMatch.this)
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          // _ match { case ...; case ... }
          case Apply(_, List(Function(List(ValDef(mods, TermName("x$1"), _, EmptyTree)), Match(Ident(TermName("x$1")), _)))) => warn(tree)
          case Apply(TypeApply(_, _), List(Function(List(ValDef(mods, TermName("x$1"), _, EmptyTree)), Match(Ident(TermName("x$1")), _)))) => warn(tree)
          case TypeApply(_, List(Function(List(ValDef(mods, TermName("x$1"), _, EmptyTree)), Match(Ident(TermName("x$1")), _)))) => warn(tree)
          // need to not warn on the partial function style, they use x0$1
          case Apply(_, List(Function(List(ValDef(mods, TermName("x0$1"), _, EmptyTree)), Match(Ident(TermName("x0$1")), _)))) =>
          case Apply(TypeApply(_, _), List(Function(List(ValDef(mods, TermName("x0$1"), _, EmptyTree)), Match(Ident(TermName("x0$1")), _)))) =>
          case TypeApply(_, List(Function(List(ValDef(mods, TermName("x0$1"), _, EmptyTree)), Match(Ident(TermName("x0$1")), _)))) =>
          // a => a match { case ...; case ... }
          case Apply(_, List(Function(List(ValDef(mods, x1, TypeTree(), EmptyTree)), Match(x2, _))))
            if x1.toString == x2.toString() =>
            warn(tree)
          case _ => continue(tree)
        }
      }
    }
  }
}
