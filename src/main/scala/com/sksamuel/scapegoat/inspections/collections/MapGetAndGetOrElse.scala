package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author Sanjiv Sahayam
 *
 * Inspired by Intellij inspection that does:
 *   myMap.get(key).getOrElse(defaultValue) –> myMap.getOrElse(key, defaultValue)
 */
class MapGetAndGetOrElse extends Inspection("Use of .get.getOrElse instead of .getOrElse", Levels.Error) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def isMap(tree: Tree): Boolean = tree.tpe <:< typeOf[scala.collection.MapLike[_, _, _]]

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(Apply(Select(left, TermName("get")), List(key)),
            TermName("getOrElse")), _), List(defaultValue)) if isMap(left) =>
            context.warn(tree.pos, self,
              s"Use of .get($key).getOrElse($defaultValue) instead of getOrElse($key, $defaultValue): " + tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}
