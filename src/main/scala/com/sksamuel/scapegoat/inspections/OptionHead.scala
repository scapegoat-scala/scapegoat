package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

/** @author Stephen Samuel */
class OptionHead extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(left, TermName("head")) =>
          if (left.tpe.typeSymbol.fullName.toString.startsWith("scala.Option.option2Iterable"))
            reporter.warn("Use of Option.head", tree, Levels.Error, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
