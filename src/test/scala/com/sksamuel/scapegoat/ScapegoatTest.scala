package com.sksamuel.scapegoat

import com.sksamuel.scapegoat._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class ScapegoatTest extends AnyFreeSpec with Matchers {

  "ScalaVersionPattern" - {
    "shoud extract components" in {
      extractComponents("2.13.12") shouldBe (2, 13, 12)
    }
  }
}
