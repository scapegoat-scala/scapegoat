package com.sksamuel.scapegoat.inspections.naming

import scala.reflect.internal.Flags

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class MethodNames extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val regex = "^[a-z][A-Za-z0-9]*(_\\$eq)?$"

      override def inspect(tree: Tree): Unit = {
        tree match {
          case dd: DefDef if dd.symbol != null && dd.symbol.isSynthetic =>
          case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flags.ACCESSOR) =>
          case DefDef(_, nme.CONSTRUCTOR, _, _, _, _) =>
          case DefDef(mods, _, _, _, _, _) if tree.symbol != null && tree.symbol.isConstructor =>
          case DefDef(_, name, _, _, _, _) if !name.decode.exists(_.isLetter) =>
          case DefDef(_, name, _, _, _, _) if !name.toString.matches(regex) =>
            context.warn("Method name not recommended",
              tree.pos,
              Levels.Info,
              s"Methods should be in camelCase style with the first letter lower-case. See http://docs.scala-lang.org/style/naming-conventions.html#methods",
              MethodNames.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
