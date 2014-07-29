package com.sksamuel.scapegoat

/** @author Stephen Samuel */
trait Inspection {

  import scala.reflect.runtime.universe
  import scala.reflect.runtime.universe._

  def traverser(reporter: Reporter): universe.Traverser

  trait SuppressAwareTraverser extends universe.Traverser {

    def isSuppressed(defDef: universe.DefDef) = {
      defDef.symbol != null &&
        defDef.symbol.annotations.nonEmpty &&
        defDef.symbol.annotations.head.tree.tpe.typeSymbol.fullName == classOf[SuppressWarnings].getCanonicalName
    }

    abstract override def traverse(tree: universe.Tree): Unit = {
      tree match {
        case dd@DefDef(mods, _, _, _, _, _) if isSuppressed(dd) =>
        case _ => super.traverse(tree)
      }
    }
  }
}

