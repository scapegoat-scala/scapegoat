package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class ComparingUnrelatedTypesTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(new ComparingUnrelatedTypes)

  "equals on disjoint types" - {
    "should report warning" in {

      val code = """class Test {
                      val a = new RuntimeException
                      val b = new Exception

                      "sammy" == BigDecimal(10) // warning 1
                      "sammy" == "bobby" // same type
                      a == b // superclass
                      a == "sammy" // warning 2
                      Some("sam") == None // warning 3
                      Some("sam") == Option("laura") // ok
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 3
    }
  }
}
