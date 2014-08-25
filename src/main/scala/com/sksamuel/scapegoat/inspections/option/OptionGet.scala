package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class OptionGet extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(left, TermName("get")) =>
            if (left.tpe.typeSymbol.fullName == "scala.Option")
              context.warn("Use of Option.get", tree.pos, Levels.Error, tree.toString().take(500), OptionGet.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
