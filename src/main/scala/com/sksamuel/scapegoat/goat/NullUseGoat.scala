package com.sksamuel.scapegoat.goat

import com.sksamuel.scapegoat.{Inspection, Reporter, ScapegoatUniverse}

/** @author Stephen Samuel */
object NullUseGoat extends Inspection {

  import scala.reflect.runtime.universe
  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Literal(Constant(null)) => reporter.warn("null use")
        case _ => super.traverse(tree)
      }
    }
  }
}
