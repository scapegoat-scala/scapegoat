package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author Stephen Samuel
 *
 *         Inspired by IntelliJ
 *
 *         Checks for filter.size > 0, filter.size == 0, etc
 */
class FilterDotSizeComparison extends Inspection("TODO", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          // todo
          case Select(Apply(Select(_, TermName("filter")), _), TermName("isEmpty")) =>
            context.warn(tree.pos, self,
              "TODO" + tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}
