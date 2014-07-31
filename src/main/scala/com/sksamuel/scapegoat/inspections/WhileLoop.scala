package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat._

import scala.reflect.runtime._
import scala.tools.nsc.Global

/** @author Stephen Samuel
  * */
class WhileLoop extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case label@LabelDef(name, _, _) if name.toString.startsWith("while$") || name.toString.startsWith("doWhile$") =>
          feedback.warn("While loop", tree.pos, Levels.Error, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
