package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel
  *
  *         Inspired by http://findbugs.sourceforge.net/bugDescriptions.html#NM_CLASS_NOT_EXCEPTION
  * */
class NotExceptionNamedException extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case cdef@ClassDef(mods, name, _, impl) =>
          val isNamedException = name.toString.endsWith("Exception")
          val isException = impl.tpe <:< typeOf[Exception]
          (isNamedException, isException) match {
            case (true, false) =>
              feedback.warn("Class named exception does not derive from Exception",
                tree.pos, Levels.Error, tree.toString().take(500))
            case (false, true) =>
              feedback.warn("Class derived from Exception is not named *Exception",
                tree.pos, Levels.Error, tree.toString().take(500))
            case _ =>
          }
        case _ =>
      }
      super.traverse(tree)
    }
  }
}
