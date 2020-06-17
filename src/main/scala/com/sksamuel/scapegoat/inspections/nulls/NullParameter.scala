package com.sksamuel.scapegoat.inspections.nulls

import com.sksamuel.scapegoat._

/**
 * @author Stephen Samuel */
class NullParameter
    extends Inspection(
      text = "Null parameter",
      defaultLevel = Levels.Warning,
      description = "Checks for use of null in method invocation.",
      explanation = "Use an Option instead when the value can be empty and pass down a None instead."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser =
        new context.Traverser {

          import context.global._

          def containsNull(trees: List[Tree]) =
            trees exists {
              case Literal(Constant(null)) => true
              case _                       => false
            }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(_, _) if tree.tpe.toString == "scala.xml.Elem" =>
              case Apply(_, args) =>
                if (containsNull(args))
                  context.warn(tree.pos, self, tree.toString.take(300))
              case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flag.SYNTHETIC) =>
              case _                                                           => continue(tree)
            }
          }
        }
    }
}
