package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

import scala.reflect.internal.Flags

/** @author Stephen Samuel
  *
  *
  *         http://docs.scala-lang.org/style/naming-conventions.html#symbolic-method-names
  **/
class AvoidOperatorOverload extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          // body should have constructor only, and with synthetic methods it has 10 in total
          case DefDef(mods, name, _, _, _, _) if mods.hasFlag(Flags.SetterFlags) | mods.hasFlag(Flags.GetterFlags) =>
          case DefDef(_, name, _, _, _, _) if name.toChars.count(_ == '$') > 1 =>
            context.warn("Avoid operator overload",
              tree.pos,
              Levels.Info,
              "Scala style guide advocates against using operators as method names, except in special circumstances. See http://docs.scala-lang.org/style/naming-conventions.html#symbolic-method-names",
              AvoidOperatorOverload.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
