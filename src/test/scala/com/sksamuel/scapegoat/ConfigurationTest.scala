package com.sksamuel.scapegoat

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class ConfigurationTest extends AnyFreeSpec with Matchers {

  "Configuration" - {
    "should list all possible options in its help" - {
      // NOTE: also enforcing the internal field name is the same as the public one

      val existingOptions = classOf[Configuration].getDeclaredFields.map(_.getName)

      existingOptions.foreach { option =>
        s"$option should be listed in help" in {
          Configuration.optionsHelp.contains(s"-P:scapegoat:$option:") shouldBe true
        }
      }
    }

    "throw an exception on 'ignore' as minimal level" in {
      the[IllegalArgumentException] thrownBy Configuration.fromPluginOptions(List("minimalLevel:ignore"))
    }
  }
}
