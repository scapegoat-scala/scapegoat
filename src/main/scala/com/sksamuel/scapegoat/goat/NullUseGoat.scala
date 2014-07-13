package com.sksamuel.scapegoat.goat

import com.sksamuel.scapegoat.ScapegoatUniverse

/** @author Stephen Samuel */
object NullUseGoat extends ScapegoatUniverse {
  override def traverser: ScapegoatTraverser = new ScapegoatTraverser {
    override def traverse(tree: NullUseGoat.universe.Tree): Unit = {
      println(tree + " " + tree.symbol)
      super.traverse(tree)
    }
  }
}
