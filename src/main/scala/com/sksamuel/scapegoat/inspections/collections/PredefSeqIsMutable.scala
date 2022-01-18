package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class PredefSeqIsMutable
    extends Inspection(
      text = "Predef.Seq is mutable",
      defaultLevel = Levels.Info,
      description = "Checks for use of mutable Seq.",
      explanation = "Predef.Seq aliases scala.collection.mutable.Seq. Did you intend to use an immutable Seq?"
    ) {

  override def isEnabled: Boolean = !isScala213

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {
          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case DefDef(_, _, _, _, _, _) if tree.symbol.isAccessor =>
              case TypeTree() if tree.tpe.erasure.toString() == "Seq[Any]" =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
