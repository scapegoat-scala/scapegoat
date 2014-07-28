package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel
  *         This inspection was inspired by http://p5wscala.wordpress.com/scalaprocessing-gotchas/#t2
  * */
class CollectionPromotionToAny extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {

    import scala.reflect.runtime.universe._

    private def isSeq(symbol: Symbol): Boolean = {
      val full = symbol.typeSignature.resultType.typeSymbol.fullName
      full.startsWith("scala.collection.immutable") &&
        (full.endsWith("List") || full.endsWith("Set") || full.endsWith("Seq") || full.endsWith("Vector"))
    }

    private def isAny(tree: Tree): Boolean = tree.toString() == "Any"
    private def isAny(symbol: Symbol): Boolean = symbol.typeSignature.resultType.typeArgs.head.toString == "Any"

    private def isAnySeq(tree: Tree): Boolean = tree match {
      case select@Select(_, _) if select.symbol != null => isSeq(select.symbol) && isAny(select.symbol)
      case _ => false
    }

    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case TypeApply(Select(l, TermName("$colon$plus")), List(a, r)) =>
          if (!isAnySeq(l) && isAny(a))
            reporter.warn("Collection promotion to any", tree, Levels.Warning, tree.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }
}
