package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class DuplicateMapKey
    extends Inspection(
      text = "Duplicated map key",
      defaultLevel = Levels.Warning,
      description = "Checks for duplicate key names in Map literals.",
      explanation = "A map key is overwritten by a later entry."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val Arrow = TermName("$minus$greater")
          private val UnicodeArrow = TermName("$u2192")

          private def isDuplicateKeys(trees: List[Tree]): Boolean = {
            val keys = trees.foldLeft(List.empty[String])((keys, tree) =>
              tree match {
                case Apply(TypeApply(Select(Apply(_, args), Arrow | UnicodeArrow), _), _) =>
                  args match {
                    case Nil           => keys
                    case firstArg :: _ => keys :+ firstArg.toString
                  }
                case _ => keys
              }
            )
            keys.toSet.size < keys.size
          }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(TypeApply(Select(Select(_, TermName("Map")), TermName("apply")), _), args)
                  if isDuplicateKeys(args) =>
                context.warn(tree.pos, self, tree.toString.take(100))
              case _ => continue(tree)
            }
          }
        }
    }
}
