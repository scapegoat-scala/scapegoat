package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

import scala.collection.mutable

/** @author Stephen Samuel */
class TypeShadowing extends Inspection("Type shadowing", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def checkShadowing(tparams: List[TypeDef], trees: List[Tree]): Unit = {
        val types = mutable.HashSet[String]()
        tparams.foreach(tparam => types.add(tparam.name.toString))
        trees.foreach {
          case dd: DefDef if dd.symbol != null && dd.symbol.isSynthetic =>
          case dd @ DefDef(_, name, deftparams, _, _, _) =>
            deftparams.foreach(tparam => {
              if (types.contains(tparam.name.toString))
                warn(dd, name, tparam)
            })
          case ClassDef(_, _, tparams2, Template(_, _, body)) => checkShadowing(tparams2, body)
          case _ =>
        }
      }

      private def warn(dd: DefDef, name: TermName, tparam: TypeDef): Unit = {
        context.warn(dd.pos, self, s"Method $name declares shadowed type parameter ${tparam.name}")
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case ClassDef(_, _, tparams, Template(_, _, body)) => checkShadowing(tparams, body)
          case _ => continue(tree)
        }
      }
    }
  }
}

