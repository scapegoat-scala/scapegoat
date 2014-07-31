package com.sksamuel.scapegoat

import scala.tools.nsc.Global

/** @author Stephen Samuel */
trait Inspection {
  inspection =>

  def traverser(global: Global, reporter: Feedback): global.Traverser

  //  trait SuppressAwareTraverser extends universe.Traverser {
  //
  //    def isSuppressed(symbol: Symbol) = {
  //      symbol != null &&
  //        symbol.annotations.nonEmpty &&
  //        symbol.annotations.head.tree.tpe.typeSymbol.fullName == classOf[SuppressWarnings].getCanonicalName &&
  //        (symbol.annotations.head.javaArgs.head._2.toString.toLowerCase.contains(inspection.getClass.getSimpleName.toLowerCase) ||
  //          symbol.annotations.head.javaArgs.head._2.toString.toLowerCase.contains("\"all\""))
  //    }
  //
  //    abstract override def traverse(tree: universe.Tree): Unit = {
  //      tree match {
  //        case dd@DefDef(_, _, _, _, _, _) if isSuppressed(dd.symbol) =>
  //        case block@Block(_, _) if isSuppressed(block.symbol) =>
  //        case iff@If(_,_,_) if isSuppressed(iff.symbol) =>
  //        case tri@Try(_,_,_) if isSuppressed(tri.symbol) =>
  //        case _ => super.traverse(tree)
  //      }
  //    }
  //  }
}

