package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ParameterlessMethodReturnsUnit extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case d @ DefDef(_, name, _, vparamss, tpt, _) if tpt.tpe.toString == "Unit" && vparamss.isEmpty =>
            context.warn("Parameterless methods returns unit",
              tree.pos,
              Levels.Warning,
              "Methods should be defined with () if they have side effects. A method returning unit must have side effects, otherwise it can be removed. " + name
                .toString
                .take(300),
              ParameterlessMethodReturnsUnit.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
