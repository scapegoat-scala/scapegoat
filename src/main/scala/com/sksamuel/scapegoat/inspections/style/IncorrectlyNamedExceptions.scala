package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat._

/** @author Stephen Samuel
  *
  *         Inspired by http://findbugs.sourceforge.net/bugDescriptions.html#NM_CLASS_NOT_EXCEPTION
  **/
class IncorrectlyNamedExceptions extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case cdef@ClassDef(mods, name, _, impl) =>
            val isNamedException = name.toString.endsWith("Exception")
            val isException = impl.tpe <:< typeOf[Exception]
            (isNamedException, isException) match {
              case (true, false) =>
                context.warn("Class named exception does not derive from Exception",
                  tree.pos, Levels.Error, tree.toString().take(500))
              case (false, true) =>
                context.warn("Class derived from Exception is not named *Exception",
                  tree.pos, Levels.Error, tree.toString().take(500))
              case _ =>
            }
          case _ =>
        }
        super.traverse(tree)
      }
    }
  }
}