package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel
  *         This inspection was inspired by http://p5wscala.wordpress.com/scalaprocessing-gotchas/#t2
  * */
class SeqSeqColonAdd extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {

    import scala.reflect.runtime.universe._

    def isSeq(tree: universe.Tree): Boolean = {
      tree.toString() == "List[Any]" || tree.toString() == "Seq[Any]" || tree.toString() == "Vector[Any]"
    }

    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case TypeApply(Select(_, TermName("$colon$plus")), List(a, r)) =>
          if (a.toString() == "Any" && isSeq(r))
            reporter.warn("List :+ List", tree, Levels.Warning, tree.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }
}
