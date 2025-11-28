package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.util.SourcePosition

class IncorrectlyNamedExceptions
    extends Inspection(
      text = "Incorrectly named exceptions",
      defaultLevel = Levels.Error,
      description = "Checks for exceptions that are not called *Exception and vice versa.",
      explanation =
        "Class named exception does not derive from Exception / class derived from Exception is not named *Exception."
    ) {

  import tpd.*

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case cdef @ TypeDef(name, impl) if cdef.isClassDef =>
            val isNamedException = name.toString.endsWith("Exception")
            val isAnon = scala.util
              .Try {
                cdef.symbol.isAnonymousClass
              }
              .getOrElse(false)

            val extendsException = impl.tpe <:< ctx.definitions.ExceptionClass.typeRef
            val selfTypeIsException = impl match {
              case Template(_, _, self, _) =>
                self.tpt.tpe <:< ctx.definitions.ExceptionClass.typeRef
              case _ => false
            }

            // A class or trait is an Exception for our purposes if it either
            // inherits from exception or it is a trait which declares its
            // self-type to be Exception
            val isException = extendsException || selfTypeIsException

            (isNamedException, isAnon, isException) match {
              case (true, _, false) =>
                feedback.warn(tree.sourcePos, self, tree.asSnippet.map(_.take(500)))
              case (false, false, true) =>
                feedback.warn(tree.sourcePos, self, tree.asSnippet.map(_.take(500)))
              case _ =>
            }
            traverseChildren(tree)
          case _ =>
            traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}
