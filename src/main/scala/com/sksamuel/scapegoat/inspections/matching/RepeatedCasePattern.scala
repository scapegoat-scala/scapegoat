package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

import scala.collection.mutable

/** @author Stephen Samuel */
class RepeatedCasePattern extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      private def isUnreachable(cases: List[CaseDef]): Boolean = {
        val patterns = mutable.HashSet[String]()
        for ( casedef <- cases ) {
          patterns add casedef.pat.toString() + casedef.guard.toString()
        }
        patterns.size < cases.size
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Match(_, cases) if isUnreachable(cases) =>
            context
              .warn("Unreachable case pattern",
                tree.pos,
                Levels.Error,
                tree.toString().take(300),
                RepeatedCasePattern.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
