package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.unneccesary.ExpressionAsStatement
import org.scalatest.{OneInstancePerTest, FreeSpec, Matchers}

/** @author Stephen Samuel */
class ExpressionAsStatementTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ExpressionAsStatement)

  "non unit statements" - {
    "should report warning" in {
      val code =
        """object Test {
             System.currentTimeMillis() // 1 warning
             getClass() // 2 warnings
             def ping : Int = {
               equals(this) // 3 warnings
               4 // nowarning
             }
             def a = true
             val e = new Some("b")
           }
           class Test2 {
             val b = false
             lazy val c = "boo"
             var d = b
             isInstanceOf[Test2] // 5 warnings
             def foo : Boolean = {
               true // nowarning
             }
             try {
                hashCode() // 6 warnings
                println("hello")
             } catch {
               case e : Exception =>
             }
           } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 5
    }
  }

  "case class parameters" - {
    "should not generate warning" in {
      val code = """case class Person(name:String, age:Int=3)""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
