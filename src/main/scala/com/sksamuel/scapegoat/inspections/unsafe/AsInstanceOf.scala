package com.sksamuel.scapegoat.inspections.unsafe

import com.sksamuel.scapegoat._

class AsInstanceOf
    extends Inspection(
      text = "Use of asInstanceOf",
      defaultLevel = Levels.Warning,
      description = "Checks for use of asInstanceOf.",
      explanation =
        "Use of asInstanceOf is considered a bad practice - consider using pattern matching instead."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              // this will skip any uses of manifest etc
              case TypeApply(Select(qual, TermName("asInstanceOf")), _)
                  if qual.toString != "classOf[java.lang.Class]" =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case DefDef(modifiers, _, _, _, _, _) if modifiers.hasFlag(Flag.SYNTHETIC) => // no further
              case Match(_, cases) => // ignore selector and process cases
                cases.foreach(traverse)
              case _ => continue(tree)
            }
          }
        }
    }
}
