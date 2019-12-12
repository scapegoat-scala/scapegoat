package com.sksamuel.scapegoat.inspections.unsafe

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class IsInstanceOf extends Inspection("Use of isInstanceOf", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case TypeApply(Select(_, TermName("isInstanceOf")), _) =>
            context.warn(tree.pos, self,
              "Consider using a pattern match rather than isInstanceOf: " + tree.toString().take(500))
          case DefDef(modifiers, _, _, _, _, _) if modifiers.hasFlag(Flag.SYNTHETIC) => // avoid partial function stuff
          case Match(_, cases) => // ignore selector and process cases
            cases.foreach(traverse)
          case _ => continue(tree)
        }
      }
    }
  }
}