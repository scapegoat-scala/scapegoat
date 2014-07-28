package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

/** @author Stephen Samuel
  *
  *         Inspired by IntelliJ
  */
class FilterSizeToCount extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(Apply(Select(_, TermName("filter")), _), TermName("size")) =>
          reporter
            .warn("filter().size() instead of count()",
            tree,
            Levels.Info,
            ".filter(x => Bool).size can be replaced with count(x => Bool): " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }

}
