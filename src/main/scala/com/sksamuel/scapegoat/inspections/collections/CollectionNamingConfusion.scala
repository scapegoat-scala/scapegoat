package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class CollectionNamingConfusion extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def isNamedSet(name: String): Boolean = name.trim == "set" || name.trim.contains("Set")
      private def isNamedList(name: String): Boolean = name.trim == "list" || name.trim.contains("List")
      private def isSet(tpe: Type) = tpe <:< typeOf[scala.collection.mutable.Set[_]] || tpe <:< typeOf[scala.collection.immutable.Set[_]]
      private def isList(tpe: Type) = tpe <:< typeOf[scala.collection.immutable.List[_]]

      override def inspect(tree: Tree): Unit = {
        tree match {
          case ValDef(_, TermName(name), tpt, _) if isSet(tpt.tpe) && isNamedList(name) =>
            context.warn("A Set is named list", tree.pos, Levels.Info,
              "An instanceof Set is confusingly referred to by a variable called/containing list: " +
                tree.toString().take(300),
              CollectionNamingConfusion.this)
          case v @ ValDef(_, TermName(name), tpt, _) if isList(tpt.tpe) && isNamedSet(name) =>
            context.warn("A List is named set", tree.pos, Levels.Info,
              "An instanceof List is confusingly referred to by a variable called/containing set: " +
                tree.toString().take(300),
              CollectionNamingConfusion.this)
          case _ => continue(tree)
        }
      }
    }
  }
}