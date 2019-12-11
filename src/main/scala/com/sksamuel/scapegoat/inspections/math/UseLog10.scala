package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

class UseLog10 extends Inspection("Use log10", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {

        def isMathPackage(pack: String) =
          (pack == "scala.math.package"
            || pack == "java.lang.Math"
            || pack == "java.lang.StrictMath")

        tree match {
          case Apply(Select(Apply(Select(pack1, TermName("log")), List(_)), nme.DIV), List(Apply(Select(pack2, TermName("log")), List(Literal(Constant(10.0))))))
            if isMathPackage(pack1.symbol.fullName) && isMathPackage(pack2.symbol.fullName) =>
            val math = pack1.toString().stripSuffix(".package").substring(pack2.toString().lastIndexOf('.'))
            context.warn(tree.pos, self,
              s"$math.log10(x) is clearer and more performant than $math.log(x)/$math.log(10)")
          case _ => continue(tree)
        }
      }
    }
  }
}
