package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author Stephen Samuel
 *
 *         Inspired by Intellij
 */
class ExistsSimplifiableToContains
    extends Inspection(
      text = "Exists simplifiable to contains",
      defaultLevel = Levels.Info,
      description = "Checks if `exists()` can be simplified to `contains()`.",
      explanation = "`exists(x => x == y)` can be replaced with `contains(y)`."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser =
        new context.Traverser {

          import context.global._

          private val Equals = TermName("$eq$eq")

          private def doesElementTypeMatch(container: Tree, value: Tree): Boolean = {
            val valueType = value.tpe.underlying.typeSymbol.tpe
            val traversableType = container.tpe.underlying.baseType(typeOf[Traversable[Any]].typeSymbol)
            traversableType.typeArgs.exists(t => valueType <:< t || valueType =:= t)
          }

          private def isContainsTraversable(tree: Tree): Boolean =
            // Traversable itself doesn't include a .contains() method
            isSet(tree) || isSeq(tree) || isList(tree) || isMap(tree)

          private def countUsagesOfAVariable(trees: List[Tree], symbolName: String): Int = {
            trees.map {
              case Select(Ident(TermName(termName)), _) if termName == symbolName =>
                1
              case tree =>
                countUsagesOfAVariable(tree.children, symbolName)
            }.sum
          }

          override def inspect(tree: Tree): Unit = {
            tree match {

              case Apply(
                    Select(lhs, TermName("exists")),
                    List(
                      Function(
                        List(ValDef(_, TermName(iterationVariable), _, _)),
                        subtree @ Apply(Select(_, Equals), List(x))
                      )
                    )
                  )
                  if isContainsTraversable(lhs) && doesElementTypeMatch(lhs, x)
                  && countUsagesOfAVariable(List(subtree), iterationVariable) == 1 =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
