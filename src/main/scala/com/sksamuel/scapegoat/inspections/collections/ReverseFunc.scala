package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class ReverseFunc extends Inspection {

  val funcReplace = Map(
    "head" -> "last",
    "headOption" -> "lastOption",
    "iterator" -> "reverseIterator",
    "map" -> "reverseMap")

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Select(c, TermName("reverse")), TermName(func)) if funcReplace.contains(func) &&
            c.tpe.<:<(typeOf[Traversable[_]]) =>
            warn(func, tree)
          case Select(Apply(arrayOps1, List(Select(Apply(arrayOps2, List(_)), TermName("reverse")))), TermName(func)) if (arrayOps1.toString.contains("ArrayOps")) &&
            (arrayOps2.toString.contains("ArrayOps")) &&
            funcReplace.contains(func) =>
            warn(func, tree)
          case _ => continue(tree)
        }
      }

      private def warn(func: String, tree: Tree) =
        context.warn(s"reverse.$func instead of ${funcReplace(func)}", tree.pos, Levels.Info,
          s".reverse.$func can be replaced with ${funcReplace(func)}: " + tree.toString().take(500), ReverseFunc.this)
    }
  }
}