package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class IncorrectNumberOfArgsToFormat extends Inspection {

  // format is: %[argument_index$][flags][width][.precision][t]conversion
  final val argRegex = "%(\\d+\\$)?[-#+ 0,(\\<]*?\\d?(\\.\\d+)?[tT]?[a-zA-Z]".r

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Apply(Select(_, TermName("augmentString")), List(Literal(Constant(format)))),
            TermName("format")), args) =>
            val argCount = argRegex.findAllIn(format.toString).matchData.size
            if (argCount > args.size)
              context.warn("Incorrect number of args for format", tree.pos, Levels.Error, tree.toString().take(500), IncorrectNumberOfArgsToFormat.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
