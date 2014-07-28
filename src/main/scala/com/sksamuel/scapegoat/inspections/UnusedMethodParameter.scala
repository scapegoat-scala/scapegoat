package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Level, Reporter, Inspection}

import scala.reflect.api
import scala.reflect.runtime._

/** @author Stephen Samuel */
class UnusedMethodParameter extends Inspection {

  override def traverser(reporter: Reporter): universe.Traverser = new universe.Traverser {

    private def usesParameter(param: api.JavaUniverse#ValDef, rhs: api.JavaUniverse#Tree): Boolean = {
      rhs match {
        case universe.Ident(universe.TermName(name)) => name == param.name.toString
        case _ => rhs.children.exists(usesParameter(param, _))
      }
    }

    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case universe.DefDef(modifiers, name, tparams, vparamss, tpt, rhs) =>
          for ( vparams <- vparamss;
                vparam <- vparams ) {
            if (!usesParameter(vparam, rhs))
              reporter.warn("Unused method parameter",
                tree,
                level = Levels.Warning,
                "Unused method parameter " + name.toString.take(100))
          }
        case _ => super.traverse(tree)
      }
    }
  }
}
