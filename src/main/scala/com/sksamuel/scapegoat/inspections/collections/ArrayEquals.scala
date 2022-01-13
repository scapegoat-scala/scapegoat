package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class ArrayEquals
    extends Inspection(
      text = "Array equals",
      defaultLevel = Levels.Info,
      description = "Checks for comparison of arrays using == which will always return false.",
      explanation =
        "Array equals is not an equality check. Use a.deep == b.deep or convert to another collection type."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          protected override def isArray(tree: Tree): Boolean =
            tree match {
              // "null" is deprecated, but comparing an array to null can be useful
              // in some cases (e.g. when interfacing with null-using Java code) and
              // shouldn't trigger this warning, which relates to whether the
              // equality is reference-equality or value-equality.
              //
              // We have to add a special case here, because the 'type' of null will
              // be coerced to Array when it is compared to an array, so it would
              // otherwise match the next case.
              case Literal(Constant(null)) => false
              case x                       => super.isArray(x)
            }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(lhs, TermName("$eq$eq") | TermName("$bang$eq")), List(rhs))
                  if isArray(lhs) && isArray(rhs) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
