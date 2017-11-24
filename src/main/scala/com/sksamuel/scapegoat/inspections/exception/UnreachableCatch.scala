package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.{ Levels, Inspection, InspectionContext, Inspector }

import scala.collection.mutable

/** @author Stephen Samuel */
class UnreachableCatch extends Inspection("Unreachable catch", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      def isUnreachable(cases: List[CaseDef]) = {
        val types = mutable.HashSet[Type]()
          def check(tpe: Type): Boolean = {
            if (types.exists(tpe <:< _)) true
            else {
              types.add(tpe)
              false
            }
          }
        cases.exists {
          // matches t : Throwable
          case CaseDef(Bind(_, Typed(_, tpt)), _, _) => check(tpt.tpe)
          case CaseDef(Typed(_, tpt), _, _) if tpt.tpe =:= typeOf[Throwable] => check(tpt.tpe)
          case _ => false
        }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Try(_, cases, _) if isUnreachable(cases) =>
            context.warn(tree.pos, self,
              "One or more cases are unreachable" + tree.toString().take(300))
          case _ => continue(tree)
        }
      }
    }
  }
}

