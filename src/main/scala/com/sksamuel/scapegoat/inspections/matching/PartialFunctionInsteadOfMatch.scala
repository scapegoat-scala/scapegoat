package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class PartialFunctionInsteadOfMatch
    extends Inspection(
      text = "Match instead of a partial function",
      defaultLevel = Levels.Info,
      description = "Warns when you could use a partial function directly instead of a match block.",
      explanation = "A map match can be replaced with a partial function for greater readability."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def isPFBind(name: TermName) = {
            val b = name.toString.matches("x0\\$\\d+")
            b
          }

          override def inspect(tree: Tree): Unit = {
            tree match {
              // _ match { case ...; case ... }
              // need to not warn on the partial function style, they use x0$1
              case Apply(_, List(Function(List(ValDef(_, name1, _, EmptyTree)), Match(name2, _))))
                  if name1.toString == name2.toString() =>
                if (!isPFBind(name1)) context.warn(tree.pos, self, tree.toString.take(500))
              case Apply(
                    TypeApply(_, _),
                    List(Function(List(ValDef(_, name1, _, EmptyTree)), Match(name2, _)))
                  ) if name1.toString == name2.toString() =>
                if (!isPFBind(name1)) context.warn(tree.pos, self, tree.toString.take(500))
              case TypeApply(_, List(Function(List(ValDef(_, name1, _, EmptyTree)), Match(name2, _))))
                  if name1.toString == name2.toString() =>
                if (!isPFBind(name1)) context.warn(tree.pos, self, tree.toString.take(500))
              // a => a match { case ...; case ... }
              //          case Apply(_, List(Function(List(ValDef(mods, x1, TypeTree(), EmptyTree)), Match(x2, _))))
              //            if x1.toString == x2.toString() =>
              //            context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
