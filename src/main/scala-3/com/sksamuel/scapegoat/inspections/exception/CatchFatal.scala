package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Types.Type
import dotty.tools.dotc.util.SourcePosition

class CatchFatal
    extends Inspection(
      text = "Catch fatal exception",
      defaultLevel = Levels.Warning,
      description =
        "Checks for try blocks that catch fatal exceptions: VirtualMachineError, ThreadDeath, InterruptedException, LinkageError, ControlThrowable.",
      explanation =
        "Did you intend to catch a fatal exception? Consider using scala.util.control.NonFatal instead."
    ) {

  import tpd.*

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def isFatal(tpe: Type): Boolean = {
        val fullName = tpe.typeSymbol.fullName.toString
        fullName == "java.lang.VirtualMachineError" ||
        fullName == "java.lang.ThreadDeath" ||
        fullName == "java.lang.InterruptedException" ||
        fullName == "java.lang.LinkageError" ||
        fullName == "scala.util.control.ControlThrowable"
      }

      def catchesFatal(cases: List[CaseDef]): Boolean = {
        cases.exists {
          // matches t : FatalException
          case CaseDef(Bind(_, Typed(_, tpt)), _, _) if isFatal(tpt.tpe) => true
          // matches _ : FatalException
          case CaseDef(Typed(_, tpt), _, _) if isFatal(tpt.tpe) => true
          case _                                                => false
        }
      }

      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case Try(_, cases, _) if catchesFatal(cases) =>
            feedback.warn(tree.sourcePos, self, tree.asSnippet.map(_.take(300)))
          case _ => traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}
