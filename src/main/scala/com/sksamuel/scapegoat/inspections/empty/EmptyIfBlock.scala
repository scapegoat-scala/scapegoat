package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptyIfBlock extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case If(_, Literal(Constant(())), _) =>
            context.warn("Empty if statement",
              tree.pos,
              Levels.Warning,
              tree.toString().take(500),
              EmptyIfBlock.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
