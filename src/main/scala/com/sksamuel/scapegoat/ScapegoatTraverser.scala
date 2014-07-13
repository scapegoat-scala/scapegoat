package com.sksamuel.scapegoat

/** @author Stephen Samuel */
trait ScapegoatTraverser {
  import scala.reflect.runtime.universe
  def traverser(reporter: Reporter): universe.Traverser
}
