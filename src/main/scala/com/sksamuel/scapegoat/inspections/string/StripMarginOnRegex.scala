package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class StripMarginOnRegex extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val R = TermName("r")
      private val StripMargin = TermName("stripMargin")
      private val Augment = TermName("augmentString")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Apply(_, List(Select(Apply(Select(_, Augment), List(Literal(Constant(str: String)))), StripMargin))), R) if str.contains('|') =>
            context
              .warn("Strip margin on regex",
                tree.pos,
                Levels.Error,
                "Strip margin will strip | from regex - possible corrupted regex",
                StripMarginOnRegex.this)
          case _ => continue(tree)
        }
      }
    }
  }
}