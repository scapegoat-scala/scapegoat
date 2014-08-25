package com.sksamuel.scapegoat.inspections.string

import java.util.regex.PatternSyntaxException

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class InvalidRegex extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Apply(Select(_, TermName("augmentString")), List(Literal(Constant(regex)))), TermName("r")) =>
            try {
              regex.toString.r
            } catch {
              case e: PatternSyntaxException =>
                context.warn("Invalid regex", tree.pos, Levels.Info, e.getMessage, InvalidRegex.this)
            }
          case _ => continue(tree)
        }
      }
    }
  }
}