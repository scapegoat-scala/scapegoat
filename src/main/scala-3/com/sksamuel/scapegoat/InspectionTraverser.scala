package com.sksamuel.scapegoat

import dotty.tools.dotc.ast.Trees
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.ast.tpd.*
import dotty.tools.dotc.core.Constants
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Names
import dotty.tools.dotc.core.Symbols
import dotty.tools.dotc.core.Symbols.requiredClass
import dotty.tools.dotc.util.NoSource

abstract class InspectionTraverser(using inspection: Inspection) extends TreeTraverser {

  override protected def traverseChildren(tree: tpd.Tree)(using Context): Unit = {
    if (!isSuppressed(tree)) {
      super.traverseChildren(tree)
    }
  }

  private def isSuppressed(t: tpd.Tree)(using Context): Boolean = {
    val symbol = t.symbol
    val annotation = symbol.getAnnotation(requiredClass("java.lang.SuppressWarnings"))
    val arg = annotation.flatMap(_.argument(0)).map(extractArg)
    val inspectionName = inspection.getClass.getSimpleName
    arg match {
      case Some(
            Apply(Apply(TypeApply(Select(Ident(_), _), _), List(Typed(SeqLiteral(args, _), _))), _)
          ) =>
        args.exists {
          case Literal(value) if value.tag == Constants.StringTag =>
            value.stringValue == "all" || value.stringValue == inspectionName
          case _ => false
        }
      case _ => false
    }
  }

  // Scala 3.3 doesn't insert NamedArg, skip it while trying to match
  private def extractArg(t: tpd.Tree): tpd.Tree = t match {
    case NamedArg(_, tree) => tree
    case _                 => t
  }

  extension (tree: Tree)(using Context)
    def asSnippet: Option[String] = tree.source match
      case NoSource => None
      case _        => Some(tree.source.content().slice(tree.sourcePos.start, tree.sourcePos.end).mkString)

}

object InspectionTraverser {
  val array = Names.termName("Array")
}
