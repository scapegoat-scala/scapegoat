package com.sksamuel.scapegoat.inspections.inference

import scala.reflect.internal.Flags

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class MethodReturningAny
    extends Inspection(
      text = "Method returning Any",
      defaultLevel = Levels.Warning,
      description = "Checks for functions that are defined or inferred to return Any.",
      explanation = "Method returns Any. Consider using a more specialized type."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case DefDef(mods, _, _, _, _, _) if mods.isSynthetic                =>
              case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flags.SetterFlags) =>
              case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flags.GetterFlags) =>
              case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flags.ACCESSOR)    =>
              // ignore overridden methods as the parent will receive the warning
              case DefDef(mods, _, _, _, _, _) if mods.isOverride =>
              case DefDef(_, _, _, _, tpt, _) if tpt.tpe =:= typeOf[Any] || tpt.tpe =:= typeOf[AnyRef] =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case _ => continue(tree)
            }
          }
        }
    }
}
