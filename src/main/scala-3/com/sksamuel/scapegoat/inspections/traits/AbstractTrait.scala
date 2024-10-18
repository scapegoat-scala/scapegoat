package com.sksamuel.scapegoat.inspections.traits

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Flags
import dotty.tools.dotc.core.Types.TypeRef
import dotty.tools.dotc.core.Symbols.ClassSymbol
import dotty.tools.dotc.util.SourcePosition

class AbstractTrait
    extends Inspection(
      text = "Use of abstract trait",
      defaultLevel = Levels.Info,
      description = "Traits are automatically abstract.",
      explanation =
        "The abstract modifier is used in class definitions. It is redundant for traits, and mandatory for all other classes which have incomplete members."
    ) {

  import tpd.*

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case tDef: TypeDef =>
            tDef.tpe match {
              case TypeRef(_, kls: ClassSymbol)
                  if kls.flags.is(Flags.Trait) && kls.flags.is(Flags.Abstract) =>
                feedback.warn(tree.sourcePos, self, tree.asSnippet)
              case _ =>
            }

          case _ =>
            traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}
