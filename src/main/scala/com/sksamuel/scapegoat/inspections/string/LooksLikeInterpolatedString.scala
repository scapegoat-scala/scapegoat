package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

class LooksLikeInterpolatedString extends Inspection {

  final val regex1 = "\\$\\{[a-z][.a-zA-Z0-9_]*\\}".r
  final val regex2 = "\\$[a-z][.a-zA-Z0-9_]*".r

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(qual@Ident(TermName("scala")), name@TermName("StringContext")) => continue(tree)
          case Literal(Constant(str: String)) =>
            val possibles1 = regex1.findAllIn(str).toList.filterNot(_.contains("$anonfun"))
            val possibles2 = regex2.findAllIn(str).toList.filterNot(_.contains("$anonfun"))
            if ((possibles1 ++ possibles2).nonEmpty) {
              context.warn("Looks Like Interpolated String",
                tree.pos,
                Levels.Warning,
                str,
                LooksLikeInterpolatedString.this)
            }
          case _ => continue(tree)
        }
      }
    }
  }
}
