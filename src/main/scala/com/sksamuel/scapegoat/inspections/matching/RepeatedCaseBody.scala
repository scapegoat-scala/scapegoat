package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

import scala.collection.mutable

/** @author Stephen Samuel */
class RepeatedCaseBody extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def isRepeated(cases: List[CaseDef]): Boolean = {
        val _cases = cases.filter(casedef => casedef.guard == EmptyTree && casedef.body.children.size > 4)
        val bodies = mutable.HashSet[String]()
        for (casedef <- _cases) {
          bodies add casedef.body.toString()
        }
        bodies.size < _cases.size
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Match(_, cases) if isRepeated(cases) =>
            context.warn("Repeated case body",
              tree.pos,
              Levels.Warning,
              "Case body is repeated. Consider merging pattern clauses together: " + tree.toString().take(500),
              RepeatedCaseBody.this)
          case _ => continue(tree)
        }
      }
    }
  }
}