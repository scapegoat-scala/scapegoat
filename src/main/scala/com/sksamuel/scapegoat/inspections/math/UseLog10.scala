package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/** @author Matic Potočnik */
class UseLog10 extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Apply(Select(pack1, TermName("log")), List(number)), nme.DIV), List(Apply(Select(pack2, TermName("log")), List(Literal(Constant(10.0)))))) 
            if(pack1.toString() == "scala.math.`package`"
            || pack1.toString() == "java.this.lang.Math"
            || pack1.toString() == "java.this.lang.StrictMath")
            && pack2.toString() == pack1.toString() =>
            val math = pack1.toString().stripSuffix(".`package`").substring(pack2.toString().lastIndexOf('.'))
            context.warn(s"Use ${math}.log10", tree.pos, Levels.Info,
              s"${math}.log10(x) is clearer and more performant than ${math}.log(x)/${math}.log(10)",
              UseLog10.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
