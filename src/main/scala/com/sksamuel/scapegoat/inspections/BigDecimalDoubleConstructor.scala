package com.sksamuel.scapegoat
package inspections

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class BigDecimalDoubleConstructor extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._
    import definitions.{DoubleClass, FloatClass}

    def isBigDecimal(pack: Tree) =
      pack.toString == "scala.`package`.BigDecimal" || pack.toString == "java.math.BigDecimal"
    def isFloatingPointType(tree: Tree) = tree.tpe <:< FloatClass.tpe || tree.tpe <:< DoubleClass.tpe

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(pack, TermName("apply")), arg :: tail) if isBigDecimal(pack) && isFloatingPointType(arg) =>
          feedback
            .warn("Big decimal double constructor",
              tree.pos,
              Levels.Warning,
              "The results of this constructor can be somewhat unpredictable. " +
                "Eg, writing new BigDecimal(0.1) in Java creates a BigDecimal which is actually equal to 0.1000000000000000055511151231257827021181583404541015625. " +
                "This is because 0.1 cannot be represented exactly as a double. " + tree.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }
}
