package com.sksamuel.scapegoat

import scala.reflect.internal.util.Position
import scala.tools.nsc.Global

/**
 * @author
 *   Stephen Samuel
 */
abstract class Inspection(
  val text: String,
  val defaultLevel: Level,
  val description: String,
  val explanation: String
) {

  val self: Inspection = this

  def inspector(context: InspectionContext): Inspector

  def isEnabled: Boolean = true

  def name: String = getClass.getSimpleName
}

abstract class Inspector(val context: InspectionContext) {

  /**
   * This traverser, if defined, is invoked after the typer phase of the compiler has returned.
   */
  def postTyperTraverser: context.Traverser

  /**
   * This method is invoked after all phases of the compiler have completed. This method can be used to clean
   * up inspections; to report errors after all phases are complete.
   */
  def postInspection(): Unit = ()
}

final case class InspectionContext(global: Global, feedback: Feedback) {

  def warn(pos: Position, inspection: Inspection): Unit =
    feedback.warn(pos, inspection, None, None)

  def warn(pos: Position, inspection: Inspection, snippet: String): Unit =
    feedback.warn(pos, inspection, Some(snippet), None)

  def warn(
    pos: Position,
    inspection: Inspection,
    snippet: String,
    adhocExplanation: String
  ): Unit =
    feedback.warn(pos, inspection, Some(snippet), Some(adhocExplanation))

  trait Traverser extends global.Traverser {

    import global._

    private val SuppressWarnings = typeOf[SuppressWarnings]

    @scala.annotation.tailrec
    private def inspectionClass(klass: Class[_]): Class[_] =
      Option(klass.getEnclosingClass) match {
        case None    => klass
        case Some(k) => inspectionClass(k)
      }

    private def isThisDisabled(an: AnnotationInfo): Boolean = {
      val cls = inspectionClass(getClass)
      val names = Set("all", cls.getSimpleName, cls.getCanonicalName).map(_.toLowerCase)
      val suppressedNames: Seq[String] = an.javaArgs.values.headOption.toSeq.flatMap {
        case ArrayAnnotArg(args) =>
          args.collect { case LiteralAnnotArg(Constant(suppressedName: String)) =>
            suppressedName.toLowerCase
          }
        case _ =>
          Seq.empty[String]
      }
      names.intersect(suppressedNames.toSet).nonEmpty
    }

    private def isSkipAnnotation(an: AnnotationInfo) =
      // Workaround for #222: we can't use typeOf[Safe] here it requires Scapegoat to be on the
      // compile classpath.
      an.tree.tpe =:= SuppressWarnings || an.tree.tpe.erasure.toString == "com.sksamuel.scapegoat.Safe"

    private def isSuppressed(symbol: Symbol) =
      symbol != null && symbol.annotations.exists(an => isSkipAnnotation(an) && isThisDisabled(an))

    protected def continue(tree: Tree): Unit = super.traverse(tree)

    protected def inspect(tree: Tree): Unit

    override final def traverse(tree: Tree): Unit = {
      tree match {
        // ignore synthetic methods added
        case DefDef(_, _, _, _, _, _) if tree.symbol.isSynthetic =>
        case ValDef(_, _, _, _) if tree.symbol.isSynthetic       =>
        case member: MemberDef if isSuppressed(member.symbol)    =>
        case block @ Block(_, _) if isSuppressed(block.symbol)   =>
        case iff @ If(_, _, _) if isSuppressed(iff.symbol)       =>
        case tri @ Try(_, _, _) if isSuppressed(tri.symbol)      =>
        case ClassDef(_, _, _, Template(parents, _, _))
            if parents.map(_.tpe.typeSymbol.fullName).contains("scala.reflect.api.TypeCreator") =>
        case _ if analyzer.hasMacroExpansionAttachment(tree) => // skip macros as per http://bit.ly/2uS8BrU
        case _                                               => inspect(tree)
      }
    }

    protected def isArray(tree: Tree): Boolean = tree.tpe.typeSymbol.fullName == "scala.Array"
    protected def isIterable(tree: Tree): Boolean = tree.tpe <:< typeOf[Iterable[Any]]
    protected def isSeq(t: Tree): Boolean = t.tpe <:< typeOf[Seq[Any]]
    protected def isIndexedSeq(t: Tree): Boolean = t.tpe <:< typeOf[IndexedSeq[Any]]
    protected def isSet(t: Tree, allowMutableSet: Boolean = true): Boolean = {
      t.tpe.widen.baseClasses.exists { c =>
        (allowMutableSet && c.fullName == "scala.collection.mutable.Set") || c.fullName == "scala.collection.immutable.Set"
      }
    }
    protected def isList(t: Tree): Boolean = t.tpe <:< typeOf[scala.collection.immutable.List[Any]]
    protected def isMap(tree: Tree): Boolean =
      tree.tpe.baseClasses.exists {
        _.fullName == "scala.collection.Map"
      }
  }
}
