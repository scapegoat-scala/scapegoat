package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class DuplicateMapKey extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    private def isDuplicateKeys(trees: List[Tree]): Boolean = {
      val unique = trees.foldLeft(Set[String]())((set, tree) => tree match {
        case Apply(TypeApply(Select(Apply(_, args), TermName("$minus$greater")), _), _) =>
          set + args.head.toString()
        case _ => set
      })
      unique.size < trees.size
    }

    private def warn(tree: Tree) = {
      feedback.warn("Duplicated map key",
        tree.pos,
        Levels.Error,
        "A map key is overwriten by a later entry: " + tree.toString().take(100))
    }

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(TypeApply(Select(Select(_, TermName("Map")), TermName("apply")), _),
        args) if isDuplicateKeys(args) =>
          warn(tree)
        case _ => super.traverse(tree)
      }
    }
  }
}
