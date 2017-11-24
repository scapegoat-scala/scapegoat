package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

class FindAndNotEqualsNoneReplaceWithExists extends Inspection(
  "find(x => ) != None instead of exists(x =>)", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Apply(Select(_, TermName("find")), _), TermName("$bang$eq")),
            List(Select(_, TermName("None")))) =>
            context.warn(tree.pos, self,
              ".find(x => ) != None can be replaced with exists(x =>): " + tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}