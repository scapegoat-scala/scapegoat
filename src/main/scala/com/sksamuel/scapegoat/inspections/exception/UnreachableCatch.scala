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
        def check(tpe: Type, guard: Tree): Boolean = {
          if (types.exists(tpe <:< _)) {
            true
          } else {
            if (guard == EmptyTree) {
              types.add(tpe)
            }
            false
          }
        }
        cases.exists {
          // matches t : Throwable
          case CaseDef(Bind(_, Typed(_, tpt)), guard, _) => check(tpt.tpe, guard)
          case CaseDef(Typed(_, tpt), guard, _) if tpt.tpe =:= typeOf[Throwable] => check(tpt.tpe, guard)
          case _ => false
        }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Try(_, cases, _) if isUnreachable(cases) =>
            context.warn(tree.pos, self,
              "One or more cases are unreachable " + tree.toString().take(300))
          case _ => continue(tree)
        }
      }
    }
  }
}

