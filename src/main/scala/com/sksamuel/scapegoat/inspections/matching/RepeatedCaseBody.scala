package com.sksamuel.scapegoat.inspections.matching

import scala.collection.mutable

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class RepeatedCaseBody
    extends Inspection(
      text = "Repeated case body",
      defaultLevel = Levels.Warning,
      description = "Checks for case statements which have the same body.",
      explanation = "Case body is repeated. Consider merging pattern clauses together."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def isRepeated(cases: List[CaseDef]): Boolean = {
            val _cases = cases.filter(casedef => casedef.guard == EmptyTree && casedef.body.children.size > 4)
            val bodies = mutable.HashSet[String]()
            for (casedef <- _cases)
              bodies add casedef.body.toString()
            bodies.size < _cases.size
          }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Match(_, cases) if isRepeated(cases) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
