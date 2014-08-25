package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

import scala.reflect.internal.Flags

/**
 * @author Stephen Samuel
 *
 *
 *         http://docs.scala-lang.org/style/naming-conventions.html#symbolic-method-names
 */
class AvoidOperatorOverload extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flags.SetterFlags) | mods.hasFlag(Flags.GetterFlags) =>
          case DefDef(_, nme.CONSTRUCTOR, _, _, _, _) =>
          case DefDef(_, TermName("$init$"), _, _, _, _) =>
          case DefDef(_, name, _, _, _, _) if name.toChars.count(_ == '$') > 2 =>
            context.warn("Avoid operator overload",
              tree.pos,
              Levels.Info,
              s"Scala style guide advocates against routinely using operators as method names (${name.decode}})." +
                "See http://docs.scala-lang.org/style/naming-conventions.html#symbolic-method-names",
              AvoidOperatorOverload.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
