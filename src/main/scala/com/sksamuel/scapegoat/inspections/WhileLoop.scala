package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat._

import scala.reflect.runtime._

/** @author Stephen Samuel
  * */
class WhileLoop extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {

    import scala.reflect.runtime.universe._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case label@LabelDef(name, _, _) if name.toString.startsWith("while$") || name.toString.startsWith("doWhile$") =>
          reporter.warn("While loop", tree, Levels.Error, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
