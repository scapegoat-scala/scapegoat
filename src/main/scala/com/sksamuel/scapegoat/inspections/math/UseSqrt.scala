package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.{Levels, Feedback, Inspection}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class UseSqrt extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    private def isOneOverTwo(value: Any): Boolean = value match {
      case i: Int => i == 1
      case _ => false
    }

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(pack, TermName("pow")), List(_, Literal(Constant(0.5d))))
          if pack.toString() == "java.this.lang.Math" =>
          feedback.warn("Use Math.sqrt", tree.pos, Levels.Info,
            "Math.sqrt is clearer and more performance than Math.pow(x, 0.5)")
        case _ => super.traverse(tree)
      }
    }
  }

}
