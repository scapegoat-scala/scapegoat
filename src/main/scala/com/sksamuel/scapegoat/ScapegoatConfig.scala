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
    new ComparingUnrelatedTypes,
    new ComparisonWithSelf,
    new ConstantIf,
    new DuplicateMapKey,
    new EitherGet,
    new EmptyCatchBlock,
    new EmptyIfBlock,
    new EmptyMethod,
    new EmptySynchronizedBlock,
    new EmptyTryBlock,
    new ExpressionAsStatement,
    new FilterSizeToCount,
    new IllegalFormatString,
    new IncorrectlyNamedExceptions,
    new IncorrectNumberOfArgsToFormat,
    new IterableHead,
    new JavaConversionsUse,
    new ModOne,
    new IncorrectlyNamedExceptions,
    new NullUse,
    new OptionGet,
    new OptionSize,
    new ParameterlessMethodReturnsUnit,
    new PreferSeqEmpty,
    new PreferSetEmpty,
    new RedundantFinalizer,
    new ReturnUse,
    new TryGet,
    new UnncessaryIf,
    new UnusedMethodParameter,
    new UseExistsNotFindAndIsDefined,
    new VarUse,
    new WhileLoop)
  //  def names: Seq[String] = {
  //    val conf = ConfigFactory.load()
  //    conf.getObject("scapegoat").keySet.asScala.toSeq
  //  }
  //  def instance(name: String): Inspection = Class.forName(name).newInstance.asInstanceOf[Inspection]
  //  def inspections: Seq[Inspection] = names.map(instance)
}
