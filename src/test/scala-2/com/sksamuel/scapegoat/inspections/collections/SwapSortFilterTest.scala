package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class SwapSortFilterTest extends InspectionTest {

  override val inspections = Seq[Inspection](new SwapSortFilter)

  "sort then filter" - {
    "should report warning" in {
      val code = """class Test1 {
                     List(1, 2, 3).sorted.filter(_ < 3)
                     List(1, 2, 3).sortBy(_.hashCode).filter(_ < 3)
                     List(1, 2, 3).sortWith((x, y) => x < y).filter(_ < 3)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 3
    }
  }

  "filter then sort" - {
    "should not report warning" in {
      val code = """class Test2 {
                      val a = List(1, 2, 3).filter(_ < 3).sorted
                      val b = List(1, 2, 3).filter(_ < 3).sortBy(_.hashCode)
                      val c = List(1, 2, 3).filter(_ < 3).sortWith((x, y) => x < y)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
