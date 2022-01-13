package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class ComparingUnrelatedTypes
    extends Inspection(
      text = "Comparing unrelated types",
      defaultLevel = Levels.Error,
      description = "Checks for equality comparisons that cannot succeed.",
      explanation =
        "In most case comparing unrelated types cannot succeed and it's usually an indication of a bug."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {

            def isIntegral(t: Type) =
              Seq(typeOf[Byte], typeOf[Char], typeOf[Short], typeOf[Int], typeOf[Long]).exists(t <:< _)

            def integralLiteralFitsInType(literal: Literal, targetType: Type): Boolean = {
              if (!isIntegral(literal.tpe) || !isIntegral(targetType))
                false
              else
                // convertTo has built-in range checking and will return null if the value cannot be
                // accurately represented in the target type.
                literal.value.convertTo(targetType) != null
            }

            tree match {
              // -- Special cases ---------------------------------------------------------------------

              // Comparing any numeric value to a literal 0 should be ignored:
              case Apply(Select(lhs, TermName("$eq$eq" | "$bang$eq")), List(Literal(Constant(0))))
                  if lhs.tpe.typeSymbol.isNumericValueClass =>
              case Apply(Select(Literal(Constant(0)), TermName("$eq$eq" | "$bang$eq")), List(rhs))
                  if rhs.tpe.typeSymbol.isNumericValueClass =>
              // Comparing a integral value to a integral literal should be ignored if the literal
              // fits in the in range of the other value's type. For example, in general it may be
              // unsafe to compare ints to chars because not all ints fit in the range of a char, but
              // such comparisons are safe for small integers: thus `(c: Char) == 97` is a valid
              // comparision but `(c: Char) == 128000` is not.
              case Apply(Select(value, TermName("$eq$eq" | "$bang$eq")), List(lit @ Literal(_)))
                  if integralLiteralFitsInType(lit, value.tpe) =>
              case Apply(Select(lit @ Literal(_), TermName("$eq$eq" | "$bang$eq")), List(value))
                  if integralLiteralFitsInType(lit, value.tpe) =>
              // -- End special cases ------------------------------------------------------------------

              case Apply(Select(lhs, op @ TermName("$eq$eq" | "$bang$eq")), List(rhs)) =>
                val equality = if (op string_== "$eq$eq") "==" else "!="
                def related(lt: Type, rt: Type) = lt <:< rt || rt <:< lt || lt =:= rt
                def hasSpecificEq(t: Type): Boolean =
                  t.members.exists { sym =>
                    sym.isMethod && sym.nameString == equality && (sym.paramss match {
                      case List(List(p)) =>
                        val pt = p.tpe.deconst
                        val rt = rhs.tpe.deconst

                        // rule out (synthetic) `==(Any)` methods
                        !(pt =:= typeOf[Any]) && (pt =:= rt || rt <:< pt)

                      case _ => false
                    })
                  }

                if (!hasSpecificEq(lhs.tpe.deconst) && !related(lhs.tpe.widen, rhs.tpe.widen))
                  context.warn(tree.pos, self, tree.toString.take(500))

              case _ => continue(tree)
            }
          }
        }
    }
}
