package com.sksamuel.scapegoat.inspections.string

import java.util.IllegalFormatException

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class IllegalFormatString extends Inspection {

  // format is: %[argument_index$][flags][width][.precision][t]conversion
  final val argRegex = "%(\\d+\\$)?[-#+ 0,(\\<]*?\\d?(\\.\\d+)?[tT]?[a-zA-Z]".r

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(Apply(Select(_, TermName("augmentString")), List(Literal(Constant(format)))), TermName("format")), _) =>
          val argCount = argRegex.findAllIn(format.toString).matchData.size
          val args = Nil.padTo(argCount, null)
          try {
            String.format(format.toString, args: _*)
          } catch {
            case e: IllegalFormatException =>
              println(e)
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
