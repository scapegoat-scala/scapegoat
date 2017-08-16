package com.sksamuel.scapegoat

import scala.reflect.internal.util.Position
import scala.tools.nsc.Global

/** @author Stephen Samuel */
trait Inspection {
  def inspector(context: InspectionContext): Inspector
}

abstract class Inspector(val context: InspectionContext) {

  /**
   * This traverser, if defined, is invoked after the parser phase of the compiler has returned.
   */
  def postParseTraverser: Option[context.Traverser] = None

  /**
   * This traverser, if defined, is invoked after the typer phase of the compiler has returned.
   */
  def postTyperTraverser: Option[context.Traverser] = None

  /**
   * This traverser, if defined, is invoked after the refChecks phase of the compiler has returned.
   */
  def postRefChecksTraverser: Option[context.Traverser] = None

  /**
   * This method is invoked after all phases of the compiler have completed.
   * This method can be used to clean up inspections; to report errors after all phases are complete.
   */
  def postInspection(): Unit = ()
}

case class InspectionContext(global: Global, feedback: Feedback) {

  def warn(text: String, pos: Position, level: Level, inspection: Inspection): Unit = {
    feedback.warn(text, pos, level, inspection)
  }
  def warn(text: String, pos: Position, level: Level, snippet: String, inspection: Inspection): Unit = {
    feedback.warn(text, pos, level, snippet, inspection)
  }

  trait Traverser extends global.Traverser {

    import global._

    private val SuppressWarnings = typeOf[SuppressWarnings]
    private val Safe = typeOf[Safe]

    private def inspectionClass(klass: Class[_]): Class[_] = Option(klass.getEnclosingClass) match {
      case None    => klass
      case Some(k) => inspectionClass(k)
    }

    private def isAllDisabled(an: AnnotationInfo) = {
      an.javaArgs.head._2.toString.toLowerCase.contains("\"all\"")
    }

    private def isThisDisabled(an: AnnotationInfo) = {
      an.javaArgs.head._2.toString.toLowerCase.contains(inspectionClass(getClass).getSimpleName.toLowerCase)
    }

    private def isSkipAnnotation(an: AnnotationInfo) = an.tree.tpe =:= SuppressWarnings || an.tree.tpe =:= Safe

    private def isSuppressed(symbol: Symbol) = {
      symbol != null &&
        symbol.annotations.exists(an => isSkipAnnotation(an) && (isAllDisabled(an) || isThisDisabled(an)))
    }

    protected def continue(tree: Tree) = super.traverse(tree)

    protected def inspect(tree: Tree): Unit

    override final def traverse(tree: Tree): Unit = {
      tree match {
        // ignore synthetic methods added
        case DefDef(mods, _, _, _, _, _) if tree.symbol.isSynthetic =>
        case dd @ DefDef(_, _, _, _, _, _) if isSuppressed(dd.symbol) =>
        case block @ Block(_, _) if isSuppressed(block.symbol) =>
        case iff @ If(_, _, _) if isSuppressed(iff.symbol) =>
        case tri @ Try(_, _, _) if isSuppressed(tri.symbol) =>
        case mod: ModuleDef if isSuppressed(mod.symbol) =>
        case ClassDef(_, _, _, Template(parents, _, _)) if parents.map(_.tpe.typeSymbol.fullName).contains("scala.reflect.api.TypeCreator") =>
        case classdef: ClassDef if isSuppressed(classdef.symbol) =>
        case _ if analyzer.hasMacroExpansionAttachment(tree) => //skip macros as per http://bit.ly/2uS8BrU
        case _ => inspect(tree)
      }
    }
  }
}

