package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ComparingUnrelatedTypes extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {

        def isIntegral(t: Type) =
          Seq(typeOf[Byte], typeOf[Char], typeOf[Short], typeOf[Int], typeOf[Long]).exists(t <:< _)

        def integralLiteralFitsInType(literal: Literal, targetType: Type): Boolean = {
          if (!isIntegral(literal.tpe) || !isIntegral(targetType)) {
            false
          } else {
            // convertTo has built-in range checking and will return null if the value cannot be
            // accurately represented in the target type.
            literal.value.convertTo(targetType) != null
          }
        }

        tree match {
          // -- Special cases ---------------------------------------------------------------------

          // Comparing any numeric value to a literal 0 should be ignored:
          case Apply(Select(lhs, TermName("$eq$eq")), List(Literal(Constant(0)))) if lhs.tpe.typeSymbol.isNumericValueClass =>
          case Apply(Select(Literal(Constant(0)), TermName("$eq$eq")), List(rhs)) if rhs.tpe.typeSymbol.isNumericValueClass =>

          // Comparing a integral value to a integral literal should be ignored if the literal
          // fits in the in range of the other value's type. For example, in general it may be
          // unsafe to compare ints to chars because not all ints fit in the range of a char, but
          // such comparisons are safe for small integers: thus `(c: Char) == 97` is a valid
          // comparision but `(c: Char) == 128000` is not.
          case Apply(Select(value, TermName("$eq$eq")), List(lit @ Literal(_))) if integralLiteralFitsInType(lit, value.tpe) =>
          case Apply(Select(lit @ Literal(_), TermName("$eq$eq")), List(value)) if integralLiteralFitsInType(lit, value.tpe)  =>

          // -- End special cases ------------------------------------------------------------------

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
