package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class ArrayEquals extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def isArray(tree: Tree) = tree match {
        // "null" is deprecated, but comparing an array to null can be useful
        // in some cases (e.g. when interfacing with null-using Java code) and
        // shouldn't trigger this warning, which relates to whether the
        // equality is reference-equality or value-equality.
        //
        // We have to add a special case here, because the 'type' of null will
        // be coerced to Array when it is compared to an array, so it would
        // otherwise match the next case.
        case Literal(Constant(null)) => false
        case x                       => x.tpe <:< typeOf[Array[_]]
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("$eq$eq") | TermName("$bang$eq")), List(rhs)) if isArray(lhs) && isArray(rhs) =>

            context.warn("Array equals",
              tree.pos,
              Levels.Info,
              "Array equals is not an equality check. Use a.deep == b.deep or convert to another collection type",
              ArrayEquals.this)
          case _ => continue(tree)
        }
      }
    }
  }
}