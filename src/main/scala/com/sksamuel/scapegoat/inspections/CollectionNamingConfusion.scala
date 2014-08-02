package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Feedback, Inspection}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class CollectionNamingConfusion extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    def isNamedSet(name: String): Boolean = name == "set" || name.contains("Set")
    def isNamedList(name: String): Boolean = name == "list" || name.contains("List")

    override def traverse(tree: Tree): Unit = {
      tree match {
        case ValDef(_, TermName(name), tpt, _) if tpt.tpe <:< typeOf[Set[_]] && isNamedList(name) =>
          feedback.warn("A Set is named list", tree.pos, Levels.Warning, "An instanceof Set is confusingly referred to by a variable called list: " + tree.toString().take(300))
        case v@ValDef(_, TermName(name), tpt, _) if tpt.tpe <:< typeOf[List[_]] && isNamedSet(name) =>
          feedback.warn("A List is named set", tree.pos, Levels.Warning, "An instanceof List is confusingly referred to by a variable called set: " + tree.toString().take(300))
        case _ => super.traverse(tree)
      }
    }
  }
}
