package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/** @author Matic Potočnik */
class UseCbrt extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(pack, TermName("pow")), List(_, Literal(Constant(third: Double))))
            if(pack.toString() == "scala.math.`package`"
            || pack.toString() == "java.this.lang.Math"
            || pack.toString() == "java.this.lang.StrictMath")
            && third >= 0.3333332
            && third <= 0.3333334 =>
            val math = pack.toString().stripSuffix(".`package`").substring(pack.toString().lastIndexOf('.'))
            context.warn(s"Use ${math}.cbrt", tree.pos, Levels.Info,
              s"${math}.cbrt is clearer and more performant than ${math}.pow(x, 1/3)",
              UseCbrt.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
