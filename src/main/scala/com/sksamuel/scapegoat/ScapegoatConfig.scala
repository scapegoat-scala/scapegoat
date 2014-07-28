package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.inspections._

/** @author Stephen Samuel */
object ScapegoatConfig extends App {
  def inspections: Seq[Inspection] = Seq(new AsInstanceOf,
    new BrokenOddness,
    new CatchNpe,
    new CollectionPromotionToAny,
    new ConstantIf,
    new ComparisonWithSelf,
    new ComparingFloatingPointTypes,
    new EitherGet,
    //new EmptyElseBlock,
    new EmptyIfBlock,
    new EmptyCatchBlock,
    //new ExpressionAsStatement,
    new JavaConversionsUse,
    new NullUse,
    new OptionGet,
    new ParameterlessMethodReturnsUnit,
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
