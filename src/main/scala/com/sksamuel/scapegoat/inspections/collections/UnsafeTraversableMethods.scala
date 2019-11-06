package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

class UnsafeTraversableMethods extends Inspection("Use of unsafe Traversable methods (head, tail, init, last, reduce, reduceLeft, reduceRight, max, maxBy, min, minBy)", Levels.Error) {

  private val unsafeMethods = Set(
    "head",
    "tail",
    "init",
    "last",
    "reduce",
    "reduceLeft",
    "reduceRight",
    "max",
    "maxBy",
    "min",
    "minBy"
  )

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(left, TermName(method)) =>
            if (isTraversable(left) && unsafeMethods.contains(method))
              context.warn(tree.pos, self, tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}
