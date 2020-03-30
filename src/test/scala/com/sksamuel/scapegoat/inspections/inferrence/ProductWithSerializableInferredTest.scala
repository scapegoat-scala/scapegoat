package com.sksamuel.scapegoat.inspections.inferrence

import com.sksamuel.scapegoat.InspectionTest
import com.sksamuel.scapegoat.inspections.inference.ProductWithSerializableInferred

/** @author Stephen Samuel */
class ProductWithSerializableInferredTest extends InspectionTest {

  override val inspections = Seq(new ProductWithSerializableInferred)

  "when Product with Serializable is inferred" - {
    "should report warning" in {

      val code =
        """class Test {
          |case class A()
          |case class B()
          |val list1 = List(A(), B()) // trigger warning
          |val list2 = List(A()) // fine
          |val list3 = List(A()) :+ A() // fine
          |} """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
