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
              context.warn(tree.pos, self, "Unnecessary toInt on instance of Int: " + tree.toString.take(200))

          case Select(lhs, TermName("toLong")) if lhs.tpe <:< LongClass.tpe =>
              context.warn(tree.pos, self, "Unnecessary toLong on instance of Long: " + tree.toString.take(200))

          case Select(lhs, TermName("toSet")) if isSet(lhs, allowMutableSet = false) =>
              context.warn(tree.pos, self, "Unnecessary toSet on a set: " + tree.toString.take(200))

          case Select(lhs, TermName("toList")) if isList(lhs) =>
              context.warn(tree.pos, self, "Unnecessary toList on a list: " + tree.toString.take(200))

          case Select(lhs, TermName("toSeq")) if isSeq(lhs) =>
              context.warn(tree.pos, self, "Unnecessary toSeq on a seq: " + tree.toString.take(200))

          case _ =>
        }
        continue(tree)
      }
    }
  }
}
