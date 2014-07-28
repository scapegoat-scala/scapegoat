package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

/** @author Stephen Samuel */
class TryGet extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(left, TermName("get")) =>
          if (left.tpe.typeSymbol.fullName.toString == "scala.util.Try")
            reporter.warn("Use of Try.get", tree, Levels.Error, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
