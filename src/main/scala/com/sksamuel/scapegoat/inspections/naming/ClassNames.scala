package com.sksamuel.scapegoat.inspections.naming

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class ClassNames
    extends Inspection(
      text = "Class name not recommended",
      defaultLevel = Levels.Info,
      description = "Ensures class names adhere to the style guidelines.",
      explanation = "Class names should begin with uppercase letter."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val regex = "^[A-Z][A-Za-z0-9]*$"

          override def inspect(tree: Tree): Unit = {
            tree match {
              case ClassDef(_, name, _, _) if name.toString.contains("$anon") =>
              case ClassDef(mods, name, _, _) if !mods.isSynthetic && !name.toString.matches(regex) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
