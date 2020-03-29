package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{isScala213, InspectionTest}

/** @author Stephen Samuel */
class JavaConversionsUseTest extends InspectionTest {

  override val inspections = Seq(new JavaConversionsUse)

  "JavaConversionsUse" - {
    "should report warning (for Scala < 2.13)" in {

      val code = """import scala.collection.JavaConversions._
                    object Test {
                      val jul = new java.util.ArrayList[String]
                      val jum = new java.util.HashMap[String,String]
                      val a = jul.exists(_ == "sammy")
                   |  val b = jum.toSeq
                    } """.stripMargin
      val expectedWarnings = if (isScala213) 0 else 1

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings should have size expectedWarnings
    }
  }
}
