package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author Stephen Samuel
 *
 *         Inspired by IntelliJ
 *
 *         Checks for filter.size > 0, filter.size == 0, etc
 */
class FilterDotSizeComparison extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          // todo
          case Select(Apply(Select(_, TermName("filter")), _), TermName("isEmpty")) =>
            context.warn("filter().isEmpty instead of !exists()",
              tree.pos,
              Levels.Info,
              ".filter(x => Bool).isEmpty can be replaced with !exists(x => Bool): " + tree.toString().take(500),
              FilterDotSizeComparison.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
