package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class IncorrectNumberOfArgsToFormat extends Inspection {

  // format is: %[argument_index$][flags][width][.precision][t]conversion
  private final val argRegex = "%(\\d+\\$)?[-#+ 0,(\\<]*?\\d?(\\.\\d+)?[tT]?[a-zA-Z]".r

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(Apply(Select(_, TermName("augmentString")), List(Literal(Constant(format)))), TermName("format")), args) =>
          val argCount = argRegex.findAllIn(format.toString).matchData.size
          if (argCount > args.size)
            feedback.warn("Incorrect number of args for format", tree.pos, Levels.Error, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
