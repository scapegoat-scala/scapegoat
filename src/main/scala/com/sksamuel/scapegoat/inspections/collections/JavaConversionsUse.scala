package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class JavaConversionsUse extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Import(expr, selectors) if expr.symbol.fullName == "scala.collection.JavaConversions" =>
            context.warn("Java conversions", tree.pos, Levels.Warning,
              "Use of java conversions can lead to unusual behaviour. It is recommended to use JavaConverters",
              JavaConversionsUse.this)
          case _ => continue(tree)
        }
      }
    }
  }
}