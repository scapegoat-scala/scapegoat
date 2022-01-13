package com.sksamuel.scapegoat.inspections

import scala.collection.mutable

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class TypeShadowing
    extends Inspection(
      text = "Type shadowing",
      defaultLevel = Levels.Warning,
      description = "Checks for shadowed type parameters in methods.",
      explanation = "Shadowing type parameters is considered a bad practice and should be avoided."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def checkShadowing(tparams: List[TypeDef], trees: List[Tree]): Unit = {
            val types = mutable.HashSet[String]()
            tparams.foreach(tparam => types.add(tparam.name.toString))
            trees.foreach {
              case dd: DefDef if dd.symbol != null && dd.symbol.isSynthetic =>
              case dd @ DefDef(_, _, deftparams, _, _, _) =>
                deftparams.foreach { tparam =>
                  if (types.contains(tparam.name.toString))
                    context.warn(dd.pos, self)
                }
              case ClassDef(_, _, tparams2, Template(_, _, body)) => checkShadowing(tparams2, body)
              case _                                              =>
            }
          }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case ClassDef(_, _, tparams, Template(_, _, body)) => checkShadowing(tparams, body)
              case _                                             => continue(tree)
            }
          }
        }
    }
}
