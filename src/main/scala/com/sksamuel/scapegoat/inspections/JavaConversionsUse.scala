package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class JavaConversionsUse extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Import(expr, selectors) if expr.symbol.fullName == "scala.collection.JavaConversions" =>
          feedback.warn("Java conversions",
            tree.pos, Levels.Error,
            "Use of java conversions can lead to unusual behaviour. It is recommended to use JavaConverters")
        case _ => super.traverse(tree)
      }
    }
  }
}
