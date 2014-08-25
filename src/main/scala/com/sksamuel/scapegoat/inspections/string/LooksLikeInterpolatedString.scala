package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class LooksLikeInterpolatedString extends Inspection {

  final val regex1 = "\\$\\{[a-z][.a-zA-Z0-9_]*\\}".r
  final val regex2 = "\\$[a-z][.a-zA-Z0-9_]*".r

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Literal(Constant(str: String)) if regex1.findFirstIn(str).isDefined || regex2.findFirstIn(str).isDefined =>
            context.warn("Looks Like Interpolated String",
              tree.pos,
              Levels.Warning,
              str,
              LooksLikeInterpolatedString.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
