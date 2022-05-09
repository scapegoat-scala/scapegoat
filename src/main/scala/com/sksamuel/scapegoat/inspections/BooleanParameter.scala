package com.sksamuel.scapegoat.inspections

import scala.reflect.internal.Flags

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Bendix Saeltz
 */
class BooleanParameter
    extends Inspection(
      text = "Method with Boolean parameter",
      defaultLevel = Levels.Info,
      description = "Checks for functions that have a Boolean parameter.",
      explanation = "Method has Boolean parameter. Consider splitting into two methods or using a case class."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser = new context.Traverser {
        import context.global._

        private def hasBooleanParameter(vparamss: List[List[ValDef]]): Boolean =
          vparamss.exists(_.exists(isBooleanParameter))

        private def isBooleanParameter(valDef: ValDef): Boolean =
          valDef.tpt.tpe =:= typeOf[Boolean]

        override def inspect(tree: Tree): Unit = tree match {
          case DefDef(mods, _, _, _, _, _) if mods.isSynthetic                =>
          case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flags.SetterFlags) =>
          case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flags.GetterFlags) =>
          case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flags.ACCESSOR)    =>
          // ignore overridden methods as the parent will receive the warning
          case DefDef(mods, _, _, _, _, _) if mods.isOverride =>
          case DefDef(_, name, _, vparamss, _, _)
              if hasBooleanParameter(vparamss) && name != TermName("<init>") =>
            context.warn(tree.pos, self, tree.toString.take(300))
          case _ => continue(tree)
        }
      }
    }
}
