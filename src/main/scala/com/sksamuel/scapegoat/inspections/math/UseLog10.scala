package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

class UseLog10 extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {

        def isMathPackage(pack: String) =
          (pack == "scala.math.package"
            || pack == "java.lang.Math"
            || pack == "java.lang.StrictMath")

        tree match {
          case Apply(Select(Apply(Select(pack1, TermName("log")), List(number)), nme.DIV), List(Apply(Select(pack2, TermName("log")), List(Literal(Constant(10.0))))))
            if isMathPackage(pack1.symbol.fullName) && isMathPackage(pack2.symbol.fullName) =>
            val math = pack1.toString().stripSuffix(".package").substring(pack2.toString().lastIndexOf('.'))
            context.warn(s"Use $math.log10", tree.pos, Levels.Info,
              s"$math.log10(x) is clearer and more performant than $math.log(x)/$math.log(10)",
              UseLog10.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
