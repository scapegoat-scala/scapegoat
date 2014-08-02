package com.sksamuel.scapegoat.inspections

import java.util.{IllegalFormatException, MissingFormatArgumentException}

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class IllegalFormatString extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(Apply(Select(_, TermName("augmentString")), List(Literal(Constant(format)))), TermName("format")), _) =>
          try {
            String.format(format.toString)
          } catch {
            // ignore this as we're attempting to format to find other errors
            case _: MissingFormatArgumentException =>
            case e: IllegalFormatException =>
              feedback.warn("Illegal format string",
                tree.pos,
                Levels.Error,
                "A format string contains an illegal syntax: " + e.getMessage)
            case _ =>
          }
        case _ => super.traverse(tree)
      }
    }
  }
}
