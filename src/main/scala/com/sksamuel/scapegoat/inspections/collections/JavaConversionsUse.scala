package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class JavaConversionsUse
    extends Inspection(
      text = "Java conversions",
      defaultLevel = Levels.Warning,
      description = "Checks for use of Java conversions.",
      explanation =
        "Use of Java conversions can lead to unusual behaviour. It is recommended to use JavaConverters."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Import(expr, _) if expr.symbol.fullName == "scala.collection.JavaConversions" =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
