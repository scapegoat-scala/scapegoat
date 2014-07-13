package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.Reporter

/**
 * An Inspection is a bad practice. It can be a bug, an incorrect idiom, bad practice, incorrect code,
 * hard to read source, or anything along these lines. It's more than a bug, it's a goat.
 *
 * It is something that should be avoided.
 *
 * @author Stephen Samuel
 **/
trait Goat {

  import scala.reflect.runtime.{universe => u}

  /**
   * Analyze the given tree and report any problems.
   */
  def analyze(tree: u.Tree, reporter: Reporter): Unit

  def description: String
}
