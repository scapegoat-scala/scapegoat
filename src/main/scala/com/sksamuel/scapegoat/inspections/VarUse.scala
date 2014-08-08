package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class VarUse extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      private val xmlLiteralClassNames = Seq("scala.xml.NamespaceBinding", "scala.xml.MetaData")
      private def isXmlLiteral(tpe: Type) = xmlLiteralClassNames.contains(tpe.typeSymbol.fullName)

      override def inspect(tree: Tree): Unit = {
        tree match {
          case ValDef(mods, _, _, _) if mods.isSynthetic || mods.isMacro =>
          case ValDef(_, _, tpt, _) if isXmlLiteral(tpt.tpe) =>
          case v@ValDef(modifiers, name, tpt, rhs) if modifiers.hasFlag(Flag.MUTABLE) =>
            context.warn("Use of var", tree.pos, Levels.Warning, "var used: " + tree.toString().take(300), VarUse.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
