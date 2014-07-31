package com.sksamuel.scapegoat

/** @author Stephen Samuel */
trait Inspection {
  inspection =>

  import scala.reflect.runtime.universe
  import scala.reflect.runtime.universe._

  def traverser(reporter: Reporter): universe.Traverser

  trait SuppressAwareTraverser extends universe.Traverser {

    def isSuppressed(defDef: universe.DefDef) = {
      defDef.symbol != null &&
        defDef.symbol.annotations.nonEmpty &&
        defDef.symbol.annotations.head.tree.tpe.typeSymbol.fullName == classOf[SuppressWarnings].getCanonicalName &&
        (defDef.symbol.annotations.head.javaArgs.head._2.toString.toLowerCase.contains(inspection.getClass.getSimpleName.toLowerCase) ||
          defDef.symbol.annotations.head.javaArgs.head._2.toString.toLowerCase.contains("\"all\""))
    }

    abstract override def traverse(tree: universe.Tree): Unit = {
      tree match {
        case dd@DefDef(mods, _, _, _, _, _) if isSuppressed(dd) =>
        case block@Block(statements, expressions) =>
          super.traverse(block)
        case _ => super.traverse(tree)
      }
    }
  }
}

