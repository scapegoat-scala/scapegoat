package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

class LooksLikeInterpolatedString extends Inspection("Looks Like Interpolated String", Levels.Warning) {

  final val regex1 = "\\$\\{[a-z][.a-zA-Z0-9_]*\\}".r
  final val regex2 = "\\$[a-z][.a-zA-Z0-9_]*".r

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Literal(Constant(str: String)) =>
            val possibles = Seq(regex1, regex2)
              .flatMap(_.findAllIn(str).toList.filterNot(_.contains("$anonfun")))
            if (possibles.nonEmpty && !str.startsWith("$")) {
              context.warn(tree.pos, self, str)
            }
          case _ => continue(tree)
        }
      }
    }
  }
}
