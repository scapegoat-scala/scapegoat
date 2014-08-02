package com.sksamuel.scapegoat.inspections.unsafe

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class NullUse extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = {

    import global._

    new Traverser {

      def containsNull(trees: List[Tree]) = trees exists {
        case Literal(Constant(null)) => true
        case _ => false
      }

      override def traverse(tree: Tree): Unit = {
        tree match {
          case Apply(_, args) =>
            if (containsNull(args))
              feedback.warn("null use", tree.pos, Levels.Error, "null as method argument: " + tree.toString().take(300))
          case Literal(Constant(null)) =>
            feedback.warn("null use", tree.pos, Levels.Error, "null used on line " + tree.pos.line)
          case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flag.SYNTHETIC) =>
          case _ => super.traverse(tree)
        }
      }
    }
  }
}