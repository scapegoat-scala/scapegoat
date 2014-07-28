package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class ParameterlessMethodReturnsUnit extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {

    import scala.reflect.runtime.universe._

    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case d@DefDef(_, name, _, vparamss, tpt, _) if tpt.tpe.toString == "Unit" && vparamss.isEmpty =>
          reporter.warn("Parameterless methods returns unit", tree, Levels.Warning, name.toString.take(100))
        case _ => super.traverse(tree)
      }
    }
  }
}
