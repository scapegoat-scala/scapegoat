package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/** @author Matic Potočnik */
class UseLog1P extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._
      
      def isMathPackage(pack: String) = 
          (pack == "scala.math.`package`"
        || pack == "java.this.lang.Math"
        || pack == "java.this.lang.StrictMath" || true)

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(pack, TermName("log")), List(Apply(Select(Literal(Constant(1)), nme.ADD), _))) 
            if isMathPackage(pack.toString()) => 
            val math = pack.toString().stripSuffix(".`package`").substring(pack.toString().lastIndexOf('.'))
            context.warn(s"Use ${math}.log1p", tree.pos, Levels.Info,
              s"${math}.log1p(x) is clearer and more performant than ${math}.log(1 + x)",
              UseLog1P.this)

          case Apply(Select(pack, TermName("log")), List(Apply(Select(_, nme.ADD), List(Literal(Constant(1))))))
            if isMathPackage(pack.toString()) => 
            val math = pack.toString().stripSuffix(".`package`").substring(pack.toString().lastIndexOf('.'))
            context.warn(s"Use ${math}.log1p", tree.pos, Levels.Info,
              s"${math}.log1p(x) is clearer and more performant than ${math}.log(x + 1)",
              UseLog1P.this)

          case _ => continue(tree)
        }
      }
    }
  }
}
