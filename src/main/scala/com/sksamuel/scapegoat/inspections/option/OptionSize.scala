package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class OptionSize extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Apply(option2Iterable, List(opt)), TermName("size")) ⇒
            if (option2Iterable.symbol.fullName == "scala.Option.option2Iterable")
              context.warn("Prefer Option.isDefined instead of Option.size",
                tree.pos, Levels.Error, tree.toString().take(500), OptionSize.this)
          case _ ⇒ continue(tree)
        }
      }
    }
  }
}