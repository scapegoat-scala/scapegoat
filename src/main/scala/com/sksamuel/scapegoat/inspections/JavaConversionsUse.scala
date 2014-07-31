package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Feedback}

import scala.reflect.runtime._
import scala.tools.nsc.Global

/** @author Stephen Samuel */
class JavaConversionsUse extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(_, TermName("JavaConversions")) =>
          feedback.warn("Java conversions",
            tree.pos, Levels.Error,
            "Use of java conversions can lead to unusual implicit behaviour. It is recommended to use JavaConverters: " + tree
              .toString()
              .take(400))
        case _ => super.traverse(tree)
      }
    }
  }
}
