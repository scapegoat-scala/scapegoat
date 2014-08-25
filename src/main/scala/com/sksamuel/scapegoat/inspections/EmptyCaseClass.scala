package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class EmptyCaseClass extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      def accessors(trees: List[Tree]): List[ValDef] = {
        trees.collect {
          case v: ValDef => v
        }.filter(_.mods.isCaseAccessor)
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          // body should have constructor only, and with synthetic methods it has 10 in total
          case ClassDef(mods, _, List(), Template(_, _, body)) if mods.isCase && accessors(body).isEmpty =>
            context.warn("Empty case class", tree.pos, Levels.Info,
              "Empty case class can be rewritten as a case object",
              EmptyCaseClass.this)
          case _ => continue(tree)
        }
      }
    }
  }
}

