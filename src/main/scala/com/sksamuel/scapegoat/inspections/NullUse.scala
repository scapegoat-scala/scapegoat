package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Reporter}

/** @author Stephen Samuel */
object NullUse extends Inspection {

  import scala.reflect.runtime.universe
  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Literal(Constant(null)) => reporter.warn("null use", tree.pos.line)
        case _ => super.traverse(tree)
      }
    }
  }
}
