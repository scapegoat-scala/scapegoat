package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

/** @author Stephen Samuel */
class ComparingUnrelatedTypes extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(l, TermName("$eq$eq")), r) =>
          val left = rootMirror.staticClass(l.tpe.erasure.typeSymbol.fullName).toType.erasure
          val right = rootMirror.staticClass(r.head.tpe.erasure.typeSymbol.fullName).toType.erasure
          if (!(left <:< right || right <:< left)) {
            reporter.warn("Comparing unrelated types", tree, Levels.Error, tree.toString().take(500))
          }
        case _ => super.traverse(tree)
      }
    }
  }
}
