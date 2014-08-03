package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ComparingUnrelatedTypes extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(l, TermName("$eq$eq")), r) =>
            val left = rootMirror.staticClass(l.tpe.erasure.typeSymbol.fullName).toType.erasure
            val right = rootMirror.staticClass(r.head.tpe.erasure.typeSymbol.fullName).toType.erasure
            if (!(left <:< right || right <:< left)) {
              context.warn("Comparing unrelated types", tree.pos, Levels.Error, tree.toString().take(500))
            }
          case _ => super.traverse(tree)
        }
      }
    }
  }
}
