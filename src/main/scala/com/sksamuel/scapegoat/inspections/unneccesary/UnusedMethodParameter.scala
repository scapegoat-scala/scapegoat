package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class UnusedMethodParameter extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      private def usesParameter(param: ValDef, rhs: Tree): Boolean = {
        rhs match {
          case Ident(TermName(name)) => name == param.name.toString
          case _ => rhs.children.exists(usesParameter(param, _))
        }
      }

      override final def inspect(tree: Tree): Unit = {
        tree match {
          // ignore abstract methods obv.
          case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flag.ABSTRACT) =>
          case d@DefDef(_, _, _, _, _, _) if d.symbol != null && d.symbol.isAbstract =>
          // ignore constructors, those params become fields
          case DefDef(_, nme.CONSTRUCTOR, _, _, _, _) =>
          case d@DefDef(mods, _, _, vparamss, _, rhs) =>
            for ( vparams <- vparamss;
                  vparam <- vparams ) {
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
