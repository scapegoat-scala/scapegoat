package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class UnusedMethodParameter extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter): Traverser = new Traverser {

    private def usesParameter(param: ValDef, rhs: Tree): Boolean = {
      rhs match {
        case Ident(TermName(name)) => name == param.name.toString
        case _ => rhs.children.exists(usesParameter(param, _))
      }
    }

    override def traverse(tree: Tree): Unit = {
      tree match {
        case DefDef(modifiers, name, tparams, vparamss, tpt, rhs) =>
          for ( vparams <- vparamss;
                vparam <- vparams ) {
            if (!usesParameter(vparam, rhs))
              reporter.warn("Unused method parameter", tree, level = Levels.Warning,
                "Unused method parameter " + name.toString.take(100))
          }
        case _ => super.traverse(tree)
      }
    }
  }
}
