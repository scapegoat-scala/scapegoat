package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.inspections._

/** @author Stephen Samuel */
object ScapegoatConfig extends App {
  def inspections: Seq[Inspection] = Seq(new AsInstanceOf,
    new CatchNpe,
    new ComparisonWithSelf,
    new ComparingFloatingPointTypes,
    new EmptyElseBlock,
    new EmptyFinallyBlock,
    new JavaConversionsUse,
    new NullUse,
    new OptionGet,
    new ReturnUse,
    new UnusedMethodParameter,
    new VarUse)
  //    def names: Seq[String] = {
  //      val conf = ConfigFactory.load()
  //      conf.getObject("scapegoat").keySet.asScala.toSeq
  //    }
  //    def instance(name: String): Inspection = Class.forName(name).newInstance.asInstanceOf[Inspection]
  //    names.map(instance)
  //  }
}
