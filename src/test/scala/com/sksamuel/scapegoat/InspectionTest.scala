package com.sksamuel.scapegoat

import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

abstract class InspectionTest extends AnyFreeSpec with Matchers with PluginRunner with OneInstancePerTest {}
