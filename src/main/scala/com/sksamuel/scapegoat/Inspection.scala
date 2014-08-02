package com.sksamuel.scapegoat

import scala.tools.nsc.Global

/** @author Stephen Samuel */
trait Inspection {
  def traverser(global: Global, feedback: Feedback): global.Traverser
}

class SuppressAwareInspection(wrapped: Inspection) extends Inspection {

  def traverser(global: Global, feedback: Feedback): global.Traverser = {

    import global._

    new global.Traverser {

      val delegate = wrapped.traverser(global, feedback)

      def isSuppressed(symbol: Symbol) = {
        symbol != null &&
          symbol.annotations.nonEmpty &&
          symbol.annotations.head.tree.tpe.typeSymbol.fullName == classOf[SuppressWarnings].getCanonicalName &&
          (symbol.annotations.head.javaArgs.head._2.toString.toLowerCase.contains(delegate.getClass.getSimpleName.toLowerCase) ||
            symbol.annotations.head.javaArgs.head._2.toString.toLowerCase.contains("\"all\""))
      }

      override def traverse(tree: Tree): Unit = {
        tree match {
          case dd@DefDef(_, _, _, _, _, _) if isSuppressed(dd.symbol) =>
          case block@Block(_, _) if isSuppressed(block.symbol) =>
          case iff@If(_, _, _) if isSuppressed(iff.symbol) =>
          case tri@Try(_, _, _) if isSuppressed(tri.symbol) =>
          case _ => delegate.traverse(tree)
        }
      }
    }
  }
}


