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

abstract class InspectionTraverser extends TreeTraverser {

  override protected def traverseChildren(tree: tpd.Tree)(using Context): Unit = {
    if (!isSuppressed(tree)) {
      super.traverseChildren(tree)
    }
  }

  private def isSuppressed(t: tpd.Tree)(using Context): Boolean = {
    t.symbol.getAnnotation(requiredClass("java.lang.SuppressWarnings")).flatMap(_.argument(0)) match {
      case Some(
            NamedArg(
              _,
              Apply(
                Apply(TypeApply(Select(Ident(name), _), _), List(Typed(SeqLiteral(args, _), _))),
                _
              )
            )
          ) if name.toTermName == Names.termName("Array") =>
        args.collectFirst {
          case Literal(value) if value.tag == Constants.StringTag =>
            value.stringValue == "all"
        }.isDefined
      case _ => false
    }
  }

  extension (tree: Tree)(using Context)
    def asSnippet: Option[String] = tree.source match
      case NoSource => None
      case _        => Some(tree.source.content().slice(tree.sourcePos.start, tree.sourcePos.end).mkString)

}
