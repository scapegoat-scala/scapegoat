package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

/** @author Stephen Samuel */
class NullUse extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser with SuppressAwareTraverser {

    def containsNull(trees: List[Tree]) = trees exists {
      case Literal(Constant(null)) => true
      case _ => false
    }

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(_, args) =>
          if (containsNull(args))
            reporter.warn("null use", tree, Levels.Error, "null as method argument: " + tree.toString().take(300))
        case Literal(Constant(null)) =>
          reporter.warn("null use", tree, Levels.Error, "null used on line " + tree.pos.line)
        case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flag.SYNTHETIC) =>
        case _ => super.traverse(tree)
      }
    }
  }
}
