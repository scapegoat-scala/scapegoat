package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat._

class VarUse
    extends Inspection(
      text = "Use of var",
      defaultLevel = Levels.Warning,
      description = "Checks for use of variables (var).",
      explanation =
        "Use of variables is generally discouraged, especially in the context of a shared mutable state. Consider using an immutable state or other referentially transparent alternatives."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser =
        new context.Traverser {

          import context.global._

          private val xmlLiteralClassNames = Seq("scala.xml.NamespaceBinding", "scala.xml.MetaData")
          private def isXmlLiteral(tpe: Type) = xmlLiteralClassNames.contains(tpe.typeSymbol.fullName)
          private def isActor(tree: Tree): Boolean = tree.toString == "akka.actor.Actor"

          override def inspect(tree: Tree): Unit = {
            tree match {
              case ClassDef(_, _, _, Template(parents, _, _)) if parents.exists(isActor) =>
              case ModuleDef(_, _, Template(parents, _, _)) if parents.exists(isActor)   =>
              case ValDef(mods, _, _, _) if mods.isSynthetic || mods.isMacro             =>
              case ValDef(_, _, tpt, _) if isXmlLiteral(tpt.tpe)                         =>
              case ValDef(modifiers, _, _, _) if modifiers.hasFlag(Flag.MUTABLE) =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case _ => continue(tree)
            }
          }
        }
    }
}
