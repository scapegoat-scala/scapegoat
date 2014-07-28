package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Reporter}

/** @author Stephen Samuel */
class BlockingActor extends Inspection {

  import scala.reflect.runtime.universe._

  // todo check for Await.result and Await.ready in actor's receive
  override def traverser(reporter: Reporter) = new Traverser {

    override def traverse(tree: Tree): Unit = {
      tree match {
        case _ => super.traverse(tree)
      }
    }
  }
}