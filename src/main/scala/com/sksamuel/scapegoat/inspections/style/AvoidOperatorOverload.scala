package com.sksamuel.scapegoat.inspections.style

import scala.reflect.internal.Flags

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 *
 * http://docs.scala-lang.org/style/naming-conventions.html#symbolic-method-names
 */
class AvoidOperatorOverload
    extends Inspection(
      text = "Avoid operator overload",
      defaultLevel = Levels.Info,
      description = "Checks for symbolic method names.",
      explanation =
        "Scala style guide advocates against routinely using operators as method names, see http://docs.scala-lang.org/style/naming-conventions.html#symbolic-method-names."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case DefDef(mods, _, _, _, _, _)
                  if mods.hasFlag(Flags.SetterFlags) | mods.hasFlag(Flags.GetterFlags) =>
              case DefDef(_, nme.CONSTRUCTOR, _, _, _, _)    =>
              case DefDef(_, TermName("$init$"), _, _, _, _) =>
              case DefDef(_, name, _, _, _, _) if name.toChars.count(_ == '$') > 2 =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
