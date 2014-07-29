package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.inspections._

/** @author Stephen Samuel */
object ScapegoatConfig extends App {
  def inspections: Seq[Inspection] = Seq(
    new AsInstanceOf,
    new BrokenOddness,
    new CatchNpe,
    new CollectionPromotionToAny,
    new ComparingFloatingPointTypes,
    new ComparisonWithSelf,
    new ComparingUnrelatedTypes,
    new ConstantIf,
    new EitherGet,
    new EmptyCatchBlock,
    new EmptyIfBlock,
    new EmptyMethod,
    new EmptySynchronizedBlock,
    new EmptyTryBlock,
    new ExpressionAsStatement,
    new FilterSizeToCount,
    new IterableHead,
    new JavaConversionsUse,
    new ModOne,
    new NullUse,
    new OptionGet,
    new ParameterlessMethodReturnsUnit,
    new PreferSeqEmpty,
    new PreferSetEmpty,
    new RedundantFinalizer,
    new ReturnUse,
    new TryGet,
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
