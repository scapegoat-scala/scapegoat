package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class SuspiciousMatchOnClassObject extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Match(selector, cases) =>
            checkCases(cases)
            continue(tree)
          case _ => continue(tree)
        }
      }

      private def checkCases(cases: List[CaseDef]): Unit = {
        cases.exists {
          case c @ CaseDef(pat, _, _) // if we have a case object and a companion class, then we are matching on an object instead of a class
          if pat.symbol != null &&
            pat.symbol.isModuleOrModuleClass &&
            pat.tpe.typeSymbol.companionClass.isClass &&
            !pat.tpe.typeSymbol.companionClass.isAbstractClass =>
            warn(c)
            true
          case _ => false
        }
      }

      private def warn(tree: Tree) {
        context.warn("Suspicious match on class object",
          tree.pos,
          Levels.Warning,
          tree.toString().take(500),
          SuspiciousMatchOnClassObject.this)
      }
    }
  }
}