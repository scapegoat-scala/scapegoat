package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class PublicFinalizer
    extends Inspection(
      text = "PublicFinalizer",
      defaultLevel = Levels.Info,
      description = "Checks for overridden finalizes that are public.",
      explanation = "Public finalizer should be avoided as finalizers should not be programmatically invoked."
    ) {

  override def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {

      import context.global._

      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          override def inspect(tree: Tree): Unit = {
            tree match {
              case DefDef(mods, TermName("finalize"), Nil, Nil, tpt, _)
                  if mods.isPublic && tpt.tpe <:< typeOf[Unit] =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
