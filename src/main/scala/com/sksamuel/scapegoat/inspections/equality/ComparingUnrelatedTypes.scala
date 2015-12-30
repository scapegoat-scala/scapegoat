package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ComparingUnrelatedTypes extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("$eq$eq")), List(Literal(Constant(0)))) if lhs.tpe.typeSymbol.isNumericValueClass =>
          case Apply(Select(Literal(Constant(0)), TermName("$eq$eq")), List(rhs)) if rhs.tpe.typeSymbol.isNumericValueClass =>
          case Apply(Select(lhs, TermName("$eq$eq")), List(rhs)) =>
            def related(lt: Type, rt: Type) =
              lt <:< rt || rt <:< lt || lt =:= rt
            def isDerivedValueClass(ts: Symbol) =
              (ts.isClass && ts.asClass.isDerivedValueClass)
            def warn(): Unit = context.warn(
              "Comparing unrelated types",
              tree.pos,
              Levels.Error,
              tree.toString().take(500),
              ComparingUnrelatedTypes.this)
            def eraseIfNecessaryAndCompare(lt: Type, rt: Type): Unit = {
              val lTypeSymbol = lt.typeSymbol
              val rTypeSymbol = rt.typeSymbol
              val (l, r) = if (isDerivedValueClass(lTypeSymbol) || isDerivedValueClass(rTypeSymbol)) {
                (lt, rt)
              } else if (lTypeSymbol.isParameter || rTypeSymbol.isParameter) {
                (lt, rt)
              } else {
                (lt.erasure, rt.erasure)
              }

              if (!related(l, r)) {
                warn()
              } else {
                lt.typeArgs.zip(rt.typeArgs).foreach {
                  case (ltInner, rtInner) =>
                    eraseIfNecessaryAndCompare(ltInner, rtInner)
                }
              }
            }

            eraseIfNecessaryAndCompare(lhs.tpe, rhs.tpe)

          case _ => continue(tree)
        }
      }
    }
  }
}
