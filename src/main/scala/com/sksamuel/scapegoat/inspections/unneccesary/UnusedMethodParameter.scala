package com.sksamuel.scapegoat.inspections.unneccesary

import scala.reflect.internal.Flags

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class UnusedMethodParameter
    extends Inspection(
      text = "Unused parameter",
      defaultLevel = Levels.Warning,
      description = "Checks for unused method parameters.",
      explanation = "Unused constructor or method parameters should be removed."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._
          import definitions._

          private def usesParameter(paramName: String, tree: Tree): Boolean = {
            tree match {
              case Ident(TermName(name)) =>
                name == paramName
              case _ =>
                tree.children.exists(usesParameter(paramName, _))
            }
          }

          private def usesField(paramName: String, tree: Tree): Boolean = {
            tree match {
              case Select(This(_), TermName(name)) =>
                // FIXME: why is "trim" needed here? Is that a scalac bug?
                // A test will fail if you take this out!
                name.trim == paramName
              case _ =>
                tree.children.exists(usesField(paramName, _))
            }
          }

          private def isParameterExcused(param: ValDef): Boolean =
            param.symbol.annotations.exists(_.atp.toString == "scala.annotation.unused")

          /**
           * For constructor params, some params become vals / fields of the class:
           *   1. all params in the first argument list for case classes
           *   1. all params marked "val"
           *
           * In both cases, by the time we see the tree, a "def x = this.x" method will have been added by the
           * compiler, so "usesField" will notice and not mark the param as unused.
           */
          private def checkConstructor(
            vparamss: List[List[ValDef]],
            constructorBody: Tree,
            classBody: Tree
          ): Unit = {

            for {
              vparams <- vparamss
              vparam  <- vparams
            } {
              val paramName = vparam.name.toString
              if (!usesParameter(paramName, constructorBody) && !usesField(paramName, classBody))
                context.warn(vparam.pos, self, s"Unused constructor parameter (${vparam.name}).")
            }
          }

          override def inspect(tree: Tree): Unit = {
            tree match {
              // ignore traits, quite often you define a method in a trait with default impl that does nothing
              case ClassDef(_, _, _, _) if tree.symbol.isTrait     =>
              case ClassDef(mods, _, _, _) if mods.hasAbstractFlag =>
              case ClassDef(_, _, _, classBody @ Template(_, _, classTopLevelStmts)) =>
                classTopLevelStmts.foreach {
                  case DefDef(_, nme.CONSTRUCTOR, _, vparamss, _, constructorBody) =>
                    checkConstructor(vparamss, constructorBody, classBody)
                  case DefDef(_, _, _, vparamss, _, constructorBody)
                      if tree.symbol != null && tree.symbol.isConstructor =>
                    checkConstructor(vparamss, constructorBody, classBody)
                  case _ =>
                }
                continue(tree)

              // ignore abstract methods obv.
              case DefDef(mods, _, _, _, _, _) if mods.hasFlag(Flag.ABSTRACT)              =>
              case d @ DefDef(_, _, _, _, _, _) if d.symbol != null && d.symbol.isAbstract =>
              // ignore constructors, they're handled above
              case DefDef(_, nme.CONSTRUCTOR, _, _, _, _)                                       =>
              case DefDef(_, _, _, _, _, _) if tree.symbol != null && tree.symbol.isConstructor =>
              // ignore methods that just throw, e.g. "???"
              case DefDef(_, _, _, _, tpt, _) if tpt.tpe =:= NothingTpe =>
              // ignore methods that just throw, e.g. "???" or "js.native"
              case DefDef(_, _, _, _, _, rhs) if rhs.tpe =:= NothingTpe =>
              // ignore overridden methods, the parameter might be used by other classes
              case DefDef(mods, _, _, _, _, _)
                  if mods.isOverride ||
                    mods.hasFlag(Flags.OVERRIDE) ||
                    (tree.symbol != null && (tree.symbol.isAnyOverride || tree.symbol.isOverridingSymbol)) =>
              // ignore main method
              case DefDef(_, name, _, List(List(param)), tpt, _)
                  if name.toString == "main" &&
                    param.name.toString == "args" &&
                    tpt.tpe =:= UnitTpe &&
                    param.tpt.tpe =:= typeOf[Array[String]] =>
              case DefDef(_, _, _, vparamss, _, rhs) =>
                for {
                  vparams <- vparamss
                  vparam  <- vparams
                } if (!isParameterExcused(vparam) && !usesParameter(vparam.name.toString, rhs)) {
                  context.warn(tree.pos, self, s"Unused method parameter ($vparam).")
                }
              case _ => continue(tree)
            }
          }
        }
    }
}
