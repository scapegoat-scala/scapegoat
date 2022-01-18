package com.sksamuel.scapegoat.inspections.naming

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class ObjectNames
    extends Inspection(
      text = "Object name not recommended",
      defaultLevel = Levels.Info,
      description = "Ensures object names adhere to the Scala style guidelines.",
      explanation = "Object names should only contain alphanumeric characters."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val regex = "^[A-Za-z0-9]*$"

          override def inspect(tree: Tree): Unit = {
            tree match {
              case ModuleDef(mods, name, _) if !mods.isSynthetic && !name.toString.matches(regex) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
