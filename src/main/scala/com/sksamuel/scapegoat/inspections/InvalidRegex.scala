package com.sksamuel.scapegoat.inspections

import java.util.regex.PatternSyntaxException

import com.sksamuel.scapegoat.{Levels, Feedback, Inspection}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class InvalidRegex extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(Apply(Select(_, TermName("augmentString")), List(Literal(Constant(regex)))), TermName("r")) =>
          try {
            regex.toString.r
          } catch {
            case e: PatternSyntaxException =>
              val f = e
              feedback.warn("Invalid regex", tree.pos, Levels.Info, e.getMessage)
          }
        case _ => super.traverse(tree)
      }
    }
  }
}
