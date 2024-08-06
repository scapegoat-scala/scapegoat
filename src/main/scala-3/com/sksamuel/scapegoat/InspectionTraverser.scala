package com.sksamuel.scapegoat

import dotty.tools.dotc.ast.tpd.*
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.util.NoSource

abstract class InspectionTraverser extends TreeTraverser {

  extension (tree: Tree)(using Context)
    def asSnippet: Option[String] = tree.source match
      case NoSource => None
      case _        => Some(tree.source.content().slice(tree.sourcePos.start, tree.sourcePos.end).mkString)

}
