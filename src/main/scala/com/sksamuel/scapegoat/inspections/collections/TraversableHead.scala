package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class TraversableHead extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(left, TermName("head")) =>
            println(left.tpe.typeSymbol.fullName.toString)
            if (left.tpe.typeSymbol.fullName.toString == "scala.collection.Iterable")
              context.warn("Use of Option.head", tree.pos, Levels.Error, tree.toString().take(500))
          case _ => super.traverse(tree)
        }
      }
    }
  }
}