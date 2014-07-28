package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

/** @author Stephen Samuel */
class AsInstanceOf extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case TypeApply(Select(_, TermName("asInstanceOf")), _) =>
          reporter.warn("Use of asInstanceOf", tree, Levels.Warning,
            "asInstanceOf used near " + tree.toString().take(500) + ". Consider using pattern matching.")
        case DefDef(modifiers, _, _, _, _, _) if modifiers.hasFlag(Flag.SYNTHETIC) => // no further
        case Match(selector, cases) => // ignore selector and process cases
          cases.foreach(traverse)
        case _ => super.traverse(tree)
      }
    }
  }
}
