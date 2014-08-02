package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class ComparingUnrelatedTypes extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(l, TermName("$eq$eq")), r) =>
          val left = rootMirror.staticClass(l.tpe.erasure.typeSymbol.fullName).toType.erasure
          val right = rootMirror.staticClass(r.head.tpe.erasure.typeSymbol.fullName).toType.erasure
          if (!(left <:< right || right <:< left)) {
            feedback.warn("Comparing unrelated types", tree.pos, Levels.Error, tree.toString().take(500))
          }
        case _ => super.traverse(tree)
      }
    }
  }
}
