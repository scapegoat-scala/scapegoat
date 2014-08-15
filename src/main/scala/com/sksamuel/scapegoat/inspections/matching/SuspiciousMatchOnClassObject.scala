package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class SuspiciousMatchOnClassObject extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      private def containsClassLiteral(cases: List[CaseDef]) = {
        cases.exists {
          case CaseDef(Ident(TermName(binding)), EmptyTree, _) => binding != "_"
          case _ => false
        }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case DefDef(mods, _, _, _, _, _) if mods.isSynthetic =>
          case Match(selector, cases) if containsClassLiteral(cases) =>
            context.warn("Suspicious match on class object",
              tree.pos,
              Levels.Warning,
              tree.toString().take(500),
              SuspiciousMatchOnClassObject.this)
          case _ => continue(tree)
        }
      }
    }
  }
}