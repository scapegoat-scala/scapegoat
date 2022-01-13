package com.sksamuel.scapegoat.inspections

import scala.reflect.internal.Flags

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class AnyUse
    extends Inspection(
      text = "Use of Any",
      defaultLevel = Levels.Info,
      description = "Checks for code returning Any.",
      explanation = "Code returning Any is most likely an indication of a programming error."
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
              case ValDef(_, _, tpt, _) if tpt.tpe =:= typeOf[Any] =>
                context.warn(tree.pos, self, tree.toString.take(200))
              case DefDef(_, _, _, _, tpt, _) if tpt.tpe =:= typeOf[Any] =>
                context.warn(tree.pos, self, tree.toString.take(200))
              case _ => continue(tree)
            }
          }
        }
    }
}
