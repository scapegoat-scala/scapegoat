package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author Stephen Samuel
 *
 *         Inspired by IntelliJ
 */
class FilterDotSize extends Inspection("filter().size() instead of count()", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Apply(Select(_, TermName("filter")), _), TermName("size")) =>
            context.warn(tree.pos, self,
              ".filter(x => Bool).size can be replaced with count(x => Bool): " + tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}
