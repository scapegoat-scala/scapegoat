package com.sksamuel.scapegoat.inspections.nulls

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class NullAssignment
    extends Inspection(
      text = "Null assignment",
      defaultLevel = Levels.Warning,
      description = "Checks for use of null in assignments.",
      explanation = "Use an Option instead when the value can be empty."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case ValDef(_, _, _, Literal(Constant(null))) =>
                context.warn(tree.pos, self)
              case Apply(Select(_, name), List(Literal(Constant(null)))) =>
                if (name.endsWith("_$eq"))
                  context.warn(tree.pos, self)
              case Assign(_, Literal(Constant(null))) =>
                context.warn(tree.pos, self)
              case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flag.SYNTHETIC) =>
              case _                                                           => continue(tree)
            }
          }
        }
    }
}
