package com.sksamuel.scapegoat.inspections.string

import java.util.IllegalFormatException

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class IllegalFormatString extends Inspection {

  // format is: %[argument_index$][flags][width][.precision][t]conversion
  final val argRegex = "%(\\d+\\$)?[-#+ 0,(\\<]*?\\d?(\\.\\d+)?[tT]?[a-zA-Z]".r

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Apply(Select(_, TermName("augmentString")), List(Literal(Constant(format)))),
            TermName("format")), _) =>
            val argCount = argRegex.findAllIn(format.toString).matchData.size
            val args = Nil.padTo(argCount, null)
            try {
              String.format(format.toString, args: _*)
            } catch {
              case e: IllegalFormatException =>
                println(e)
                context.warn("Illegal format string",
                  tree.pos,
                  Levels.Error,
                  "A format string contains an illegal syntax: " + e.getMessage,
                  IllegalFormatString.this)
            }
          case _ => continue(tree)
        }
      }
    }
  }
}
