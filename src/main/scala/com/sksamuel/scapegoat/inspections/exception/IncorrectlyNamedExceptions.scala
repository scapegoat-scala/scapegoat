package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat._

/**
 * @author Stephen Samuel
 *
 *         Inspired by http://findbugs.sourceforge.net/bugDescriptions.html#NM_CLASS_NOT_EXCEPTION
 */
class IncorrectlyNamedExceptions extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case cdef@ClassDef(mods, name, _, impl) =>
            val isNamedException = name.toString.endsWith("Exception")
            val isAnon = scala.util.Try {
              cdef.symbol.isAnonymousClass
            } getOrElse false
            val isException = impl.tpe <:< typeOf[Exception]
            (isNamedException, isAnon, isException) match {
              case (true, _, false) =>
                context.warn("Class named exception does not derive from Exception",
                  tree.pos, Levels.Error, tree.toString().take(500), IncorrectlyNamedExceptions.this)
              case (false, false, true) =>
                context.warn("Class derived from Exception is not named *Exception",
                  tree.pos, Levels.Error, tree.toString().take(500), IncorrectlyNamedExceptions.this)
              case _ =>
            }
          case _ =>
        }
        continue(tree)
      }
    }
  }
}