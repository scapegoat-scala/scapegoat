package com.sksamuel.scapegoat.inspections.option

import scala.util.Either.{LeftProjection, RightProjection}

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.StdNames.*
import dotty.tools.dotc.util.SourcePosition

class EitherGet
    extends Inspection(
      text = "Use of Either.right or Either.left projection followed by a get",
      defaultLevel = Levels.Error,
      description = "Checks for use of .get on Left or Right projection.",
      explanation =
        "Method .get on a Left and a Right projection is deprecated since 2.13, use Either.getOrElse or Either.swap.getOrElse instead."
    ) {

  import tpd.*

  private val PROJECTION_TYPES: Seq[String] = Seq(
    classOf[LeftProjection[?, ?]].getName,
    // While deprecated, we should still flag it
    classOf[RightProjection[?, ?]].getName
  ).map(_.replace("$", "$."))

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case Select(left, nme.get)
              if PROJECTION_TYPES.contains(left.tpe.typeSymbol.lastKnownDenotation.fullName.toString) =>
            feedback.warn(tree.sourcePos, self, tree.asSnippet)
          case _ => traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}
