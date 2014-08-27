package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class UnusedMethodParameter extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._
      import definitions._

      private def usesParameter(param: ValDef, rhs: Tree): Boolean = {
        rhs match {
          case Ident(TermName(name)) => name == param.name.toString
          case _                     => rhs.children.exists(usesParameter(param, _))
        }
      }

      override final def inspect(tree: Tree): Unit = {
        tree match {
          // ignore traits, quite often you define a method in a trait with default impl that does nothing
          case ClassDef(_, _, _, _) if tree.symbol.isTrait                                  =>
          // ignore abstract methods obv.
          case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flag.ABSTRACT)                   =>
          case d @ DefDef(_, _, _, _, _, _) if d.symbol != null && d.symbol.isAbstract      =>
          // ignore constructors, those params become fields
          case DefDef(_, nme.CONSTRUCTOR, _, _, _, _)                                       =>
          case DefDef(_, _, _, _, _, _) if tree.symbol != null && tree.symbol.isConstructor =>
          case DefDef(_, _, _, _, tpt, _) if tpt.tpe =:= NothingTpe                         =>
          case d @ DefDef(mods, _, _, vparamss, _, rhs) =>
            for (
              vparams <- vparamss;
              vparam <- vparams
            ) {
              if (!usesParameter(vparam, rhs)) {
                val level = if (mods.isOverride) Levels.Info else Levels.Warning
                context.warn("Unused method parameter", tree.pos, level,
                  s"Unused method parameter ($vparam)", UnusedMethodParameter.this)
              }
            }
          case _ => continue(tree)
        }
      }
    }
  }
}
