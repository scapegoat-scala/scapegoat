package com.sksamuel.scapegoat.inspections.unsafe

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class NullUse extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      def containsNull(trees: List[Tree]) = trees exists {
        case Literal(Constant(null)) => true
        case _ => false
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(_, args) =>
            if (containsNull(args))
              context
                .warn("null use", tree.pos, Levels.Error, "null as method argument: " + tree.toString().take(300))
          case Literal(Constant(null)) =>
            context.warn("null use", tree.pos, Levels.Error, "null used on line " + tree.pos.line)
          case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flag.SYNTHETIC) =>
          case _ => continue(tree)
        }
      }
    }
  }
}