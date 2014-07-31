package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class ExpressionAsStatementTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

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

             {
               "nested blocks" // 4 warnings
             }

             new Some("b") // 5 warnings
             val e = new Some("b")
           }
           class Test2 {
             val b = false
             lazy val c = "boo"
             var d = b
             isInstanceOf[Test2] // 6 warnings
             def foo : Boolean = {
               new String("sammy") // 7 warnings
               true // nowarning
             }
             try {
                hashCode() // 8 warnings
                println("hello")
             } catch {
               case e : Exception =>
             }
           } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 8
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
