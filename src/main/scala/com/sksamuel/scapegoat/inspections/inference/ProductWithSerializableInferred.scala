package com.sksamuel.scapegoat.inspections.inference

import com.sksamuel.scapegoat.{ Levels, Inspection, InspectionContext, Inspector }

import scala.reflect.internal.Flags

/** @author Stephen Samuel */
class ProductWithSerializableInferred extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Product = typeOf[Product]
      private val Serializable = typeOf[Serializable]
      private val Obj = typeOf[Object]

      private def isProductWithSerializable(tpe: Type): Boolean = {
        tpe.typeArgs match {
          case List(RefinedType(List(Product, Serializable, Obj), decls)) => true
          case _ => false
        }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case ValDef(mods, _, _, _) if mods.hasFlag(Flags.SYNTHETIC) =>
          case ValDef(mods, name, tpt, rhs) if isProductWithSerializable(tpt.tpe) =>
            context.warn("Product with Serializable inferred", tree.pos, Levels.Warning,
              "It is unlikely that this was your target type: " + tree.toString().take(300),
              ProductWithSerializableInferred.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
