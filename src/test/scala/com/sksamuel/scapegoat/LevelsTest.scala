package com.sksamuel.scapegoat

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class LevelsTest extends AnyFreeSpec with Matchers {
  "Levels" - {
    "#fromName" - {
      "should return correct object" - {
        "for 'error'" in {
          Levels.fromName("error") should be(Levels.Error)
        }

        "for 'warning'" in {
          Levels.fromName("warning") should be(Levels.Warning)
        }

        "for 'info'" in {
          Levels.fromName("info") should be(Levels.Info)
        }

        "for 'ignore'" in {
          Levels.fromName("ignore") should be(Levels.Ignore)
        }
      }

      "throw an exception when uunknown level is provided" in {
        the[IllegalArgumentException] thrownBy Levels.fromName(
          "UNKNOWN"
        ) should have message "Unrecognised level 'UNKNOWN'"
      }
    }
  }

  "Level" - {
    "#higherOrEqual" - {
      "should be true for levels with higher weight" - {
        val levels = Seq(Levels.Ignore, Levels.Info, Levels.Warning, Levels.Error)

        "for ignore" in {
          levels.map(other =>
            Levels.Ignore.higherOrEqualTo(other)
          ) should contain theSameElementsInOrderAs Seq(true, false, false, false)
        }

        "for info" in {
          levels.map(other => Levels.Info.higherOrEqualTo(other)) should contain theSameElementsInOrderAs Seq(
            true,
            true,
            false,
            false
          )
        }

        "for warning" in {
          levels.map(other =>
            Levels.Warning.higherOrEqualTo(other)
          ) should contain theSameElementsInOrderAs Seq(true, true, true, false)
        }

        "for error" in {
          levels.map(other =>
            Levels.Error.higherOrEqualTo(other)
          ) should contain theSameElementsInOrderAs Seq(true, true, true, true)
        }
      }
    }
  }
}
