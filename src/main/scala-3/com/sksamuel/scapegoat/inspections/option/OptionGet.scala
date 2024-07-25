package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.StdNames.*
import dotty.tools.dotc.core.Symbols.*
import dotty.tools.dotc.core.Types.TermRef
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

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using Context): Unit = {
    val traverser = new TreeTraverser {
      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case Select(qual, nme.get) =>
            val optType = defn.OptionClass.typeRef
            qual.tpe match {
              case tref: TermRef if tref.info.typeConstructor <:< optType =>
                feedback.warn(tree.sourcePos, self)
              case _ =>
            }
            traverseChildren(tree)
          case _ => traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }

  /**
   * def inspector(ctx: InspectionContext): Inspector = new Inspector(ctx) { override def postTyperTraverser:
   * context.Traverser = new context.Traverser {
   *
   * import context.global._
   *
   * override def inspect(tree: Tree): Unit = { tree match { case Select(left, TermName("get")) => if
   * (left.tpe.typeSymbol.fullName == "scala.Option") context.warn(tree.pos, self, tree.toString.take(500))
   * case _ => continue(tree) } } } }
   */
}
