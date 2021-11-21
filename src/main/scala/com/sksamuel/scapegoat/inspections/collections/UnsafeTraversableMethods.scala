package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

class UnsafeTraversableMethods
    extends Inspection(
      text = "Use of unsafe Traversable methods.",
      defaultLevel = Levels.Error,
      description = "Checks for use of unsafe methods on Traversable.",
      explanation =
        "The following methods on Traversable are considered to be unsafe (head, tail, init, last, reduce, reduceLeft, reduceRight, max, maxBy, min, minBy)."
    ) {

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

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(left, TermName(method)) =>
                if (isIterable(left) && unsafeMethods.contains(method))
                  context.warn(tree.pos, self, tree.toString.take(500))
                else continue(tree)
              case _ => continue(tree)
            }
          }
        }
    }
}
