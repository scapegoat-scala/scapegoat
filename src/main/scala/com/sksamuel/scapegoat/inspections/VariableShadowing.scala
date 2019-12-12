package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class VariableShadowing extends Inspection("Variable shadowing", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val contexts = new mutable.Stack[ListBuffer[String]]()

      private def isDefined(name: String): Boolean = contexts exists (_.contains(name.trim))

      private def warn(tree: Tree): Unit = {
        context.warn(tree.pos, self, "Variable is shadowed: " + tree.toString().take(200))
      }

      private def enter(): Unit = contexts.push(new ListBuffer[String])
      private def exit(): Unit = contexts.pop()

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Block(_, _) =>
            enter(); continue(tree); exit()
          case ClassDef(_, _, _, Template(_, _, body)) =>
            enter(); continue(tree); exit()
          case ModuleDef(_, _, Template(_, _, _)) =>
            enter(); continue(tree); exit()
          case DefDef(_, _, _, vparamss, _, rhs) =>
            enter()
            vparamss.foreach(_.foreach(inspect))
            inspect(rhs)
            exit()
          case ValDef(_, TermName(name), _, _) =>
            if (isDefined(name)) warn(tree)
            contexts.top.append(name.trim)
          case Match(_, cases) =>
            cases.foreach {
              case CaseDef(Bind(name, _), _, _) =>
                if (isDefined(name.toString)) warn(tree)
              case _ => // do nothing
            }
            continue(tree)
          case _ => continue(tree)
        }
      }
    }
  }
}
