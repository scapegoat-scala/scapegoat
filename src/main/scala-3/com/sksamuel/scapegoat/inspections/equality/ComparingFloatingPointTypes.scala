package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.util.SourcePosition

class ComparingFloatingPointTypes
    extends Inspection(
      text = "Floating type comparison",
      defaultLevel = Levels.Error,
      description = "Checks for equality checks on floating point types.",
      explanation =
        "Due to minor rounding errors, it is not advisable to compare floating-point numbers using the == operator. Either use a threshold based comparison, or switch to a BigDecimal."
    ) {

  import tpd.*

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case Apply(Select(left, name), List(right))
              if name.toString == "==" || name.toString == "!=" =>
            val leftTypeName = left.tpe.typeSymbol.fullName.toString
            val rightTypeName = right.tpe.typeSymbol.fullName.toString
            val leftFloating = leftTypeName == "scala.Double" || leftTypeName == "scala.Float"
            val rightFloating = rightTypeName == "scala.Double" || rightTypeName == "scala.Float"
            if (leftFloating && rightFloating)
              feedback.warn(tree.sourcePos, self, tree.asSnippet)
          case _ => traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}
