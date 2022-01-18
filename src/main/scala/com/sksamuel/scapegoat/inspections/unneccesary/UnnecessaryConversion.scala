package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class UnnecessaryConversion
    extends Inspection(
      text = "Unnecessary conversion",
      defaultLevel = Levels.Warning,
      description = "Checks for unnecessary toInt on instances of Int or toString on Strings, etc.",
      explanation =
        "Calling e.g. toString on a String or toList on a List is completely unnecessary and it's an equivalent to identity."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._
          import definitions._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(lhs, TermName("toString")) if lhs.tpe <:< StringClass.tpe =>
                context
                  .warn(
                    tree.pos,
                    self,
                    tree.toString.take(200),
                    "Unnecessary toString on instance of String."
                  )
              case Select(lhs, TermName("toInt"))
                  if lhs.tpe <:< IntClass.tpe && Option(lhs.symbol)
                    .fold(ifEmpty = true)(_.baseClasses.nonEmpty) =>
                context.warn(tree.pos, self, tree.toString.take(200), "Unnecessary toInt on instance of Int.")
              case Select(lhs, TermName("toLong")) if lhs.tpe <:< LongClass.tpe =>
                context.warn(
                  tree.pos,
                  self,
                  tree.toString.take(200),
                  "Unnecessary toLong on instance of Long."
                )
              case Select(lhs, TermName("toSet")) if isSet(lhs, allowMutableSet = false) =>
                context.warn(tree.pos, self, tree.toString.take(200), "Unnecessary toSet on a Set.")
              case Select(lhs, TermName("toList")) if isList(lhs) =>
                context.warn(tree.pos, self, tree.toString.take(200), "Unnecessary toList on a List.")
              case Select(lhs, TermName("toSeq")) if isSeq(lhs) =>
                context.warn(tree.pos, self, tree.toString.take(200), "Unnecessary toSeq on a Seq.")
              case _ =>
            }
            continue(tree)
          }
        }
    }
}
