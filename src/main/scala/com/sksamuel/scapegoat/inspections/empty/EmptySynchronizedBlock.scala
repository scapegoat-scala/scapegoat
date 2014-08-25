package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptySynchronizedBlock extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Sync = TermName("synchronized")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(_, Sync), _), List(Literal(Constant(())))) =>
            context.warn("Empty synchronized block",
              tree.pos,
              Levels.Warning,
              tree.toString().take(500),
              EmptySynchronizedBlock.this)
          case _ => continue(tree)
        }
      }
    }
  }
}