package com.sksamuel.scapegoat.inspections.option

import org.scalatest.freespec.AnyFreeSpec
import com.sksamuel.scapegoat.DottyRunner

class OptionGetTest extends AnyFreeSpec {

  val runner = new DottyRunner(classOf[OptionGet])

  "option.get use" - {
    "should report warning" in {

      val code = """class Test {
                      val o = Option("sammy")
                      o.get
                    } """.stripMargin

      val r = runner.compileCodeSnippet(code)
      val e = r.allErrors
      e.toString
    }
  }

}
