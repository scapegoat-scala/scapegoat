package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class UnnecessaryConversion extends Inspection("Unnecessary conversion", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._
      import definitions._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(lhs, TermName("toString")) if lhs.tpe <:< StringClass.tpe =>
            context.warn(tree.pos, self, "Unnecessary toString on instance of String: " + tree.toString().take(200))

          case Select(lhs, TermName("toInt"))
            if lhs.tpe <:< IntClass.tpe && Option(lhs.symbol).fold(ifEmpty = true)(_.baseClasses.nonEmpty) =>
              context.warn(tree.pos, self, s"Unnecessary invocation of toInt on instance of Int: " + tree.toString.take(200))

          case _ =>
        }
        continue(tree)
      }
    }
  }
}
