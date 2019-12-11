package com.sksamuel.scapegoat.inspections.inference

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

import scala.reflect.internal.Flags

/** @author Stephen Samuel */
class ProductWithSerializableInferred extends Inspection("Product with Serializable inferred", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Product = typeOf[Product]
      private val Serializable = typeOf[Serializable]
      private val Obj = typeOf[Object]

      private def isProductWithSerializable(tpe: Type): Boolean = {
        tpe.typeArgs match {
          case List(RefinedType(parents, decls)) if parents.size == 3 =>
            Seq(Product, Serializable, Obj).forall(t => parents.exists(_ =:= t))

          case _ => false
        }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case ValDef(mods, _, _, _) if mods.hasFlag(Flags.SYNTHETIC) =>
          case ValDef(mods, name, tpt, rhs) if isProductWithSerializable(tpt.tpe) =>
            context.warn(tree.pos, self,
              "It is unlikely that this was your target type: " + tree.toString().take(300))
          case _ => continue(tree)
        }
      }
    }
  }
}
