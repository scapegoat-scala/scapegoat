package com.sksamuel.scapegoat.inspections.inference

import scala.reflect.internal.Flags

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class ProductWithSerializableInferred
    extends Inspection(
      text = "Product with Serializable inferred",
      defaultLevel = Levels.Warning,
      description = "Checks for values that have Product with Serializable as their inferred type.",
      explanation =
        "It is unlikely that Product with Serializable was your target type. This is often an indication of mixing up incompatible types."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val Product = typeOf[Product]
          private val Serializable = typeOf[Serializable]
          private val Obj = typeOf[Object]

          private def isProductWithSerializable(tpe: Type): Boolean = {
            tpe.typeArgs match {
              case List(RefinedType(parents, _)) if parents.size == 3 =>
                Seq(Product, Serializable, Obj).forall(t => parents.exists(_ =:= t))

              case _ => false
            }
          }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case ValDef(mods, _, _, _) if mods.hasFlag(Flags.SYNTHETIC) =>
              case ValDef(_, _, tpt, _) if isProductWithSerializable(tpt.tpe) =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case _ => continue(tree)
            }
          }
        }
    }
}
