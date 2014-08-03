package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class CollectionNamingConfusion extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      def isNamedSet(name: String): Boolean = name == "set" || name.contains("Set")
      def isNamedList(name: String): Boolean = name == "list" || name.contains("List")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case ValDef(_, TermName(name), tpt, _) if tpt.tpe <:< typeOf[Set[_]] && isNamedList(name) =>
            context.warn("A Set is named list", tree.pos, Levels.Info,
              "An instanceof Set is confusingly referred to by a variable called list: " + tree.toString().take(300))
          case v@ValDef(_, TermName(name), tpt, _) if tpt.tpe <:< typeOf[List[_]] && isNamedSet(name) =>
            context.warn("A List is named set", tree.pos, Levels.Info,
              "An instanceof List is confusingly referred to by a variable called set: " + tree.toString().take(300))
          case _ => continue(tree)
        }
      }
    }
  }
}