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

      override def traverse(tree: Tree): Unit = {
        tree match {
          // ignore abstract methods obv.
          case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flag.ABSTRACT) =>
          case d@DefDef(mods, _, _, _, _, _) if d.symbol != null && d.symbol.isAbstract =>
          // ignore constructors, those params become fields
          case DefDef(_, name, _, _, _, _) if name.toString == "<init>" =>
          case d@DefDef(_, name, _, vparamss, _, rhs) =>
            for ( vparams <- vparamss;
                  vparam <- vparams ) {
              if (!usesParameter(vparam, rhs))
                context.warn("Unused method parameter", tree.pos, Levels.Warning,
                  s"Unused method parameter ($vparam) at " + name.toString.take(100))
            }
          case _ => super.traverse(tree)
        }
      }
    }
  }
}
