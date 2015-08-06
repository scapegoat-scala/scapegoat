package com.sksamuel.scapegoat.inspections.akka

import com.sksamuel.scapegoat.test.ScapegoatTestPluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class AkkaSenderClosureTest extends FreeSpec with Matchers with ScapegoatTestPluginRunner with OneInstancePerTest {

  override val inspections = Seq(new AkkaSenderClosure)

}
