package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/** @author Matic Potočnik */
class UseExpM1 extends Inspection("Use expm1", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Apply(Select(pack, TermName("exp")), List(_)), nme.SUB), List(Literal(Constant(1)))) =>
            val math = pack.toString().stripSuffix(".`package`").substring(pack.toString().lastIndexOf('.'))
            context.warn(tree.pos, self,
              s"$math.expm1(x) is clearer and more performant than $math.exp(x) - 1")

          case _ => continue(tree)
        }
      }
    }
  }
}
