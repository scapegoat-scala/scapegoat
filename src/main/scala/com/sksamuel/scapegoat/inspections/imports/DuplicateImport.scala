package com.sksamuel.scapegoat.inspections.imports

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

import scala.collection.mutable

/** @author Stephen Samuel */
class DuplicateImport extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {

    private val imports = mutable.HashSet[String]()

    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case PackageDef(_, _) =>
            imports.clear(); continue(tree)
          case ModuleDef(_, _, _) =>
            imports.clear(); continue(tree)
          case ClassDef(_, _, _, _) =>
            imports.clear(); continue(tree)
          case Import(expr, selectors) =>
            selectors.foreach(selector => {
              val name = expr.toString + "." + selector.name
              if (imports.contains(name)) {
                context.warn("Duplicated Import", tree.pos, Levels.Info, name, DuplicateImport.this)
              }
              imports.add(name)
            })
          case DefDef(_, _, _, _, _, _) => // check imports inside defs
          case _                        => continue(tree)
        }
      }
    }
  }
}

