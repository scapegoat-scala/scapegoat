package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class SuspiciousMatchOnClassObject
    extends Inspection(
      text = "Suspicious match on class object",
      defaultLevel = Levels.Warning,
      description = "Checks for code where matching is taking place on class literals.",
      explanation = "Matching on an companion object of a case class is probably not what you intended."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Match(_, cases) =>
                checkCases(cases)
                continue(tree)
              case _ => continue(tree)
            }
          }

          private def checkCases(cases: List[CaseDef]): Unit = {
            cases.exists {
              case c @ CaseDef(
                    pat,
                    _,
                    _
                  ) // if we have a case object and a companion class, then we are matching on an object instead of a class
                  if pat.symbol != null &&
                    pat.symbol.isModuleOrModuleClass &&
                    pat.tpe.typeSymbol.companionClass.isClass &&
                    !pat.tpe.typeSymbol.companionClass.isAbstractClass =>
                context.warn(c.pos, self, c.toString.take(500))
                true
              case _ => false
            }
          }
        }
    }
}
