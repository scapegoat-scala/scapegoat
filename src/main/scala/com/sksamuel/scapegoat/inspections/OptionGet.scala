package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Reporter, Inspection}

import scala.reflect.runtime._
import scala.reflect.runtime.universe._

/** @author Stephen Samuel */
class OptionGet extends Inspection {
  val optionSymbol = rootMirror.staticClass("scala.Option")
  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Select(left, TermName("get")) =>
          if (left.tpe.typeSymbol.fullName == optionSymbol.asType.fullName)
            reporter.warn("Use of Option.get", tree.pos.line, level = Levels.Major)
        case _ => super.traverse(tree)
      }
    }
  }
}
