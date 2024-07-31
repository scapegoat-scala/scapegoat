package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.StdNames.*
import dotty.tools.dotc.util.SourcePosition

/**
 * @author
 *   Stephen Samuel
 */
class OptionGet
    extends Inspection(
      text = "Use of Option.get",
      defaultLevel = Levels.Error,
      description = "Checks for use of Option.get.",
      explanation =
        "Using Option.get defeats the purpose of using Option in the first place. Use the following instead: Option.getOrElse, Option.fold, pattern matching or don't take the value out of the container and map over it to transform it."
    ) {

  import tpd.*

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case Select(left, nme.get)
              if left.tpe.typeSymbol.lastKnownDenotation.fullName.toString == "scala.Option" =>
            feedback.warn(tree.sourcePos, self, tree.asSnippet)
          case _ => traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}
