package com.sksamuel.scapegoat.inspections

import scala.collection.mutable

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class LonelySealedTrait
    extends Inspection(
      text = "Lonely sealed trait",
      defaultLevel = Levels.Error,
      description = "Checks for sealed traits without any classes extending it.",
      explanation = "A sealed trait that is not extended is considered dead code."
    ) {

  override def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {

      import context.global._

      private val sealedClasses = mutable.HashMap[String, ClassDef]()
      private val implementedClasses = mutable.HashSet[String]()

      override def postInspection(): Unit = {
        for ((name, cdef) <- sealedClasses)
          if (!implementedClasses.contains(name))
            context.warn(cdef.pos, self)
      }

      private def inspectParents(parents: List[Tree]): Unit = {
        parents.foreach { parent =>
          for (c <- parent.tpe.baseClasses)
            implementedClasses.add(c.name.toString)
        }
      }

      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          override def inspect(tree: Tree): Unit = {
            tree match {
              case cdef @ ClassDef(mods, _, _, _) if mods.isSealed =>
                sealedClasses.put(cdef.name.toString, cdef)
              case ClassDef(_, _, _, Template(parents, _, _)) => inspectParents(parents)
              case ModuleDef(_, _, Template(parents, _, _))   => inspectParents(parents)
              case _                                          =>
            }
            continue(tree)
          }
        }
    }
}
