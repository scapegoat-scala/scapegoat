package com.sksamuel.scapegoat.inspections

/** @author Stephen Samuel */
class FloatingPointEqualityInspection extends Goat {

  import scala.reflect.runtime.{universe => u}

  /**
   * Analyze the given tree and report any problems.
   */
  override def analyze(tree: u.Tree, reporter: _root_.com.sksamuel.scapegoat.Reporter): Unit = {}
  override def description: String = ""
}
