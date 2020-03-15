package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class VariableShadowing
    extends Inspection(
      text = "Variable shadowing",
      defaultLevel = Levels.Warning,
      description = "Checks for multiple uses of the variable name in nested scopes.",
      explanation =
        "Variable shadowing is very useful, but can easily lead to nasty bugs in your code. Shadowed variables can be potentially confusing to other maintainers when the same name is adopted to have a new meaning in a nested scope."
    ) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = new context.Traverser {

      import context.global._

      private val contexts = new mutable.Stack[ListBuffer[String]]()

      private def isDefined(name: String): Boolean = contexts exists (_.contains(name.trim))

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
            if (isDefined(name)) context.warn(tree.pos, self, tree.toString.take(200))
            contexts.top.append(name.trim)
          case Match(_, cases) =>
            cases.foreach {
              case CaseDef(Bind(name, _), _, _) =>
                if (isDefined(name.toString)) context.warn(tree.pos, self, tree.toString.take(200))
              case _ => // do nothing
            }
            continue(tree)
          case _ => continue(tree)
        }
      }
    }
  }
}
