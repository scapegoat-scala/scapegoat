package com.sksamuel.scapegoat.inspections

/** @author Stephen Samuel */
object ComparingUnrelatedTypes extends Goat {

  import scala.reflect.runtime.{universe => u}

  override def description: String = "Detects comparisons of expressions which cannot be of the same type."

  /**
   * Analyze the given tree and report any problems.
   */
  override def analyze(tree: u.Tree, reporter: _root_.com.sksamuel.scapegoat.Reporter): Unit = {
    tree match {
      case u.Select(qualifier, name) =>
        val c = qualifier
        println(qualifier + " : " + name)
      case _ =>
        tree.children.foreach(analyze(_, reporter))
    }
  }
}
