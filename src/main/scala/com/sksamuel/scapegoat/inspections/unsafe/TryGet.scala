package com.sksamuel.scapegoat.inspections.unsafe

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class TryGet extends Inspection("Use of Try.get", Levels.Error) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(left, TermName("get")) =>
            if (left.tpe.typeSymbol.fullName == "scala.util.Try")
              context.warn(tree.pos, self, tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}
