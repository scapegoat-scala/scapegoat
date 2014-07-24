package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Reporter}

import scala.reflect.api
import scala.reflect.runtime._

/** @author Stephen Samuel */
object CatchNpe extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {

    private def catchesNpe(cases: List[api.JavaUniverse#CaseDef]): Boolean = {
      cases.exists(_.pat.tpe.toString == "NullPointerException")
    }

    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case universe.Try(block, catches, finalizer) if catchesNpe(catches) =>
          reporter.warn("Catching NPE", tree.pos.line)
        case _ => super.traverse(tree)
      }
    }
  }
}
