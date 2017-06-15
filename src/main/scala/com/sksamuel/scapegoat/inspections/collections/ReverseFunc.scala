package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class ReverseFunc extends Inspection {

  object FuncReplace {

    private val funcReplace = Map(
      "head" -> "last",
      "headOption" -> "lastOption",
      "iterator" -> "reverseIterator",
      "map" -> "reverseMap")

    def unapply(func: String): Option[(String, String)] =
      funcReplace.find(_._1 == func)
  }

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Select(c, TermName("reverse")), TermName(FuncReplace(func, replace)))
            if c.tpe <:< typeOf[Traversable[Any]] =>
            warn(func, replace, tree)
          case Select(Apply(arrayOps1, List(Select(Apply(arrayOps2, List(_)), TermName("reverse")))), TermName(FuncReplace(func, replace)))
            if arrayOps1.toString.contains("ArrayOps") && arrayOps2.toString.contains("ArrayOps") =>
            warn(func, replace, tree)
          case _ => continue(tree)
        }
      }

      private def warn(func: String, replace: String, tree: Tree) =
        context.warn(s"reverse.$func instead of $replace", tree.pos, Levels.Info,
          s".reverse.$func can be replaced with $replace: " + tree.toString().take(500), ReverseFunc.this)

    }
  }
}